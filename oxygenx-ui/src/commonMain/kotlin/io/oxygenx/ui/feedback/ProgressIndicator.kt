// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.feedback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import io.oxygenx.motion.OxygenLoopSpec
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.motion.rememberOxygenLoop
import io.oxygenx.theme.OxygenTheme
import kotlin.math.abs

/**
 * 线性进度：极细的胶囊轨道（厚度取 `size.progress.linear.height`，与顶栏分段指示器同档），
 * 不采用 M3 的 4dp 厚度 —— 项目整体是「细条」风格。
 *
 * - Spec ID: `CMP-PROGRESS`
 * - Baseline: `OxygenX P3 细条基线 / OnePlus 9R (LE2100) / 2026-11-14`
 * - Reference: `catalog/reference/README.md`（细条厚度取自顶栏分段指示器实测 6px @450dpi）
 * - Motion: 确定态走 [motion]（`MOT-STATE-002`）；不确定态走 [loop] 的
 *   `MOT-PROGRESS-001`，一轮循环内条从头出现、右移、尾部收尽。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier；轨道宽度由调用方约束决定。
 * @param progress 确定态进度 `0f..1f`；传 `null` 表示不确定态。
 * @param enabled 是否使用禁用色；不确定态也可禁用（例如后台任务被暂停）。
 * @param colors 活动段与轨道颜色集合。
 * @param motion 确定态进度插值的动效规格。
 * @param loop 不确定态的循环规格。
 */
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    enabled: Boolean = true,
    colors: ProgressColors = ProgressDefaults.colors(),
    motion: OxygenMotionSpec = ProgressDefaults.Motion,
    loop: OxygenLoopSpec = ProgressDefaults.LinearLoop,
) {
    val indeterminate = progress == null
    val animated = rememberOxygenAnimation(spec = motion, initialValue = progress ?: 0f)
    LaunchedEffect(progress) {
        if (progress != null) animated.animateTo(progress.coerceIn(0f, 1f))
    }
    // 不确定态才跑循环；确定态不保留没有消费者的无限动画。
    val phase = if (indeterminate) rememberOxygenLoop(loop) else 0f

    val activeColor = if (enabled) colors.active else colors.activeDisabled
    val trackColor = if (enabled) colors.track else colors.trackDisabled

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(ProgressDefaults.LinearHeight)
            .indicatorSemantics(progress),
    ) {
        val radius = size.height / 2f
        val corner = CornerRadius(radius, radius)
        drawRoundRect(color = trackColor, cornerRadius = corner)
        if (indeterminate) {
            val start = linearIndeterminateStart(phase).coerceIn(0f, 1f)
            val end = linearIndeterminateEnd(phase).coerceIn(0f, 1f)
            if (end > start) {
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(size.width * start, 0f),
                    size = Size(size.width * (end - start), size.height),
                    cornerRadius = corner,
                )
            }
        } else {
            val fraction = animated.value.coerceIn(0f, 1f)
            if (fraction > 0f) {
                drawRoundRect(
                    color = activeColor,
                    size = Size(size.width * fraction, size.height),
                    cornerRadius = corner,
                )
            }
        }
    }
}

/**
 * 圆环进度：细描边圆环（描边取 `size.progress.circular.stroke`，与线性进度同厚度）。
 *
 * - Spec ID: `CMP-PROGRESS-CIRCULAR`
 * - Baseline: `OxygenX P3 细条基线 / OnePlus 9R (LE2100) / 2026-11-14`
 * - Reference: `catalog/reference/README.md`
 * - Motion: 确定态走 [motion]；不确定态走 [loop] 的 `MOT-PROGRESS-002`，
 *   同一周期内匀速旋转一圈、弧长往返伸缩。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param progress 确定态进度 `0f..1f`；传 `null` 表示不确定态。
 * @param enabled 是否使用禁用色。
 * @param diameter 圆环直径，默认 `size.progress.circular.size`。
 * @param strokeWidth 描边宽度，默认 `size.progress.circular.stroke`。
 * @param colors 活动弧与底环颜色集合。
 * @param motion 确定态进度插值的动效规格。
 * @param loop 不确定态的循环规格。
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    enabled: Boolean = true,
    diameter: Dp = ProgressDefaults.CircularSize,
    strokeWidth: Dp = ProgressDefaults.CircularStroke,
    colors: ProgressColors = ProgressDefaults.colors(),
    motion: OxygenMotionSpec = ProgressDefaults.Motion,
    loop: OxygenLoopSpec = ProgressDefaults.CircularLoop,
) {
    val indeterminate = progress == null
    val animated = rememberOxygenAnimation(spec = motion, initialValue = progress ?: 0f)
    LaunchedEffect(progress) {
        if (progress != null) animated.animateTo(progress.coerceIn(0f, 1f))
    }
    val phase = if (indeterminate) rememberOxygenLoop(loop) else 0f

    val activeColor = if (enabled) colors.active else colors.activeDisabled
    val trackColor = if (enabled) colors.track else colors.trackDisabled

    Canvas(
        modifier = modifier
            .size(diameter)
            .indicatorSemantics(progress),
    ) {
        val stroke = strokeWidth.toPx()
        if (stroke <= 0f) return@Canvas
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(stroke / 2f, stroke / 2f)
        val style = Stroke(width = stroke, cap = StrokeCap.Round)
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style,
        )
        if (indeterminate) {
            drawArc(
                color = activeColor,
                startAngle = -90f + phase * 360f,
                sweepAngle = circularIndeterminateSweep(phase) * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = style,
            )
        } else {
            val fraction = animated.value.coerceIn(0f, 1f)
            if (fraction > 0f) {
                drawArc(
                    color = activeColor,
                    startAngle = -90f,
                    sweepAngle = 360f * fraction,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = style,
                )
            }
        }
    }
}

object ProgressDefaults {
    val LinearHeight: Dp
        @Composable get() = OxygenTheme.spacing.progressLinearHeight

    val CircularSize: Dp
        @Composable get() = OxygenTheme.spacing.progressCircularSize

    val CircularStroke: Dp
        @Composable get() = OxygenTheme.spacing.progressCircularStroke

    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.switchToggle()

    val LinearLoop: OxygenLoopSpec
        @Composable get() = OxygenMotionDefaults.progressLinearLoop()

    val CircularLoop: OxygenLoopSpec
        @Composable get() = OxygenMotionDefaults.progressCircularLoop()

    /** 下拉刷新松手后的回弹与收尾。 */
    val RefreshMotion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.pullRefresh()

    @Composable
    fun colors(
        active: Color = OxygenTheme.colorScheme.primary,
        track: Color = OxygenTheme.colorScheme.surfaceContainerHighest,
        activeDisabled: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
        trackDisabled: Color = OxygenTheme.colorScheme.surfaceDisabled,
    ): ProgressColors = remember(active, track, activeDisabled, trackDisabled) {
        ProgressColors(
            active = active,
            track = track,
            activeDisabled = activeDisabled,
            trackDisabled = trackDisabled,
        )
    }
}

@Immutable
data class ProgressColors(
    val active: Color,
    val track: Color,
    val activeDisabled: Color,
    val trackDisabled: Color,
)

/**
 * 不确定态两端的相位差：尾部比头部晚 [LINEAR_INDETERMINATE_LEAD] 个周期出发。
 * 差值同时决定循环中段可见条的最大长度。
 */
internal const val LINEAR_INDETERMINATE_LEAD = 0.35f

/** 圆环不确定态弧长比例的下限，避免收尽时完全看不见。 */
internal const val CIRCULAR_INDETERMINATE_MIN_SWEEP = 0.08f

/** 圆环不确定态弧长比例的上限，留出「缺口」让旋转方向可读。 */
internal const val CIRCULAR_INDETERMINATE_MAX_SWEEP = 0.75f

/**
 * 线性不确定态的条起点（`0f..1f`，可能为负，表示还在轨道左侧之外）。
 *
 * 与 [linearIndeterminateEnd] 共用同一条线性推进：`phase = 0` 时两端重合于轨道外，
 * 随后头部先推进、尾部滞后 [LINEAR_INDETERMINATE_LEAD] 追上，形成 M3 的
 * 「长出 → 右移 → 收尽」观感；不用三角波，因此不会出现条从中间向外长的怪相。
 */
internal fun linearIndeterminateStart(phase: Float): Float = indeterminateEdge(phase - LINEAR_INDETERMINATE_LEAD)

/** 线性不确定态的条终点（`0f..1f`，封顶在轨道右端）。 */
internal fun linearIndeterminateEnd(phase: Float): Float = indeterminateEdge(phase)

private fun indeterminateEdge(phase: Float): Float {
    val span = 1f - LINEAR_INDETERMINATE_LEAD
    val local = (phase / span).coerceIn(0f, 1f)
    return -LINEAR_INDETERMINATE_LEAD + (1f + LINEAR_INDETERMINATE_LEAD) * local
}

/** 圆环不确定态的弧长比例：在下限与上限之间按三角波往返，一周一次。 */
internal fun circularIndeterminateSweep(phase: Float): Float {
    val triangle = 1f - abs(2f * (phase % 1f) - 1f)
    return CIRCULAR_INDETERMINATE_MIN_SWEEP + (CIRCULAR_INDETERMINATE_MAX_SWEEP - CIRCULAR_INDETERMINATE_MIN_SWEEP) * triangle
}

/** 不确定态与确定态共用一份进度语义：不确定态报 `Indeterminate`，屏幕阅读器据此播报「加载中」。 */
private fun Modifier.indicatorSemantics(progress: Float?): Modifier = semantics {
    progressBarRangeInfo = if (progress == null) {
        ProgressBarRangeInfo.Indeterminate
    } else {
        ProgressBarRangeInfo(progress.coerceIn(0f, 1f), 0f..1f)
    }
}
