// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.settings

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.IndicationDefaults
import io.oxygenx.ui.basic.Switch
import io.oxygenx.ui.basic.SwitchColors
import io.oxygenx.ui.basic.SwitchDefaults

/**
 * 开关设置项：整行可点击切换，行尾是纯展示开关（不额外进入语义树）。
 *
 * 整行只有一个 `Role.Switch` 节点，点击文字或开关任意位置都切换；
 * 需要「点左边进二级页、点右边开关切换」的形态用 `SettingsSplitItem`。
 *
 * - Spec ID: `CMP-SETTINGS-SWITCH`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-13`
 * - Reference: `catalog/reference/CMP-SETTINGS-SWITCH/`
 * - Motion: `MOT-STATE-002`（开关拇指位移），按压反馈走统一水波纹。
 * - Status: `draft`
 *
 * @param onCheckedChange 请求切换时回调；`enabled = false` 时不触发。
 * @param title 行标题。
 * @param checked 当前是否开启；受控状态由调用方持有。
 * @param modifier 外部 Modifier。
 * @param summary 副标题。
 * @param enabled 是否可交互；禁用时文字与开关都走禁用色。
 * @param colors 文字与分割线颜色集合。
 * @param switchColors 行尾开关颜色集合。
 * @param motion 行尾开关的拇指位移动效规格。
 */
@Composable
fun SettingsSwitchItem(
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    colors: SettingsColors = SettingsDefaults.colors(),
    switchColors: SwitchColors = SwitchDefaults.colors(),
    motion: OxygenMotionSpec = SettingsDefaults.Motion,
) {
    val spacing = OxygenTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = SettingsDefaults.RowMinHeight)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(
                start = SettingsDefaults.ItemInset,
                end = SettingsDefaults.SwitchInset,
                top = SettingsDefaults.VerticalPadding,
                bottom = SettingsDefaults.VerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
    ) {
        SettingsItemText(
            title = title,
            summary = summary,
            enabled = enabled,
            colors = colors,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier.size(SettingsDefaults.SwitchCell),
            contentAlignment = Alignment.Center,
        ) {
            Switch(
                checked = checked,
                // 整行是一个开关节点，开关本身只做展示（不进语义树）。
                onCheckedChange = null,
                enabled = enabled,
                // 按压状态从行传给开关，波纹才知道该在哪里亮。
                pressed = pressed,
                colors = switchColors,
                motion = motion,
            )
        }
    }
}
