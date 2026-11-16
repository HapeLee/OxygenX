// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.settings

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Checkbox
import io.oxygenx.ui.basic.CheckboxColors
import io.oxygenx.ui.basic.CheckboxDefaults
import io.oxygenx.ui.basic.RadioButton
import io.oxygenx.ui.basic.RadioButtonColors
import io.oxygenx.ui.basic.RadioButtonDefaults

/**
 * 单选设置项：整行可点，行尾是只做展示的单选框。
 *
 * - Spec ID: `CMP-SETTINGS-RADIO-ITEM`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / 杜比全景声 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-SETTINGS-RADIO-ITEM/`
 * - Status: `draft`
 *
 * @param selected 是否选中。
 * @param onClick 点击整行的回调。
 * @param title 主标题。
 * @param modifier 外部 Modifier。
 * @param summary 副标题，为空则不占位。
 * @param enabled 是否可用。
 */
@Composable
fun SettingsRadioItem(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    colors: SettingsColors = SettingsDefaults.colors(),
    radioColors: RadioButtonColors = RadioButtonDefaults.colors(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = SettingsDefaults.RowMinHeight)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(
                start = SettingsDefaults.ItemInset,
                end = SettingsDefaults.ItemInset,
                top = SettingsDefaults.VerticalPadding,
                bottom = SettingsDefaults.VerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
    ) {
        SettingsItemText(
            title = title,
            summary = summary,
            enabled = enabled,
            colors = colors,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = radioColors,
        )
    }
}

/**
 * 复选设置项：整行可点，行尾是只做展示的复选框。
 *
 * - Spec ID: `CMP-SETTINGS-CHECK-ITEM`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-14`
 * - Reference: `catalog/reference/CMP-SETTINGS-CHECK-ITEM/`
 * - Status: `draft`
 *
 * @param checked 是否勾选。
 * @param onCheckedChange 勾选变化回调。
 * @param title 主标题。
 * @param modifier 外部 Modifier。
 * @param summary 副标题，为空则不占位。
 * @param enabled 是否可用。
 */
@Composable
fun SettingsCheckItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    colors: SettingsColors = SettingsDefaults.colors(),
    checkboxColors: CheckboxColors = CheckboxDefaults.colors(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = SettingsDefaults.RowMinHeight)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = onCheckedChange,
            )
            .padding(
                start = SettingsDefaults.ItemInset,
                end = SettingsDefaults.ItemInset,
                top = SettingsDefaults.VerticalPadding,
                bottom = SettingsDefaults.VerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
    ) {
        SettingsItemText(
            title = title,
            summary = summary,
            enabled = enabled,
            colors = colors,
            modifier = Modifier.weight(1f),
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            colors = checkboxColors,
        )
    }
}
