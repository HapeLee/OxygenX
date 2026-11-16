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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme

/**
 * 复选框：16dp 圆角方块，未选中是 1.5dp 中性描边，选中是强调色实心块加白色对勾。
 *
 * - Spec ID: `CMP-CHECKBOX`
 * - Baseline: `Settings 11 (com.android.settings) / OnePlus 9R (LE2100) / ColorOS 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-CHECKBOX/`
 * - Motion: 选中时实心块放大、对勾跟随出现（`MOT-STATE-002`）。
 * - Status: `draft`
 *
 * @param checked 是否勾选。
 * @param onCheckedChange 请求切换时回调（参数是**切换后**的值）；`null` 表示只展示、不接收点击
 *   （整行已经承担点击的场合，例如 `SettingsCheckItem`），与 M3 `Checkbox` 同口径。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可用。
 * @param colors 方框、描边与对勾颜色。
 * @param motion 状态切换动效规格。
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    motion: OxygenMotionSpec = CheckboxDefaults.Motion,
) {
    val size = CheckboxDefaults.Size
    val borderWidth = CheckboxDefaults.BorderWidth
    val cornerRadius = CheckboxDefaults.CornerRadius
    val progress = rememberOxygenAnimation(spec = motion)
    LaunchedEffect(checked, progress) { progress.animateTo(if (checked) 1f else 0f) }
    val change = onCheckedChange
    val toggle: (() -> Unit)? = if (change != null) {
        { change(!checked) }
    } else {
        null
    }

    Canvas(
        modifier = modifier
            .size(size)
            .then(
                if (toggle != null && enabled) {
                    Modifier.clickable(role = Role.Checkbox) { toggle() }
                } else {
                    Modifier
                },
            )
            .semantics {
                toggleableState = ToggleableState(checked)
                if (!enabled) disabled()
            },
    ) {
        val box = this.size.minDimension
        val stroke = borderWidth.toPx()
        val radius = cornerRadius.toPx()

        // 底框常驻：强调色从描边往内加粗，外沿保持不动。
        drawRoundRect(
            color = colors.resolveBorderColor(enabled),
            topLeft = Offset(stroke / 2f, stroke / 2f),
            size = Size(box - stroke, box - stroke),
            cornerRadius = CornerRadius(radius),
            style = Stroke(width = stroke),
        )
        if (progress.value > 0f) {
            // 强调色从描边往内加粗，外沿固定。这里画的是「圆角外框挖掉圆角内框」的环，
            // 而不是加粗描边：描边宽度一旦超过圆角半径，四个角就会被磨成直角。
            val fillColor = colors.resolveFillColor(enabled)
            // 外框圆角与常驻底框的外沿重合（底框是内缩半线宽的描边），填充才能完全盖住它。
            val outerRadius = radius + stroke / 2f
            // 前半程向内包围到一半厚度就停手：再往里收，洞会小到只剩几个 dp，
            // 那时的圆角已经看不出来，压在对勾上就是一块脏点。
            val spread = (progress.value * 2f).coerceAtMost(1f)
            val inset = stroke + (box / 2f - stroke) * 0.5f * spread
            val innerSize = box - inset * 2f
            // 洞按外框等比缩小，圆角跟着收，画出来始终是同一形状的缩放版。
            val innerRadius = outerRadius * (innerSize / box)
            // 剩下的洞改用淡入收口，和对勾一起出现。
            val holeAlpha = ((progress.value - 0.5f) / 0.35f).coerceIn(0f, 1f)
            if (holeAlpha >= 1f) {
                // 环和内框是两次绘制，接缝处会各叠一层抗锯齿、留下一条浅线，
                // 所以洞填满之后直接画整块。
                drawRoundRect(
                    color = fillColor,
                    size = Size(box, box),
                    cornerRadius = CornerRadius(outerRadius),
                )
            } else {
                val ring = Path().apply {
                    fillType = PathFillType.EvenOdd
                    addRoundRect(RoundRect(0f, 0f, box, box, outerRadius, outerRadius))
                    addRoundRect(
                        RoundRect(
                            inset,
                            inset,
                            box - inset,
                            box - inset,
                            innerRadius,
                            innerRadius,
                        ),
                    )
                }
                drawPath(path = ring, color = fillColor)
                if (holeAlpha > 0f) {
                    // 内框外扩 1px，压住它和环之间的抗锯齿缝。
                    val bleed = 1f
                    drawRoundRect(
                        color = fillColor.copy(alpha = fillColor.alpha * holeAlpha),
                        topLeft = Offset(inset - bleed, inset - bleed),
                        size = Size(innerSize + bleed * 2f, innerSize + bleed * 2f),
                        cornerRadius = CornerRadius(innerRadius + bleed),
                    )
                }
            }
            // 对勾等包围走完大半程再出现，避免和填充过程抢视觉。
            val checkProgress = ((progress.value - 0.55f) / 0.45f).coerceIn(0f, 1f)
            if (checkProgress > 0f) {
                // 取资源向量 op_checkbox 的相对坐标：对勾横跨方块宽度的 62%，
                // 线宽约 7%（16dp 方块上约 1.1dp），而不是常见的粗勾。
                val center = box / 2f
                val scale = 0.8f + 0.2f * checkProgress
                val strokeWidth = box * 0.07f
                val path = Path().apply {
                    moveTo(center + (box * -0.31f) * scale, center + (box * 0.01f) * scale)
                    lineTo(center + (box * -0.07f) * scale, center + (box * 0.20f) * scale)
                    lineTo(center + (box * 0.31f) * scale, center + (box * -0.18f) * scale)
                }
                drawPath(
                    path = path,
                    color = colors.check.copy(alpha = colors.check.alpha * checkProgress),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}

object CheckboxDefaults {
    val Size: Dp
        @Composable get() = OxygenTheme.spacing.controlCheckboxSize

    val BorderWidth: Dp
        @Composable get() = OxygenTheme.spacing.controlBorderWidth

    /** 方块圆角：资源值 op_checkbox 在 63 视口里用 8 的圆角，折合约 2dp。 */
    val CornerRadius: Dp
        @Composable get() = OxygenTheme.spacing.controlCornerRadius

    val Motion: OxygenMotionSpec
        @Composable get() = ChoiceMotion

    @Composable
    fun colors(
        border: Color = OxygenTheme.colorScheme.outline,
        checkedFill: Color = OxygenTheme.colorScheme.primary,
        check: Color = OxygenTheme.colorScheme.controlDot,
        disabledBorder: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
        disabledFill: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): CheckboxColors = remember(
        border,
        checkedFill,
        check,
        disabledBorder,
        disabledFill,
    ) {
        CheckboxColors(
            border = border,
            checkedFill = checkedFill,
            check = check,
            disabledBorder = disabledBorder,
            disabledFill = disabledFill,
        )
    }
}

@Immutable
data class CheckboxColors(
    val border: Color,
    val checkedFill: Color,
    val check: Color,
    val disabledBorder: Color,
    val disabledFill: Color,
) {
    internal fun resolveBorderColor(enabled: Boolean): Color = if (enabled) border else disabledBorder

    internal fun resolveFillColor(enabled: Boolean): Color = if (enabled) checkedFill else disabledFill
}
