// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import kotlinx.coroutines.launch

/**
 * 底部面板：从底部滑入的模态面板，带拖拽手柄，向下拖动超过 30% 高度即关闭。
 *
 * 与 [io.oxygenx.ui.basic.Dialog] 的分工：Dialog 是贴底的「弹窗卡片」（左右留 8dp、
 * 20dp 圆角、内容由调用方排）；底部面板通栏贴底、只有上面两个角是圆角，
 * 用于分享 / 动作列表 / 筛选面板。内容里要主动关闭时调 [ModalBottomSheetScope.dismiss]，
 * 它会先播完下滑动画再回调 [onDismissRequest]。
 *
 * - Spec ID: `CMP-BOTTOM-SHEET`
 * - Baseline: `Material 3 modal bottom sheet / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-BOTTOM-SHEET/`
 * - Motion: `MOT-ENTER-001`（`spec.dialog.slide`），与弹窗同一套滑入滑出，可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param onDismissRequest 退场动画结束后回调；点遮罩、返回键、[ModalBottomSheetScope.dismiss] 都会先播完动画。
 * @param modifier 外部 Modifier，作用在整块弹层上（遮罩 + 面板）。
 * @param motion 入场 / 退场的动效规格。
 * @param content 面板内容槽，已经下发内容色与 `type.body.medium` 排版。
 */
@Composable
fun ModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    motion: OxygenMotionSpec = OxygenMotionDefaults.dialogSlide(),
    content: @Composable ModalBottomSheetScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val shown = rememberOxygenAnimation(spec = motion)
    val drag = rememberOxygenAnimation(spec = motion)
    var sheetHeight by remember { mutableIntStateOf(0) }
    val sheetScope = remember(onDismissRequest) {
        ModalBottomSheetScope {
            scope.launch {
                // 先滑下去，再通知宿主移除，避免「面板瞬间消失」。
                shown.animateTo(0f)
                onDismissRequest()
            }
        }
    }

    LaunchedEffect(Unit) {
        shown.snapTo(0f)
        shown.animateTo(1f)
    }

    Popup(
        onDismissRequest = { sheetScope.dismiss() },
        properties = PopupProperties(focusable = true),
    ) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val maxSheetHeight = maxHeight * 0.9f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = shown.value }
                    .background(OxygenTheme.colorScheme.scrim)
                    .pointerInput(Unit) { detectTapGestures { sheetScope.dismiss() } },
            )
            val sheetModifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = maxSheetHeight)
                .graphicsLayer {
                    // 入场位移 + 手指拖拽位移叠加；拖拽只允许向下。
                    translationY = (1f - shown.value) * sheetHeight + drag.value
                }
                .clip(
                    // 只有上面两个角是圆的：用卡片圆角令牌的 CornerSize 拼一个上圆下方。
                    RoundedCornerShape(
                        topStart = OxygenTheme.shapes.cornerCard.topStart,
                        topEnd = OxygenTheme.shapes.cornerCard.topEnd,
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp),
                    ),
                )
                .background(OxygenTheme.colorScheme.surfaceContainer)
                .onSizeChanged { sheetHeight = it.height }
                .draggable(
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            drag.snapTo((drag.value + delta).coerceIn(0f, sheetHeight.toFloat()))
                        }
                    },
                    orientation = Orientation.Vertical,
                    onDragStopped = {
                        if (drag.value > sheetHeight * 0.3f) {
                            sheetScope.dismiss()
                        } else {
                            scope.launch { drag.animateTo(0f) }
                        }
                    },
                )

            CompositionLocalProvider(
                LocalContentColor provides OxygenTheme.colorScheme.onSurface,
                LocalTextStyle provides OxygenTheme.typography.bodyMedium,
            ) {
                Column(modifier = sheetModifier) {
                    BottomSheetHandle()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
                    ) {
                        with(sheetScope) { content() }
                        Spacer(modifier = Modifier.height(OxygenTheme.spacing.gapSmall))
                    }
                }
            }
        }
    }
}

/** 顶部拖拽手柄：32×4 药丸，居中在 22dp 区域里。 */
@Composable
private fun BottomSheetHandle() {
    val spacing = OxygenTheme.spacing
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(spacing.sheetHandleInset),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = spacing.sheetHandleWidth, height = spacing.sheetHandleHeight)
                .clip(RoundedCornerShape(spacing.sheetHandleHeight / 2))
                .background(OxygenTheme.colorScheme.outlineVariant),
        )
    }
}

/** 面板内容的 receiver：提供 [dismiss]。 */
class ModalBottomSheetScope internal constructor(private val onDismiss: () -> Unit) {
    /** 播放退场动画并关闭面板。 */
    fun dismiss() = onDismiss()
}
