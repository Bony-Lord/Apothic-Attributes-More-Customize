# Apothic Attributes: More Customize

A client-side NeoForge addon that gives players and modpack authors detailed control over how Apothic Attributes values are combined, formatted, and colored.

## Main features

- Displays standalone dynamic attributes as `125% + 3`, `+ 3`, or `125%` instead of an unknown-value symbol.
- Keeps standalone non-dynamic attributes in the exact native Apothic Attributes format; only their color may be changed.
- Keeps a group made only from non-dynamic attributes in native non-dynamic display as well.
- Automatically reduces each Minecraft attribute operation pipeline to `f(x) = kx + c` and composes merged formulas in the configured order.
- Uses normal-before-dynamic ordering by default while preserving config order inside each category.
- Supports independent percentage/constant colors and common `STANDARD`, `PRIORITY`, and `LAYERED` modes.
- Separately controls highlighting, exception rules, inverted rules, and custom evaluation bases for:
  - standalone non-dynamic attributes;
  - standalone dynamic attributes;
  - merged groups.
- Separately controls formula display for standalone dynamic attributes and groups containing dynamic attributes.
- Supports fully configurable scientific notation for generated percentages and constants, including large/small thresholds, mantissa precision, style, exponent sign, and padding.
- Provides a native NeoForge configuration screen through **Mods → Apothic Attributes: More Customize → Config**.
- Detects missing configured attributes and can suggest the closest available registry ID.
- Fully client-side: no server installation and no gameplay changes.

## Display rules

### Standalone non-dynamic attributes

The addon never rebuilds the value component created by Apothic Attributes. Number format, percentage signs, spacing, translations, units, and other text structure remain native. Semantic highlighting may replace only the color when the corresponding target switch is enabled.

### Groups containing only non-dynamic attributes

The group is still merged into one entry, but it is rendered as an ordinary non-dynamic value. The synthetic entry stores the mathematically composed final value, and Apothic Attributes performs the visible number/unit formatting.

### Standalone dynamic attributes and groups containing a dynamic member

These entries may use an affine preview:

```text
125% + 3
+ 3
125%
```

A dynamic attribute always transforms a symbolic input. Its stored service base is not inserted as an extra constant.

### Scientific notation

Scientific notation is applied only to generated dynamic formulas. Native non-dynamic text is never rebuilt. Large values can switch by the number of integer digits, while tiny values can switch by the number of leading zeroes after the decimal point. Examples with the defaults: `123456789 → 1.235E+8` and `0.00001 → 1E-5`.

## Automatic formula calculation

Minecraft operations are reduced to:

```text
value = base + Σ(ADD_VALUE)
value += base × Σ(ADD_MULTIPLIED_BASE)
value *= Π(1 + ADD_MULTIPLIED_TOTAL)
```

which becomes:

```text
f(x) = kx + c
```

Merged attributes are composed by substitution:

```text
f_merged(x) = f_last(...f_second(f_first(x)))
```

## Configuration

Open the in-game screen:

```text
Mods → Apothic Attributes: More Customize → Config
```

The same settings are stored in:

```text
config/apothic_dynamic_preview-client.toml
```

The technical mod ID and config filename intentionally remain `apothic_dynamic_preview` so settings from earlier versions are preserved.

Default example:

```toml
[attribute_display_merging]
enabled = true
groups = ["puffish_attributes:sprinting_speed|artifacts:generic.sprinting_speed"]
mergeOrder = "NORMAL_BEFORE_DYNAMIC"
includeChangedBaseValues = true

[formula_display]
showFormulaText = true
showStandaloneDynamicFormulaText = true
showDynamicGroupFormulaText = true
percentDecimals = 0
constantDecimals = 2

[formula_display.scientific_notation]
scientificNotationEnabled = true
scientificStandaloneDynamicFormulas = true
scientificDynamicGroupFormulas = true
scientificPercentages = true
scientificConstants = true
scientificIntegerDigitsThreshold = 7
scientificMantissaDecimals = 3
scientificNotationStyle = "E_UPPER"

[highlighting]
enabled = true
highlightNonDynamicAttributes = true
highlightDynamicAttributes = true
highlightMergedGroups = true
applyExceptionsToNonDynamicAttributes = true
applyExceptionsToDynamicAttributes = true
applyExceptionsToMergedGroups = true
applyInversionToNonDynamicAttributes = true
applyInversionToDynamicAttributes = true
applyInversionToMergedGroups = true
applyBaseValuesToNonDynamicAttributes = true
applyBaseValuesToDynamicAttributes = true
applyBaseValuesToMergedGroups = true
layout = "SEPARATE"
commonHighlightMode = "STANDARD"
```

See [docs/CONFIGURATION.md](docs/CONFIGURATION.md) or [docs/CONFIGURATION_RU.md](docs/CONFIGURATION_RU.md) for every option. Ready-to-publish Modrinth descriptions are available in [MODRINTH.md](MODRINTH.md) and [MODRINTH_RU.md](MODRINTH_RU.md).

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.60 or newer in the 21.1 line
- Apothic Attributes 2.4.0–2.9.x
- Placebo required by Apothic Attributes
- Java 21

## Building

### Windows

Run `build.bat`. It verifies Java 21 and downloads a local Gradle 8.10.2 distribution when needed. Output is placed in `build/libs`.

### Linux/macOS

```bash
./gradlew build
```

## Installation

Place the produced JAR in the client's `mods` directory together with Apothic Attributes and its dependencies. The server does not need this addon.

## License

MIT. See [LICENSE](LICENSE).
