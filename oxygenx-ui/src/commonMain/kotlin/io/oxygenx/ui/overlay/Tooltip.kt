// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.ui.basic.Surface

/**
 * 文字提示：长按内容时在其上方弹出反白气泡，松手消失。
 *
 * 用于图标按钮这类「没有可见文案」的控件补说明；有可见文案的场合不要用，
 * 语义已经够读屏了。气泡里的裸 `Text` 会拿到 `type.caption` 与反白内容色。
 *
 * - Spec ID: `CMP-TOOLTIP`
 * - Baseline: `Material 3 plain tooltip / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-TOOLTIP/`
 * - Motion: 无（长按即显、松手即隐，不做淡入淡出，避免手指还按着时出现残影）。
 * - Status: `draft`
 *
 * @param tooltip 气泡内容槽。
 * @param modifier 外部 Modifier，作用在触发区上。
 * @param content 触发区内容，通常是图标按钮。
 */
@Composable
fun TooltipBox(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                // 长按成立才显示：短按仍然是点击，不弹气泡；抬手收起。
                if (awaitLongPressOrCancellation(down.id) != null) {
                    visible = true
                    waitForUpOrCancellation()
                    visible = false
                }
            }
        },
    ) {
        content()
        if (visible) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -TooltipDefaults.OFFSET_PX),
                onDismissRequest = { visible = false },
                properties = PopupProperties(focusable = false),
            ) {
                TooltipSurface { tooltip() }
            }
        }
    }
}

/** 反白气泡本体；需要自己控制摆放时可以直接用。 */
@Composable
fun TooltipSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = OxygenTheme.colorScheme
    val spacing = OxygenTheme.spacing
    Surface(
        modifier = modifier.widthIn(max = spacing.tooltipMaxWidth),
        color = colors.inverseSurface,
        shape = OxygenTheme.shapes.cornerInput,
    ) {
        ProvideContentStyle(
            contentColor = colors.inverseOnSurface,
            textStyle = OxygenTheme.typography.bodySmall,
        ) {
            Box(
                modifier = Modifier.padding(
                    horizontal = spacing.tooltipPaddingHorizontal,
                    vertical = spacing.tooltipPaddingVertical,
                ),
            ) {
                content()
            }
        }
    }
}

object TooltipDefaults {
    /** 气泡与触发区之间的间距（气泡贴在正上方，留一点空隙避免贴死）。 */
    const val OFFSET_PX: Int = 8
}
