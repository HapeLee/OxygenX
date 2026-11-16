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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Checkbox
import io.oxygenx.ui.basic.RadioButton
import io.oxygenx.ui.settings.SettingsCheckItem
import io.oxygenx.ui.settings.SettingsRadioItem

fun LazyListScope.radioButtonSection() {
    item(key = "cmp-radio") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            SmallTitle(text = "RadioButton / 单选", specId = "CMP-RADIOBUTTON")
            DemoCard {
                RadioButtonVariants()
            }
        }
    }
}

fun LazyListScope.checkboxSection() {
    item(key = "cmp-checkbox") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            SmallTitle(text = "Checkbox / 复选", specId = "CMP-CHECKBOX")
            DemoCard {
                CheckboxVariants()
            }
        }
    }
}

fun LazyListScope.settingsChoiceItemSection() {
    item(key = "cmp-settings-choice") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            // 设置行通栏延伸到屏幕左右，不套卡片；标题和说明仍留左右内距。
            SmallTitle(
                text = "SettingsChoiceItem / 单选与复选行",
                specId = "CMP-SETTINGS-RADIO-ITEM",
                modifier = Modifier.padding(horizontal = OxygenTheme.spacing.gutter),
            )
            SettingsChoiceItemVariants()
        }
    }
}

@Composable
private fun RadioButtonVariants() {
    val options = listOf(
        "动态" to "识别声音内容并自动调节，为你提供最佳音效体验",
        "电影" to "环绕立体声加上清晰的对白，打造影院级声音享受",
        "音乐" to "更加饱满而细致的音效，令音乐更具震撼力",
    )
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapLarge)) {
        options.forEachIndexed { index, (title, summary) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            ) {
                RadioButton(
                    selected = selected == index,
                    onClick = { selected = index },
                )
                Column(modifier = Modifier.weight(1f)) {
                    // 必须显式取 onSurface：`typography.*` 不带颜色，BasicText 会落到平台默认黑，
                    // 深色主题下正文直接看不见。
                    BasicText(
                        text = title,
                        style = OxygenTheme.typography.bodyMedium.copy(
                            color = OxygenTheme.colorScheme.onSurface,
                        ),
                    )
                    BasicText(
                        text = summary,
                        style = OxygenTheme.typography.caption.copy(
                            color = OxygenTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            RadioButton(selected = true, onClick = null, enabled = false)
            BasicText(
                text = "禁用态（选中 / 未选中同一套禁用色）",
                style = OxygenTheme.typography.caption.copy(
                    color = OxygenTheme.colorScheme.onSurfaceDisabled,
                ),
            )
            RadioButton(selected = false, onClick = null, enabled = false)
        }
    }
}

@Composable
private fun CheckboxVariants() {
    var wifi by remember { mutableStateOf(true) }
    var bluetooth by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            Checkbox(checked = wifi, onCheckedChange = { wifi = it })
            BasicText(
                text = "允许 WLAN 下自动更新",
                style = OxygenTheme.typography.bodyMedium.copy(
                    color = OxygenTheme.colorScheme.onSurface,
                ),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            Checkbox(checked = bluetooth, onCheckedChange = { bluetooth = it })
            BasicText(
                text = "允许蓝牙共享网络",
                style = OxygenTheme.typography.bodyMedium.copy(
                    color = OxygenTheme.colorScheme.onSurface,
                ),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            Checkbox(checked = true, onCheckedChange = null, enabled = false)
            Checkbox(checked = false, onCheckedChange = null, enabled = false)
            BasicText(
                text = "禁用态",
                style = OxygenTheme.typography.caption.copy(
                    color = OxygenTheme.colorScheme.onSurfaceDisabled,
                ),
            )
        }
    }
}

@Composable
private fun SettingsChoiceItemVariants() {
    var scene by remember { mutableIntStateOf(0) }
    var allowUpdate by remember { mutableStateOf(true) }
    var allowShare by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsRadioItem(
            selected = scene == 0,
            onClick = { scene = 0 },
            title = "动态",
            summary = "识别声音内容并自动调节",
        )
        SettingsRadioItem(
            selected = scene == 1,
            onClick = { scene = 1 },
            title = "电影",
            summary = "环绕立体声加上清晰的对白",
        )
        SettingsRadioItem(
            selected = scene == 2,
            onClick = { scene = 2 },
            title = "禁用态",
            summary = "整行与单选框都不响应",
            enabled = false,
        )
        SettingsCheckItem(
            checked = allowUpdate,
            onCheckedChange = { allowUpdate = it },
            title = "自动更新",
            summary = "WLAN 下允许后台下载",
        )
        SettingsCheckItem(
            checked = allowShare,
            onCheckedChange = { allowShare = it },
            title = "共享网络",
        )
        DemoNote(
            text = "整行是一个单选 / 复选节点：点文字或右侧控件都会切换，" +
                "控件本身只做展示（按压反馈由整行承担）。",
            inset = OxygenTheme.spacing.gutter,
        )
    }
}

@Composable
private fun DemoNote(text: String, inset: Dp = 0.dp) {
    BasicText(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = inset),
        style = OxygenTheme.typography.caption.copy(
            color = OxygenTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
