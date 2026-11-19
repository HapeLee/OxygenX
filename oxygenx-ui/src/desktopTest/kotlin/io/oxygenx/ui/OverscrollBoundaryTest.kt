// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class OverscrollBoundaryTest {
    @Test
    fun realLazyColumnFlingInTheMiddleNeverDisplaysOverscroll() = runComposeUiTest {
        mainClock.autoAdvance = false
        val state = LazyListState(100)
        var effect: OverscrollEffect? = null
        setContent {
            OxygenTheme {
                val factory = requireNotNull(LocalOverscrollFactory.current)
                val overscroll = remember(factory) { factory.createOverscrollEffect() }
                effect = overscroll
                LazyColumn(Modifier.size(240.dp).testTag("list"), state = state, overscrollEffect = overscroll) {
                    items(1000) { Box(Modifier.fillMaxWidth().height(48.dp)) }
                }
            }
        }
        onNodeWithTag("list").performTouchInput {
            swipe(Offset(centerX, bottom - 20), Offset(centerX, top + 20), durationMillis = 150)
        }
        var sawOverscroll = false
        repeat(180) {
            mainClock.advanceTimeByFrame()
            runOnIdle { sawOverscroll = sawOverscroll || requireNotNull(effect).isInProgress }
        }
        runOnIdle {
            assertTrue(state.firstVisibleItemIndex > 100)
            assertTrue(state.canScrollBackward && state.canScrollForward)
            assertFalse(state.isScrollInProgress)
            assertFalse(sawOverscroll, "A middle-of-list fling must not create spring displacement")
        }
    }

    @Test
    fun realLazyColumnReboundsWhenAFlingActuallyHitsTheTop() = runComposeUiTest {
        mainClock.autoAdvance = false
        val state = LazyListState(3)
        var effect: OverscrollEffect? = null
        setContent {
            OxygenTheme {
                val factory = requireNotNull(LocalOverscrollFactory.current)
                val overscroll = remember(factory) { factory.createOverscrollEffect() }
                effect = overscroll
                LazyColumn(Modifier.size(240.dp).testTag("list"), state = state, overscrollEffect = overscroll) {
                    items(100) { Box(Modifier.fillMaxWidth().height(48.dp)) }
                }
            }
        }
        onNodeWithTag("list").performTouchInput {
            swipe(Offset(centerX, centerY - 60), Offset(centerX, centerY + 60), durationMillis = 70)
        }
        var sawOverscroll = false
        repeat(180) {
            mainClock.advanceTimeByFrame()
            runOnIdle {
                if (requireNotNull(effect).isInProgress) {
                    assertFalse(state.canScrollBackward, "The spring must start only after the list reaches its top")
                    sawOverscroll = true
                }
            }
        }
        runOnIdle {
            assertFalse(state.canScrollBackward)
            assertTrue(sawOverscroll, "A fling that hits the top should retain the rebound")
            assertFalse(requireNotNull(effect).isInProgress)
        }
    }
}
