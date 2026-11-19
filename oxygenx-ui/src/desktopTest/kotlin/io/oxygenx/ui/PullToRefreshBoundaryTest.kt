// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.feedback.PullToRefreshBox
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.LargeTopAppBar
import io.oxygenx.ui.navigation.TopAppBarState
import io.oxygenx.ui.navigation.rememberTopAppBarState
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class PullToRefreshBoundaryTest {
    @Test
    fun inertiaAtTheTopDoesNotPullOrRefresh() = runComposeUiTest {
        val dispatcher = NestedScrollDispatcher()
        var refreshes = 0
        setContent {
            OxygenTheme {
                PullToRefreshBox(isRefreshing = false, onRefresh = { refreshes++ }, modifier = Modifier.size(240.dp)) {
                    Box(Modifier.size(240.dp).nestedScroll(remember { object : NestedScrollConnection {} }, dispatcher))
                }
            }
        }
        runOnIdle {
            dispatcher.coroutineScope.launch {
                dispatcher.dispatchPreFling(Velocity(0f, 4000f))
                assertEquals(Offset.Zero, dispatcher.dispatchPostScroll(Offset.Zero, Offset(0f, 400f), NestedScrollSource.SideEffect))
                dispatcher.dispatchPostFling(Velocity.Zero, Velocity(0f, 4000f))
            }
        }
        waitForIdle()
        runOnIdle { assertEquals(0, refreshes) }
    }

    @Test
    fun appBarExpandsBeforeASeparatePullCanRefresh() = runComposeUiTest {
        val dispatcher = NestedScrollDispatcher()
        lateinit var topBar: TopAppBarState
        var refreshes = 0
        setContent {
            OxygenTheme {
                val state = rememberTopAppBarState(initialCollapsedFraction = 1f)
                topBar = state
                Scaffold(
                    modifier = Modifier.size(360.dp, 550.dp),
                    topBar = { LargeTopAppBar(title = { Text("Large title") }, state = state) },
                    topBarState = state,
                ) {
                    PullToRefreshBox(
                        isRefreshing = false,
                        onRefresh = { refreshes++ },
                        modifier = Modifier.size(240.dp),
                    ) {
                        Box(Modifier.size(240.dp).nestedScroll(remember { object : NestedScrollConnection {} }, dispatcher))
                    }
                }
            }
        }
        runOnIdle {
            dispatcher.coroutineScope.launch {
                val velocity = Velocity(0f, 3000f)
                suspend fun release() {
                    dispatcher.dispatchPreFling(velocity)
                    dispatcher.dispatchPostFling(Velocity.Zero, velocity)
                }
                fun pull(pixels: Float, source: NestedScrollSource = NestedScrollSource.UserInput): Offset = dispatcher.dispatchPostScroll(Offset.Zero, Offset(0f, pixels), source)

                pull(240f)
                assertEquals(0f, topBar.collapsedFraction)
                assertEquals(0, refreshes)
                release()
                assertEquals(0, refreshes)

                // A short real pull followed by a large inertial delta is still subthreshold.
                pull(30f)
                assertEquals(velocity, dispatcher.dispatchPreFling(velocity))
                assertEquals(Offset.Zero, pull(400f, NestedScrollSource.SideEffect))
                dispatcher.dispatchPostFling(Velocity.Zero, velocity)
                assertEquals(0, refreshes)

                assertEquals(Offset(0f, 600f), pull(600f))
                assertEquals(0, refreshes)
                release()
                assertEquals(1, refreshes)
                release()
                assertEquals(1, refreshes)
            }
        }
        waitForIdle()
        runOnIdle { assertTrue(topBar.collapsedFraction == 0f && refreshes == 1) }
    }
}
