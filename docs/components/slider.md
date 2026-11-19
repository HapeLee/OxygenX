# Slider

Unofficial / draft. Import `io.oxygenx.ui.basic.Slider`. Value is controlled by the caller.

## Usage

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }
Slider(
    value = value,
    onValueChange = { value = it },
    valueRange = 0f..1f,
    steps = 3,
    onValueChangeFinished = { save(value) },
)
```

## Properties and defaults

| Property | Default / behavior |
| --- | --- |
| value / onValueChange | Required finite value and update callback |
| modifier / enabled | Modifier / true |
| valueRange | 0f..1f; finite, ordered endpoints |
| steps | 0 continuous; positive counts intermediate steps, so 3 gives 5 values |
| colors / motion | SliderDefaults.colors() / SliderDefaults.Motion |
| interactionSource | null; internally remembered |
| onValueChangeFinished | null; touch end, changed keyboard step or accessibility commit |
| track / thumb | null; optional SliderScope slots |

## Input contract

RTL mirrors the track, touch coordinates and custom-thumb placement. Horizontal arrows follow physical direction; Up/Down increase/decrease. Home/End select range endpoints; Page Up/Down change about 10% of the interval count. Continuous keyboard adjustment uses 100 intervals.

Accessibility SetProgress snaps and clamps, returns false for an unchanged value, and invokes both callbacks once for a change. Disabled sliders do not handle input. Keyboard focus has a visible thumb ring.

## Colors and motion

Track/thumb/ticks/ripple colors come from SliderDefaults.colors(). Thumb position follows input directly; motion controls the press ripple. Custom track content receives a logical fraction and must handle its own RTL drawing, as ordinary start-aligned layouts do. The default track and custom-thumb position are mirrored by the component.

## Regression

The catalog includes RTL with current/committed values. Desktop tests cover touch, keyboard, disabled state, accessibility commits, custom-thumb positioning and four light/dark × LTR/RTL PNG goldens.
