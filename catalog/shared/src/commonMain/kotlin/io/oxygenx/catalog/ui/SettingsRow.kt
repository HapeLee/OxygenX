// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.OxygenTheme

/**
 * 卡片内的紧凑演示行：标题 + 可选副标题 + 可选行尾内容。属于 catalog 演示小组件。
 *
 * 整页设置列表已经改用库里的 `SettingsItem` / `SettingsSwitchItem`，
 * 这里只服务于「把若干行塞进一张卡片」的组件演示。
 */
@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingText: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    role: Role? = null,
) {
    val colors = OxygenTheme.colorScheme
    val spacing = OxygenTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = role,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .defaultMinSize(minHeight = spacing.listRowMinHeight)
            .padding(horizontal = spacing.listItemInset, vertical = spacing.gapSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = title,
                style = OxygenTheme.typography.listItemTitle.copy(color = colors.onSurface),
            )
            if (subtitle != null) {
                BasicText(
                    text = subtitle,
                    style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurfaceVariant),
                )
            }
        }
        if (trailingText != null) {
            BasicText(
                text = trailingText,
                style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurfaceVariant),
            )
        }
        trailing?.invoke()
    }
}

/** 设置卡片内的通栏分割线，左侧留出与文字对齐的内缩。 */
@Composable
fun SettingsDivider(
    modifier: Modifier = Modifier,
    startInset: Dp = OxygenTheme.spacing.cardPadding,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = startInset)
            .height(OxygenTheme.spacing.dividerThickness)
            .background(OxygenTheme.colorScheme.outlineVariant),
    )
}
