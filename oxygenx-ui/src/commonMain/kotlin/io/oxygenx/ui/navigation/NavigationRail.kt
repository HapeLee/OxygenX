// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.ui.basic.IndicationDefaults

/**
 * 侧边导航条：竖排的窄栏（`size.rail.width`），放 3~7 个一级入口，用于平板 / 桌面。
 *
 * 与 [NavigationBar] 同一套语义：选中只换内容色、不铺底；按压反馈也走同一条规则——半径
 * 等于分块高度的**完整大圆**，直径是分块高的两倍，横向溢出栏宽的两截被整条栏裁平。
 * 分块用 [NavigationRailItem]，[header] 槽用于放悬浮按钮或头像。
 *
 * - Spec ID: `CMP-NAVIGATION-RAIL`
 * - Baseline: `Material 3 navigation rail / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-NAVIGATION-RAIL/`
 * - Motion: `MOT-STATE-002`（选中色插值），与底栏同一条状态弹簧。
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
            // 分块的按压水波纹是「半径 = 分块高」的大圆，直径远大于 80dp 栏宽，左右必然溢出；
            // 这里把整条栏裁住，溢出的两截自然被切平（底栏是同一个做法，只是裁的方向不同）。
            .clipToBounds()
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
 * - Motion: 按压水波纹（半径 = `size.rail.item.height`，与底栏「半径 = 栏高」同一口径，
 *   可被 `LocalRippleEnabled` 关闭）+ `MOT-STATE-002` 选中色插值，可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param selected 是否选中；只影响内容色。
 * @param onClick 点击回调。
 * @param icon 图标槽。
 * @param modifier 外部 Modifier。
 * @param label 文案槽，传 `null` 表示只有图标。
 * @param enabled 是否可用。
 * @param motion 选中色过渡的动效规格。
 */
@Composable
fun NavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    motion: OxygenMotionSpec = NavigationRailDefaults.Motion,
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val progress = rememberOxygenAnimation(spec = motion, initialValue = if (selected) 1f else 0f)
    LaunchedEffect(selected) { progress.animateTo(if (selected) 1f else 0f) }
    val contentColor = if (enabled) {
        lerp(colors.onSurfaceVariant, colors.primary, progress.value)
    } else {
        colors.onSurfaceDisabled
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = spacing.railItemHeight)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                // 波纹不挂在 selectable 上：默认水波纹会被分块矩形裁成方块。
                indication = null,
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            // 与底栏同一条规则：unbounded 的圆、半径取分块高，直径是分块高的两倍，
            // 溢出部分由整条栏的 `clipToBounds()` 切掉。
            .indication(
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(
                    bounded = false,
                    radius = spacing.railItemHeight,
                ),
            )
            .padding(vertical = spacing.gapXs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ProvideContentStyle(
            contentColor = contentColor,
            textStyle = OxygenTheme.typography.labelMedium,
        ) {
            icon()
            if (label != null) {
                Spacer(modifier = Modifier.height(spacing.gapXs))
                label()
            }
        }
    }
}

object NavigationRailDefaults {
    /** 选中色过渡用的状态弹簧，与 `NavigationBar` 同一条（`MOT-STATE-002`）。 */
    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.switchToggle()
}
