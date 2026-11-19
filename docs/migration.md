# Migration notes

Unofficial / draft. Changes from the pre-hardening development build, 2026-11-17.

## Source and binary compatibility

`OxygenTheme.darkTheme: Boolean = false` becomes `Boolean? = null`. Existing explicit Boolean calls still compile; omission now inherits outer colors. Use `darkTheme = false` to intentionally reset a subtree to default light colors.

Both TextField overloads gain a defaulted `motion` parameter. Existing Kotlin source calls remain valid. The theme and TextField JVM signatures change: recompile dependents and publish all modules at the same new version. ABI snapshots are updated; no compatibility with previously compiled development artifacts is promised.

## Behavior changes

- Text respects TextStyle.color unless an explicit color overrides it.
- Slider mirrors RTL gestures, track and custom thumb; focus and keyboard input are supported.
- Slider accessibility changes call onValueChangeFinished once; unchanged values return false without callbacks.
- Slider rejects negative steps, non-finite values and invalid ranges.
- Snackbar cancellation clears the current message; accessible timeout and live-region announcements are supported.
- TextField focus motion is token-based and drawn without changing layout height.
- Libraries export Compose dependencies used by their public signatures, so consumers receive runtime, UI and layout types without relying on unrelated dependencies.

## Regression and remaining work

2026-11-17 state-input update: TextField adds a Foundation TextFieldState overload with M3-compatible common editing parameters; existing value overloads remain. Locals now derive defaults at the read location and nested themes preserve explicit slot styles. Unknown Card/Scaffold colors inherit ambient content color. TextField slots receive explicit defaults and all overloads honor textStyle.color. New ProvideContentStyle/contentColorFor helpers live in oxygenx-theme; optional ProvideMaterial3Content bridges Oxygen content styles to M3. Recompile dependents after updating the library. See [TextField](components/text-field.md) and [theme interoperability](theme.md).

Catalog reproductions live on Slider, TextField and Scaffold capability pages. Desktop rendering tests, four Slider PNGs and two TextField slot PNGs guard these contracts. Device TalkBack, full component goldens, OxygenOS frame calibration, performance benchmarks and remote CI validation remain separate acceptance items.

2026-11-17 Text / Icon update: omitted Icon tint still inherits content color; explicit Unspecified now preserves original colors, so remove that argument where the old inheritance behavior was intended. Text adds autoSize, AnnotatedString.inlineContent, fontFamily and textDecoration. Use named arguments and recompile callers. The catalog adds a Text / Icon page; see [Android performance](performance.md) for the new measurement entry.

Text keeps onTextLayout last. Existing calls that passed it through a full positional argument list must name it as onTextLayout = ... after the inserted optional properties. Named callbacks and trailing lambdas remain valid.

2026-11-17 overscroll fix: compute unconsumed fling velocity according to the Foundation contract, eliminating spurious rebound after a middle-of-list fling. Existing pulls also settle on zero-velocity release. No public API or token changes.

2026-11-18: NavigationBar adds a RowScope slot overload and NavigationBarItem extension for existing host dispatch layers. The list overload remains; recompile consumers. See [NavigationBar](components/navigation-bar.md).

2026-11-18 M3 typography and menu motion: the basic scale now has 15 M3 roles. labelMedium is 12sp; buttons/menus use labelLarge at 14sp. Legacy fields, the 13-style constructor and JVM copy bridge remain; prefer the M3-named factory or Typography.toOxygenTypography() for complete configuration. Menus now animate scale/opacity from the anchor and stay until exit completes. Existing calls get the animation; use motion / fadeMotion to customize. Hosts retain their M3 / Legado semantic roles rather than rewriting titleSmall through legacy aliases.

Standard and collapsed app bar titles use calibrated 18sp / 24sp sizing, with theme fonts and weight inherited from titleLarge. Expanded titles use displayMedium (45sp / 52sp). Custom navigation icons can inherit the slot size through LocalTopAppBarNavigationIconSize.
