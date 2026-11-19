// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SnackbarStateTest {
    @Test
    fun cancellingVisibleMessageClearsItAndAllowsNextMessage() = runTest {
        val state = SnackbarHostState()
        val first = launch { state.showSnackbar("first", duration = SnackbarDuration.Indefinite) }
        runCurrent()
        assertEquals("first", state.currentSnackbarData?.message)
        first.cancelAndJoin()
        assertNull(state.currentSnackbarData)

        val next = async { state.showSnackbar("next", duration = SnackbarDuration.Indefinite) }
        runCurrent()
        assertEquals("next", state.currentSnackbarData?.message)
        state.dismiss()
        assertEquals(SnackbarResult.Dismissed, next.await())
        assertNull(state.currentSnackbarData)
    }

    @Test
    fun cancellingQueuedMessageDoesNotDismissVisibleMessage() = runTest {
        val state = SnackbarHostState()
        val first = async { state.showSnackbar("first", duration = SnackbarDuration.Indefinite) }
        val queued = launch { state.showSnackbar("queued") }
        runCurrent()
        queued.cancelAndJoin()
        assertEquals("first", state.currentSnackbarData?.message)
        state.currentSnackbarData?.onAction?.invoke()
        assertEquals(SnackbarResult.ActionPerformed, first.await())
        assertNull(state.currentSnackbarData)
    }

    @Test
    fun shortMessageTimesOutWithoutRealDelay() = runTest {
        val state = SnackbarHostState()
        assertEquals(SnackbarResult.Dismissed, state.showSnackbar("timeout"))
        assertEquals(4_000L, testScheduler.currentTime)
        assertNull(state.currentSnackbarData)
    }

    @Test
    fun accessibilityTimeoutIncludesDismissControls() = runTest {
        val state = SnackbarHostState()
        state.recommendedTimeoutMillis = { original, controls ->
            assertEquals(true, controls)
            original * 2
        }
        state.showSnackbar("accessible", withDismissAction = true)
        assertEquals(8_000L, testScheduler.currentTime)
        assertNull(state.currentSnackbarData)
    }
}
