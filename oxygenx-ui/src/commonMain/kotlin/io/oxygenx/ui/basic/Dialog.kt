// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import androidx.compose.ui.window.Dialog as PlatformDialog

/** 弹窗卡片在安全区内的垂直位置。 */
enum class DialogAlignment {
    /** 居中，M3 `AlertDialog` 的位置。 */
    Center,

    /** 贴底，一加「设备名称」编辑弹窗的位置。 */
    Bottom,
}

/**
 * [Dialog] 的内容作用域：除 [ColumnScope] 的垂直排列能力外，还提供带动画的关闭入口。
 */
interface DialogScope : ColumnScope {
    /**
     * 请求关闭：卡片先向下滑出屏幕，动画跑完才回调宿主的 `onDismissRequest`。
     *
     * 「确定 / 取消」这类按钮应当调它，而不是直接把宿主的状态置 false —— 后者会让
     * 弹窗立刻从组合里移除，看不到消失动画。
     */
    fun dismiss()
}

private class DialogScopeImpl(
    private val columnScope: ColumnScope,
    private val onDismiss: () -> Unit,
) : DialogScope,
    ColumnScope by columnScope {
    override fun dismiss() = onDismiss()
}

/**
 * 弹窗：遮罩由平台 [Dialog] 提供，卡片只负责小圆角矩形外观，默认从底部滑入、向下滑出。
 *
 * 卡片**不加内边距**：内容区自己用 `size.dialog.padding` 控制左右留白，而按钮行
 * 不加水平内边距才能像一加那样通栏、水波纹一直延伸到卡片圆角边缘（按钮下方只剩
 * 内容自己留的 8dp，卡片底边不再额外加内边距）。
 *
 * 出入场走 `MOT-ENTER-001`（比 `MOT-STATE-002` 快一档的弹簧）：卡片从自身高度处位移到 0，
 * 关闭时沿原路滑回
 * 屏幕下方。[DialogScope.dismiss] 会等位移动画结束再回调宿主的
 * `onDismissRequest`，因此宿主的 `if (visible)` 状态必须在那一刻才置 false。
 *
 * - Spec ID: `CMP-DIALOG`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-DIALOG/`
 * - Motion: `MOT-ENTER-001`
 * - Status: `draft`
 *
 * @param onDismissRequest 关闭动画结束后回调；点击遮罩、返回键、[DialogScope.dismiss]
 *   都会先跑完动画再触发它。
 * @param modifier 外部 Modifier，作用在卡片上。
 * @param alignment 垂直位置。
 * @param properties 平台弹窗属性，默认关掉平台默认宽度以便全宽布局。
 * @param content 卡片内容，垂直排列；内边距与按钮通栏由内容自己决定。
 */
// modifier 语义上作用于卡片而不是全屏定位容器，因此不能放在根 Box 上。
@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: DialogAlignment = DialogAlignment.Bottom,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable DialogScope.() -> Unit,
) {
    val spacing = OxygenTheme.spacing
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    // 1 = 完全滑出屏幕，0 = 落位。
    val slide = rememberOxygenAnimation(spec = OxygenMotionDefaults.dialogSlide(), initialValue = 1f)
    var dismissRequested by remember { mutableStateOf(false) }
    val requestDismiss: () -> Unit = remember { { dismissRequested = true } }

    // 卡片底边到窗口底还有 safeDrawing（手势条 52px）+ dialogInset（8dp）这段距离：
    // 只按卡片高度位移的话，卡片顶部仍会停在屏幕内 40~70px，压在导航栏上。
    val bottomGapPx = with(LocalDensity.current) {
        WindowInsets.safeDrawing.getBottom(this).toFloat() +
            if (alignment == DialogAlignment.Bottom) spacing.dialogInset.toPx() else 0f
    }

    LaunchedEffect(Unit) { slide.animateTo(0f) }
    LaunchedEffect(dismissRequested) {
        if (dismissRequested) {
            // 等卡片滑出屏幕，再让宿主把它从组合里移除。
            slide.animateTo(1f)
            currentOnDismissRequest()
        }
    }

    // 平台的 Dialog 用别名导入：本文件里的 `Dialog` 是本项目自己的组件，同名会互相遮蔽。
    PlatformDialog(
        onDismissRequest = requestDismiss,
        properties = properties,
    ) {
        // 平台的 dismissOnClickOutside 只认「内容之外」的点击，而这里的容器铺满整屏，
        // 因此遮罩点击要自己接管：外层整屏铺一层无反馈的点击区。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { requestDismiss() })
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    // 真机贴底弹窗离手势条还有 8dp：卡片底边到屏幕底 68px ≈ 24dp，
                    // 而 safeDrawing 的下边距只有 52px ≈ 18dp，差的正是这圈外边距。
                    // 居中态不加，否则多出来的下边距会把卡片顶偏。
                    .padding(
                        start = spacing.dialogInset,
                        end = spacing.dialogInset,
                        bottom = if (alignment == DialogAlignment.Bottom) spacing.dialogInset else 0.dp,
                    ),
                contentAlignment = when (alignment) {
                    DialogAlignment.Center -> Alignment.Center
                    DialogAlignment.Bottom -> Alignment.BottomCenter
                },
            ) {
                // 卡片高度要等测量完成才知道，先用可用高度兜底，保证首帧在屏幕之外。
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 卡片自己吃掉落在空白处的点击，否则会一路冒泡到遮罩把弹窗关掉；
                        // 子级可点击元素命中时先消费，不受影响。
                        .pointerInput(Unit) { detectTapGestures(onTap = {}) },
                ) {
                    val fallbackPx = with(LocalDensity.current) { maxHeight.toPx() }
                    var cardHeightPx by remember { mutableIntStateOf(0) }
                    val exitDistance = if (cardHeightPx > 0) cardHeightPx + bottomGapPx else fallbackPx
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .onSizeChanged { cardHeightPx = it.height }
                            .graphicsLayer {
                                val progress = slide.value
                                translationY = progress * exitDistance
                                // 居中态下方还有大片空间，纯位移滑不完，配一次渐隐直接消失。
                                alpha = if (alignment == DialogAlignment.Center) {
                                    (1f - progress).coerceIn(0f, 1f)
                                } else {
                                    1f
                                }
                            }
                            .clip(OxygenTheme.shapes.cornerCard)
                            .background(OxygenTheme.colorScheme.surfaceContainer),
                    ) {
                        val scope = remember { DialogScopeImpl(this, requestDismiss) }
                        with(scope) { content() }
                    }
                }
            }
        }
    }
}
