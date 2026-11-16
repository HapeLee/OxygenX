// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
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
 * 「点击 + 开关」合并设置项：竖分割线左侧点击进入二级页，右侧开关切换本体功能。
 *
 * 一加设置页的 WLAN / 蓝牙行就是这个形态：点标题进 WLAN 页面，点右侧开关开关 WLAN，
 * 两者之间是一条与开关同高的竖分割线；左右是两个独立的点击区域，互不触发。
 *
 * - Spec ID: `CMP-SETTINGS-SPLIT`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-13`
 * - Reference: `catalog/reference/CMP-SETTINGS-SPLIT/`
 * - Motion: `MOT-STATE-002`（开关拇指位移与拇指处波纹），左侧点击区走统一水波纹。
 * - Status: `draft`
 *
 * @param onClick 左侧点击区回调（进入二级页）；`enabled = false` 时不触发。
 * @param onCheckedChange 右侧开关的切换回调。
 * @param title 行标题。
 * @param checked 开关当前是否开启；受控状态由调用方持有。
 * @param modifier 外部 Modifier。
 * @param summary 副标题。
 * @param enabled 是否可交互；禁用时两侧都不响应。
 * @param colors 文字与分割线颜色集合。
 * @param switchColors 右侧开关颜色集合。
 * @param motion 右侧开关的动效规格。
 */
@Composable
fun SettingsSplitItem(
    onClick: () -> Unit,
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = SettingsDefaults.RowMinHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = interactionSource,
                    indication = IndicationDefaults.ripple(),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(
                    start = SettingsDefaults.ItemInset,
                    end = SettingsDefaults.DividerGap,
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
        }
        // 竖分割线：只分隔两个点击区，不贯穿整行。
        Box(
            modifier = Modifier
                .width(SettingsDefaults.DividerThickness)
                .height(SettingsDefaults.DividerHeight)
                .background(colors.resolveDividerColor(enabled)),
        )
        Box(
            modifier = Modifier
                .padding(start = SettingsDefaults.DividerGap, end = SettingsDefaults.SwitchInset)
                .size(SettingsDefaults.SwitchCell),
            contentAlignment = Alignment.Center,
        ) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = switchColors,
                motion = motion,
            )
        }
    }
}
