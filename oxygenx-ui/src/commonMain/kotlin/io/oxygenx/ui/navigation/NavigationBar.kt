// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.IndicationDefaults

/**
 * 底栏导航：等分分栏、图标在上文字在下、顶部一条 1dp 细分割线，选中态取 `color.primary`。
 *
 * 形态取自一加文件管理这类「一级入口放底栏」的应用：55dp 内容高度（`size.bottom.bar.height`，
 * 与时钟应用底栏同一份真机基线）、每栏等分整行宽度、图标 24dp 压一行 `caption` 文案，
 * 分割线与顶栏用的是同一条 `color.outline.variant` 细线，因此上下两条栏同属一套细线语言。
 * 与 [TabRow] 的分工：页签在内容区内切换分页（指示器是内容体的一部分），
 * 底栏是页面外壳的一级导航（由 `Scaffold` 的 `bottomBar` 槽承载）。
 *
 * 选中态只改内容颜色（选中栏 `primary`、其余 `onSurfaceVariant`），不铺底色；
 * 按压反馈是一个**完整的圆**：`bounded = false` 的波纹挂在整栏上、半径取栏高，直径是栏高
 * 的两倍，所以圆会溢出这条底栏，溢出部分被整行的 `clipToBounds()` 切掉——一加文件管理里
 * 按下分栏时看到的那块大弧形高亮就是这个圆被底栏边缘裁掉后的样子。
 * 选中色与未选中色之间走 [motion]，与开关、页签同一条状态弹簧。
 *
 * - Spec ID: `CMP-BOTTOM-BAR`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Clock 5.3.0 / 2026-11-13`（高度令牌）
 * - Reference: `catalog/reference/README.md`
 * - Motion: `MOT-STATE-002`（选中色插值）。可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param items 分栏，按显示顺序排列；每栏至少要有文案与图标。
 * @param selectedIndex 当前选中下标；越界时收敛到有效范围。
 * @param onSelect 点击可用的分栏时回调目标下标。
 * @param modifier 外部 Modifier；底栏通常由 `Scaffold` 的 `bottomBar` 槽提供，
 *   底部安全区由 `Scaffold` 统一让开，这里不重复加内边距。
 * @param colors 容器、分割线与文字图标颜色集合。
 * @param motion 选中色过渡的动效规格。
 */
@Composable
fun NavigationBar(
    items: List<NavigationBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: NavigationBarColors = NavigationBarDefaults.colors(),
    motion: OxygenMotionSpec = NavigationBarDefaults.Motion,
) {
    if (items.isEmpty()) return
    val spacing = OxygenTheme.spacing
    val safeIndex = bottomBarSafeIndex(selectedIndex, items.size)

    Column(modifier = modifier.fillMaxWidth().background(colors.containerColor)) {
        // 与顶栏同一条细线；画在背景之上、内容之前，滚动内容不会盖住它。
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.dividerThickness)
                .background(colors.dividerColor),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.bottomBarHeight)
                // 按压水波纹是「半径 = 栏高」的大圆，直径超过栏高，上下会溢出这条栏；
                // 这里把整行裁住，溢出的部分自然被切掉（真机上看起来就是大圆压到屏幕边缘）。
                .clipToBounds(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                BottomBarTab(
                    label = item.label,
                    icon = item.icon,
                    selected = index == safeIndex,
                    enabled = item.enabled,
                    onClick = { onSelect(index) },
                    colors = colors,
                    motion = motion,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomBarTab(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    colors: NavigationBarColors,
    motion: OxygenMotionSpec,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val progress = rememberOxygenAnimation(spec = motion, initialValue = if (selected) 1f else 0f)
    LaunchedEffect(selected) { progress.animateTo(if (selected) 1f else 0f) }
    val contentColor = if (enabled) {
        lerp(colors.unselectedContent, colors.selectedContent, progress.value)
    } else {
        colors.disabledContent
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                // 波纹不挂在 selectable 上：这里的默认水波纹会被条目边界裁成矩形。
                indication = null,
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            // 点击反馈是**一个完整的圆**：unbounded 让波纹不受条目尺寸限制，
            // 半径取栏高，直径是栏高的两倍，所以圆的下半截会溢出整条底栏；
            // 溢出部分由上一层的 `clipToBounds()` 切掉。选中态只改内容颜色，不铺底。
            .indication(
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(
                    bounded = false,
                    radius = OxygenTheme.spacing.bottomBarHeight,
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor)
        Spacer(modifier = Modifier.height(OxygenTheme.spacing.gapXs))
        BasicText(
            text = label,
            style = OxygenTheme.typography.caption.copy(color = contentColor),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * 把外部传入的选中下标收敛到 `0..count-1`；[count] 非正时返回 0。
 *
 * 抽成纯函数便于单测：调用方常把「当前路由」直接当下标传进来，切换页面期间可能出现
 * 越界值，组件本身不应该因此崩掉或整行都不高亮。
 */
internal fun bottomBarSafeIndex(index: Int, count: Int): Int {
    if (count <= 0) return 0
    return index.coerceIn(0, count - 1)
}

object NavigationBarDefaults {
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.bottomBarHeight

    val DividerHeight: Dp
        @Composable get() = OxygenTheme.spacing.dividerThickness

    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.switchToggle()

    @Composable
    fun colors(
        containerColor: Color = OxygenTheme.colorScheme.surfaceContainer,
        dividerColor: Color = OxygenTheme.colorScheme.outlineVariant,
        selectedContent: Color = OxygenTheme.colorScheme.primary,
        unselectedContent: Color = OxygenTheme.colorScheme.onSurfaceVariant,
        disabledContent: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): NavigationBarColors = remember(
        containerColor,
        dividerColor,
        selectedContent,
        unselectedContent,
        disabledContent,
    ) {
        NavigationBarColors(
            containerColor = containerColor,
            dividerColor = dividerColor,
            selectedContent = selectedContent,
            unselectedContent = unselectedContent,
            disabledContent = disabledContent,
        )
    }
}

/**
 * 底栏配色：[selectedContent] 是选中栏的图标与文字，[dividerColor] 是顶部分割线。
 *
 * 选中态只靠内容色区分（不铺底）：底栏的深浅由容器色决定，按压反馈是覆盖整栏的大圆
 * 水波纹，两者叠在同一块背景上会互相干扰。
 */
@Immutable
data class NavigationBarColors(
    val containerColor: Color,
    val dividerColor: Color,
    val selectedContent: Color,
    val unselectedContent: Color,
    val disabledContent: Color,
)

/**
 * 底栏的一个分栏。
 *
 * @param label 文案，单行省略。
 * @param icon 图标，视觉尺寸固定为 `icon.size.medium`（24dp）。
 * @param enabled 为 `false` 时不可点击、取 `disabledContent` 色。
 */
@Immutable
data class NavigationBarItem(
    val label: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
)
