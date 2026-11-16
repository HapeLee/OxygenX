// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.oxygenx.theme.OxygenTheme

/**
 * 设置分组标题：通栏列表里的灰色小标题，靠上方间距分组、不给行加分割线。
 *
 * - Spec ID: `CMP-SETTINGS-GROUP-TITLE`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-13`
 * - Reference: `catalog/reference/CMP-SETTINGS-GROUP-TITLE/`
 * - Motion: 无。
 * - Status: `draft`
 *
 * @param text 分组名，例如「连接」「WLAN 设置」。
 * @param modifier 外部 Modifier。
 */
@Composable
fun SettingsGroupTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    val spacing = OxygenTheme.spacing
    BasicText(
        text = text,
        modifier = modifier.padding(
            start = SettingsDefaults.ItemInset,
            end = SettingsDefaults.ItemInset,
            top = spacing.gapLarge,
            bottom = spacing.gapXs,
        ),
        style = OxygenTheme.typography.caption.copy(
            color = OxygenTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
