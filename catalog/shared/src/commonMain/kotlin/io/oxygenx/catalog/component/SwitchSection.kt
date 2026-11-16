// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Switch
import io.oxygenx.ui.settings.SettingsSwitchItem

fun LazyListScope.switchSection() {
    item(key = "cmp-switch") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            SmallTitle(text = "Switch / 开关", specId = "CMP-SWITCH")
            SwitchVariants()
        }
    }
}

@Composable
private fun SwitchVariants() {
    var checked by remember { mutableStateOf(true) }
    var unchecked by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsSwitchItem(
            onCheckedChange = { checked = it },
            title = "受控开关",
            summary = "拇指位移走 MOT-STATE-002",
            checked = checked,
        )
        SettingsSwitchItem(
            onCheckedChange = { unchecked = it },
            title = "默认关闭",
            checked = unchecked,
        )
        SettingsSwitchItem(
            onCheckedChange = {},
            title = "禁用态",
            summary = "保留语义，但不响应点击",
            checked = true,
            enabled = false,
        )
        BareSwitchRow(checked = checked)
        BareSwitchWithThumb()
        BasicText(
            text = "按压时波纹以拇指为圆心扩散（直径约等于轨道宽度，可溢出轨道），" +
                "而不是铺满 48dp 触摸盒；抬起后淡出。",
            modifier = Modifier.fillMaxWidth(),
            style = OxygenTheme.typography.caption.copy(
                color = OxygenTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

/** 拇指内容槽：M3 `thumbContent` 的形态，内容色由开关按状态下发。 */
@Composable
private fun BareSwitchWithThumb() {
    var local by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OxygenTheme.spacing.gapSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
    ) {
        BasicText(
            text = "thumbContent（勾 / 叉）",
            modifier = Modifier.weight(1f),
            style = OxygenTheme.typography.listItemTitle.copy(
                color = OxygenTheme.colorScheme.onSurface,
            ),
        )
        Switch(
            checked = local,
            onCheckedChange = { local = it },
            thumbContent = {
                Icon(
                    imageVector = if (local) OxygenIcons.Check else OxygenIcons.Close,
                    contentDescription = null,
                    size = OxygenTheme.spacing.switchThumbSize * 0.7f,
                )
            },
        )
    }
}

/** 不带设置行的裸开关：方便单独观察拇指位移与波纹。 */
@Composable
private fun BareSwitchRow(checked: Boolean) {
    var local by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OxygenTheme.spacing.gapSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
    ) {
        BasicText(
            text = "裸开关（按住观察波纹）",
            modifier = Modifier.weight(1f),
            style = OxygenTheme.typography.listItemTitle.copy(
                color = OxygenTheme.colorScheme.onSurface,
            ),
        )
        Switch(checked = local, onCheckedChange = { local = it })
    }
}
