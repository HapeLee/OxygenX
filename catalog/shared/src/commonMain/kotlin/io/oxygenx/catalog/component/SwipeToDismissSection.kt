// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.feedback.SwipeToDismissBox
import io.oxygenx.ui.feedback.SwipeToDismissBoxValue

fun LazyListScope.swipeToDismissSection() {
    item(key = "cmp-swipe-to-dismiss") {
        SmallTitle(text = "SwipeToDismissBox / 滑动删除", specId = "CMP-SWIPE-TO-DISMISS")
        DemoCard {
            SwipeToDismissVariants()
        }
    }
}

@Composable
private fun SwipeToDismissVariants() {
    val colors = OxygenTheme.colorScheme
    val caption = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant)
    var items by remember { mutableStateOf(listOf("下载项 A", "下载项 B", "下载项 C")) }
    var lastRemoved by remember { mutableStateOf("—") }

    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "左右滑动条目：越过 40% 宽度后滑出并回调，未越过回弹。背景层铺满整行，" +
                "删除图标靠右放，左滑时露出",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        items.forEach { item ->
            SwipeToDismissBox(
                onDismiss = { direction ->
                    lastRemoved = if (direction == SwipeToDismissBoxValue.EndToStart) {
                        "$item（左滑）"
                    } else {
                        "$item（右滑）"
                    }
                    items = items - item
                },
                backgroundContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.error)
                            .padding(horizontal = OxygenTheme.spacing.cardPadding),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = OxygenIcons.Close,
                            contentDescription = "删除",
                            tint = colors.onPrimary,
                        )
                    }
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainer)
                        .padding(
                            horizontal = OxygenTheme.spacing.cardPadding,
                            vertical = OxygenTheme.spacing.gapLarge,
                        ),
                ) {
                    BasicText(
                        text = item,
                        style = OxygenTheme.typography.listItemTitle.copy(color = colors.onSurface),
                    )
                }
            }
        }
        if (items.isEmpty()) {
            BasicText(
                text = "已全部删除",
                modifier = Modifier.fillMaxWidth(),
                style = caption,
            )
        }
        BasicText(
            text = "最近删除：$lastRemoved",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
    }
}
