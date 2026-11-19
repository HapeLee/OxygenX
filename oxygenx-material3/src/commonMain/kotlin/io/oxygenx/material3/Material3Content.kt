// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.takeOrElse
import io.oxygenx.theme.OxygenTheme
import androidx.compose.material3.LocalContentColor as MaterialContentColor
import androidx.compose.material3.LocalTextStyle as MaterialTextStyle
import io.oxygenx.theme.LocalContentColor as OxygenContentColor
import io.oxygenx.theme.LocalTextStyle as OxygenTextStyle

/**
 * 在 OxygenTheme 内提供 Oxygen → M3 的内容色 / 文本样式桥接。
 *
 * 在页面或应用入口包一次即可：computed local 在读取位置求值，能跟随更深层 Oxygen
 * Button、顶栏、输入槽位的样式及禁用态，不会把根级正文颜色缓存给所有子节点。
 * M3 组件自己提供的更近层 LocalContentColor / LocalTextStyle 仍优先。
 *
 * 仅桥接内容样式，不生成 MaterialTheme，也不做双向递归绑定；宿主仍负责提供同源 M3 色板。
 * M3 槽位里的 Oxygen Text 不会因此反向继承 M3 样式。
 */
@Composable
fun ProvideMaterial3Content(content: @Composable () -> Unit) {
    val fallbackColor = OxygenTheme.colorScheme.onSurface
    CompositionLocalProvider(
        MaterialContentColor providesComputed { OxygenContentColor.currentValue.takeOrElse { fallbackColor } },
        MaterialTextStyle providesComputed { OxygenTextStyle.currentValue },
        content = content,
    )
}
