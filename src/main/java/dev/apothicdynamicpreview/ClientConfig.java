package dev.apothicdynamicpreview;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final String DEFAULT_SPRINTING_SPEED_GROUP =
        "puffish_attributes:sprinting_speed|artifacts:generic.sprinting_speed";

    public enum MergeOrder {
        /** Stable partition: normal attributes first, dynamic attributes second. */
        NORMAL_BEFORE_DYNAMIC,
        /** Use the exact left-to-right order written in every group. */
        CONFIG_ORDER,
        /** Stable partition: dynamic attributes first, normal attributes second. */
        DYNAMIC_BEFORE_NORMAL
    }

    public enum HighlightLayout {
        /** Multiplier percentage and additive constant receive independent colors. */
        SEPARATE,
        /** The complete formula receives one color selected by commonHighlightMode. */
        COMMON
    }

    public enum CommonHighlightMode {
        /** Evaluate the complete formula at the configured base value. */
        STANDARD,
        /** Use multiplier color first; only use constant color when multiplier is neutral. */
        PRIORITY,
        /** Same colors stay colored, opposite colors become neutral, neutral yields to the other part. */
        LAYERED
    }

    public enum EvaluationBaseFallback {
        /** Use the attribute's registered default value. */
        REGISTERED_DEFAULT,
        /** Use the current AttributeInstance base value. */
        CURRENT_BASE,
        /** Use zero. */
        ZERO,
        /** Use one. */
        ONE
    }

    public enum TextColor {
        BLACK(ChatFormatting.BLACK),
        DARK_BLUE(ChatFormatting.DARK_BLUE),
        DARK_GREEN(ChatFormatting.DARK_GREEN),
        DARK_AQUA(ChatFormatting.DARK_AQUA),
        DARK_RED(ChatFormatting.DARK_RED),
        DARK_PURPLE(ChatFormatting.DARK_PURPLE),
        GOLD(ChatFormatting.GOLD),
        GRAY(ChatFormatting.GRAY),
        DARK_GRAY(ChatFormatting.DARK_GRAY),
        BLUE(ChatFormatting.BLUE),
        GREEN(ChatFormatting.GREEN),
        AQUA(ChatFormatting.AQUA),
        RED(ChatFormatting.RED),
        LIGHT_PURPLE(ChatFormatting.LIGHT_PURPLE),
        YELLOW(ChatFormatting.YELLOW),
        WHITE(ChatFormatting.WHITE);

        private final ChatFormatting formatting;

        TextColor(ChatFormatting formatting) {
            this.formatting = formatting;
        }

        public ChatFormatting formatting() {
            return this.formatting;
        }
    }

    private static final Map<ResourceLocation, ResourceLocation> LEGACY_ATTRIBUTE_IDS = Map.of(
        ResourceLocation.fromNamespaceAndPath("pufferfish_attributes", "sprintingspeed"),
        ResourceLocation.fromNamespaceAndPath("puffish_attributes", "sprinting_speed"),
        ResourceLocation.fromNamespaceAndPath("pufferfish_attributes", "sprinting_speed"),
        ResourceLocation.fromNamespaceAndPath("puffish_attributes", "sprinting_speed")
    );

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_DISPLAY_MERGE_GROUPS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> DISPLAY_MERGE_GROUPS;
    public static final ModConfigSpec.EnumValue<MergeOrder> MERGE_ORDER;
    public static final ModConfigSpec.BooleanValue INCLUDE_CHANGED_BASE_VALUES;
    public static final ModConfigSpec.BooleanValue NOTIFY_MISSING_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue SUGGEST_CLOSEST_ATTRIBUTE;
    public static final ModConfigSpec.BooleanValue LOG_RESOLVED_GROUPS;

    public static final ModConfigSpec.BooleanValue SHOW_FORMULA_TEXT;
    public static final ModConfigSpec.BooleanValue SHOW_STANDALONE_DYNAMIC_FORMULA_TEXT;
    public static final ModConfigSpec.BooleanValue SHOW_DYNAMIC_GROUP_FORMULA_TEXT;
    public static final ModConfigSpec.IntValue PERCENT_DECIMALS;
    public static final ModConfigSpec.IntValue CONSTANT_DECIMALS;
    public static final ModConfigSpec.BooleanValue ENABLE_SCIENTIFIC_NOTATION;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_STANDALONE_DYNAMIC_FORMULAS;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_DYNAMIC_GROUP_FORMULAS;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_PERCENTAGES;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_CONSTANTS;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_LARGE_NUMBERS;
    public static final ModConfigSpec.IntValue SCIENTIFIC_INTEGER_DIGITS_THRESHOLD;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_SMALL_NUMBERS;
    public static final ModConfigSpec.IntValue SCIENTIFIC_LEADING_FRACTION_ZEROS_THRESHOLD;
    public static final ModConfigSpec.IntValue SCIENTIFIC_MANTISSA_DECIMALS;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_TRIM_TRAILING_ZEROS;
    public static final ModConfigSpec.EnumValue<ScientificNumberFormatter.NotationStyle> SCIENTIFIC_NOTATION_STYLE;
    public static final ModConfigSpec.BooleanValue SCIENTIFIC_ALWAYS_SHOW_EXPONENT_SIGN;
    public static final ModConfigSpec.IntValue SCIENTIFIC_MINIMUM_EXPONENT_DIGITS;
    public static final ModConfigSpec.DoubleValue IDENTITY_EPSILON;
    public static final ModConfigSpec.BooleanValue SHOW_IDENTITY_AS_DASH;

    public static final ModConfigSpec.BooleanValue ENABLE_HIGHLIGHTING;
    public static final ModConfigSpec.BooleanValue HIGHLIGHT_NON_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue HIGHLIGHT_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue HIGHLIGHT_MERGED_GROUPS;
    public static final ModConfigSpec.BooleanValue APPLY_EXCEPTIONS_TO_NON_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue APPLY_EXCEPTIONS_TO_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue APPLY_EXCEPTIONS_TO_MERGED_GROUPS;
    public static final ModConfigSpec.BooleanValue APPLY_INVERSION_TO_NON_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue APPLY_INVERSION_TO_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue APPLY_INVERSION_TO_MERGED_GROUPS;
    public static final ModConfigSpec.BooleanValue APPLY_BASE_VALUES_TO_NON_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue APPLY_BASE_VALUES_TO_DYNAMIC_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue APPLY_BASE_VALUES_TO_MERGED_GROUPS;
    public static final ModConfigSpec.EnumValue<HighlightLayout> HIGHLIGHT_LAYOUT;
    public static final ModConfigSpec.EnumValue<CommonHighlightMode> COMMON_HIGHLIGHT_MODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HIGHLIGHT_EXCEPTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> INVERTED_RULE_ATTRIBUTES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ATTRIBUTE_BASE_VALUES;
    public static final ModConfigSpec.EnumValue<EvaluationBaseFallback> EVALUATION_BASE_FALLBACK;
    public static final ModConfigSpec.EnumValue<TextColor> POSITIVE_COLOR;
    public static final ModConfigSpec.EnumValue<TextColor> NEGATIVE_COLOR;
    public static final ModConfigSpec.EnumValue<TextColor> NEUTRAL_COLOR;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment(
            "Client-side merging of equivalent attributes in the Apothic Attributes GUI.",
            "This affects display only. It never changes actual gameplay values."
        ).translation("config.apothic_dynamic_preview.section.attribute_display_merging").push("attribute_display_merging");

        ENABLE_DISPLAY_MERGE_GROUPS = builder
            .comment(
                "Master switch for configured display groups.",
                "When disabled, no attributes are merged and all original entries remain visible."
            )
            .translation("config.apothic_dynamic_preview.merge_groups_enabled")
            .define("enabled", true);

        DISPLAY_MERGE_GROUPS = builder
            .comment(
                "Groups of equivalent attribute IDs. Separate IDs with |.",
                "The first group that claims an attribute wins when groups overlap.",
                "Example: puffish_attributes:sprinting_speed|artifacts:generic.sprinting_speed",
                "Legacy Pufferfish namespace/spelling aliases are converted automatically."
            )
            .translation("config.apothic_dynamic_preview.groups")
            .defineListAllowEmpty("groups", List.of(DEFAULT_SPRINTING_SPEED_GROUP), ClientConfig::isValidGroup);

        MERGE_ORDER = builder
            .comment(
                "How member formulas are composed.",
                "NORMAL_BEFORE_DYNAMIC (default): normal attributes are applied before dynamic ones;",
                "within each category, the order written in the group is preserved.",
                "CONFIG_ORDER: exact left-to-right group order.",
                "DYNAMIC_BEFORE_NORMAL: dynamic attributes first, preserving order within categories."
            )
            .translation("config.apothic_dynamic_preview.merge_order")
            .defineEnum("mergeOrder", MergeOrder.NORMAL_BEFORE_DYNAMIC);

        INCLUDE_CHANGED_BASE_VALUES = builder
            .comment(
                "Include differences between a normal AttributeInstance base value and its registered default",
                "as an additive term in the automatically derived formula.",
                "Dynamic attributes always use a symbolic input base, so their stored base value is intentionally ignored."
            )
            .translation("config.apothic_dynamic_preview.include_changed_base_values")
            .define("includeChangedBaseValues", true);

        NOTIFY_MISSING_ATTRIBUTES = builder
            .comment("Show one chat warning per session for every configured attribute that is unavailable.")
            .translation("config.apothic_dynamic_preview.notify_missing_attributes")
            .define("notifyMissingAttributes", true);

        SUGGEST_CLOSEST_ATTRIBUTE = builder
            .comment("When warning about a missing ID, search for and display the closest available ID.")
            .translation("config.apothic_dynamic_preview.suggest_closest_attribute")
            .define("suggestClosestAttribute", true);

        LOG_RESOLVED_GROUPS = builder
            .comment("Write successful and skipped merge-group diagnostics to latest.log once per session.")
            .translation("config.apothic_dynamic_preview.log_resolved_groups")
            .define("logResolvedGroups", true);

        builder.pop();

        builder.comment(
            "Formatting of automatically calculated affine formulas.",
            "A formula has the form output = input * multiplier + constant."
        ).translation("config.apothic_dynamic_preview.section.formula_display").push("formula_display");

        SHOW_FORMULA_TEXT = builder
            .comment(
                "Master switch for generated percentage/constant text.",
                "Standalone non-dynamic attributes and groups containing only non-dynamic attributes",
                "always keep the native Apothic Attributes value display.",
                "When disabled, dynamic entries use a dash instead of a generated formula."
            )
            .translation("config.apothic_dynamic_preview.show_formula_text")
            .define("showFormulaText", true);

        SHOW_STANDALONE_DYNAMIC_FORMULA_TEXT = builder
            .comment(
                "Show generated formulas for standalone dynamic attributes.",
                "This switch is subordinate to showFormulaText."
            )
            .translation("config.apothic_dynamic_preview.show_standalone_dynamic_formula_text")
            .define("showStandaloneDynamicFormulaText", true);

        SHOW_DYNAMIC_GROUP_FORMULA_TEXT = builder
            .comment(
                "Show generated formulas for merged groups containing at least one dynamic attribute.",
                "Groups containing only non-dynamic attributes always use native non-dynamic display.",
                "This switch is subordinate to showFormulaText."
            )
            .translation("config.apothic_dynamic_preview.show_dynamic_group_formula_text")
            .define("showDynamicGroupFormulaText", true);

        PERCENT_DECIMALS = builder
            .comment("Decimal places in the multiplier percentage. 0 displays whole percentages.")
            .translation("config.apothic_dynamic_preview.percent_decimals")
            .defineInRange("percentDecimals", 0, 0, 8);

        CONSTANT_DECIMALS = builder
            .comment(
                "Decimal places in an additive constant when ordinary decimal notation is used.",
                "Scientific notation has its own mantissa precision setting."
            )
            .translation("config.apothic_dynamic_preview.constant_decimals")
            .defineInRange("constantDecimals", 2, 0, 8);

        builder.comment(
            "Scientific notation for generated dynamic formula numbers.",
            "This formatter never touches native non-dynamic Apothic Attributes values."
        ).translation("config.apothic_dynamic_preview.section.scientific_notation").push("scientific_notation");

        ENABLE_SCIENTIFIC_NOTATION = builder
            .comment(
                "Master switch for scientific notation in generated dynamic formulas.",
                "Native non-dynamic values, including groups made only from non-dynamic attributes, are never reformatted."
            )
            .translation("config.apothic_dynamic_preview.scientific_notation_enabled")
            .define("scientificNotationEnabled", true);

        SCIENTIFIC_STANDALONE_DYNAMIC_FORMULAS = builder
            .comment("Allow scientific notation for standalone dynamic attribute formulas.")
            .translation("config.apothic_dynamic_preview.scientific_standalone_dynamic_formulas")
            .define("scientificStandaloneDynamicFormulas", true);

        SCIENTIFIC_DYNAMIC_GROUP_FORMULAS = builder
            .comment("Allow scientific notation for merged groups containing at least one dynamic attribute.")
            .translation("config.apothic_dynamic_preview.scientific_dynamic_group_formulas")
            .define("scientificDynamicGroupFormulas", true);

        SCIENTIFIC_PERCENTAGES = builder
            .comment("Allow scientific notation for the percentage/multiplier part of generated formulas.")
            .translation("config.apothic_dynamic_preview.scientific_percentages")
            .define("scientificPercentages", true);

        SCIENTIFIC_CONSTANTS = builder
            .comment("Allow scientific notation for the additive constant part of generated formulas.")
            .translation("config.apothic_dynamic_preview.scientific_constants")
            .define("scientificConstants", true);

        SCIENTIFIC_LARGE_NUMBERS = builder
            .comment("Use scientific notation for sufficiently large displayed numbers.")
            .translation("config.apothic_dynamic_preview.scientific_large_numbers")
            .define("scientificLargeNumbers", true);

        SCIENTIFIC_INTEGER_DIGITS_THRESHOLD = builder
            .comment(
                "Minimum number of digits before the decimal point that activates scientific notation.",
                "For example, 7 means that 1000000 is eligible while 999999 is not."
            )
            .translation("config.apothic_dynamic_preview.scientific_integer_digits_threshold")
            .defineInRange("scientificIntegerDigitsThreshold", 7, 1, 309);

        SCIENTIFIC_SMALL_NUMBERS = builder
            .comment(
                "Use scientific notation for sufficiently small non-zero displayed numbers.",
                "This prevents small constants from being rounded to zero by ordinary decimal formatting."
            )
            .translation("config.apothic_dynamic_preview.scientific_small_numbers")
            .define("scientificSmallNumbers", true);

        SCIENTIFIC_LEADING_FRACTION_ZEROS_THRESHOLD = builder
            .comment(
                "Minimum count of zeroes after the decimal point before the first non-zero digit.",
                "For example, 4 makes 0.00001 eligible because it has four leading fractional zeroes."
            )
            .translation("config.apothic_dynamic_preview.scientific_leading_fraction_zeros_threshold")
            .defineInRange("scientificLeadingFractionZerosThreshold", 4, 1, 323);

        SCIENTIFIC_MANTISSA_DECIMALS = builder
            .comment(
                "Digits after the decimal point in the scientific mantissa.",
                "Example with precision 3: 1.235E+8."
            )
            .translation("config.apothic_dynamic_preview.scientific_mantissa_decimals")
            .defineInRange("scientificMantissaDecimals", 3, 0, 15);

        SCIENTIFIC_TRIM_TRAILING_ZEROS = builder
            .comment(
                "Remove redundant trailing zeroes from the mantissa.",
                "Enabled: 1.2E+6; disabled with precision 3: 1.200E+6."
            )
            .translation("config.apothic_dynamic_preview.scientific_trim_trailing_zeros")
            .define("scientificTrimTrailingZeros", true);

        SCIENTIFIC_NOTATION_STYLE = builder
            .comment(
                "Scientific notation style.",
                "E_UPPER: 1.25E+6; E_LOWER: 1.25e+6; TIMES_TEN_CARET: 1.25×10^+6."
            )
            .translation("config.apothic_dynamic_preview.scientific_notation_style")
            .defineEnum("scientificNotationStyle", ScientificNumberFormatter.NotationStyle.E_UPPER);

        SCIENTIFIC_ALWAYS_SHOW_EXPONENT_SIGN = builder
            .comment("Show '+' before non-negative exponents. Negative exponents always keep '-'.")
            .translation("config.apothic_dynamic_preview.scientific_always_show_exponent_sign")
            .define("scientificAlwaysShowExponentSign", true);

        SCIENTIFIC_MINIMUM_EXPONENT_DIGITS = builder
            .comment(
                "Minimum width of the exponent, padded with leading zeroes.",
                "Example with width 2: E+06. Use 1 for no extra padding."
            )
            .translation("config.apothic_dynamic_preview.scientific_minimum_exponent_digits")
            .defineInRange("scientificMinimumExponentDigits", 1, 1, 6);

        builder.pop();

        IDENTITY_EPSILON = builder
            .comment(
                "Terms closer to the identity than this value are hidden.",
                "Increase slightly only when another mod produces visible floating-point noise."
            )
            .translation("config.apothic_dynamic_preview.identity_epsilon")
            .defineInRange("identityEpsilon", 1.0E-9D, 0.0D, 0.01D);

        SHOW_IDENTITY_AS_DASH = builder
            .comment("Display an em dash when a displayed formula is exactly the identity transformation.")
            .translation("config.apothic_dynamic_preview.show_identity_as_dash")
            .define("showIdentityAsDash", true);

        builder.pop();

        builder.comment(
            "Semantic color highlighting for displayed attribute values and generated formulas.",
            "These rules apply to merged groups, standalone dynamic attributes and the original values of all regular attributes."
        ).translation("config.apothic_dynamic_preview.section.highlighting").push("highlighting");

        ENABLE_HIGHLIGHTING = builder
            .comment(
                "Master switch for semantic color highlighting.",
                "When disabled, standalone regular values keep their original Apothic Attributes style,",
                "while generated dynamic/merged formulas use neutralColor."
            )
            .translation("config.apothic_dynamic_preview.highlighting_enabled")
            .define("enabled", true);

        HIGHLIGHT_NON_DYNAMIC_ATTRIBUTES = builder
            .comment(
                "Apply semantic recoloring to standalone non-dynamic attributes.",
                "Their native text and number formatting are never replaced."
            )
            .translation("config.apothic_dynamic_preview.highlight_non_dynamic_attributes")
            .define("highlightNonDynamicAttributes", true);

        HIGHLIGHT_DYNAMIC_ATTRIBUTES = builder
            .comment("Apply semantic highlighting to standalone dynamic attributes.")
            .translation("config.apothic_dynamic_preview.highlight_dynamic_attributes")
            .define("highlightDynamicAttributes", true);

        HIGHLIGHT_MERGED_GROUPS = builder
            .comment(
                "Apply semantic highlighting to merged groups.",
                "This is independent from the standalone dynamic/non-dynamic switches."
            )
            .translation("config.apothic_dynamic_preview.highlight_merged_groups")
            .define("highlightMergedGroups", true);

        APPLY_EXCEPTIONS_TO_NON_DYNAMIC_ATTRIBUTES = builder
            .comment("Use the exceptions list for standalone non-dynamic attributes.")
            .translation("config.apothic_dynamic_preview.apply_exceptions_to_non_dynamic_attributes")
            .define("applyExceptionsToNonDynamicAttributes", true);

        APPLY_EXCEPTIONS_TO_DYNAMIC_ATTRIBUTES = builder
            .comment("Use the exceptions list for standalone dynamic attributes.")
            .translation("config.apothic_dynamic_preview.apply_exceptions_to_dynamic_attributes")
            .define("applyExceptionsToDynamicAttributes", true);

        APPLY_EXCEPTIONS_TO_MERGED_GROUPS = builder
            .comment("Use the exceptions list for merged groups; any matching member excludes the group.")
            .translation("config.apothic_dynamic_preview.apply_exceptions_to_merged_groups")
            .define("applyExceptionsToMergedGroups", true);

        APPLY_INVERSION_TO_NON_DYNAMIC_ATTRIBUTES = builder
            .comment("Use inverted-rule patterns for standalone non-dynamic attributes.")
            .translation("config.apothic_dynamic_preview.apply_inversion_to_non_dynamic_attributes")
            .define("applyInversionToNonDynamicAttributes", true);

        APPLY_INVERSION_TO_DYNAMIC_ATTRIBUTES = builder
            .comment("Use inverted-rule patterns for standalone dynamic attributes.")
            .translation("config.apothic_dynamic_preview.apply_inversion_to_dynamic_attributes")
            .define("applyInversionToDynamicAttributes", true);

        APPLY_INVERSION_TO_MERGED_GROUPS = builder
            .comment("Use inverted-rule patterns for merged groups; any matching member inverts the group.")
            .translation("config.apothic_dynamic_preview.apply_inversion_to_merged_groups")
            .define("applyInversionToMergedGroups", true);

        APPLY_BASE_VALUES_TO_NON_DYNAMIC_ATTRIBUTES = builder
            .comment("Use attributeBaseValues overrides when coloring standalone non-dynamic attributes in STANDARD mode.")
            .translation("config.apothic_dynamic_preview.apply_base_values_to_non_dynamic_attributes")
            .define("applyBaseValuesToNonDynamicAttributes", true);

        APPLY_BASE_VALUES_TO_DYNAMIC_ATTRIBUTES = builder
            .comment("Use attributeBaseValues overrides when coloring standalone dynamic attributes in STANDARD mode.")
            .translation("config.apothic_dynamic_preview.apply_base_values_to_dynamic_attributes")
            .define("applyBaseValuesToDynamicAttributes", true);

        APPLY_BASE_VALUES_TO_MERGED_GROUPS = builder
            .comment("Use attributeBaseValues overrides when coloring merged groups in STANDARD mode.")
            .translation("config.apothic_dynamic_preview.apply_base_values_to_merged_groups")
            .define("applyBaseValuesToMergedGroups", true);

        HIGHLIGHT_LAYOUT = builder
            .comment(
                "SEPARATE: percentage and constant are colored independently.",
                "COMMON: both parts use one color selected by commonHighlightMode."
            )
            .translation("config.apothic_dynamic_preview.highlight_layout")
            .defineEnum("layout", HighlightLayout.SEPARATE);

        COMMON_HIGHLIGHT_MODE = builder
            .comment(
                "Used only when layout = COMMON.",
                "STANDARD: substitute a configured base value and compare final output with that base.",
                "PRIORITY: multiplier color wins unless it is neutral, then constant color is used.",
                "LAYERED: equal colors remain; opposite positive/negative colors become neutral; neutral yields to the other part."
            )
            .translation("config.apothic_dynamic_preview.common_highlight_mode")
            .defineEnum("commonHighlightMode", CommonHighlightMode.STANDARD);

        HIGHLIGHT_EXCEPTIONS = builder
            .comment(
                "Attribute IDs or glob patterns that must never receive semantic highlighting.",
                "A matching standalone regular value keeps its complete original style.",
                "A matching generated dynamic/merged formula uses neutralColor.",
                "Examples: minecraft:generic.luck, apothic_attributes:*"
            )
            .translation("config.apothic_dynamic_preview.highlight_exceptions")
            .defineListAllowEmpty("exceptions", List.of(), ClientConfig::isValidAttributePattern);

        INVERTED_RULE_ATTRIBUTES = builder
            .comment(
                "Attribute IDs or glob patterns whose positive/negative highlighting rule is inverted.",
                "Example: minecraft:generic.gravity",
                "For merged groups, matching any member inverts the rule for the whole combined formula."
            )
            .translation("config.apothic_dynamic_preview.inverted_rule_attributes")
            .defineListAllowEmpty("invertedRuleAttributes", List.of(), ClientConfig::isValidAttributePattern);

        ATTRIBUTE_BASE_VALUES = builder
            .comment(
                "Per-attribute base values used by COMMON + STANDARD.",
                "Format: namespace:path=value",
                "For merged groups, the first matching member in composed order is used.",
                "Examples: minecraft:generic.movement_speed=0.1, puffish_attributes:sprinting_speed=1.0"
            )
            .translation("config.apothic_dynamic_preview.attribute_base_values")
            .defineListAllowEmpty("attributeBaseValues", List.of(), ClientConfig::isValidBaseOverride);

        EVALUATION_BASE_FALLBACK = builder
            .comment(
                "Base source used by COMMON + STANDARD when no attributeBaseValues override matches.",
                "REGISTERED_DEFAULT is recommended."
            )
            .translation("config.apothic_dynamic_preview.evaluation_base_fallback")
            .defineEnum("evaluationBaseFallback", EvaluationBaseFallback.REGISTERED_DEFAULT);

        POSITIVE_COLOR = builder
            .comment("Display color for a beneficial/positive term.")
            .translation("config.apothic_dynamic_preview.positive_color")
            .defineEnum("positiveColor", TextColor.GREEN);

        NEGATIVE_COLOR = builder
            .comment("Display color for a harmful/negative term.")
            .translation("config.apothic_dynamic_preview.negative_color")
            .defineEnum("negativeColor", TextColor.RED);

        NEUTRAL_COLOR = builder
            .comment("Display color for generated formula parts that are neutral, disabled, excluded or conflicting.")
            .translation("config.apothic_dynamic_preview.neutral_color")
            .defineEnum("neutralColor", TextColor.WHITE);

        builder.pop();
        SPEC = builder.build();
    }

    public static ScientificNumberFormatter.Options scientificNumberOptions() {
        return new ScientificNumberFormatter.Options(
            ENABLE_SCIENTIFIC_NOTATION.get(),
            SCIENTIFIC_LARGE_NUMBERS.get(),
            SCIENTIFIC_INTEGER_DIGITS_THRESHOLD.get(),
            SCIENTIFIC_SMALL_NUMBERS.get(),
            SCIENTIFIC_LEADING_FRACTION_ZEROS_THRESHOLD.get(),
            SCIENTIFIC_MANTISSA_DECIMALS.get(),
            SCIENTIFIC_TRIM_TRAILING_ZEROS.get(),
            SCIENTIFIC_NOTATION_STYLE.get(),
            SCIENTIFIC_ALWAYS_SHOW_EXPONENT_SIGN.get(),
            SCIENTIFIC_MINIMUM_EXPONENT_DIGITS.get()
        );
    }

    private ClientConfig() {
    }

    public static List<List<ResourceLocation>> getMergeGroups() {
        List<List<ResourceLocation>> groups = new ArrayList<>();
        for (String encoded : DISPLAY_MERGE_GROUPS.get()) {
            List<ResourceLocation> ids = parseGroup(encoded);
            if (ids.size() >= 2) groups.add(ids);
        }
        return groups;
    }

    public static boolean matchesHighlightException(Iterable<ResourceLocation> ids) {
        return matchesAnyPattern(HIGHLIGHT_EXCEPTIONS.get(), ids);
    }

    public static boolean usesInvertedRule(Iterable<ResourceLocation> ids) {
        return matchesAnyPattern(INVERTED_RULE_ATTRIBUTES.get(), ids);
    }

    public static Double findConfiguredBaseValue(Iterable<ResourceLocation> ids) {
        Map<ResourceLocation, Double> overrides = parseBaseOverrides();
        for (ResourceLocation id : ids) {
            Double value = overrides.get(id);
            if (value != null) return value;
        }
        return null;
    }

    private static Map<ResourceLocation, Double> parseBaseOverrides() {
        java.util.LinkedHashMap<ResourceLocation, Double> overrides = new java.util.LinkedHashMap<>();
        for (String encoded : ATTRIBUTE_BASE_VALUES.get()) {
            int separator = encoded.lastIndexOf('=');
            if (separator <= 0 || separator >= encoded.length() - 1) continue;
            ResourceLocation id = ResourceLocation.tryParse(encoded.substring(0, separator).trim());
            if (id == null) continue;
            try {
                double value = Double.parseDouble(encoded.substring(separator + 1).trim());
                if (Double.isFinite(value)) overrides.putIfAbsent(normalizeLegacyId(id), value);
            }
            catch (NumberFormatException ignored) {
            }
        }
        return overrides;
    }

    private static boolean matchesAnyPattern(List<? extends String> patterns, Iterable<ResourceLocation> ids) {
        for (ResourceLocation id : ids) {
            String value = id.toString();
            for (String pattern : patterns) {
                if (globMatches(pattern.trim().toLowerCase(Locale.ROOT), value)) return true;
            }
        }
        return false;
    }

    private static boolean globMatches(String pattern, String value) {
        int patternIndex = 0;
        int valueIndex = 0;
        int starIndex = -1;
        int starValueIndex = -1;

        while (valueIndex < value.length()) {
            if (patternIndex < pattern.length()
                && (pattern.charAt(patternIndex) == '?' || pattern.charAt(patternIndex) == value.charAt(valueIndex))) {
                patternIndex++;
                valueIndex++;
            }
            else if (patternIndex < pattern.length() && pattern.charAt(patternIndex) == '*') {
                starIndex = patternIndex++;
                starValueIndex = valueIndex;
            }
            else if (starIndex != -1) {
                patternIndex = starIndex + 1;
                valueIndex = ++starValueIndex;
            }
            else {
                return false;
            }
        }

        while (patternIndex < pattern.length() && pattern.charAt(patternIndex) == '*') patternIndex++;
        return patternIndex == pattern.length();
    }

    private static boolean isValidGroup(Object value) {
        return value instanceof String encoded && parseGroup(encoded).size() >= 2;
    }

    private static List<ResourceLocation> parseGroup(String encoded) {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (String part : encoded.split("\\|")) {
            ResourceLocation id = ResourceLocation.tryParse(part.trim());
            if (id == null) return List.of();
            id = normalizeLegacyId(id);
            if (!ids.add(id)) return List.of();
        }
        return List.copyOf(ids);
    }

    private static ResourceLocation normalizeLegacyId(ResourceLocation id) {
        return LEGACY_ATTRIBUTE_IDS.getOrDefault(id, id);
    }

    private static boolean isValidAttributePattern(Object value) {
        if (!(value instanceof String pattern)) return false;
        pattern = pattern.trim().toLowerCase(Locale.ROOT);
        int separator = pattern.indexOf(':');
        if (separator <= 0 || separator >= pattern.length() - 1) return false;
        return pattern.matches("[a-z0-9_.?*-]+:[a-z0-9_./?*-]+");
    }

    private static boolean isValidBaseOverride(Object value) {
        if (!(value instanceof String encoded)) return false;
        int separator = encoded.lastIndexOf('=');
        if (separator <= 0 || separator >= encoded.length() - 1) return false;
        ResourceLocation id = ResourceLocation.tryParse(encoded.substring(0, separator).trim());
        if (id == null) return false;
        try {
            return Double.isFinite(Double.parseDouble(encoded.substring(separator + 1).trim()));
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }
}
