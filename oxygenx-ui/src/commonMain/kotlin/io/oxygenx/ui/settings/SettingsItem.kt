// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.IndicationDefaults

/**
 * 可点击设置项：标题 + 可选副标题 / 行尾数值 / 行尾内容 / 导航箭头。
 *
 * 一加设置页的行没有横向分割线，行与行直接相接；进二级页的行在行尾放
 * [SettingsItemChevron]，只显示数值的行用 [value]。
 *
 * - Spec ID: `CMP-SETTINGS-ITEM`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-13`
 * - Reference: `catalog/reference/CMP-SETTINGS-ITEM/`
 * - Motion: 无位移动效，按压反馈走统一水波纹（`color.state.pressed`）。
 * - Status: `draft`
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param title 行标题，单行显示。
 * @param modifier 外部 Modifier。
 * @param summary 副标题，长文本按可用宽度换行。
 * @param value 行尾数值文案，例如「82%」「120Hz」。
 * @param leading 行首内容，例如图标徽标。
 * @param trailing 行尾自定义内容，排在 [value] 与箭头之前。
 * @param enabled 是否可交互；禁用时文字走 `color.on.surface.disabled` 且不响应点击。
 * @param showChevron 是否显示行尾箭头（进入二级页的行）。
 * @param colors 文字与分割线颜色集合。
 */
@Composable
fun SettingsItem(
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    value: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    showChevron: Boolean = false,
    colors: SettingsColors = SettingsDefaults.colors(),
) {
    val spacing = OxygenTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = SettingsDefaults.RowMinHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                start = SettingsDefaults.ItemInset,
                end = SettingsDefaults.ItemInset,
                top = SettingsDefaults.VerticalPadding,
                bottom = SettingsDefaults.VerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
    ) {
        leading?.invoke()
        SettingsItemText(
            title = title,
            summary = summary,
            enabled = enabled,
            colors = colors,
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            BasicText(
                text = value,
                style = OxygenTheme.typography.bodyMedium.copy(
                    color = colors.resolveValueColor(enabled),
                ),
            )
        }
        trailing?.invoke()
        if (showChevron) {
            SettingsItemChevron(enabled = enabled, colors = colors)
        }
    }
}
