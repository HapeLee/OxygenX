// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.IconButton
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.overlay.TooltipBox

fun LazyListScope.tooltipSection() {
    item(key = "cmp-tooltip") {
        SmallTitle(text = "Tooltip / 文字提示", specId = "CMP-TOOLTIP")
        DemoCard {
            TooltipVariants()
        }
    }
}

@Composable
private fun TooltipVariants() {
    val colors = OxygenTheme.colorScheme
    val caption = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant)
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "长按下方图标：气泡在正上方弹出（反白底 + type.caption），松手消失；短按仍然是普通点击。",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TooltipBox(tooltip = { Text("搜索书源", maxLines = 1) }) {
                IconButton(onClick = {}) {
                    Icon(imageVector = OxygenIcons.Search, contentDescription = "搜索")
                }
            }
            TooltipBox(tooltip = { Text("新建分组", maxLines = 1) }) {
                IconButton(onClick = {}) {
                    Icon(imageVector = OxygenIcons.Add, contentDescription = "新建")
                }
            }
            TooltipBox(tooltip = { Text("这一条会折行，最长 200dp，超出后自动换行显示", maxLines = 3) }) {
                IconButton(onClick = {}) {
                    Icon(imageVector = OxygenIcons.Info, contentDescription = "说明")
                }
            }
        }
    }
}
