// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme

/**
 * 组件目录的统一页面边距——catalog 里页面级的左右上下边距只在这里定义一次，
 * 演示页不再各自 `padding`，值全部来自 design token、与真机设置界面对齐：
 *
 * - 左右 [horizontal]：`space.gutter`（16dp，设置界面分组容器的边距）。
 *   通栏页（[fullBleed]）为 0：行延伸到屏幕左右，文字离屏幕的距离由组件自身的
 *   `space.list.item.inset`（20dp）决定。
 * - 顶部 [top]：`space.list.top`（9dp，真机列表页顶）。
 * - 底部 [bottom]：`space.gutter`（16dp），导航条内距由调用方通过
 *   [contentPadding] 传入。
 * - 分块之间 [blockGap]：`space.list.group.gap`（21dp）。
 */
data class CatalogPageSpacing(
    val horizontal: Dp,
    val top: Dp,
    val bottom: Dp,
    val blockGap: Dp,
) {
    /** 组装成 LazyColumn / Column 用的完整 [PaddingValues]；[bottomInset] 是导航条等安全区。 */
    fun contentPadding(bottomInset: Dp = 0.dp): PaddingValues = PaddingValues(
        start = horizontal,
        end = horizontal,
        top = top,
        bottom = bottom + bottomInset,
    )
}

/** 取统一页面边距；[fullBleed] = true 时左右为 0（通栏列表页）。 */
@Composable
fun catalogPageSpacing(fullBleed: Boolean = false): CatalogPageSpacing = CatalogPageSpacing(
    horizontal = if (fullBleed) 0.dp else OxygenTheme.spacing.gutter,
    top = OxygenTheme.spacing.listTopInset,
    bottom = OxygenTheme.spacing.gutter,
    blockGap = OxygenTheme.spacing.listGroupGap,
)
