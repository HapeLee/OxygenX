// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class TabRowMathTest {
    @Test
    fun keepsScrollWhenTabFullyVisible() {
        // 视口 [0,720]，页签 [200,320] 完整可见：不产生多余滚动。
        assertEquals(0, tabScrollTarget(tabLeft = 200, tabWidth = 120, viewport = 720, current = 0))
    }

    @Test
    fun scrollsBackToTabOnTheLeft() {
        // 视口从 400 起，页签在 200：回到页签左边界。
        assertEquals(200, tabScrollTarget(tabLeft = 200, tabWidth = 120, viewport = 720, current = 400))
    }

    @Test
    fun scrollsForwardToRevealTabOnTheRight() {
        // 视口 [0,720]，页签 [700,820]：右移 100 让页签贴住右边界。
        assertEquals(100, tabScrollTarget(tabLeft = 700, tabWidth = 120, viewport = 720, current = 0))
    }

    @Test
    fun ignoresUnknownViewport() {
        // 首次布局时视口还没测出来：保持当前滚动位置，等下一帧再处理。
        assertEquals(48, tabScrollTarget(tabLeft = 700, tabWidth = 120, viewport = 0, current = 48))
    }
}
