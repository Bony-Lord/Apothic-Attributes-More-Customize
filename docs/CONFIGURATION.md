# Apothic Attributes: More Customize 1.6.0 configuration

Open the native NeoForge screen through:

```text
Mods → Apothic Attributes: More Customize → Config
```

Config file:

```text
config/apothic_dynamic_preview-client.toml
```

All settings are client-side display settings.

## Entry categories

The addon treats three targets independently:

1. standalone non-dynamic attributes;
2. standalone dynamic attributes;
3. merged groups.

A group switch never inherits the standalone dynamic or non-dynamic switch.

### Native non-dynamic display

Standalone non-dynamic entries keep the exact component produced by Apothic Attributes. A merged group containing only non-dynamic members also uses native non-dynamic display: one final numeric value formatted by Apothic Attributes, never a generated `percentage + constant` string.

### Formula display

Generated formulas are limited to standalone dynamic attributes and groups containing at least one dynamic member.

## Full example

```toml
[attribute_display_merging]
enabled = true
groups = ["puffish_attributes:sprinting_speed|artifacts:generic.sprinting_speed"]
mergeOrder = "NORMAL_BEFORE_DYNAMIC"
includeChangedBaseValues = true
notifyMissingAttributes = true
suggestClosestAttribute = true
logResolvedGroups = true

[formula_display]
showFormulaText = true
showStandaloneDynamicFormulaText = true
showDynamicGroupFormulaText = true
percentDecimals = 0
constantDecimals = 2
identityEpsilon = 1.0E-9
showIdentityAsDash = true

[formula_display.scientific_notation]
scientificNotationEnabled = true
scientificStandaloneDynamicFormulas = true
scientificDynamicGroupFormulas = true
scientificPercentages = true
scientificConstants = true
scientificLargeNumbers = true
scientificIntegerDigitsThreshold = 7
scientificSmallNumbers = true
scientificLeadingFractionZerosThreshold = 4
scientificMantissaDecimals = 3
scientificTrimTrailingZeros = true
scientificNotationStyle = "E_UPPER"
scientificAlwaysShowExponentSign = true
scientificMinimumExponentDigits = 1

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
exceptions = []
invertedRuleAttributes = []
attributeBaseValues = []
evaluationBaseFallback = "REGISTERED_DEFAULT"
positiveColor = "GREEN"
negativeColor = "RED"
neutralColor = "WHITE"
```

## Group controls

- `attribute_display_merging.enabled` — enables all configured groups.
- `groups` — `|`-separated registry IDs.
- `mergeOrder` — `NORMAL_BEFORE_DYNAMIC`, `CONFIG_ORDER`, or `DYNAMIC_BEFORE_NORMAL`.
- `includeChangedBaseValues` — includes changed regular bases in composed formulas.
- diagnostics: `notifyMissingAttributes`, `suggestClosestAttribute`, `logResolvedGroups`.

## Formula controls

- `showFormulaText` — master generated-formula switch.
- `showStandaloneDynamicFormulaText` — standalone dynamic entries only.
- `showDynamicGroupFormulaText` — groups containing a dynamic member only.
- regular-only groups always retain native non-dynamic display.
- `percentDecimals` and `constantDecimals` control ordinary decimal formatting; `identityEpsilon` and `showIdentityAsDash` control hidden identity terms.

### Scientific notation

`[formula_display.scientific_notation]` affects generated formulas only. Native non-dynamic values and regular-only merged groups are never reformatted.

- `scientificNotationEnabled` — master switch.
- `scientificStandaloneDynamicFormulas` / `scientificDynamicGroupFormulas` — independent target switches for standalone dynamics and dynamic groups.
- `scientificPercentages` / `scientificConstants` — independent target switches.
- `scientificLargeNumbers` and `scientificIntegerDigitsThreshold` — enable notation from a configured count of integer digits (`7` converts `1000000`, but not `999999`).
- `scientificSmallNumbers` and `scientificLeadingFractionZerosThreshold` — enable notation for tiny values (`4` converts `0.00001`).
- `scientificMantissaDecimals` — digits after the mantissa decimal point.
- `scientificTrimTrailingZeros` — turns `1.200E+6` into `1.2E+6`.
- `scientificNotationStyle` — `E_UPPER`, `E_LOWER`, or `TIMES_TEN_CARET`.
- `scientificAlwaysShowExponentSign` and `scientificMinimumExponentDigits` control exponent text.

Small values are tested before ordinary rounding, so a scientific constant can remain visible instead of becoming zero.

## Highlight target switches

- `highlightNonDynamicAttributes` — standalone non-dynamic entries.
- `highlightDynamicAttributes` — standalone dynamic entries.
- `highlightMergedGroups` — all merged groups.

## Exception target switches

- `applyExceptionsToNonDynamicAttributes`;
- `applyExceptionsToDynamicAttributes`;
- `applyExceptionsToMergedGroups`.

When disabled for a target, the target ignores the `exceptions` list.

## Inversion target switches

- `applyInversionToNonDynamicAttributes`;
- `applyInversionToDynamicAttributes`;
- `applyInversionToMergedGroups`.

When disabled for a target, `invertedRuleAttributes` does not affect it.

## Base override target switches

- `applyBaseValuesToNonDynamicAttributes`;
- `applyBaseValuesToDynamicAttributes`;
- `applyBaseValuesToMergedGroups`.

When disabled for a target, `attributeBaseValues` is skipped and `evaluationBaseFallback` is used.

## Color modes

- `SEPARATE` — percentage and constant receive independent colors.
- `COMMON` — both use one color selected by:
  - `STANDARD`: evaluates the formula at the selected base;
  - `PRIORITY`: multiplier color first, constant only when neutral;
  - `LAYERED`: equal tones remain, opposite tones become neutral.

Non-dynamic native display has one visible value, so it receives one common semantic color.

## Pattern lists

`exceptions` and `invertedRuleAttributes` accept exact IDs and `*`/`?` glob patterns. `attributeBaseValues` uses `namespace:path=value` entries.

## Fallback bases

- `REGISTERED_DEFAULT`;
- `CURRENT_BASE`;
- `ZERO`;
- `ONE`.
