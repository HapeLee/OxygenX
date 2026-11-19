// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle

/**
 * 容器色对应的内容色；未知容器色继承当前内容色，与 M3 的主题级 contentColorFor 一致。
 * [OxygenColorScheme.contentColorFor] 是无主题回退的纯映射，两者按使用场景区分。
 */
@Composable
@ReadOnlyComposable
fun contentColorFor(backgroundColor: Color): Color = OxygenTheme.colorScheme.contentColorFor(backgroundColor).takeOrElse { OxygenTheme.contentColor }

/**
 * 组件槽位的统一入口：下发内容色，并把 [textStyle] 的显式属性合并进外层样式。
 * 只改颜色时保留外层排版；显式 TextStyle.color 按 Text 的颜色优先级继续生效。
 * 不修改全局主题，也不依赖 Material；混用 M3 的桥接在 oxygenx-material3。
 */
@Composable
fun ProvideContentStyle(
    contentColor: Color = OxygenTheme.contentColor,
    textStyle: TextStyle = TextStyle.Default,
    content: @Composable () -> Unit,
) {
    val resolvedColor = contentColor.takeOrElse { OxygenTheme.contentColor }
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides resolvedColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}
