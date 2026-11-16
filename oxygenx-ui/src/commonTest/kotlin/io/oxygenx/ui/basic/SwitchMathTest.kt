// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SwitchMathTest {
    // 真机令牌：轨道 34×17dp、拇指 17dp（轨道高度与拇指等值）。
    private val trackWidth = 34.dp
    private val trackHeight = 17.dp
    private val thumbSize = 17.dp

    @Test
    fun thumbTravelEqualsTrackMinusThumb() {
        assertEquals(17.dp, switchThumbTravel(trackWidth, trackHeight, thumbSize))
    }

    @Test
    fun thumbTravelSubtractsBothInsetsWhenThumbIsSmaller() {
        // 拇指比轨道矮 4dp → 两侧各留 2dp，行程仍是 17dp。
        assertEquals(17.dp, switchThumbTravel(trackWidth, trackHeight, thumbSize = 13.dp))
    }

    @Test
    fun thumbTravelNeverGoesNegative() {
        assertEquals(0.dp, switchThumbTravel(trackWidth, trackHeight, thumbSize = 40.dp))
    }

    @Test
    fun centerOffsetIsSymmetricAroundTrackCenter() {
        val off = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 0f)
        val on = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 1f)
        assertEquals(-8.5.dp, off)
        assertEquals(8.5.dp, on)
        assertEquals(0.dp, off + on)
    }

    @Test
    fun centerOffsetMatchesTravelDistance() {
        val off = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 0f)
        val on = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 1f)
        assertEquals(switchThumbTravel(trackWidth, trackHeight, thumbSize), on - off)
    }

    @Test
    fun centerOffsetClampsProgress() {
        val on = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 1f)
        val beyond = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 3f)
        val below = switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = -2f)
        assertEquals(on, beyond)
        assertEquals(switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, 0f), below)
    }

    @Test
    fun centerOffsetKeepsThumbInsideTrackWhenThumbIsSmaller() {
        val smaller = 13.dp
        val off = switchThumbCenterOffset(trackWidth, trackHeight, smaller, progress = 0f)
        // 拇指左缘 = 圆心 − 半径 = 轨道左缘 + 2dp 内边距。
        assertEquals(-trackWidth / 2f + 2.dp, off - smaller / 2f)
    }
}
