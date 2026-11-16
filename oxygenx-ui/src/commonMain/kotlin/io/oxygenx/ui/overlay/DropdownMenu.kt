// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.CardDefaults
import io.oxygenx.ui.basic.IndicationDefaults
import io.oxygenx.ui.basic.Surface

/**
 * 下拉菜单：贴着锚点下方弹出的浮层，点外部 / 返回键关闭。
 *
 * 用法是把 [DropdownMenu] 放在锚点所在的 `Box` 里——它用 `Popup` 相对**父布局**定位，
 * 所以父 `Box` 就是它对齐的锚点（与 M3 同口径）：
 *
 * ```kotlin
 * Box {
 *     Button(onClick = { expanded = true }) { Text("排序") }
 *     DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
 *         DropdownMenuItem(text = { Text("按名称") }, onClick = { … })
 *     }
 * }
 * ```
 *
 * 下方空间不够时自动翻到锚点上方，并且始终夹在窗口内。
 *
 * - Spec ID: `CMP-DROPDOWN-MENU`
 * - Baseline: `Material 3 dropdown menu / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-DROPDOWN-MENU/`
 * - Motion: 无（弹出不做缩放 / 淡入，与项目「只有水波纹」的取向一致）。
 * - Status: `draft`
 *
 * @param expanded 是否展开；由调用方持有。
 * @param onDismissRequest 点外部或返回键时回调，调用方负责把 [expanded] 置 false。
 * @param modifier 外部 Modifier，作用在菜单面板上。
 * @param offset 相对锚点左下角的偏移，默认紧贴锚点下沿。
 * @param content 菜单项槽，按显示顺序放 [DropdownMenuItem]。
 */
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!expanded) return
    val density = LocalDensity.current
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    Popup(
        popupPositionProvider = remember(density, offset) { DropdownMenuPositionProvider(density, offset) },
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            modifier = modifier,
            color = colors.surfaceContainer,
            shape = OxygenTheme.shapes.cornerCard,
            border = CardDefaults.border(),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides colors.onSurface,
                LocalTextStyle provides OxygenTheme.typography.listItemTitle,
            ) {
                // IntrinsicSize.Max + item.fillMaxWidth：菜单宽度取最宽项，短项也整行可点。
                // 不能直接给 item 用 fillMaxWidth：Popup 的约束是窗口大小，会把菜单撑满屏。
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(vertical = spacing.menuPaddingVertical),
                ) {
                    // 最窄也要有 size.menu.min.width，否则单字菜单会缩成一小块。
                    Spacer(modifier = Modifier.width(spacing.menuMinWidth))
                    content()
                }
            }
        }
    }
}

/**
 * 菜单项：整行可点，左侧可放图标，右侧可放勾选 / 快捷键提示。
 *
 * - Spec ID: `CMP-DROPDOWN-MENU`
 * - Baseline: `Material 3 dropdown menu / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-DROPDOWN-MENU/`
 * - Motion: 按压只有水波纹（可被 `LocalRippleEnabled` 关闭）。
 * - Status: `draft`
 *
 * @param text 文案槽（已下发 `type.list.item.title` 与内容色）。
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param leadingIcon 左侧图标槽。
 * @param trailingIcon 右侧图标槽。
 * @param enabled 是否可用。
 */
@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val iconColor = if (enabled) colors.onSurfaceVariant else colors.onSurfaceDisabled
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = spacing.menuItemHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.menuItemPaddingHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
    ) {
        if (leadingIcon != null) {
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                leadingIcon()
            }
        }
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) colors.onSurface else colors.onSurfaceDisabled,
        ) {
            Column(modifier = Modifier.weight(1f)) { text() }
        }
        if (trailingIcon != null) {
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                trailingIcon()
            }
        }
    }
}

/**
 * 菜单定位：默认贴锚点下沿，下方放不下就翻到上方，横向夹在窗口内。
 *
 * `anchorBounds` 由 `Popup` 传入（即 [DropdownMenu] 的父布局），所以调用方把菜单
 * 放进锚点所在的 `Box` 即可，不需要额外传坐标。
 */
private class DropdownMenuPositionProvider(
    private val density: Density,
    private val offset: DpOffset,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left + with(density) { offset.x.roundToPx() }
        val yBelow = anchorBounds.bottom + with(density) { offset.y.roundToPx() }
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val maxY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val y = if (yBelow <= maxY) {
            yBelow
        } else {
            (anchorBounds.top - popupContentSize.height).coerceIn(0, maxY)
        }
        return IntOffset(x.coerceIn(0, maxX), y)
    }
}
