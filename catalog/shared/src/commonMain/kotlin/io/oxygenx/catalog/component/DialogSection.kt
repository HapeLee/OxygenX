// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.AlertDialog
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.DialogAlignment
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextButton
import io.oxygenx.ui.basic.TextField

fun LazyListScope.dialogSection() {
    item(key = "cmp-dialog") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            SmallTitle(text = "AlertDialog / 弹窗", specId = "CMP-ALERT-DIALOG")
            DialogVariants()
        }
    }
}

@Composable
private fun DialogVariants() {
    val typography = OxygenTheme.typography
    val colors = OxygenTheme.colorScheme
    var showEdit by remember { mutableStateOf(false) }
    var showCenter by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("OnePlus 9R") }

    DemoCard {
        BasicText(
            text = "复刻一加「设备名称」编辑弹窗：从底部滑入、卡片贴底、左右各 8dp、20dp 圆角、" +
                "18sp 居中标题、40dp 下划线输入框（文字贴底）；确定 / 取消通栏，左右无内边距，" +
                "水波纹延伸到卡片圆角边缘；点击卡片外的遮罩即可关闭，关闭时卡片沿入场路径向下滑出。" +
                "标题 / 说明 / 按钮都是槽位，布局参数由 AlertDialog 统一给；需要完全自定义的卡片时用底层的 Dialog（CMP-DIALOG）。",
            style = typography.bodyMedium.copy(color = colors.onSurface),
        )
        Button(onClick = { showEdit = true }) { Text("打开设备名称弹窗（贴底）") }
        Button(onClick = { showCenter = true }) { Text("居中弹窗（Center）") }
    }

    if (showEdit) {
        AlertDialog(
            onDismissRequest = { showEdit = false },
            title = { Text("设备名称", maxLines = 1) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        // 一加输入行是「文字贴底」：40dp 容器里上方空出 16dp。
                        contentPadding = PaddingValues(top = OxygenTheme.spacing.cardPadding),
                    )
                    Spacer(modifier = Modifier.height(OxygenTheme.spacing.cardPadding))
                    Text("在使用WLAN直连、蓝牙设备互联等操作时，都会看到手机名称。")
                }
            },
            // 走 dismiss() 而不是直接改 showEdit：让卡片先滑下去再移除。
            confirmButton = { TextButton(onClick = { dismiss() }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { dismiss() }) { Text("取消") } },
        )
    }

    if (showCenter) {
        AlertDialog(
            onDismissRequest = { showCenter = false },
            alignment = DialogAlignment.Center,
            title = { Text("设备名称", maxLines = 1) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        contentPadding = PaddingValues(top = OxygenTheme.spacing.cardPadding),
                    )
                    Spacer(modifier = Modifier.height(OxygenTheme.spacing.cardPadding))
                    Text("在使用WLAN直连、蓝牙设备互联等操作时，都会看到手机名称。")
                }
            },
            confirmButton = { TextButton(onClick = { dismiss() }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { dismiss() }) { Text("取消") } },
        )
    }
}
