// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.animation.core.spring
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OverscrollFlingTest {
    @Test
    fun fullyConsumedContentFlingNeverStartsAnOverscrollSpring() = runTest {
        val fixture = Fixture(coroutineContext)
        val velocity = Velocity(250f, -1800f)
        var calls = 0
        fixture.effect.applyToFling(velocity) {
            calls++
            assertEquals(velocity, it)
            it // Foundation 回调返回已消费速度：列表中间应全部消费。
        }
        testScheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(0f, fixture.peak)
        assertFalse(fixture.effect.isInProgress)
    }

    @Test
    fun unconsumedVelocityWithoutAnEdgeDeltaDoesNotBounce() = runTest {
        val fixture = Fixture(coroutineContext)
        fixture.effect.applyToFling(Velocity(0f, 1600f)) { Velocity.Zero }
        testScheduler.advanceUntilIdle()
        assertEquals(0f, fixture.peak)
        assertEquals(0f, fixture.effect.offsetY)
        assertFalse(fixture.effect.isInProgress)
    }

    @Test
    fun releasingAnExistingPullWithZeroVelocityStillSettles() = runTest {
        val fixture = Fixture(coroutineContext)
        fixture.effect.applyToScroll(Offset(0f, 60f), NestedScrollSource.UserInput) { Offset.Zero }
        assertTrue(fixture.effect.offsetY > 0f)
        var calls = 0
        fixture.effect.applyToFling(Velocity.Zero) {
            calls++
            assertEquals(Velocity.Zero, it)
            it
        }
        testScheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(0f, fixture.effect.offsetY)
        assertFalse(fixture.effect.isInProgress)
    }

    @Test
    fun fullyConsumedFlingAfterPullWasReleasedDoesNotReintroduceOverscroll() = runTest {
        val fixture = Fixture(coroutineContext)
        fixture.effect.applyToScroll(Offset(0f, 40f), NestedScrollSource.UserInput) { Offset.Zero }
        fixture.effect.applyToScroll(Offset(0f, -60f), NestedScrollSource.UserInput) { it }
        assertEquals(0f, fixture.effect.offsetY)
        fixture.effect.applyToFling(Velocity(0f, -1200f)) { it }
        testScheduler.advanceUntilIdle()
        assertEquals(0f, fixture.peak)
        assertFalse(fixture.effect.isInProgress)
    }

    @Test
    fun unconsumedHorizontalVelocityDoesNotCreateVerticalOverscroll() = runTest {
        val fixture = Fixture(coroutineContext)
        fixture.effect.applyToFling(Velocity(2000f, 0f)) { Velocity.Zero }
        testScheduler.advanceUntilIdle()
        assertEquals(0f, fixture.peak)
        assertFalse(fixture.effect.isInProgress)
    }

    @Test
    fun flingAtEitherEdgeBuildsDisplacementAndReturnsToRest() = runTest {
        for (direction in listOf(-1f, 1f)) {
            val fixture = Fixture(coroutineContext)
            fixture.effect.applyToFling(Velocity(0f, direction * 1600f)) {
                fixture.effect.applyToScroll(Offset(0f, direction * 32f), NestedScrollSource.SideEffect) { Offset.Zero }
                assertTrue(fixture.effect.offsetY * direction > 0f)
                it
            }
            testScheduler.advanceUntilIdle()
            assertTrue(fixture.peak > 0f)
            assertFalse(fixture.effect.isInProgress)
        }
    }

    /** Test dispatcher 的虚拟帧时钟，记录整段动画，避免只检查已归零的结束帧。 */
    private class Fixture(context: kotlin.coroutines.CoroutineContext) {
        var peak = 0f
        private var frame = 0L
        private val clock: MonotonicFrameClock = object : MonotonicFrameClock {
            override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R {
                delay(16)
                frame += 16_000_000L
                return onFrame(frame).also { peak = max(peak, abs(effect.offsetY)) }
            }
        }
        val effect: OxygenOverscrollEffect = OxygenOverscrollEffect(
            maxPx = 200f,
            dragScale = 0.5f,
            settle = spring(),
            scope = CoroutineScope(context + clock),
        )
    }
}
