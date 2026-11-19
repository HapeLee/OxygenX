// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Text
import kotlinx.coroutines.launch

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
    val state = rememberLazyListState(initialFirstVisibleItemIndex = 40)
    val scope = rememberCoroutineScope()
    val factory = LocalOverscrollFactory.current
    val effect = remember(factory) { factory?.createOverscrollEffect() }
    val position by remember(state) { derivedStateOf { state.firstVisibleItemIndex + 1 } }
    val overscrolling by remember(effect) { derivedStateOf { effect?.isInProgress == true } }
    Column(verticalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
        Label("先在列表中间甩动：正常惯性不应触发回弹；切到顶部或底部后继续往外拖，才出现过冲")
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
            Button(onClick = { scope.launch { state.scrollToItem(0) } }) { Text("顶部") }
            Button(onClick = { scope.launch { state.scrollToItem(40) } }) { Text("中间") }
            Button(onClick = { scope.launch { state.scrollToItem(79) } }) { Text("底部") }
        }
        Label("首项：$position / 80 · 过冲：${if (overscrolling) "是" else "否"}")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(OxygenTheme.shapes.cornerCard),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                overscrollEffect = effect,
                contentPadding = PaddingValues(vertical = spacing.gapSmall),
            ) {
                items(count = 80) { index ->
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
                "只吸收未被内容消费的纵向残量，横向交给父级；由 OxygenTheme 全局注入",
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
