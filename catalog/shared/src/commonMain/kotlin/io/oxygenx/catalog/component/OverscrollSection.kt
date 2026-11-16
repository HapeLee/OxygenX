// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme

fun LazyListScope.overscrollSection() {
    item(key = "cmp-overscroll") {
        SmallTitle(text = "Overscroll / 滚动过冲", specId = "CMP-OVERSCROLL")
        DemoCard {
            OverscrollDemo()
        }
    }
}

/**
 * 固定高度的滚动区，方便在页内直接试「拖到边界再继续拖」。
 *
 * 这里**不需要**额外包一层过冲容器：`OxygenTheme` 已经把
 * `LocalOverscrollFactory` 换成本项目的弹簧回弹，任何 `LazyColumn` 都自动生效。
 */
@Composable
private fun OverscrollDemo() {
    val spacing = OxygenTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
        Label("滚到顶或底后继续拖：内容跟着走、越拖越沉；松手用紧凑弹簧弹回，甩得越快回得越快")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(OxygenTheme.shapes.cornerCard),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = spacing.gapSmall),
            ) {
                items(count = 16) { index ->
                    Box(
                        modifier = Modifier.fillMaxWidth().height(spacing.listRowMinHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Label("列表项 ${index + 1}")
                    }
                }
            }
        }
        Label(
            "位移上限 space.overscroll.max = 72dp：三次曲线 x - x² + x³/3，起手跟手 " +
                "effect.overscroll.drag.scale = 0.5，越拉越沉；回弹走 MOT-SCROLL-001" +
                "（spring.overscroll：刚度 900 / 阻尼 0.6），甩动时带初速度、余势继续回弹；" +
                "横向与纵向各自独立，由 OxygenTheme 全局注入",
        )
    }
}

@Composable
private fun Label(text: String) {
    BasicText(
        text = text,
        style = OxygenTheme.typography.caption.copy(color = OxygenTheme.colorScheme.onSurfaceVariant),
    )
}
