# NavigationBar

Unofficial / draft. Items have equal width, token-driven selection colors and Tab semantics. The host owns system-bar Insets.

## Slots

```kotlin
NavigationBar {
    NavigationBarItem(
        selected = selected,
        onClick = onSelect,
        label = { Text("Books") },
        icon = { Icon(bookIcon, contentDescription = null) },
    )
}
```

The item is a RowScope extension. It supplies content color and caption text style and supports disabled state. Hosts may omit labels or provide custom icons. Import both NavigationBar and the NavigationBarItem extension.

The existing list overload and NavigationBarItem data class remain available and render through the same slots. Slot items expose colors / motion. In a mixed M3 host, wrap the Oxygen bar in ProvideMaterial3Content below any M3 container providers so retained M3 slot content receives Oxygen styles.

## Verification

Desktop regression covers selection, disabled semantics and one callback per click. OxygenOS frame fitting and component performance acceptance remain pending.
