// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.feedback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgressMathTest {
    @Test
    fun linearIndeterminateStartsOutsideTrackAndClosesAtEnd() {
        // 一轮开始时条还在轨道左侧之外、长度为 0；结束时两端都收在右端。
        assertTrue(linearIndeterminateEnd(0f) <= 0f)
        assertEquals(1f, linearIndeterminateEnd(1f), 0.0001f)
        assertEquals(1f, linearIndeterminateStart(1f), 0.0001f)
    }

    @Test
    fun linearIndeterminateEndIsMonotonic() {
        var previous = linearIndeterminateEnd(0f)
        for (step in 1..100) {
            val current = linearIndeterminateEnd(step / 100f)
            assertTrue(current >= previous - 0.0001f, "终点在 phase=${step / 100f} 处回退")
            previous = current
        }
    }

    @Test
    fun linearIndeterminateBarNeverInverts() {
        for (step in 0..100) {
            val phase = step / 100f
            assertTrue(
                linearIndeterminateEnd(phase) >= linearIndeterminateStart(phase),
                "phase=$phase 处条长为负",
            )
        }
    }

    @Test
    fun linearIndeterminateMidCycleKeepsVisibleBar() {
        // 半程时尾部已进轨道、头部还没离场，条长应占轨道相当一段。
        val length = linearIndeterminateEnd(0.5f) - linearIndeterminateStart(0.5f)
        assertTrue(length > 0.2f, "半程条长只有 $length")
    }

    @Test
    fun circularSweepBouncesBetweenBounds() {
        assertEquals(CIRCULAR_INDETERMINATE_MIN_SWEEP, circularIndeterminateSweep(0f), 0.0001f)
        assertEquals(CIRCULAR_INDETERMINATE_MAX_SWEEP, circularIndeterminateSweep(0.5f), 0.0001f)
        assertEquals(CIRCULAR_INDETERMINATE_MIN_SWEEP, circularIndeterminateSweep(1f), 0.0001f)
    }

    @Test
    fun circularSweepStaysWithinBounds() {
        for (step in 0..200) {
            val sweep = circularIndeterminateSweep(step / 200f)
            assertTrue(sweep >= CIRCULAR_INDETERMINATE_MIN_SWEEP - 0.0001f, "phase=${step / 200f} 的弧长过短")
            assertTrue(sweep <= CIRCULAR_INDETERMINATE_MAX_SWEEP + 0.0001f, "phase=${step / 200f} 的弧长过长")
        }
    }
}
