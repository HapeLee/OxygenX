// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.IndicationDefaults

/**
 * 模态侧边抽屉：从起始边滑出、带遮罩，点遮罩 / 返回键关闭。
 *
 * 与 [NavigationRail] 的分工：抽屉承载「一级入口 + 账号 / 设置」这类可收起的导航，
 * 侧边条是常驻的窄栏。抽屉盖在内容之上，内容本身保持原样、不参与布局压缩。
 *
 * - Spec ID: `CMP-NAVIGATION-DRAWER`
 * - Baseline: `Material 3 modal navigation drawer / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-NAVIGATION-DRAWER/`
 * - Motion: `MOT-ENTER-001`（`spec.dialog.slide`），与弹窗同一套滑入滑出，可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param open 是否展开；由调用方持有。
 * @param onDismissRequest 点遮罩时回调，调用方负责把 [open] 置 false。
 * @param drawerContent 抽屉内容槽（列作用域），已下发内容色与 `type.body.medium`。
 * @param modifier 外部 Modifier，作用在整块区域上（通常直接给 `Scaffold` 的内容）。
 * @param motion 滑入 / 滑出的动效规格。
 * @param content 页面主体。
 */
@Composable
fun ModalNavigationDrawer(
    open: Boolean,
    onDismissRequest: () -> Unit,
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    motion: OxygenMotionSpec = OxygenMotionDefaults.dialogSlide(),
    content: @Composable () -> Unit,
) {
    val shown = rememberOxygenAnimation(spec = motion)
    var drawerWidthPx by remember { mutableIntStateOf(0) }
    LaunchedEffect(open) { shown.animateTo(if (open) 1f else 0f) }

    Box(modifier = modifier) {
        content()
        if (open || shown.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = shown.value }
                    .background(OxygenTheme.colorScheme.scrim)
                    .pointerInput(Unit) { detectTapGestures { onDismissRequest() } },
            )
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                // 窄屏时留出 56dp，让页面还能露出一点，暗示「可以点外面关掉」。
                val panelWidth = minOf(maxWidth, OxygenTheme.spacing.drawerWidth)
                CompositionLocalProvider(
                    LocalContentColor provides OxygenTheme.colorScheme.onSurface,
                    LocalTextStyle provides OxygenTheme.typography.bodyMedium,
                ) {
                    Column(
                        modifier = Modifier
                            .width(panelWidth)
                            .fillMaxHeight()
                            .background(OxygenTheme.colorScheme.surfaceContainer)
                            .onSizeChanged { drawerWidthPx = it.width }
                            .graphicsLayer {
                                translationX = -(1f - shown.value) * drawerWidthPx
                            }
                            .safeDrawingPadding()
                            .padding(vertical = OxygenTheme.spacing.gapSmall),
                        verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapXs),
                    ) {
                        drawerContent()
                    }
                }
            }
        }
    }
}

/**
 * 抽屉里的分块：图标 + 文案的整行，选中时垫一层 `secondaryContainer` 药丸。
 *
 * - Spec ID: `CMP-NAVIGATION-DRAWER`
 * - Baseline: `Material 3 modal navigation drawer / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-NAVIGATION-DRAWER/`
 * - Motion: 按压只有水波纹（可被 `LocalRippleEnabled` 关闭）。
 * - Status: `draft`
 *
 * @param selected 是否选中。
 * @param onClick 点击回调。
 * @param label 文案槽。
 * @param modifier 外部 Modifier。
 * @param icon 图标槽。
 * @param enabled 是否可用。
 */
@Composable
fun NavigationDrawerItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val containerColor = if (selected) colors.secondaryContainer else Color.Transparent
    val contentColor = when {
        !enabled -> colors.onSurfaceDisabled
        selected -> colors.onSecondaryContainer
        else -> colors.onSurface
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.gapSmall)
            .heightIn(min = spacing.menuItemHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.cardPadding),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            if (icon != null) {
                icon()
            }
            Box(modifier = Modifier.weight(1f)) { label() }
        }
    }
}
