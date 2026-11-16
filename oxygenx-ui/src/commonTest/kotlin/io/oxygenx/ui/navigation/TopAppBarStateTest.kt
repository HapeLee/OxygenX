// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals

class TopAppBarStateTest {
    private fun state(rangePx: Float = 100f): TopAppBarState = TopAppBarState(spring()).apply { collapseRangePx = rangePx }

    @Test
    fun collapsingScrollUpdatesFractionProportionally() {
        val state = state(rangePx = 100f)
        // 嵌套滚动的 available.y 负值 = 内容上移 = 收拢
        val consumed = state.applyScroll(-40f, allowCollapse = true, allowExpand = false)
        assertEquals(0.4f, state.collapsedFraction, 0.0001f)
        assertEquals(Offset(0f, -40f), consumed)
    }

    @Test
    fun collapsingStopsAtOneAndConsumesOnlyTheRemainder() {
        val state = state(rangePx = 100f)
        state.applyScroll(-70f, allowCollapse = true, allowExpand = false)
        val consumed = state.applyScroll(-70f, allowCollapse = true, allowExpand = false)
        assertEquals(1f, state.collapsedFraction, 0.0001f)
        assertEquals(-30f, consumed.y, 0.0001f)
    }

    @Test
    fun expandingIsIgnoredWhenNotAllowed() {
        val state = state(rangePx = 100f)
        state.applyScroll(-100f, allowCollapse = true, allowExpand = false)
        val consumed = state.applyScroll(50f, allowCollapse = true, allowExpand = false)
        assertEquals(1f, state.collapsedFraction, 0.0001f)
        assertEquals(Offset.Zero, consumed)
    }

    @Test
    fun expandingRunsFromThePostScrollPass() {
        val state = state(rangePx = 100f)
        state.applyScroll(-100f, allowCollapse = true, allowExpand = false)
        val consumed = state.applyScroll(30f, allowCollapse = false, allowExpand = true)
        assertEquals(0.7f, state.collapsedFraction, 0.0001f)
        assertEquals(30f, consumed.y, 0.0001f)
    }

    @Test
    fun expandingWhenAlreadyExpandedConsumesNothing() {
        val state = state(rangePx = 100f)
        val consumed = state.applyScroll(20f, allowCollapse = false, allowExpand = true)
        assertEquals(0f, state.collapsedFraction, 0.0001f)
        assertEquals(Offset.Zero, consumed)
    }

    @Test
    fun zeroRangeConsumesNothing() {
        val state = state(rangePx = 0f)
        assertEquals(Offset.Zero, state.applyScroll(-40f, allowCollapse = true, allowExpand = false))
        assertEquals(0f, state.collapsedFraction, 0.0001f)
    }
}
