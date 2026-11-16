// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import kotlin.test.Test
import kotlin.test.assertEquals

class SliderMathTest {
    @Test
    fun valueToFractionMapsRangeEndpoints() {
        assertEquals(0f, valueToFraction(0f, 0f..1f))
        assertEquals(1f, valueToFraction(1f, 0f..1f))
        assertEquals(0.25f, valueToFraction(25f, 0f..100f))
    }

    @Test
    fun valueToFractionClampsOutOfRangeValues() {
        assertEquals(0f, valueToFraction(-10f, 0f..1f))
        assertEquals(1f, valueToFraction(10f, 0f..1f))
    }

    @Test
    fun valueToFractionHandlesDegenerateRange() {
        assertEquals(0f, valueToFraction(5f, 5f..5f))
    }

    @Test
    fun fractionToValueIsContinuousWithoutSteps() {
        assertEquals(0.5f, fractionToValue(0.5f, 0f..1f, steps = 0), 0.0001f)
        assertEquals(30f, fractionToValue(0.3f, 0f..100f, steps = 0), 0.0001f)
    }

    @Test
    fun fractionToValueSnapsToDiscreteSteps() {
        // steps = 3 → 0, 25, 50, 75, 100
        assertEquals(0f, fractionToValue(0.1f, 0f..100f, steps = 3), 0.0001f)
        assertEquals(50f, fractionToValue(0.45f, 0f..100f, steps = 3), 0.0001f)
        assertEquals(100f, fractionToValue(0.95f, 0f..100f, steps = 3), 0.0001f)
    }

    @Test
    fun snapSliderValueKeepsValuesInsideRange() {
        assertEquals(10f, snapSliderValue(10f, 0f..100f, steps = 0))
        assertEquals(0f, snapSliderValue(-5f, 0f..100f, steps = 4))
        assertEquals(100f, snapSliderValue(105f, 0f..100f, steps = 4))
    }

    @Test
    fun snapSliderValueHandlesDegenerateRange() {
        assertEquals(7f, snapSliderValue(7f, 7f..7f, steps = 3))
    }
}
