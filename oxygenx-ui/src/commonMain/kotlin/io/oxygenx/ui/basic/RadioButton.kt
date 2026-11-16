// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme

/**
 * 单选框：未选中是 1.5dp 中性圆环，选中是强调色实心圆加 4dp 白点。
 *
 * - Spec ID: `CMP-RADIOBUTTON`
 * - Baseline: `Settings 11 (com.android.settings) / OnePlus 9R (LE2100) / ColorOS 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-RADIOBUTTON/`
 * - Motion: 选中态实心圆与内点按 `MOT-STATE-002` 从中心放大，取消时缩回。
 * - Status: `draft`
 *
 * @param selected 是否选中。
 * @param onClick 点击回调；`null` 表示只展示、不接收点击（整行已经承担点击的场合）。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可用；禁用时用禁用色。
 * @param colors 圆环、实心圆与内点颜色。
 * @param motion 状态切换动效规格。
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    motion: OxygenMotionSpec = RadioButtonDefaults.Motion,
) {
    val size = RadioButtonDefaults.Size
    val borderWidth = RadioButtonDefaults.BorderWidth
    val dotSize = RadioButtonDefaults.DotSize
    val progress = rememberOxygenAnimation(spec = motion)
    LaunchedEffect(selected, progress) { progress.animateTo(if (selected) 1f else 0f) }

    Canvas(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(role = Role.RadioButton) { onClick() }
                } else {
                    Modifier
                },
            )
            .semantics {
                this.selected = selected
                if (!enabled) disabled()
            },
    ) {
        val radius = this.size.minDimension / 2f
        val stroke = borderWidth.toPx()
        // 底圈常驻：过渡期间强调色是「从描边往圆心加粗」，不是从中心长出一个圆。
        drawCircle(
            color = colors.resolveBorderColor(enabled),
            radius = radius - stroke / 2f,
            style = Stroke(width = stroke),
        )
        if (progress.value > 0f) {
            // 外沿固定、线宽从 1.5dp 加到半径，视觉上就是红色由边框向内包围。
            val width = stroke + (radius - stroke) * progress.value
            drawCircle(
                color = colors.resolveFillColor(enabled),
                radius = radius - width / 2f,
                style = Stroke(width = width),
            )
            // 内点等填充走完大半程再出现，避免和包围过程抢视觉。
            val dotProgress = ((progress.value - 0.55f) / 0.45f).coerceIn(0f, 1f)
            if (dotProgress > 0f) {
                drawCircle(
                    color = colors.dot,
                    radius = dotSize.toPx() / 2f * dotProgress,
                )
            }
        }
    }
}

/** 单选/复选框共用的状态动效：与开关同一套。 */
internal val ChoiceMotion: OxygenMotionSpec
    @Composable get() = OxygenMotionDefaults.switchToggle()

object RadioButtonDefaults {
    val Size: Dp
        @Composable get() = OxygenTheme.spacing.controlRadioSize

    val BorderWidth: Dp
        @Composable get() = OxygenTheme.spacing.controlBorderWidth

    val DotSize: Dp
        @Composable get() = OxygenTheme.spacing.controlDotSize

    val Motion: OxygenMotionSpec
        @Composable get() = ChoiceMotion

    @Composable
    fun colors(
        border: Color = OxygenTheme.colorScheme.outline,
        selectedFill: Color = OxygenTheme.colorScheme.primary,
        dot: Color = OxygenTheme.colorScheme.controlDot,
        disabledBorder: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
        disabledFill: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): RadioButtonColors = remember(
        border,
        selectedFill,
        dot,
        disabledBorder,
        disabledFill,
    ) {
        RadioButtonColors(
            border = border,
            selectedFill = selectedFill,
            dot = dot,
            disabledBorder = disabledBorder,
            disabledFill = disabledFill,
        )
    }
}

@Immutable
data class RadioButtonColors(
    val border: Color,
    val selectedFill: Color,
    val dot: Color,
    val disabledBorder: Color,
    val disabledFill: Color,
) {
    internal fun resolveBorderColor(enabled: Boolean): Color = if (enabled) border else disabledBorder

    internal fun resolveFillColor(enabled: Boolean): Color = if (enabled) selectedFill else disabledFill
}
