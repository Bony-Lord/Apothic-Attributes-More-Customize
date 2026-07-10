package dev.apothicdynamicpreview.mixin.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.apothicdynamicpreview.ApothicAttributesMoreCustomize;
import dev.apothicdynamicpreview.AttributeFormula;
import dev.apothicdynamicpreview.ClientConfig;
import dev.apothicdynamicpreview.FormulaHighlighter;
import dev.apothicdynamicpreview.ScientificNumberFormatter;
import dev.apothicdynamicpreview.FormulaHighlighter.Tone;
import dev.shadowsoffire.apothic_attributes.ALConfig;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.apothic_attributes.client.AttributesGui;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;

/**
 * Adds mathematically composed affine formulas to merged/dynamic entries and
 * configurable semantic color highlighting to all entries in the compact list.
 */
@Mixin(value = AttributesGui.class, remap = false)
public abstract class AttributesGuiMixin {
    @Shadow
    protected static boolean hideUnchanged;

    @Shadow
    protected static float scrollOffset;

    @Shadow
    protected List<AttributeInstance> data;

    @Shadow
    protected int startIndex;

    @Shadow
    @Final
    protected Player player;

    @Shadow
    protected abstract int compareAttrs(AttributeInstance first, AttributeInstance second);

    @Unique
    private AttributeInstance apothicDynamicPreview$currentAttribute;

    @Unique
    private final Map<String, AttributeInstance> apothicDynamicPreview$mergedCache = new HashMap<>();

    @Unique
    private final Map<AttributeInstance, AttributeFormula> apothicDynamicPreview$mergedFormulas = new IdentityHashMap<>();

    @Unique
    private final Map<AttributeInstance, List<ResourceLocation>> apothicDynamicPreview$mergedAttributeIds = new IdentityHashMap<>();

    @Unique
    private final Map<AttributeInstance, Boolean> apothicDynamicPreview$mergedHasDynamicMember = new IdentityHashMap<>();

    @Unique
    private static final Set<String> apothicDynamicPreview$loggedGroups = new HashSet<>();

    @Unique
    private static final Set<String> apothicDynamicPreview$chatWarnings = new HashSet<>();

    @Inject(method = "refreshData", at = @At("RETURN"))
    private void apothicDynamicPreview$mergeDisplayGroups(CallbackInfo callback) {
        Set<AttributeInstance> claimed = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        Set<String> activeCacheKeys = new HashSet<>();
        int groupIndex = 0;

        List<List<ResourceLocation>> configuredGroups = ClientConfig.ENABLE_DISPLAY_MERGE_GROUPS.get()
            ? ClientConfig.getMergeGroups()
            : List.of();

        for (List<ResourceLocation> group : configuredGroups) {
            List<AttributeInstance> members = new java.util.ArrayList<>();
            List<ResourceLocation> missingIds = new java.util.ArrayList<>();
            String cacheKey = group.stream().map(ResourceLocation::toString).collect(Collectors.joining("|"));

            for (ResourceLocation id : group) {
                java.util.Optional<AttributeInstance> instance = this.apothicDynamicPreview$findInstance(id);
                if (instance.isEmpty()) {
                    missingIds.add(id);
                }
                else if (!ALConfig.hiddenAttributes.contains(id) && !claimed.contains(instance.get())) {
                    members.add(instance.get());
                }
            }

            if (!missingIds.isEmpty() && ClientConfig.NOTIFY_MISSING_ATTRIBUTES.get()) {
                Set<ResourceLocation> excluded = new HashSet<>(group);
                for (ResourceLocation missingId : missingIds) {
                    this.apothicDynamicPreview$notifyMissingAttribute(missingId, excluded);
                }
            }

            if (members.size() < 2) {
                if (ClientConfig.LOG_RESOLVED_GROUPS.get() && this.apothicDynamicPreview$loggedGroups.add("missing:" + cacheKey)) {
                    ApothicAttributesMoreCustomize.LOGGER.warn(
                        "Attribute display merge group '{}' resolved only {} of {} attributes and was skipped.",
                        cacheKey,
                        members.size(),
                        group.size()
                    );
                }
                groupIndex++;
                continue;
            }

            members = this.apothicDynamicPreview$orderMembers(members);
            boolean hasDynamicMember = members.stream()
                .anyMatch(instance -> instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES));
            boolean changed = members.stream().anyMatch(AttributesGuiMixin::apothicDynamicPreview$isChanged);
            activeCacheKeys.add(cacheKey);

            if (ClientConfig.LOG_RESOLVED_GROUPS.get() && this.apothicDynamicPreview$loggedGroups.add("merged:" + cacheKey)) {
                ApothicAttributesMoreCustomize.LOGGER.info(
                    "Merged {} attribute GUI entries into display group '{}'.",
                    members.size(),
                    cacheKey
                );
            }

            Set<ResourceLocation> groupIds = new HashSet<>(group);
            this.data.removeIf(instance -> instance.getAttribute().unwrapKey()
                .map(key -> groupIds.contains(key.location()))
                .orElse(false));
            claimed.addAll(members);

            if (!hideUnchanged || changed) {
                AttributeInstance merged = this.apothicDynamicPreview$createMergedInstance(
                    cacheKey,
                    members,
                    hasDynamicMember,
                    groupIndex
                );
                this.data.add(merged);
            }

            groupIndex++;
        }

        this.apothicDynamicPreview$mergedCache.keySet().retainAll(activeCacheKeys);
        this.apothicDynamicPreview$mergedFormulas.keySet().retainAll(this.apothicDynamicPreview$mergedCache.values());
        this.apothicDynamicPreview$mergedAttributeIds.keySet().retainAll(this.apothicDynamicPreview$mergedCache.values());
        this.apothicDynamicPreview$mergedHasDynamicMember.keySet().retainAll(this.apothicDynamicPreview$mergedCache.values());

        if (hideUnchanged) {
            this.data.removeIf(instance ->
                instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES)
                    && instance.getModifiers().stream().noneMatch(modifier -> Math.abs(modifier.amount()) > 0.0000001D));
        }

        this.data.sort(this::compareAttrs);
        int offScreenRows = Math.max(0, this.data.size() - AttributesGui.MAX_ENTRIES);
        if (offScreenRows == 0) {
            scrollOffset = 0.0F;
            this.startIndex = 0;
        }
        else {
            // Apothic Attributes refreshes the unmerged list first every tick.
            // Never derive scrollOffset back from the shorter merged list, or
            // rounding drift will move the list a little on every frame.
            this.startIndex = Math.max(0, Math.min(offScreenRows, Math.round(scrollOffset * offScreenRows)));
        }
    }

    @Unique
    private java.util.Optional<AttributeInstance> apothicDynamicPreview$findInstance(ResourceLocation id) {
        java.util.Optional<AttributeInstance> visible = this.data.stream()
            .filter(instance -> instance.getAttribute().unwrapKey()
                .map(key -> key.location().equals(id))
                .orElse(false))
            .findFirst();

        if (visible.isPresent()) {
            return visible;
        }

        return BuiltInRegistries.ATTRIBUTE.getHolder(id).map(this.player::getAttribute);
    }

    @Unique
    private void apothicDynamicPreview$notifyMissingAttribute(ResourceLocation missingId, Set<ResourceLocation> excluded) {
        ResourceLocation closest = ClientConfig.SUGGEST_CLOSEST_ATTRIBUTE.get()
            ? this.apothicDynamicPreview$findClosestAttribute(missingId, excluded)
            : null;
        String warningKey = missingId + "->" + closest;
        if (!apothicDynamicPreview$chatWarnings.add(warningKey)) return;

        MutableComponent message = Component.literal("[Apothic Attributes: More Customize] ")
            .withStyle(ChatFormatting.GOLD)
            .append(Component.translatable("message.apothic_dynamic_preview.attribute_not_found")
                .withStyle(ChatFormatting.RED))
            .append(Component.literal(" " + missingId).withStyle(ChatFormatting.YELLOW));

        if (closest != null) {
            message.append(Component.literal("\n"))
                .append(Component.translatable("message.apothic_dynamic_preview.closest_attribute")
                    .withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" " + closest).withStyle(ChatFormatting.GREEN));
        }

        message.append(Component.literal("\n"))
            .append(Component.translatable("message.apothic_dynamic_preview.edit_config")
                .withStyle(ChatFormatting.DARK_GRAY));
        this.player.displayClientMessage(message, false);
    }

    @Unique
    private ResourceLocation apothicDynamicPreview$findClosestAttribute(
        ResourceLocation missingId,
        Set<ResourceLocation> excluded
    ) {
        String missingPath = apothicDynamicPreview$normalizePath(missingId.getPath());
        String missingNamespace = apothicDynamicPreview$normalizeName(missingId.getNamespace());

        List<ResourceLocation> candidates = BuiltInRegistries.ATTRIBUTE.holders()
            .filter(holder -> this.player.getAttribute(holder) != null)
            .flatMap(holder -> holder.unwrapKey().stream())
            .map(key -> key.location())
            .filter(candidate -> !excluded.contains(candidate))
            .toList();

        List<ResourceLocation> sameNamespace = candidates.stream()
            .filter(candidate -> candidate.getNamespace().equals(missingId.getNamespace()))
            .toList();
        List<ResourceLocation> searchPool = sameNamespace.isEmpty() ? candidates : sameNamespace;

        return searchPool.stream()
            .min(java.util.Comparator
                .comparingInt((ResourceLocation candidate) ->
                    10 * apothicDynamicPreview$levenshtein(
                        missingPath,
                        apothicDynamicPreview$normalizePath(candidate.getPath())
                    )
                        + apothicDynamicPreview$levenshtein(
                            missingNamespace,
                            apothicDynamicPreview$normalizeName(candidate.getNamespace())
                        ))
                .thenComparing(ResourceLocation::toString))
            .orElse(null);
    }

    @Unique
    private static String apothicDynamicPreview$normalizePath(String value) {
        String normalized = value.toLowerCase(java.util.Locale.ROOT);
        if (normalized.startsWith("generic.")) normalized = normalized.substring("generic.".length());
        if (normalized.startsWith("player.")) normalized = normalized.substring("player.".length());
        return apothicDynamicPreview$normalizeName(normalized);
    }

    @Unique
    private static String apothicDynamicPreview$normalizeName(String value) {
        return value.toLowerCase(java.util.Locale.ROOT)
            .replace("_", "")
            .replace(".", "")
            .replace("-", "");
    }

    @Unique
    private static int apothicDynamicPreview$levenshtein(String left, String right) {
        int[] previous = new int[right.length() + 1];
        int[] current = new int[right.length() + 1];

        for (int j = 0; j <= right.length(); j++) previous[j] = j;
        for (int i = 1; i <= left.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= right.length(); j++) {
                int substitution = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(
                    Math.min(current[j - 1] + 1, previous[j] + 1),
                    previous[j - 1] + substitution
                );
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }

        return previous[right.length()];
    }

    @Unique
    private static boolean apothicDynamicPreview$isChanged(AttributeInstance instance) {
        if (instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES)) {
            return instance.getModifiers().stream().anyMatch(modifier -> Math.abs(modifier.amount()) > 0.0000001D);
        }
        return Math.abs(instance.getValue() - instance.getBaseValue()) > 0.0000001D;
    }

    @Unique
    private List<AttributeInstance> apothicDynamicPreview$orderMembers(List<AttributeInstance> members) {
        if (ClientConfig.MERGE_ORDER.get() == ClientConfig.MergeOrder.CONFIG_ORDER) {
            return members;
        }

        boolean normalFirst = ClientConfig.MERGE_ORDER.get() == ClientConfig.MergeOrder.NORMAL_BEFORE_DYNAMIC;
        List<AttributeInstance> ordered = new java.util.ArrayList<>(members.size());
        for (int pass = 0; pass < 2; pass++) {
            boolean wantDynamic = normalFirst ? pass == 1 : pass == 0;
            for (AttributeInstance member : members) {
                boolean dynamic = member.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);
                if (dynamic == wantDynamic) ordered.add(member);
            }
        }
        return ordered;
    }

    @Unique
    private AttributeInstance apothicDynamicPreview$createMergedInstance(
        String cacheKey,
        List<AttributeInstance> members,
        boolean hasDynamicMember,
        int groupIndex
    ) {
        AttributeInstance displaySource = hasDynamicMember
            ? members.stream()
                .filter(instance -> instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES))
                .findFirst()
                .orElseThrow()
            : members.getFirst();

        AttributeInstance merged = this.apothicDynamicPreview$mergedCache.get(cacheKey);
        if (merged == null || !merged.getAttribute().equals(displaySource.getAttribute())) {
            merged = new AttributeInstance(displaySource.getAttribute(), ignored -> {});
            this.apothicDynamicPreview$mergedCache.put(cacheKey, merged);
        }
        else {
            merged.removeModifiers();
        }

        merged.setBaseValue(displaySource.getAttribute().value().getDefaultValue());

        AttributeFormula formula = AttributeFormula.IDENTITY;
        for (AttributeInstance member : members) {
            boolean dynamicMember = member.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);
            AttributeFormula memberFormula = AttributeFormula.from(
                member,
                ClientConfig.INCLUDE_CHANGED_BASE_VALUES.get() && !dynamicMember
            );
            formula = formula.then(memberFormula);
        }
        this.apothicDynamicPreview$mergedFormulas.put(merged, formula);
        this.apothicDynamicPreview$mergedAttributeIds.put(merged, members.stream()
            .flatMap(member -> member.getAttribute().unwrapKey().stream())
            .map(key -> key.location())
            .toList());
        this.apothicDynamicPreview$mergedHasDynamicMember.put(merged, hasDynamicMember);

        // Keep Apothic Attributes treating the synthetic entry as changed. Rendering uses
        // mergedFormulas directly, so these modifiers are only a compatible carrier.
        double epsilon = ClientConfig.IDENTITY_EPSILON.get();
        if (Math.abs(formula.constant()) > epsilon) {
            merged.addTransientModifier(new AttributeModifier(
                ApothicAttributesMoreCustomize.loc("merged_constant/" + groupIndex),
                formula.constant(),
                Operation.ADD_VALUE
            ));
        }
        if (Math.abs(formula.multiplier() - 1.0D) > epsilon) {
            merged.addTransientModifier(new AttributeModifier(
                ApothicAttributesMoreCustomize.loc("merged_multiplier/" + groupIndex),
                formula.multiplier() - 1.0D,
                Operation.ADD_MULTIPLIED_BASE
            ));
        }

        return merged;
    }

    @Inject(method = "renderEntry", at = @At("HEAD"))
    private void apothicDynamicPreview$captureAttribute(
        GuiGraphics graphics,
        AttributeInstance instance,
        int x,
        int y,
        int mouseX,
        int mouseY,
        CallbackInfo callback
    ) {
        this.apothicDynamicPreview$currentAttribute = instance;
    }

    @Redirect(
        method = "renderEntry",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/Attribute;toValueComponent(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;DLnet/minecraft/world/item/TooltipFlag;)Lnet/minecraft/network/chat/MutableComponent;"
        ),
        require = 1
    )
    private MutableComponent apothicDynamicPreview$replaceRegularValue(
        Attribute attribute,
        Operation operation,
        double value,
        TooltipFlag flag
    ) {
        MutableComponent original = attribute.toValueComponent(operation, value, flag);
        AttributeInstance instance = this.apothicDynamicPreview$currentAttribute;
        return instance == null ? original : this.apothicDynamicPreview$buildDisplayValue(instance, original);
    }

    @Redirect(
        method = "renderEntry",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        ),
        require = 1
    )
    private MutableComponent apothicDynamicPreview$replaceUnknownValue(String ignored) {
        AttributeInstance instance = this.apothicDynamicPreview$currentAttribute;
        return instance == null
            ? Component.literal("—")
            : this.apothicDynamicPreview$buildDisplayValue(instance, Component.literal("—"));
    }

    @Inject(method = "renderEntry", at = @At("RETURN"))
    private void apothicDynamicPreview$releaseAttribute(
        GuiGraphics graphics,
        AttributeInstance instance,
        int x,
        int y,
        int mouseX,
        int mouseY,
        CallbackInfo callback
    ) {
        this.apothicDynamicPreview$currentAttribute = null;
    }

    /**
     * Keeps Apothic Attributes' original text for standalone regular attributes,
     * while dynamic and merged entries may use the calculated affine formula.
     */
    @Unique
    private MutableComponent apothicDynamicPreview$buildDisplayValue(
        AttributeInstance instance,
        MutableComponent original
    ) {
        AttributeFormula formula = this.apothicDynamicPreview$getFormula(instance);
        boolean merged = this.apothicDynamicPreview$mergedFormulas.containsKey(instance);
        boolean dynamic = this.apothicDynamicPreview$isDynamicEntry(instance);

        // Native non-dynamic display is preserved for both standalone regular attributes
        // and groups made exclusively from regular attributes. The addon may recolor the
        // returned component, but never rebuilds its number/unit formatting.
        if (!dynamic) {
            return this.apothicDynamicPreview$colorSingleValue(original, instance, formula);
        }

        boolean formulaEnabledForTarget = merged
            ? ClientConfig.SHOW_DYNAMIC_GROUP_FORMULA_TEXT.get()
            : ClientConfig.SHOW_STANDALONE_DYNAMIC_FORMULA_TEXT.get();
        if (!ClientConfig.SHOW_FORMULA_TEXT.get() || !formulaEnabledForTarget) {
            return this.apothicDynamicPreview$colorSingleValue(
                Component.literal("—"),
                instance,
                formula
            );
        }

        return this.apothicDynamicPreview$buildFormula(instance, formula);
    }

    @Unique
    private AttributeFormula apothicDynamicPreview$getFormula(AttributeInstance instance) {
        AttributeFormula formula = this.apothicDynamicPreview$mergedFormulas.get(instance);
        if (formula != null) return formula;

        boolean dynamic = instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);
        return AttributeFormula.from(
            instance,
            ClientConfig.INCLUDE_CHANGED_BASE_VALUES.get() && !dynamic
        );
    }

    @Unique
    private MutableComponent apothicDynamicPreview$buildFormula(
        AttributeInstance instance,
        AttributeFormula formula
    ) {
        int percentDecimals = ClientConfig.PERCENT_DECIMALS.get();
        int constantDecimals = ClientConfig.CONSTANT_DECIMALS.get();
        double epsilon = ClientConfig.IDENTITY_EPSILON.get();
        double rawPercent = formula.multiplier() * 100.0D;
        double rawConstant = formula.constant();
        ScientificNumberFormatter.Options scientificOptions = ClientConfig.scientificNumberOptions();
        boolean scientificForTarget = this.apothicDynamicPreview$isMergedEntry(instance)
            ? ClientConfig.SCIENTIFIC_DYNAMIC_GROUP_FORMULAS.get()
            : ClientConfig.SCIENTIFIC_STANDALONE_DYNAMIC_FORMULAS.get();
        boolean scientificForPercent = scientificForTarget && ClientConfig.SCIENTIFIC_PERCENTAGES.get();
        boolean scientificForConstant = scientificForTarget && ClientConfig.SCIENTIFIC_CONSTANTS.get();
        boolean scientificPercent = ScientificNumberFormatter.shouldUseScientific(
            rawPercent,
            scientificForPercent,
            scientificOptions
        );
        boolean scientificConstant = ScientificNumberFormatter.shouldUseScientific(
            rawConstant,
            scientificForConstant,
            scientificOptions
        );
        double roundedPercent = scientificPercent
            ? rawPercent
            : ScientificNumberFormatter.roundPlain(rawPercent, percentDecimals);
        double roundedConstant = scientificConstant
            ? rawConstant
            : ScientificNumberFormatter.roundPlain(rawConstant, constantDecimals);
        boolean showMultiplier = Math.abs(formula.multiplier() - 1.0D) > epsilon
            && (scientificPercent || roundedPercent != 100.0D);
        boolean showConstant = Math.abs(rawConstant) > epsilon
            && (scientificConstant || roundedConstant != 0.0D);

        if (!showMultiplier && !showConstant) {
            return ClientConfig.SHOW_IDENTITY_AS_DASH.get()
                ? Component.literal("—").withStyle(FormulaHighlighter.formatting(Tone.NEUTRAL))
                : Component.empty();
        }

        Attribute attribute = instance.getAttribute().value();
        List<ResourceLocation> ids = this.apothicDynamicPreview$getAttributeIds(instance);
        boolean highlighting = this.apothicDynamicPreview$shouldHighlight(instance, ids);
        boolean inverted = highlighting && this.apothicDynamicPreview$usesInvertedRule(instance, ids);

        Tone multiplierTone = highlighting
            ? FormulaHighlighter.multiplierTone(attribute, formula, inverted, epsilon)
            : Tone.NEUTRAL;
        Tone constantTone = highlighting
            ? FormulaHighlighter.constantTone(attribute, formula, inverted, epsilon)
            : Tone.NEUTRAL;
        if (!showMultiplier) multiplierTone = Tone.NEUTRAL;
        if (!showConstant) constantTone = Tone.NEUTRAL;

        if (ClientConfig.HIGHLIGHT_LAYOUT.get() == ClientConfig.HighlightLayout.COMMON) {
            Tone commonTone = this.apothicDynamicPreview$getCommonTone(
                instance,
                ids,
                formula,
                multiplierTone,
                constantTone,
                inverted,
                highlighting
            );
            multiplierTone = commonTone;
            constantTone = commonTone;
        }

        MutableComponent result = Component.empty();
        if (showMultiplier) {
            String percentText = ScientificNumberFormatter.format(
                rawPercent,
                percentDecimals,
                scientificForPercent,
                scientificOptions
            );
            result.append(Component.literal(percentText + "%")
                .withStyle(FormulaHighlighter.formatting(multiplierTone)));
        }

        if (showConstant) {
            if (showMultiplier) result.append(Component.literal(" "));
            String sign = rawConstant < 0.0D ? "- " : "+ ";
            String constantText = ScientificNumberFormatter.format(
                Math.abs(rawConstant),
                constantDecimals,
                scientificForConstant,
                scientificOptions
            );
            result.append(Component.literal(sign + constantText)
                .withStyle(FormulaHighlighter.formatting(constantTone)));
        }

        return result;
    }

    @Unique
    private MutableComponent apothicDynamicPreview$colorSingleValue(
        MutableComponent value,
        AttributeInstance instance,
        AttributeFormula formula
    ) {
        List<ResourceLocation> ids = this.apothicDynamicPreview$getAttributeIds(instance);
        boolean highlighting = this.apothicDynamicPreview$shouldHighlight(instance, ids);
        if (!highlighting) {
            return value;
        }

        boolean inverted = this.apothicDynamicPreview$usesInvertedRule(instance, ids);
        double epsilon = ClientConfig.IDENTITY_EPSILON.get();
        Tone multiplierTone = FormulaHighlighter.multiplierTone(
            instance.getAttribute().value(), formula, inverted, epsilon);
        Tone constantTone = FormulaHighlighter.constantTone(
            instance.getAttribute().value(), formula, inverted, epsilon);
        Tone tone = this.apothicDynamicPreview$getCommonTone(
            instance,
            ids,
            formula,
            multiplierTone,
            constantTone,
            inverted,
            true
        );
        return value.copy().withStyle(FormulaHighlighter.formatting(tone));
    }

    @Unique
    private Tone apothicDynamicPreview$getCommonTone(
        AttributeInstance instance,
        List<ResourceLocation> ids,
        AttributeFormula formula,
        Tone multiplierTone,
        Tone constantTone,
        boolean inverted,
        boolean highlighting
    ) {
        if (!highlighting) return Tone.NEUTRAL;

        return switch (ClientConfig.COMMON_HIGHLIGHT_MODE.get()) {
            case STANDARD -> FormulaHighlighter.standardTone(
                instance.getAttribute().value(),
                formula,
                this.apothicDynamicPreview$getEvaluationBase(instance, ids),
                inverted,
                ClientConfig.IDENTITY_EPSILON.get()
            );
            case PRIORITY -> FormulaHighlighter.priorityTone(multiplierTone, constantTone);
            case LAYERED -> FormulaHighlighter.layeredTone(multiplierTone, constantTone);
        };
    }

    @Unique
    private boolean apothicDynamicPreview$shouldHighlight(
        AttributeInstance instance,
        List<ResourceLocation> ids
    ) {
        if (!ClientConfig.ENABLE_HIGHLIGHTING.get()) return false;

        boolean merged = this.apothicDynamicPreview$isMergedEntry(instance);
        boolean standaloneDynamic = !merged
            && instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);

        if (merged) {
            if (!ClientConfig.HIGHLIGHT_MERGED_GROUPS.get()) return false;
        }
        else if (standaloneDynamic) {
            if (!ClientConfig.HIGHLIGHT_DYNAMIC_ATTRIBUTES.get()) return false;
        }
        else if (!ClientConfig.HIGHLIGHT_NON_DYNAMIC_ATTRIBUTES.get()) {
            return false;
        }

        boolean applyExceptions = merged
            ? ClientConfig.APPLY_EXCEPTIONS_TO_MERGED_GROUPS.get()
            : standaloneDynamic
                ? ClientConfig.APPLY_EXCEPTIONS_TO_DYNAMIC_ATTRIBUTES.get()
                : ClientConfig.APPLY_EXCEPTIONS_TO_NON_DYNAMIC_ATTRIBUTES.get();
        return !applyExceptions || !ClientConfig.matchesHighlightException(ids);
    }

    @Unique
    private boolean apothicDynamicPreview$usesInvertedRule(
        AttributeInstance instance,
        List<ResourceLocation> ids
    ) {
        boolean merged = this.apothicDynamicPreview$isMergedEntry(instance);
        boolean standaloneDynamic = !merged
            && instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);
        boolean applyInversion = merged
            ? ClientConfig.APPLY_INVERSION_TO_MERGED_GROUPS.get()
            : standaloneDynamic
                ? ClientConfig.APPLY_INVERSION_TO_DYNAMIC_ATTRIBUTES.get()
                : ClientConfig.APPLY_INVERSION_TO_NON_DYNAMIC_ATTRIBUTES.get();
        return applyInversion && ClientConfig.usesInvertedRule(ids);
    }

    @Unique
    private boolean apothicDynamicPreview$isMergedEntry(AttributeInstance instance) {
        return this.apothicDynamicPreview$mergedFormulas.containsKey(instance);
    }

    @Unique
    private boolean apothicDynamicPreview$isDynamicEntry(AttributeInstance instance) {
        Boolean mergedDynamic = this.apothicDynamicPreview$mergedHasDynamicMember.get(instance);
        return mergedDynamic != null
            ? mergedDynamic
            : instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);
    }

    @Unique
    private List<ResourceLocation> apothicDynamicPreview$getAttributeIds(AttributeInstance instance) {
        List<ResourceLocation> mergedIds = this.apothicDynamicPreview$mergedAttributeIds.get(instance);
        if (mergedIds != null && !mergedIds.isEmpty()) return mergedIds;
        return instance.getAttribute().unwrapKey()
            .map(key -> List.of(key.location()))
            .orElseGet(List::of);
    }

    @Unique
    private double apothicDynamicPreview$getEvaluationBase(
        AttributeInstance instance,
        List<ResourceLocation> ids
    ) {
        boolean merged = this.apothicDynamicPreview$isMergedEntry(instance);
        boolean standaloneDynamic = !merged
            && instance.getAttribute().is(ALObjects.Tags.DYNAMIC_BASE_ATTRIBUTES);
        boolean applyConfiguredBase = merged
            ? ClientConfig.APPLY_BASE_VALUES_TO_MERGED_GROUPS.get()
            : standaloneDynamic
                ? ClientConfig.APPLY_BASE_VALUES_TO_DYNAMIC_ATTRIBUTES.get()
                : ClientConfig.APPLY_BASE_VALUES_TO_NON_DYNAMIC_ATTRIBUTES.get();

        if (applyConfiguredBase) {
            Double configured = ClientConfig.findConfiguredBaseValue(ids);
            if (configured != null) return configured;
        }

        return switch (ClientConfig.EVALUATION_BASE_FALLBACK.get()) {
            case REGISTERED_DEFAULT -> instance.getAttribute().value().getDefaultValue();
            case CURRENT_BASE -> instance.getBaseValue();
            case ZERO -> 0.0D;
            case ONE -> 1.0D;
        };
    }

}
