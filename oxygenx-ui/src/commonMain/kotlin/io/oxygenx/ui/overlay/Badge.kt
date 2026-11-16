// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text

/**
 * 角标：贴在图标右上角的计数或小圆点。
 *
 * 单独使用时用 [Badge]（只有小圆点）或带文案的 `Badge { Text("3") }`；
 * 要挂到图标 / 页签图标上时用 [BadgedBox]，它负责偏移与裁切范围。
 *
 * 角标本身**不进语义树**：数量应该由宿主（导航项 / 页签）的语义描述承担，
 * 否则读屏会先念一个「3」再念标签，语义重复。
 *
 * - Spec ID: `CMP-BADGE`
 * - Baseline: `Material 3 badge / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-BADGE/`
 * - Motion: 无（数量变化不做过渡，避免数字滚动带来额外噪音）。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param containerColor 角标底色，默认 `color.primary`。
 * @param contentColor 角标内容色，默认 `color.onPrimary`。
 * @param content 角标内容槽；不给时画 [BadgeDefaults.DotSize] 的小圆点。
 */
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    containerColor: Color = OxygenTheme.colorScheme.primary,
    contentColor: Color = OxygenTheme.colorScheme.onPrimary,
    content: (@Composable () -> Unit)? = null,
) {
    val spacing = OxygenTheme.spacing
    val badgeModifier = if (content == null) {
        modifier
            .clip(CircleShape)
            .background(containerColor)
            .clearAndSetSemantics {}
            .defaultMinSize(
                minWidth = BadgeDefaults.DotSize,
                minHeight = BadgeDefaults.DotSize,
            )
    } else {
        modifier
            .clip(CircleShape)
            .background(containerColor)
            .clearAndSetSemantics {}
            .defaultMinSize(
                minWidth = spacing.badgeHeight,
                minHeight = spacing.badgeHeight,
            )
            .padding(horizontal = spacing.badgePaddingHorizontal)
    }
    Box(modifier = badgeModifier, contentAlignment = Alignment.Center) {
        if (content != null) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
    }
}

/**
 * 把角标挂到内容右上角：内容尺寸不变，角标压在右上、可溢出内容边界。
 *
 * ```kotlin
 * BadgedBox(badge = { Badge { Text("12", maxLines = 1) } }) {
 *     Icon(OxygenIcons.Settings, "设置")
 * }
 * ```
 *
 * - Spec ID: `CMP-BADGE`
 * - Baseline: `Material 3 badge / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-BADGE/`
 * - Motion: 无。
 * - Status: `draft`
 *
 * @param badge 角标槽，通常是一个 [Badge]；传 `null` 表示不显示。
 * @param modifier 外部 Modifier。
 * @param content 被装饰的内容（图标 / 页签文案）。
 */
@Composable
fun BadgedBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                // 角标中心压在内容右上角上：向右上各溢出一半，与 M3 同口径。
                .offset(x = BadgeDefaults.Offset, y = -BadgeDefaults.Offset),
        ) {
            badge()
        }
    }
}

object BadgeDefaults {
    /** 无内容时的小圆点直径（`size.badge.dot`）。 */
    val DotSize: Dp
        @Composable get() = OxygenTheme.spacing.badgeDot

    /** 带内容时的角标高度 / 最小宽度（`size.badge.height`）。 */
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.badgeHeight

    /** 角标相对内容右上角的外溢量。 */
    val Offset: Dp
        @Composable get() = OxygenTheme.spacing.badgeDot + OxygenTheme.spacing.badgePaddingHorizontal
}
