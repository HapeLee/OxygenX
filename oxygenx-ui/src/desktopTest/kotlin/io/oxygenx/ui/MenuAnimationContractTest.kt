// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.oxygenx.motion.OxygenMotion
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.ui.overlay.DropdownMenuAnimation
import io.oxygenx.ui.overlay.rememberDropdownMenuAnimation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class MenuAnimationContractTest {
    @Test
    fun bothChannelsAdvanceReverseAndReopenWithoutResetOrPerFrameComposition() = runComposeUiTest {
        mainClock.autoAdvance = false
        val expanded = mutableStateOf(true)
        val spec = OxygenMotionSpec("TEST-MENU", OxygenMotion.fade(1_000), OxygenMotion.fade(1_000))
        lateinit var actual: DropdownMenuAnimation
        var compositions = 0
        setContent {
            val state = rememberDropdownMenuAnimation(expanded.value, spec, spec)
            SideEffect {
                actual = state
                compositions++
            }
        }
        mainClock.advanceTimeBy(320)
        var opening = 0f
        var count = 0
        runOnIdle {
            opening = actual.scale.value
            assertTrue(opening > 0f && opening < 1f)
            assertEquals(actual.scale.value, actual.fade.value, 0.001f)
            count = compositions
        }
        mainClock.advanceTimeBy(64)
        runOnIdle { assertEquals(count, compositions) }
        var before = 0f
        runOnIdle {
            before = actual.scale.value
            expanded.value = false
        }
        mainClock.advanceTimeByFrame()
        runOnIdle {
            assertEquals(before, actual.scale.value, 0.08f)
            assertTrue(actual.present.value)
        }
        mainClock.advanceTimeBy(160)
        runOnIdle {
            assertTrue(actual.scale.value < before)
            before = actual.scale.value
            expanded.value = true
        }
        mainClock.advanceTimeByFrame()
        runOnIdle {
            assertEquals(before, actual.scale.value, 0.08f)
            assertTrue(actual.present.value)
        }
        mainClock.advanceTimeBy(1_100)
        runOnIdle {
            assertEquals(1f, actual.scale.value)
            assertEquals(1f, actual.fade.value)
            expanded.value = false
        }
        mainClock.advanceTimeBy(1_100)
        runOnIdle {
            assertFalse(actual.present.value)
            assertEquals(0f, actual.scale.value)
            assertEquals(0f, actual.fade.value)
        }
    }
}
