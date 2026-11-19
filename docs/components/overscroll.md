# Overscroll

Unofficial / draft. OxygenTheme supplies vertical rebound to scroll containers through Foundation LocalOverscrollFactory.

## Usage

```kotlin
OxygenTheme {
    LazyColumn {
        items(100) { index -> Text("Item $index") }
    }
}
```

## Boundary contract

Normal drag and inertia remain while content can consume scrolling. Only vertical input left unconsumed by content and the nested scroll chain enters overscroll. Horizontal input stays with the container or parent and does not create a vertical spring.

Foundation performScroll and performFling return consumed amounts. A new rebound needs an actual vertical remainder greater than half a physical pixel. Fling velocity alone cannot start one. During an active fling, same-direction residual velocity can carry an existing edge displacement into the spring. An existing pull must settle even when released with zero velocity. Once a pull is fully released, a middle-of-content fling must not reintroduce displacement.

## Pull to refresh with a large top bar

PullToRefreshBox measures remaining downward user input and checks the threshold at release, before the fling starts. Scaffold(topBarState = state) gives the bar priority automatically. With a custom scroll parent, pass `canRefresh = { topBarState.collapsedFraction <= 0f }` to let the bar expand first. The check runs when scroll is dispatched. Use `indicatorTopPadding` when an overlapping bar requires an inset for the indicator.

## Tokens and motion

Maximum displacement uses spacing.overscrollMax; initial drag following uses interaction.overscrollDragScale and progressively damped cubic resistance. MOT-SCROLL-001 spring tokens drive settling. Touch distance and animated displacement synchronize on takeover. This fix changes neither the damping curve nor spring parameters.

## Regression and acceptance

The catalog Overscroll page has an 80-item list, top/middle/bottom controls, first-item index and overscroll status. Virtual-frame tests cover full consumption, unconfirmed residual velocity, real edge motion in both directions, fractional remainder, zero-velocity release, flinging after releasing tension and horizontal velocity. Desktop LazyColumn tests sample a fling within the list and a fling that reaches the top, checking that rebound starts only in the latter case.

These tests protect OxygenX behavior; they do not replace OxygenOS frame fitting or complete component performance acceptance. The existing refresh overload remains available.
