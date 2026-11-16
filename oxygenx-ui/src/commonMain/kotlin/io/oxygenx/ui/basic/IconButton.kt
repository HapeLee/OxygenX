// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme

/**
 * 图标按钮：48dp 触摸目标 + 24dp 图标 + 圆形水波纹，对应 M3 的 `IconButton`。
 *
 * 内容走 [content] 槽位：放一个 `Icon` 就是标准图标按钮，也可以换成加载圈、角标或
 * 双图标。槽位内已下发内容色（启用 `onSurface` / 禁用 `onSurfaceDisabled`），而 `Icon`
 * 的 `tint` 默认值就是内容色，所以 `Icon(OxygenIcons.Search, "搜索")` 不需要再传色；
 * 无障碍描述跟着 `Icon` 走，不再有「按钮参数 + 图标参数」两份描述。
 *
 * 只做基础形态（无 filled / tonal / outlined 变体，也不铺底），需要底色时用
 * `Surface(shape = CircleShape, onClick = …)` 包一层。
 *
 * - Spec ID: `CMP-ICON-BUTTON`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-ICON-BUTTON/`
 * - Motion: 无。触摸反馈只走水波纹（可被 [LocalRippleEnabled] 关闭）。
 * - Status: `draft`
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互。
 * @param contentColor 内容色覆盖；`Color.Unspecified` 时按启用态解析。
 * @param interactionSource 按压态来源；不传则内部自建。
 * @param content 图标内容槽。
 */
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = Color.Unspecified,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val colors = OxygenTheme.colorScheme
    val resolvedContentColor = when {
        contentColor.isSpecified -> contentColor
        enabled -> colors.onSurface
        else -> colors.onSurfaceDisabled
    }
    CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
        Box(
            modifier = modifier
                .size(OxygenTheme.spacing.touchTargetMin)
                .clip(CircleShape)
                .clickable(
                    interactionSource = source,
                    indication = IndicationDefaults.ripple(),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
