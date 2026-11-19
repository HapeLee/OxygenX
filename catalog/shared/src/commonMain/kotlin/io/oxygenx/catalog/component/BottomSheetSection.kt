// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextButton
import io.oxygenx.ui.overlay.ModalBottomSheet
import io.oxygenx.ui.overlay.rememberModalBottomSheetState
import kotlinx.coroutines.launch

fun LazyListScope.bottomSheetSection() {
    item(key = "cmp-bottom-sheet") {
        SmallTitle(text = "ModalBottomSheet / 底部面板", specId = "CMP-BOTTOM-SHEET")
        DemoCard {
            BottomSheetVariants()
        }
    }
}

@Composable
private fun BottomSheetVariants() {
    val colors = OxygenTheme.colorScheme
    val caption = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant)
    var show by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "语义对齐 M3：默认「半屏 ↔ 全屏 ↔ 收起」三档（传 skipPartiallyExpanded = true " +
                "退化成两档），入场自动 show()，收起走 sheetState.hide()；" +
                "点遮罩 / 返回键都会先播完下滑再回调 onDismissRequest；" +
                "Android 14+ 从屏幕侧边往回划是预测式返回，面板跟着手势下移、松手前退回则弹回原档" +
                "（宿主需开启 android:enableOnBackInvokedCallback=\"true\"）",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        Button(onClick = { show = true }) { Text("打开底部面板", maxLines = 1) }
    }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = { show = false },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = OxygenTheme.spacing.cardPadding),
                verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            ) {
                Text("分享到", maxLines = 1)
                Text(
                    text = "向上拖到全屏、向下拖回半屏或收起；下面这块内容自己可滚，" +
                        "滚到顶再下拉才轮到面板——这是 nested-scroll 仲裁的结果。",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
                    TextButton(onClick = { scope.launch { sheetState.expand() } }) { Text("展开") }
                    TextButton(onClick = { scope.launch { sheetState.partialExpand() } }) { Text("半屏") }
                    TextButton(onClick = { scope.launch { sheetState.hide() } }) { Text("关闭") }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
                ) {
                    repeat(8) { index ->
                        Text("面板内可滚动项 ${index + 1}", maxLines = 1)
                    }
                }
            }
        }
    }
}
