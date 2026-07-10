package dev.apothicdynamicpreview;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.Attribute;

/** Pure color-selection rules for affine formula parts. */
public final class FormulaHighlighter {
    public enum Tone {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }

    private FormulaHighlighter() {
    }

    public static Tone multiplierTone(Attribute attribute, AttributeFormula formula, boolean inverted, double epsilon) {
        double difference = formula.multiplier() - 1.0D;
        if (Math.abs(difference) <= epsilon) return Tone.NEUTRAL;
        return toneForDirection(attribute, difference > 0.0D, inverted);
    }

    public static Tone constantTone(Attribute attribute, AttributeFormula formula, boolean inverted, double epsilon) {
        if (Math.abs(formula.constant()) <= epsilon) return Tone.NEUTRAL;
        return toneForDirection(attribute, formula.constant() > 0.0D, inverted);
    }

    public static Tone standardTone(
        Attribute attribute,
        AttributeFormula formula,
        double evaluationBase,
        boolean inverted,
        double epsilon
    ) {
        double difference = formula.apply(evaluationBase) - evaluationBase;
        if (!Double.isFinite(difference) || Math.abs(difference) <= epsilon) return Tone.NEUTRAL;
        return toneForDirection(attribute, difference > 0.0D, inverted);
    }

    public static Tone priorityTone(Tone multiplier, Tone constant) {
        return multiplier != Tone.NEUTRAL ? multiplier : constant;
    }

    public static Tone layeredTone(Tone multiplier, Tone constant) {
        if (multiplier == constant) return multiplier;
        if (multiplier == Tone.NEUTRAL) return constant;
        if (constant == Tone.NEUTRAL) return multiplier;
        return Tone.NEUTRAL;
    }

    public static ChatFormatting formatting(Tone tone) {
        return switch (tone) {
            case POSITIVE -> ClientConfig.POSITIVE_COLOR.get().formatting();
            case NEGATIVE -> ClientConfig.NEGATIVE_COLOR.get().formatting();
            case NEUTRAL -> ClientConfig.NEUTRAL_COLOR.get().formatting();
        };
    }

    private static Tone toneForDirection(Attribute attribute, boolean increase, boolean inverted) {
        ChatFormatting style = attribute.getStyle(inverted ? !increase : increase);
        return switch (style) {
            case BLUE, GREEN, DARK_GREEN, AQUA, DARK_AQUA, YELLOW, GOLD -> Tone.POSITIVE;
            case RED, DARK_RED -> Tone.NEGATIVE;
            default -> Tone.NEUTRAL;
        };
    }
}
