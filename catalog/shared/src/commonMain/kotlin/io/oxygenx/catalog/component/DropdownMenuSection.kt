// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.overlay.DropdownMenu
import io.oxygenx.ui.overlay.DropdownMenuItem

fun LazyListScope.dropdownMenuSection() {
    item(key = "cmp-dropdown-menu") {
        SmallTitle(text = "DropdownMenu / 下拉菜单", specId = "CMP-DROPDOWN-MENU")
        DemoCard {
            DropdownMenuVariants()
        }
    }
}

@Composable
private fun DropdownMenuVariants() {
    val colors = OxygenTheme.colorScheme
    val caption = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant)
    var expanded by remember { mutableStateOf(false) }
    var longExpanded by remember { mutableStateOf(false) }
    var sort by remember { mutableStateOf("按名称") }

    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "经典 M3 样式：小圆角、轻投影、48dp 行高，宽度 112–280dp。锚点缩放 / 淡入淡出，可快速反向；支持长文字、大字体、滚动与 RTL 定位。",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        Box {
            Button(onClick = { expanded = true }) {
                Text("排序：$sort", maxLines = 1)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("按名称", "按大小", "按修改时间").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, maxLines = 1) },
                        onClick = {
                            sort = option
                            expanded = false
                        },
                        trailingIcon = if (option == sort) {
                            {
                                Icon(
                                    imageVector = OxygenIcons.Check,
                                    contentDescription = null,
                                    tint = colors.primary,
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("删除全部", maxLines = 1) },
                    onClick = { expanded = false },
                    leadingIcon = {
                        Icon(imageVector = OxygenIcons.Close, contentDescription = null)
                    },
                )
                DropdownMenuItem(text = { Text("不可用操作") }, onClick = {}, enabled = false)
            }
        }
        Box {
            Button(onClick = { longExpanded = true }) { Text("长菜单与换行（40 项）") }
            DropdownMenu(expanded = longExpanded, onDismissRequest = { longExpanded = false }) {
                repeat(40) { index ->
                    DropdownMenuItem(
                        text = { Text(if (index == 0) "这是一项较长的菜单说明，会按可用宽度自动换行" else "菜单项 ${index + 1}") },
                        onClick = {
                            sort = "菜单项 ${index + 1}"
                            longExpanded = false
                        },
                    )
                }
            }
        }
        BasicText(
            text = "当前选择：$sort（点外部收起，选中项右侧带勾）",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
    }
}
