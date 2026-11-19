// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.oxygenx.motion.OxygenAnimation
import io.oxygenx.motion.OxygenMotion
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.TextField
import io.oxygenx.ui.basic.TextFieldDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class MotionContractTest {
    @Test
    fun textFieldUsesCustomMotionWithoutChangingLayoutHeight() = runComposeUiTest {
        mainClock.autoAdvance = false
        val source = MutableInteractionSource()
        val focus = FocusInteraction.Focus()
        lateinit var scope: CoroutineScope
        val spec = OxygenMotionSpec("TEST-INPUT", OxygenMotion.fade(1_000), OxygenMotion.fade(1_000))
        setContent {
            val compositionScope = rememberCoroutineScope()
            SideEffect { scope = compositionScope }
            OxygenTheme {
                TextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.width(200.dp).testTag("input"),
                    interactionSource = source,
                    colors = TextFieldDefaults.colors(indicatorColor = Color.Blue, focusedIndicatorColor = Color.Red),
                    motion = spec,
                )
            }
        }
        val before = onNodeWithTag("input").captureToImage().toPixelMap()
        assertEquals(Color.Blue, before[before.width / 2, before.height - 1])
        runOnIdle { scope.launch { source.emit(focus) } }
        mainClock.advanceTimeBy(320)
        val during = onNodeWithTag("input").captureToImage().toPixelMap()
        val midColor = during[during.width / 2, during.height - 1]
        assertTrue(midColor.red > 0f && midColor.red < 1f)
        assertTrue(midColor.blue > 0f && midColor.blue < 1f)
        assertEquals(before.height, during.height)
        runOnIdle { scope.launch { source.emit(FocusInteraction.Unfocus(focus)) } }
        mainClock.advanceTimeBy(1_100)
        val after = onNodeWithTag("input").captureToImage().toPixelMap()
        assertEquals(Color.Blue, after[after.width / 2, after.height - 1])
        assertEquals(before.height, after.height)
    }

    @Test
    fun interruptedAnimationReversesFromItsCurrentValue() = runComposeUiTest {
        mainClock.autoAdvance = false
        lateinit var animation: OxygenAnimation
        lateinit var scope: CoroutineScope
        lateinit var job: Job
        val spec = OxygenMotionSpec("TEST-INTERRUPTION", OxygenMotion.fade(1_000), OxygenMotion.fade(1_000))
        setContent {
            val handle = rememberOxygenAnimation(spec)
            val compositionScope = rememberCoroutineScope()
            SideEffect {
                animation = handle
                scope = compositionScope
            }
        }
        runOnIdle { job = scope.launch { animation.animateTo(1f) } }
        mainClock.advanceTimeBy(320)
        var interrupted = 0f
        runOnIdle {
            interrupted = animation.value
            assertTrue(interrupted > 0f && interrupted < 1f)
            job.cancel()
            job = scope.launch { animation.animateTo(0f) }
            assertEquals(interrupted, animation.value)
        }
        mainClock.advanceTimeBy(160)
        runOnIdle { assertTrue(animation.value in 0f..interrupted) }
        mainClock.advanceTimeBy(1_100)
        runOnIdle {
            assertEquals(0f, animation.value)
            assertFalse(animation.isRunning)
        }
        runOnIdle { job = scope.launch { animation.animateTo(1f) } }
        mainClock.advanceTimeBy(1_100)
        runOnIdle { assertEquals(1f, animation.value) }
    }
}
