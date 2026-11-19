# Icon

Unofficial / draft. Import `io.oxygenx.ui.basic.Icon`.

## Usage

```kotlin
Icon(OxygenIcons.Check, contentDescription = "Done")
Icon(MaterialSymbols.LibraryBooks, contentDescription = "Bookshelf")
Icon(imageVector = multicolorVector, contentDescription = null, tint = Color.Unspecified)
Icon(painter = painter, contentDescription = "Cover", tint = Color.Unspecified)
Icon(bitmap = bitmap, contentDescription = null)
```

## Properties and defaults

| Property | Default / behavior |
| --- | --- |
| imageVector / painter / bitmap | Required resource; choose one of three overloads |
| contentDescription | Required nullable description; localize meaningful icons, null for decoration |
| modifier | Modifier |
| tint | OxygenTheme.contentColor; explicit Unspecified disables tint |
| size | OxygenTheme.icons.medium; same token sizing for all overloads |
| mirrorInRtl | false; vector autoMirror applies independently; explicit true mirrors other resources in RTL |

## Icon collection

Depend on `io.oxygenx:oxygenx-icons-material` (BOM-managed version) and import `io.oxygenx.icons.material.MaterialSymbols`. All 128 symbols use Google Material Symbols Outlined / opsz=24 / wght=300 / FILL=0 / GRAD=0, Apache-2.0. Use `MaterialSymbols.All` for browsing; individual getters allow normal application usage to be shrunk.

The 14 `OxygenIcons` compatibility names remain, with Rounded geometry unified to Outlined. The extended collection reexports the same instances, e.g. `OxygenIcons.Back === MaterialSymbols.ArrowBackIosNew`, `Clock === Schedule`. Core components still depend only on the small module. Directional vectors declare autoMirror and work in RTL by default; also passing mirrorInRtl=true never flips them twice. Custom non-auto-mirrored resources can still opt in explicitly.

## Content color and original colors

Omitting tint inherits the slot content color, including disabled button colors. Explicit Color.Unspecified removes the color filter and preserves the vector paths, Painter or bitmap colors. Color.Transparent remains an explicit tint and hides the graphic.

Unlike Text, Unspecified means no tint. Icons do not read text style.color. Icon size constrains resources; use Foundation Image for general image display.

## Semantics and motion

Meaningful icons expose image role and description through Foundation Image; decorative icons add no description. Wrap actions in IconButton. Icon has no animation. Vectors honor their autoMirror flag; enable mirrorInRtl explicitly for other directional assets.

## Migration and regression

If old code explicitly passed tint = Color.Unspecified to inherit content color, remove that argument or use OxygenTheme.contentColor. Existing Vector parameter order remains; resource types distinguish the new overloads.

Desktop tests cover ambient/explicit tint, original colors for three resource types, RTL, fixed icon size at 2× font scale and image/decorative semantics. Light/dark Text/Icon PNGs provide static regression coverage, not OxygenOS fidelity acceptance.
