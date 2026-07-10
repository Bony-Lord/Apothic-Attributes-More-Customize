package dev.apothicdynamicpreview;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Formats generated formula numbers without changing native Apothic Attributes text.
 * Scientific notation is selected from the unrounded value so very small terms are
 * not lost to ordinary decimal-place rounding before they can be displayed.
 */
public final class ScientificNumberFormatter {
    public enum NotationStyle {
        /** Example: 1.25E+6 */
        E_UPPER,
        /** Example: 1.25e+6 */
        E_LOWER,
        /** Example: 1.25×10^6 */
        TIMES_TEN_CARET
    }

    public record Options(
        boolean enabled,
        boolean largeNumbers,
        int integerDigitsThreshold,
        boolean smallNumbers,
        int leadingFractionZerosThreshold,
        int mantissaDecimals,
        boolean trimTrailingZeros,
        NotationStyle style,
        boolean alwaysShowExponentSign,
        int minimumExponentDigits
    ) {
        public Options {
            integerDigitsThreshold = Math.max(1, integerDigitsThreshold);
            leadingFractionZerosThreshold = Math.max(1, leadingFractionZerosThreshold);
            mantissaDecimals = Math.max(0, mantissaDecimals);
            minimumExponentDigits = Math.max(1, minimumExponentDigits);
            style = style == null ? NotationStyle.E_UPPER : style;
        }
    }

    private ScientificNumberFormatter() {
    }

    public static boolean shouldUseScientific(double value, boolean allowedForPart, Options options) {
        if (!allowedForPart || !options.enabled() || !Double.isFinite(value) || value == 0.0D) {
            return false;
        }

        BigDecimal decimal = BigDecimal.valueOf(Math.abs(value)).stripTrailingZeros();
        int exponent = decimal.precision() - decimal.scale() - 1;

        if (exponent >= 0) {
            int integerDigits = exponent + 1;
            return options.largeNumbers() && integerDigits >= options.integerDigitsThreshold();
        }

        int leadingFractionZeros = -exponent - 1;
        return options.smallNumbers()
            && leadingFractionZeros >= options.leadingFractionZerosThreshold();
    }

    public static String format(
        double value,
        int plainDecimals,
        boolean allowedForPart,
        Options options
    ) {
        if (!Double.isFinite(value)) {
            return Double.toString(value);
        }

        if (shouldUseScientific(value, allowedForPart, options)) {
            return formatScientific(value, options);
        }

        return formatPlain(roundPlain(value, plainDecimals));
    }

    public static double roundPlain(double value, int scale) {
        if (!Double.isFinite(value)) {
            return value;
        }

        double rounded = BigDecimal.valueOf(value)
            .setScale(Math.max(0, scale), RoundingMode.HALF_UP)
            .doubleValue();
        return rounded == -0.0D ? 0.0D : rounded;
    }

    private static String formatPlain(double value) {
        if (!Double.isFinite(value)) {
            return Double.toString(value);
        }

        return BigDecimal.valueOf(value)
            .stripTrailingZeros()
            .toPlainString();
    }

    private static String formatScientific(double value, Options options) {
        BigDecimal decimal = BigDecimal.valueOf(value);
        int exponent = decimal.precision() - decimal.scale() - 1;
        BigDecimal mantissa = decimal.movePointLeft(exponent)
            .setScale(options.mantissaDecimals(), RoundingMode.HALF_UP);

        // Rounding 9.999... may produce 10.000, which requires exponent normalization.
        if (mantissa.abs().compareTo(BigDecimal.TEN) >= 0) {
            mantissa = mantissa.movePointLeft(1)
                .setScale(options.mantissaDecimals(), RoundingMode.HALF_UP);
            exponent++;
        }

        String mantissaText = options.trimTrailingZeros()
            ? mantissa.stripTrailingZeros().toPlainString()
            : mantissa.toPlainString();
        String exponentText = formatExponent(exponent, options);

        return switch (options.style()) {
            case E_UPPER -> mantissaText + "E" + exponentText;
            case E_LOWER -> mantissaText + "e" + exponentText;
            case TIMES_TEN_CARET -> mantissaText + "×10^" + exponentText;
        };
    }

    private static String formatExponent(int exponent, Options options) {
        String digits = Integer.toString(Math.abs(exponent));
        if (digits.length() < options.minimumExponentDigits()) {
            digits = "0".repeat(options.minimumExponentDigits() - digits.length()) + digits;
        }

        if (exponent < 0) return "-" + digits;
        return options.alwaysShowExponentSign() ? "+" + digits : digits;
    }
}
