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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.ButtonDefaults
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text

fun LazyListScope.buttonSection() {
    item(key = "cmp-button") {
        SmallTitle(text = "Button / 按钮", specId = "CMP-BUTTON")
        DemoCard {
            ButtonVariants()
        }
    }
}

@Composable
private fun ButtonVariants() {
    val colors = OxygenTheme.colorScheme
    var clicks by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = { clicks++ }) { Text("主按钮", maxLines = 1) }
            Button(onClick = {}, enabled = false) { Text("禁用", maxLines = 1) }
        }
        BasicText(
            text = "点击次数：$clicks（回调只触发一次）",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Button(
            onClick = { clicks = 0 },
            colors = ButtonDefaults.colors(
                containerColor = colors.onSurface,
                contentColor = colors.surface,
            ),
        ) {
            Text("自定义颜色并复位")
        }
        BasicText(
            text = "内容槽位：按钮内已下发 type.label.medium 与内容色，槽位里可以再排图标 / 加载态",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { clicks++ },
                colors = ButtonDefaults.secondaryColors(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapXs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = OxygenIcons.Add, contentDescription = null)
                    Text("新建")
                }
            }
            Button(
                onClick = { clicks++ },
                colors = ButtonDefaults.secondaryColors(),
            ) {
                Text("仅文案")
            }
        }
        BasicText(
            text = "secondary：底色取 color.secondary + color.on.secondary（墨色），药丸形圆角恒为高度一半",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { clicks++ },
                colors = ButtonDefaults.secondaryColors(),
            ) {
                Text("次要按钮")
            }
            Button(
                onClick = { clicks++ },
                colors = ButtonDefaults.secondaryColors(),
                shape = ButtonDefaults.PillShape,
            ) {
                Text("药丸按钮")
            }
        }
    }
}
