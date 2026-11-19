// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.test.runTest
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OverscrollMathTest {
    // ---- 曲线 ----

    @Test
    fun curveStartsAtFingerSpeedAndFlattens() {
        // x 很小时位移 ≈ x * range（起点跟手），之后每单位触摸量换到的位移越来越少。
        val range = 300f
        assertEquals(0f, overscrollDampingDistance(x = 0f, range = range))
        assertClose(27.1f, overscrollDampingDistance(x = 0.1f, range = range))
        assertClose(87.5f, overscrollDampingDistance(x = 0.5f, range = range))

        val first = overscrollDampingDistance(x = 0.2f, range = range) -
            overscrollDampingDistance(x = 0.1f, range = range)
        val second = overscrollDampingDistance(x = 0.3f, range = range) -
            overscrollDampingDistance(x = 0.2f, range = range)
        assertTrue(second < first, "增量必须递减，否则不是阻尼曲线")
    }

    @Test
    fun curveSaturatesAtOneThirdOfRange() {
        // 三次曲线的上限是 range / 3，且超出 [0, 1] 的输入被夹住，不会继续长。
        assertEquals(100f, overscrollDampingDistance(x = 1f, range = 300f))
        assertEquals(100f, overscrollDampingDistance(x = 4f, range = 300f))
        assertEquals(0f, overscrollDampingDistance(x = -1f, range = 300f))
        assertEquals(0f, overscrollDampingDistance(x = 0.5f, range = 0f))
    }

    @Test
    fun touchDistanceIsTheInverseOfDamping() {
        // 回弹被手指接管时要靠它把位移还原成等效触摸量，往返必须闭合。
        val range = 300f
        listOf(0.05f, 0.2f, 0.5f, 0.8f, 0.95f).forEach { x ->
            val offset = overscrollDampingDistance(x = x, range = range)
            assertClose(x * range, overscrollTouchDistance(offset = offset, range = range))
        }
        // 位移超过上限（只有数值误差才可能）时按满值处理。
        assertEquals(range, overscrollTouchDistance(offset = range, range = range))
        assertEquals(0f, overscrollTouchDistance(offset = 10f, range = 0f))
    }

    // ---- 效果 ----

    @Test
    fun firstDragFollowsDragScale() {
        // 起点位移 ≈ 手指位移 × dragScale：令牌语义（0.5 = 只带动一半）在曲线起点仍然成立。
        val effect = effect(maxPx = 200f, dragScale = 0.5f)
        effect.scroll(Offset(0f, 10f))
        assertClose(5f, effect.offsetY, tolerance = 0.1f)
        assertTrue(effect.isInProgress)
    }

    @Test
    fun dragKeepsConsumingEvenWhenSaturated() {
        // 拖拽必须始终算消费：否则手势会漏给父级容器（下拉刷新会抢）。
        val effect = effect(maxPx = 100f, dragScale = 1f)
        val consumed = effect.scroll(Offset(0f, 400f))
        assertEquals(400f, consumed.y)
        assertEquals(100f, effect.offsetY, absoluteTolerance = 0.01f)
    }

    @Test
    fun scrollableContentConsumesBeforeOverscrollStarts() {
        val effect = effect()
        val consumed = effect.applyToScroll(Offset(0f, 60f), NestedScrollSource.UserInput) {
            Offset(0f, 60f)
        }
        assertEquals(Offset(0f, 60f), consumed)
        assertFalse(effect.isInProgress)
    }

    @Test
    fun fractionalRemaindersDoNotAccumulateInTheMiddle() {
        val effect = effect()
        repeat(30) {
            for (source in listOf(NestedScrollSource.UserInput, NestedScrollSource.SideEffect)) {
                val consumed = effect.applyToScroll(Offset(0f, -20f), source) { Offset(0f, -19.75f) }
                assertEquals(-19.75f, consumed.y)
                assertFalse(effect.isInProgress)
            }
        }
    }

    @Test
    fun leftoversBecomeOverscroll() {
        // 内容只吃掉一半，剩下的一半原样算作被过冲消费（消耗量守恒）。
        val effect = effect()
        val consumed = effect.applyToScroll(Offset(0f, 60f), NestedScrollSource.UserInput) {
            Offset(0f, 30f)
        }
        assertEquals(Offset(0f, 60f), consumed)
        assertTrue(effect.offsetY > 0f)
    }

    @Test
    fun pullingBackShrinksOverscrollBeforeScrolling() {
        // 拉出过冲后往回拖：先把过冲收回去，内容原地不动。
        val effect = effect()
        effect.scroll(Offset(0f, 100f))
        val before = effect.offsetY

        val consumed = effect.applyToScroll(Offset(0f, -20f), NestedScrollSource.UserInput) {
            Offset.Zero
        }
        assertEquals(Offset(0f, -20f), consumed)
        assertTrue(effect.offsetY < before)
        // 收回去的部分按曲线放大：位移下降量小于 20px（因为位移是触摸量的压缩映射）。
        assertTrue(before - effect.offsetY < 20f)
    }

    @Test
    fun onlyActiveFlingCanBuildInertialOverscrollAndStopsNearSaturation() = runTest {
        val effect = effect(maxPx = 100f, dragScale = 1f)
        val delta = Offset(0f, 50f)
        assertEquals(Offset.Zero, effect.applyToScroll(delta, NestedScrollSource.SideEffect) { Offset.Zero })
        assertFalse(effect.isInProgress)
        effect.applyToFling(androidx.compose.ui.unit.Velocity(0f, 1000f)) {
            assertEquals(delta, effect.applyToScroll(delta, NestedScrollSource.SideEffect) { Offset.Zero })
            assertTrue(effect.isInProgress)
            assertEquals(Offset.Zero, effect.applyToScroll(Offset(0f, 250f), NestedScrollSource.SideEffect) { Offset.Zero })
            it
        }
    }

    @Test
    fun horizontalOverscrollIsLeftToParent() {
        // 只做纵向过冲：横向位移既不吸收也不记消费，原样留给父级容器（Pager 翻页等）。
        val effect = effect()
        val consumed = effect.applyToScroll(Offset(80f, 0f), NestedScrollSource.UserInput) {
            Offset.Zero
        }
        assertEquals(Offset.Zero, consumed)
        assertEquals(0f, effect.offsetY)
        assertFalse(effect.isInProgress)
    }

    @Test
    fun performScrollIsCalledExactlyOnce() {
        // OverscrollEffect 的契约要求每次 applyToScroll 恰好调用一次 performScroll，
        // 一次都不调内容就「不滚」，调两次内容会「多滚」。
        var calls = 0
        val effect = effect()
        effect.applyToScroll(Offset(0f, 10f), NestedScrollSource.UserInput) {
            calls++
            Offset(0f, 10f)
        }
        assertEquals(1, calls)
    }

    private fun effect(maxPx: Float = 200f, dragScale: Float = 1f): OxygenOverscrollEffect = OxygenOverscrollEffect(
        maxPx = maxPx,
        dragScale = dragScale,
        settle = spring(),
    )

    /** 内容完全滚不动（已经到顶 / 到底）时的拖拽。 */
    private fun OxygenOverscrollEffect.scroll(delta: Offset): Offset = applyToScroll(delta, NestedScrollSource.UserInput) { Offset.Zero }

    private fun assertClose(expected: Float, actual: Float, tolerance: Float = 0.5f) {
        assertTrue(
            abs(expected - actual) <= tolerance,
            "expected $expected ± $tolerance, actual $actual",
        )
    }
}
