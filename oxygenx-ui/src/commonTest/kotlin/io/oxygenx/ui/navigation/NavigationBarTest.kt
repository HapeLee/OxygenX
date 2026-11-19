// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationBarTest {
    @Test
    fun keepsIndexInsideRange() {
        // 三栏、选中第 2 栏：原样保留，不会把有效下标也收敛掉。
        assertEquals(1, navigationBarSafeIndex(index = 1, count = 3))
    }

    @Test
    fun clampsNegativeIndexToFirstTab() {
        // 路由还没对齐时会传 -1（`list.indexOf` 找不到就返回 -1）：落到第一栏。
        assertEquals(0, navigationBarSafeIndex(index = -1, count = 3))
    }

    @Test
    fun clampsOverflowIndexToLastTab() {
        // 分栏数量变少（例如权限收起了一栏）时旧下标可能越界：落到最后一栏。
        assertEquals(2, navigationBarSafeIndex(index = 9, count = 3))
    }

    @Test
    fun degradesToZeroWithoutItems() {
        // 分栏还没准备好：返回 0，组件内部只画分割线与空行，不高亮任何栏。
        assertEquals(0, navigationBarSafeIndex(index = 5, count = 0))
    }
}
