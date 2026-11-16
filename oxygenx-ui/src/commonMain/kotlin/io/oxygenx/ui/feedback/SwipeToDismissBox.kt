// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.feedback

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** 滑动删除的方向 / 结果。 */
enum class SwipeToDismissBoxValue {
    /** 向结束边滑（LTR 是右滑）。 */
    StartToEnd,

    /** 向起始边滑（LTR 是左滑）。 */
    EndToStart,

    /** 没有发生删除，回到原位。 */
    Settled,
}

/**
 * 滑动删除：条目跟手横移，越过阈值（`effect.dismiss.threshold` × 宽度）后滑出并回调，
 * 未越过则回弹。
 *
 * 层序是「背景层 → 内容层」：背景铺满整行并被内容盖住，内容横移后露出——
 * 所以删除图标放哪一侧由调用方决定（左滑删除就把图标靠右放）。
 *
 * 方向按**物理方向**判定，不随 `LayoutDirection` 翻转：`StartToEnd` 永远指「向右滑出」，
 * 布局镜像时由调用方按 `LocalLayoutDirection` 自行对调两个开关。
 *
 * - Spec ID: `CMP-SWIPE-TO-DISMISS`
 * - Baseline: `Material 3 swipe to dismiss / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-SWIPE-TO-DISMISS/`
 * - Motion: `MOT-STATE-002`（`spec.switch.toggle`），回弹与滑出走同一条弹簧，可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param onDismiss 越过阈值、滑出画面后回调，参数是滑动方向；宿主在这个回调里移除条目。
 * @param modifier 外部 Modifier。
 * @param backgroundContent 背景槽，内容横移后露出的部分（删除图标 / 底色）。
 * @param enableDismissFromStartToEnd 是否允许向右滑删除。
 * @param enableDismissFromEndToStart 是否允许向左滑删除。
 * @param motion 回弹与滑出的动效规格。
 * @param content 内容槽，未滑动时盖住背景，同时决定行高。
 */
@Composable
fun SwipeToDismissBox(
    onDismiss: (SwipeToDismissBoxValue) -> Unit,
    modifier: Modifier = Modifier,
    backgroundContent: @Composable BoxScope.() -> Unit = {},
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    motion: OxygenMotionSpec = OxygenMotionDefaults.switchToggle(),
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offset = rememberOxygenAnimation(spec = motion)
    var width by remember { mutableIntStateOf(0) }
    val threshold = OxygenTheme.interaction.dismissThreshold

    Box(
        modifier = modifier
            .onSizeChanged { width = it.width }
            .heightIn(min = OxygenTheme.spacing.listRowMinHeight),
    ) {
        // 背景不吃尺寸：用 matchParentSize，行高仍由内容决定。
        Box(modifier = Modifier.matchParentSize(), content = backgroundContent)
        Box(
            modifier = Modifier
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .draggable(
                    state = rememberDraggableState { delta ->
                        val max = width.toFloat()
                        val next = (offset.value + delta).coerceIn(-max, max)
                        scope.launch { offset.snapTo(next) }
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        val current = offset.value
                        val toEnd = current > 0
                        val allowed = if (toEnd) enableDismissFromStartToEnd else enableDismissFromEndToStart
                        val passed = abs(current) > width * threshold
                        if (passed && allowed && width > 0) {
                            val target = if (toEnd) width.toFloat() else -width.toFloat()
                            scope.launch {
                                offset.animateTo(target)
                                // 滑出后立刻回位：条目要么被宿主移除，要么复用同一行。
                                offset.snapTo(0f)
                                onDismiss(
                                    if (toEnd) {
                                        SwipeToDismissBoxValue.StartToEnd
                                    } else {
                                        SwipeToDismissBoxValue.EndToStart
                                    },
                                )
                            }
                        } else {
                            scope.launch { offset.animateTo(0f) }
                        }
                    },
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            content()
        }
    }
}
