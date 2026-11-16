// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme

/**
 * 提示弹窗：标题 / 说明 / 确认与取消按钮的常见组合，是 [Dialog] 的高层封装。
 *
 * 布局与真机「设备名称」弹窗同一套参数：标题居中、`size.dialog.padding` 左右内距，
 * 说明在输入区下方取 `onSurfaceVariant`，按钮整行通栏（`TextButton` 自带），底部留
 * `space.gap.small`。需要输入框、自定义内容或完全异形卡片时用底层 [Dialog] 自己排。
 *
 * **按钮要调 `dismiss()`**（[DialogScope.dismiss]），不要直接把宿主状态置 false：
 * 前者会等卡片滑出屏幕再回调 `onDismissRequest`，后者会让弹窗立刻消失、看不到退场。
 *
 * - Spec ID: `CMP-ALERT-DIALOG`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-DIALOG/`
 * - Motion: `MOT-ENTER-001`（继承自 [Dialog]）
 * - Status: `draft`
 *
 * @param onDismissRequest 关闭动画结束后回调；点遮罩、返回键、[DialogScope.dismiss] 都会先跑完动画。
 * @param confirmButton 确认按钮槽，用 `TextButton(onClick = { dismiss() }) { Text("确定") }`。
 * @param modifier 外部 Modifier，作用在卡片上。
 * @param title 标题槽：已下发 `type.dialog.title`、`onSurface` 与居中；传 `null` 表示无标题。
 * @param text 说明槽：已下发 `type.body.medium` 与 `onSurfaceVariant`；传 `null` 表示无说明。
 * @param dismissButton 取消按钮槽；传 `null` 表示只有一个按钮。
 * @param alignment 垂直位置，默认贴底（一加弹窗形态）；居中传 [DialogAlignment.Center]。
 * @param properties 平台弹窗属性，默认关掉平台默认宽度以便全宽布局。
 */
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable DialogScope.() -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable DialogScope.() -> Unit)? = null,
    alignment: DialogAlignment = DialogAlignment.Bottom,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
) {
    val spacing = OxygenTheme.spacing
    val typography = OxygenTheme.typography
    val colors = OxygenTheme.colorScheme

    Dialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        alignment = alignment,
        properties = properties,
    ) {
        // 卡片不额外加内边距：标题 / 说明自己留左右白，按钮行要保持通栏。
        val dialogScope = this
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title == null && text == null) {
                // 只给按钮的弹窗也要留出卡片顶边的白，否则第一个按钮贴着圆角。
                Spacer(modifier = Modifier.height(spacing.dialogPadding))
            }
            if (title != null || text != null) {
                Column(
                    modifier = Modifier.padding(
                        start = spacing.dialogPadding,
                        end = spacing.dialogPadding,
                        top = spacing.dialogPadding,
                    ),
                ) {
                    if (title != null) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.onSurface,
                            LocalTextStyle provides typography.dialogTitle.copy(textAlign = TextAlign.Center),
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) { title() }
                        }
                        Spacer(modifier = Modifier.height(spacing.dialogPadding))
                    }
                    if (text != null) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.onSurfaceVariant,
                            LocalTextStyle provides typography.bodyMedium,
                        ) {
                            text()
                        }
                        Spacer(modifier = Modifier.height(spacing.gapSmall))
                    }
                }
            }
            with(dialogScope) { confirmButton() }
            if (dismissButton != null) {
                with(dialogScope) { dismissButton() }
            }
            // 一加「取消」下方还有 8dp 留白，卡片底边不贴着按钮。
            Spacer(modifier = Modifier.height(spacing.gapSmall))
        }
    }
}
