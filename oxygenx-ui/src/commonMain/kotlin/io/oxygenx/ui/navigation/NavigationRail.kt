// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.IndicationDefaults

/**
 * 侧边导航条：竖排的窄栏（`size.rail.width`），放 3~7 个一级入口，用于平板 / 桌面。
 *
 * 与 [NavigationBar] 同一套语义：选中只换内容色、不铺底；按压反馈是标准水波纹。
 * 分块用 [NavigationRailItem]，[header] 槽用于放悬浮按钮或头像。
 *
 * - Spec ID: `CMP-NAVIGATION-RAIL`
 * - Baseline: `Material 3 navigation rail / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-NAVIGATION-RAIL/`
 * - Motion: 无（选中色直接切换，与底栏一致）。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param containerColor 底色，默认 `color.surface.container`。
 * @param header 顶部槽（悬浮按钮 / 头像），传 `null` 表示没有。
 * @param content 分块槽，按显示顺序放 [NavigationRailItem]。
 */
@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    containerColor: Color = OxygenTheme.colorScheme.surfaceContainer,
    header: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OxygenTheme.spacing
    Column(
        modifier = modifier
            .width(spacing.railWidth)
            .fillMaxHeight()
            .background(containerColor)
            .padding(vertical = spacing.gapSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.gapXs),
    ) {
        if (header != null) {
            header()
            Spacer(modifier = Modifier.height(spacing.gapSmall))
        }
        content()
    }
}

/**
 * 侧边导航条的分块：图标在上、文案在下，整块可点。
 *
 * - Spec ID: `CMP-NAVIGATION-RAIL`
 * - Baseline: `Material 3 navigation rail / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-NAVIGATION-RAIL/`
 * - Motion: 按压只有水波纹（可被 `LocalRippleEnabled` 关闭）。
 * - Status: `draft`
 *
 * @param selected 是否选中；只影响内容色。
 * @param onClick 点击回调。
 * @param icon 图标槽。
 * @param modifier 外部 Modifier。
 * @param label 文案槽，传 `null` 表示只有图标。
 * @param enabled 是否可用。
 */
@Composable
fun NavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = when {
        !enabled -> colors.onSurfaceDisabled
        selected -> colors.primary
        else -> colors.onSurfaceVariant
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = spacing.railItemHeight)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = spacing.gapXs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides OxygenTheme.typography.caption,
        ) {
            icon()
            if (label != null) {
                Spacer(modifier = Modifier.height(spacing.gapXs))
                label()
            }
        }
    }
}
