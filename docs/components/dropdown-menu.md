# DropdownMenu

Unofficial / draft. Import `io.oxygenx.ui.overlay.DropdownMenu` and `DropdownMenuItem`.

## Usage

```kotlin
Box {
    Button(onClick = { expanded = true }) { Text("Sort") }
    DropdownMenu(expanded, { expanded = false }) {
        DropdownMenuItem(
            text = { Text("By name") },
            onClick = { sortByName(); expanded = false },
        )
        DropdownMenuItem(text = { Text("Unavailable") }, onClick = {}, enabled = false)
    }
}
```

The parent Box is the anchor. The host owns expansion, selection and dismissal after a choice. Outside clicks and Back request dismissal; clicking an item only runs its callback.

## Properties and defaults

| API / property | Default / behavior |
| --- | --- |
| DropdownMenu expanded / onDismissRequest | Required host state and dismissal callback |
| modifier / offset | Panel modifier; zero offset, horizontal offset follows layout direction |
| content | ColumnScope slot; use normal items, not a nested LazyColumn |
| DropdownMenuItem text / onClick | Required composable label and callback |
| modifier / enabled | Modifier; true, disabled items do not invoke onClick |
| leadingIcon / trailingIcon | Optional slots, at least the theme's medium icon width |
| contentPadding / interactionSource | Additional overload; explicitly supply PaddingValues to customize padding and optionally share interactions |

## Style and layout

The classic M3 inspired default uses a 4dp menu corner, 3dp shadow, no card border, a surfaceContainer background and theme labelLarge typography (14sp / 20sp by default). The menu shape is separate from Card. The menu shadow is an explicit exception to the library's flat container style; Card and Surface keep their own defaults.

Width follows content between the theme's menuMinWidth (112dp) and 280dp. Rows are at least 48dp, with 16dp horizontal padding, 12dp icon gaps and 8dp container padding above and below. Long text wraps, large fonts can grow rows, and constrained menus scroll. The menu tries below the anchor, then above, then clamps within the window. RTL uses start/end alignment and reverses horizontal offsets. Normal windows retain 48dp vertical margins; tiny windows reduce these margins to keep content accessible.

Colors follow OxygenTheme, including disabled text and icon slots. In mixed M3 hosts, use ProvideMaterial3Content at the dispatch boundary so retained AppText / AppIcon slots receive the menu's locals.

## Migration and verification

Existing DropdownMenu and DropdownMenuItem signatures remain available. For custom padding, call the additional overload using named contentPadding; interactionSource can then be supplied. The default label no longer inherits unrelated parent text styles.

Desktop tests cover wrapping, 2× font scale, disabled slots, custom padding, shared interactions, scrolling to the last item and one host callback. Common tests cover RTL offsets, edge fallback and tiny windows. Light/dark PNGs exercise the same panel renderer used by Popup. These are library regressions, not OxygenOS fidelity or device / performance acceptance.

## Motion

The default scales 0.8→1 and fades from the anchor intersection; exit reverses both channels and retains Popup until both finish. Rapid close/reopen continues from the current progress without per-frame composition. Spatial and opacity channels use OxygenMotionDefaults.menuScale() / menuFade(); the explicit motion / fadeMotion overload customizes them while old calls get default animation. Keep DropdownMenu composed and pass expanded; removing the entire composable immediately prevents exit animation.

Virtual-frame regressions cover entry/exit, reversal, delayed removal and no per-frame composition. Spring parameters reference M3; the normalized threshold is a project choice and remains draft.
