package dev.apothicdynamicpreview;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

/**
 * An affine attribute transformation: {@code output = input * multiplier + constant}.
 */
public record AttributeFormula(double multiplier, double constant) {
    public static final AttributeFormula IDENTITY = new AttributeFormula(1.0D, 0.0D);

    /**
     * Builds the exact affine form of Minecraft's attribute operation pipeline:
     * <pre>
     * value = base + sum(ADD_VALUE)
     * value += base * sum(ADD_MULTIPLIED_BASE)
     * value *= product(1 + ADD_MULTIPLIED_TOTAL)
     * </pre>
     * The attribute's registered default value is treated as the input. A changed
     * instance base is represented as a constant offset when requested. Callers must
     * disable that offset for Apothic dynamic-base attributes, whose stored base is
     * only a service value and whose actual input is symbolic.
     */
    public static AttributeFormula from(AttributeInstance instance, boolean includeBaseDifference) {
        double add = instance.getModifiers().stream()
            .filter(modifier -> modifier.operation() == Operation.ADD_VALUE)
            .mapToDouble(AttributeModifier::amount)
            .sum();

        double multipliedBase = instance.getModifiers().stream()
            .filter(modifier -> modifier.operation() == Operation.ADD_MULTIPLIED_BASE)
            .mapToDouble(AttributeModifier::amount)
            .sum();

        double multipliedTotal = instance.getModifiers().stream()
            .filter(modifier -> modifier.operation() == Operation.ADD_MULTIPLIED_TOTAL)
            .mapToDouble(modifier -> 1.0D + modifier.amount())
            .reduce(1.0D, (left, right) -> left * right);

        double baseDifference = includeBaseDifference
            ? instance.getBaseValue() - instance.getAttribute().value().getDefaultValue()
            : 0.0D;

        // ADD_MULTIPLIED_BASE is based on the input/base value, not on base + additions.
        double multiplier = (1.0D + multipliedBase) * multipliedTotal;
        double constant = (baseDifference + add) * multipliedTotal;
        return new AttributeFormula(multiplier, constant);
    }

    /** Evaluates this transformation for the supplied input. */
    public double apply(double input) {
        return input * this.multiplier + this.constant;
    }

    /** Returns {@code after(this(input))}. */
    public AttributeFormula then(AttributeFormula after) {
        return new AttributeFormula(
            after.multiplier * this.multiplier,
            after.multiplier * this.constant + after.constant
        );
    }

    public boolean isIdentity(double epsilon) {
        return Math.abs(this.multiplier - 1.0D) <= epsilon && Math.abs(this.constant) <= epsilon;
    }
}
