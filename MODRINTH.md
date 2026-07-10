# Apothic Attributes: More Customize

**More control over how Apothic Attributes values are calculated, combined, formatted, and colored.**

Apothic Attributes: More Customize is a client-side NeoForge addon for players and modpack authors who want clearer and more configurable attribute displays. It replaces unreadable dynamic placeholders with mathematically accurate formulas, can merge equivalent attributes from different mods, preserves the native appearance of ordinary attributes, and adds detailed semantic highlighting and scientific notation controls.

## Features

- Displays dynamic values as readable formulas such as `125% + 3`, `+ 3`, or `125%`.
- Correctly composes merged attribute operations as ordered affine formulas instead of simply summing modifiers.
- Uses normal attributes before dynamic attributes by default, while preserving the configured order inside each category.
- Keeps standalone non-dynamic attributes in the original Apothic Attributes format and changes only their color when enabled.
- Keeps groups containing only non-dynamic attributes in native non-dynamic display instead of converting them to dynamic formulas.
- Provides separate switches for standalone regular attributes, standalone dynamic attributes, and merged groups.
- Separately controls highlighting, exceptions, inverted rules, and custom evaluation bases for every target category.
- Supports independent percentage and constant colors in `SEPARATE` mode.
- Supports `STANDARD`, `PRIORITY`, and `LAYERED` common-color modes.
- Supports configurable scientific notation for percentages and constants:
  - large-number digit threshold;
  - small-number leading-zero threshold;
  - mantissa precision;
  - trailing-zero removal;
  - `E`, `e`, or `×10^` style;
  - exponent sign and padding.
- Warns about missing configured attribute IDs and can suggest the closest available ID.
- Includes a native in-game configuration screen under **Mods → Apothic Attributes: More Customize → Config**.
- Client-side only. It does not change real attribute values and is not required on the server.

## Display behavior

Ordinary attributes always retain the number format, symbols, spacing, translations, and units produced by Apothic Attributes. This addon may recolor the existing component, but it does not rebuild it.

Dynamic attributes and groups containing at least one dynamic attribute may use generated formulas. A pure constant is displayed correctly as `+ 3`, while multiplier-only and mixed formulas remain `125%` and `125% + 3`.

## Compatibility

- Minecraft 1.21.1
- NeoForge 21.1
- Apothic Attributes 2.4.0–2.9.x
- Java 21

## Configuration

Open **Mods → Apothic Attributes: More Customize → Config**, or edit:

```text
config/apothic_dynamic_preview-client.toml
```

The technical mod ID and config filename remain `apothic_dynamic_preview` so existing settings continue to work after updating from earlier builds.
