// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Chip
import io.oxygenx.ui.basic.ChipDefaults
import io.oxygenx.ui.basic.Text

/**
 * CMP-CHIP 演示矩阵：默认形态、三档配色、领航/尾随图标、筛选组、禁用态、回调一次。
 * 未做明暗/强调色覆盖与 RTL 单独开关：主题切换在 catalog 全局，chip 的所有颜色都从
 * `OxygenTheme.colorScheme` 读取，切主题即覆盖；图标均非方向性，无需镜像。
 */
fun LazyListScope.chipSection() {
    item(key = "cmp-chip") {
        SmallTitle(text = "Chip / 芯片", specId = "CMP-CHIP")
        DemoCard {
            ChipVariants()
        }
    }
}

/** chip 的文案槽：单行省略由调用方给（与 M3 `label` 槽口径一致）。 */
@Composable
private fun ChipLabel(text: String) {
    Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun ChipVariants() {
    val spacing = OxygenTheme.spacing
    var clicks by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf(false) }
    var primarySelected by remember { mutableStateOf(true) }
    var tonalSelected by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(setOf(0)) }
    var tags by remember { mutableStateOf(listOf("可移除", "标签")) }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
        ChipCaption("默认 secondary：未选中是细线边框 + 次要文字，选中是墨色实心胶囊")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Chip(onClick = { clicks++ }, label = { ChipLabel("未选中") })
            Chip(
                onClick = { selected = !selected },
                label = { ChipLabel("选中") },
                selected = selected,
            )
        }

        ChipCaption("primary 系列：选中为一加红胶囊 + onPrimary 文字")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Chip(
                onClick = { primarySelected = !primarySelected },
                label = { ChipLabel("primary") },
                selected = primarySelected,
                colors = ChipDefaults.primaryColors(),
            )
            Chip(
                onClick = { clicks++ },
                label = { ChipLabel("未选中") },
                colors = ChipDefaults.primaryColors(),
            )
        }

        ChipCaption("tonal 系列（M3 FilterChip）：选中为 secondaryContainer + onSecondaryContainer")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Chip(
                onClick = { tonalSelected = !tonalSelected },
                label = { ChipLabel("tonal") },
                selected = tonalSelected,
                colors = ChipDefaults.tonalColors(),
            )
            Chip(
                onClick = { clicks++ },
                label = { ChipLabel("未选中") },
                colors = ChipDefaults.tonalColors(),
            )
        }

        ChipCaption("领航图标（leadingIcon）：着色自动跟随选中 / 禁用内容色")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Chip(
                onClick = { clicks++ },
                label = { ChipLabel("搜索") },
                leadingIcon = { ChipDefaults.Icon(OxygenIcons.Search, "搜索") },
            )
            Chip(
                onClick = { clicks++ },
                label = { ChipLabel("已选中") },
                selected = true,
                leadingIcon = { ChipDefaults.Icon(OxygenIcons.Info, "信息") },
            )
        }

        ChipCaption("筛选组（M3 Filter 形态）：选中时领航图标换成勾选，多选由调用方维护")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf("全部", "图片", "视频").forEachIndexed { index, label ->
                val isChecked = index in checked
                Chip(
                    onClick = {
                        checked = if (isChecked) checked - index else checked + index
                    },
                    label = { ChipLabel(label) },
                    selected = isChecked,
                    colors = ChipDefaults.tonalColors(),
                    leadingIcon = if (isChecked) {
                        { ChipDefaults.Icon(OxygenIcons.Check, "已选中") }
                    } else {
                        null
                    },
                )
            }
        }

        ChipCaption("尾随图标（M3 Input 形态）：点击整块移除；移除按钮由 slot 决定")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tags.forEach { tag ->
                Chip(
                    onClick = { tags = tags - tag },
                    label = { ChipLabel(tag) },
                    trailingIcon = { ChipDefaults.Icon(OxygenIcons.Close, "移除 $tag") },
                )
            }
            if (tags.isEmpty()) {
                Chip(
                    onClick = { tags = listOf("可移除", "标签") },
                    label = { ChipLabel("重置") },
                )
            }
        }

        ChipCaption("禁用态：选与不选都取禁用容器 / 禁用文字 / 细线边框")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Chip(onClick = {}, label = { ChipLabel("禁用") }, enabled = false)
            Chip(onClick = {}, label = { ChipLabel("选中禁用") }, enabled = false, selected = true)
        }

        ChipCaption("回调次数：$clicks（点击只触发一次）")
    }
}

@Composable
private fun ChipCaption(text: String) {
    BasicText(
        text = text,
        style = OxygenTheme.typography.caption.copy(
            color = OxygenTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
