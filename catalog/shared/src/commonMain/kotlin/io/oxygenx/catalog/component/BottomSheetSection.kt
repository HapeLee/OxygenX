// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextButton
import io.oxygenx.ui.overlay.ModalBottomSheet

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

    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "从底部滑入、通栏贴底、只有上面两角是圆角；带 32×4 拖拽手柄，" +
                "向下拖过面板高度的 30% 即关闭，点遮罩也会先播完下滑动画再回调",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        Button(onClick = { show = true }) { Text("打开底部面板", maxLines = 1) }
    }

    if (show) {
        ModalBottomSheet(onDismissRequest = { show = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = OxygenTheme.spacing.cardPadding),
                verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            ) {
                Text("分享到", maxLines = 1)
                Text("面板内容槽里已下发 type.body.medium 与 onSurface，按钮调 dismiss() 关闭。")
                TextButton(onClick = { dismiss() }) { Text("取消") }
            }
        }
    }
}
