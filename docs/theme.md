# Theme inheritance

Unofficial / draft. Import `OxygenTheme` from `io.oxygenx.theme`.

## Color resolution

| Input | Result |
| --- | --- |
| Explicit colorScheme | Use that scheme, before darkTheme |
| No scheme, darkTheme = true | Default dark scheme |
| No scheme, darkTheme = false | Default light scheme |
| Both omitted / null | Inherit outer scheme; root fallback is light |

Other theme parameters inherit when omitted. The root does not automatically follow system appearance; the host supplies the mode or scheme.

## Local typography

```kotlin
OxygenTheme(colorScheme = customColors) {
    OxygenTheme(typography = customTypography) {
        Text("Keeps customColors")
    }
}
```

## Material type scale

Defaults follow all 15 basic roles in Material3 1.5.0-beta01, including size, line height, weight and tracking. Display (57/45/36), headline (32/28/24), title (22/16/14), body (16/14/12) and label (14/12/11) remain separate semantic families. LabelMedium is 12sp; buttons and menus use LabelLarge at 14sp. Expanded top bars use DisplayMedium at 45sp and list titles use the role selected by the host.

```kotlin
val typography = OxygenTypography(
    titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
)
OxygenTheme(typography = typography) { Text("Source title", style = OxygenTheme.typography.titleSmall) }
```

The M3-named factory and getters coexist with legacy semantic fields. The old 13-style constructor and JVM copy/default dispatcher remain callable; new roles participate in equality and copy through immutable extras. Prefer the named factory or Typography.toOxygenTypography() for a coherent complete scale, including per-role font overrides. The optional material3 adapter preserves every source TextStyle rather than replacing host M3 / Legado roles with aliases.

## App bar title roles

Standard and collapsed titles use 18sp / 24sp calibrated metrics and inherit the font and weight of titleLarge. Expanded LargeTopAppBar and PagerTopBar titles use displayMedium (45sp / 52sp) and interpolate to the compact title during collapse. The standard and collapsed bars are 74dp tall. The compact navigation icon uses a 16dp drawing size inside a 48dp touch target. Custom navigation icons can read LocalTopAppBarNavigationIconSize.

TopAppBar, LargeTopAppBar and PagerTopBar place adjacent action buttons together, giving default icon actions a center-to-center distance of 48dp. Use TopAppBarAction for an icon action, including actions with a long press. The button applies the theme's 48dp default touch target and 36dp default unbounded ripple radius. Supply onLongClick and an optional onLongClickLabel for a secondary action.

## Explicit mode

```kotlin
OxygenTheme(darkTheme = true) {
    OxygenTheme(darkTheme = false) {
        Text("Explicit light subtree")
    }
}
```

## Content styles

Containers provide slot color and text style. OxygenX Text / Icon consume those values. ProvideTextStyle merges explicit attributes into the surrounding text style. [Text color precedence](components/text.md) applies inside slots.

## Slot contract

LocalContentColor and LocalTextStyle are Oxygen-owned composition locals. Without an explicit provider, they derive onSurface and bodyMedium from the theme at the read location. A nested OxygenTheme does not reset explicit outer slot styles.

ProvideTextStyle merges only supplied attributes. ProvideContentStyle combines content color and a merged text style; specifying only color preserves typography. contentColorFor(backgroundColor) maps known theme colors and inherits ambient content color for unknown colors. Surface, Card and Scaffold use this same fallback.

```kotlin
ProvideContentStyle(
    contentColor = OxygenTheme.colorScheme.primary,
    textStyle = TextStyle(fontWeight = FontWeight.Bold),
) {
    Text("Inherits size and family; overrides weight")
}
```

## Optional M3 interoperability

Add oxygenx-material3 and import io.oxygenx.material3.ProvideMaterial3Content plus toOxygenColorScheme / toOxygenTypography. Wrap once above Oxygen components to make M3 Text / Icon read deeper Oxygen slot colors and typography, including disabled state. Computed locals resolve at the consuming descendant, rather than capturing the root's body style.

```kotlin
MaterialTheme(colorScheme = materialColors) {
    OxygenTheme(colorScheme = materialColors.toOxygenColorScheme(), typography = MaterialTheme.typography.toOxygenTypography()) {
        ProvideMaterial3Content {
            Button(onClick = save) {
                androidx.compose.material3.Text("Save")
            }
        }
    }
}
```

## Interoperability boundary

The bridge is one-way (Oxygen → M3), does not alias the keys, and does not create MaterialTheme or synchronize every design token. Explicit M3 providers inside the subtree still win. It does not make Oxygen Text in M3 component slots inherit M3 styling in reverse. Keep palettes aligned through the color bridge; do not build circular two-way local bindings.
