# Changelog

## 1.6.1

- Fixed dynamic percentage previews for every attribute in the Apothic dynamic-base tag, instead of special-casing individual attribute IDs.
- Dynamic `ADD_MULTIPLIED_TOTAL` modifiers now compose additively in the client preview, so `+100%`, `-100%`, `+100%` returns to `+100%` instead of remaining at zero.
- Dynamic formula percentages now display the modifier delta: a `+100%` modifier is shown as `100%`, not `200%`.
- Corrected the aggregated modifier value in the Apothic Attributes hover tooltip while preserving its full multiplier for formula evaluation.
- Kept vanilla multiplicative composition unchanged for ordinary non-dynamic attributes.

## 1.6.0

- Renamed the public project and display name to **Apothic Attributes: More Customize**.
- Kept the technical mod ID and config namespace unchanged for upgrade compatibility.
- Rewrote the GitHub and Modrinth descriptions and added a ready-to-publish Russian Modrinth description.

- Added an independent master switch for configured attribute groups.
- Added separate formula switches for standalone dynamic attributes and groups containing dynamic members.
- Added independent highlighting switches for standalone non-dynamic attributes, standalone dynamic attributes, and merged groups.
- Added per-target switches controlling whether exception patterns, inverted rules, and custom evaluation bases apply.
- Changed regular-only merged groups to native non-dynamic display: Apothic Attributes formats one final ordinary value instead of the addon generating a percentage/constant formula.
- Kept generated formulas limited to standalone dynamic entries and groups containing at least one dynamic member.
- Updated Russian/English config labels, examples, README, and configuration documentation.
- Added fully configurable scientific notation for generated formula percentages and constants.
- Added independent percentage/constant switches, large-number digit threshold, small-number leading-zero threshold, mantissa precision, trailing-zero trimming, notation style, exponent sign, and exponent width.
- Scientific small-number detection happens before ordinary rounding, preventing visible non-zero constants from being rounded away.
- Scientific formatting never changes standalone non-dynamic values or groups made only from non-dynamic attributes.

## 1.5.0

- Restored the original Apothic Attributes text representation for every standalone non-dynamic attribute.
- Non-dynamic entries are now modified only by semantic color rules; their number format, symbols, spacing, and translations are preserved.
- Fixed constant-only dynamic formulas such as `+ 3` by treating the dynamic base as a symbolic input instead of an additive stored-base offset.
- Kept formula replacement limited to standalone dynamic attributes and configured merged entries.
- Added a native NeoForge configuration screen available from `Mods → Apothic Attributes: More Customize → Config`.
- Added localized names for every config section and value in English and Russian.
- Removed the obsolete `formulaScope` option.
- Updated examples and documentation to match the actual display and highlighting behavior.

## 1.4.0

- Added configurable semantic highlighting for regular, dynamic, and merged attributes.
- Added `SEPARATE` percentage/constant coloring.
- Added common `STANDARD`, `PRIORITY`, and `LAYERED` modes.
- Added highlight exceptions, inverted-rule patterns, dynamic highlighting toggle, and per-attribute base overrides.
- Added configurable positive, negative, and neutral colors.

## 1.3.0

- Replaced pooled-modifier merging with automatic affine formula composition.
- Corrected `ADD_MULTIPLIED_BASE`: it now multiplies the input/base, not additions.
- Added default normal-before-dynamic ordering with stable config order inside each category.
- Added `CONFIG_ORDER` and `DYNAMIC_BEFORE_NORMAL` modes.
- Added configurable percentage and constant precision.
- Added configurable identity epsilon and base-value handling.
- Added optional closest-ID suggestions and merge diagnostics.
- Rounded percentage display to whole numbers by default.
- Expanded GitHub and Modrinth documentation.

## 1.2.1

- Added display merge groups and missing-attribute warnings.
