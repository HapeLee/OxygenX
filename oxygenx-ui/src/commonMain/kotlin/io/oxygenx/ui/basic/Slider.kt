// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 滑块：轨道 + 拇指，支持点击定位与拖动，按 `steps` 量化取值。
 *
 * - Spec ID: `CMP-SLIDER`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-14`
 * - Reference: `catalog/reference/CMP-SLIDER/`
 * - Motion: 拇指直接跟随指针、不额外插值；只有按压/拖动时的圆形水波纹走
 *   `MOT-STATE-002`（与开关同一套：拇指处扩散到 32dp 后淡出）。
 * - Status: `draft`
 *
 * @param value 当前值，应落在 [valueRange] 内。
 * @param onValueChange 拖动/点击过程中的取值回调。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互。
 * @param valueRange 取值区间。
 * @param steps 量化档位数；0 表示连续取值。
 * @param colors 轨道、拇指与水波纹颜色集合。
 * @param motion 按压水波纹的扩散规格。
 * @param interactionSource 交互状态源（M3 口径），可外部观察按压；`null` 时内部自建。
 * @param onValueChangeFinished 手势结束、键盘每一步或无障碍调节完成后回调，可用于持久化。
 * @param track 轨道内容槽：给了之后默认轨道（空轨 / 进度 / 档位点）不再绘制，自己按
 *   [SliderScope] 的 `fraction` 画；槽位铺满整行高度。
 * @param thumb 拇指内容槽：给了之后默认拇指圆不再绘制，按 `fraction` 定位到轨道位置。
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors(),
    motion: OxygenMotionSpec = SliderDefaults.Motion,
    interactionSource: MutableInteractionSource? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    track: (@Composable SliderScope.() -> Unit)? = null,
    thumb: (@Composable SliderScope.() -> Unit)? = null,
) {
    require(steps >= 0) { "steps must be non-negative" }
    require(value.isFinite()) { "value must be finite" }
    require(valueRange.start.isFinite() && valueRange.endInclusive.isFinite() && valueRange.start <= valueRange.endInclusive) {
        "valueRange must be finite and ordered"
    }
    val trackHeight = SliderDefaults.TrackHeight
    val thumbSize = SliderDefaults.ThumbSize
    val rippleRadius = SliderDefaults.RippleRadius
    val tickSize = SliderDefaults.TickSize
    var pressed by remember { mutableStateOf(false) }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val gestures = rememberCoroutineScope()
    val ripple = rememberOxygenAnimation(spec = motion)
    LaunchedEffect(pressed, ripple) { ripple.animateTo(if (pressed) 1f else 0f) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnFinished by rememberUpdatedState(onValueChangeFinished)
    val currentValue by rememberUpdatedState(value)
    val fraction = valueToFraction(value, valueRange)
    val scope = remember(fraction, enabled, steps, valueRange) {
        SliderScope(
            fraction = fraction,
            enabled = enabled,
            steps = steps,
            valueRange = valueRange,
        )
    }
    fun commit(target: Float): Boolean {
        val snapped = snapSliderValue(target, valueRange, steps)
        if (snapped == currentValue.coerceIn(valueRange)) return false
        currentOnValueChange(snapped)
        currentOnFinished?.invoke()
        return true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(OxygenTheme.spacing.touchTargetMin)
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (!enabled) return@onKeyEvent false
                val target = sliderKeyTarget(currentValue, event.key, valueRange, steps, isRtl)
                    ?: return@onKeyEvent false
                if (event.type == KeyEventType.KeyDown) commit(target)
                true
            }
            .focusable(enabled = enabled, interactionSource = source)
            .pointerInput(enabled, valueRange, steps, thumbSize, isRtl, source) {
                if (!enabled) return@pointerInput
                val radius = thumbSize.toPx() / 2f
                awaitEachGesture {
                    fun update(pointerX: Float) {
                        val trackWidth = size.width - radius * 2f
                        if (trackWidth <= 0f) return
                        val visualFraction = ((pointerX - radius) / trackWidth).coerceIn(0f, 1f)
                        val rawFraction = if (isRtl) 1f - visualFraction else visualFraction
                        currentOnValueChange(fractionToValue(rawFraction, valueRange, steps))
                    }
                    val down = awaitFirstDown(requireUnconsumed = false)
                    focusRequester.requestFocus()
                    pressed = true
                    // 按压态同时写进 interactionSource：外部 collectIsPressedAsState 能拿到。
                    // 指针作用域是受限挂起作用域，emit 必须转到普通 scope 里。
                    val press = PressInteraction.Press(down.position)
                    gestures.launch { source.emit(press) }
                    update(down.position.x)
                    down.consume()
                    var completed = false
                    try {
                        completed = drag(down.id) { change ->
                            update(change.position.x)
                            change.consume()
                        }
                    } finally {
                        // 抬手或被取消都要收回水波纹，否则会卡在按下态。
                        pressed = false
                        gestures.launch {
                            source.emit(if (completed) PressInteraction.Release(press) else PressInteraction.Cancel(press))
                        }
                        currentOnFinished?.invoke()
                    }
                }
            }
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(value.coerceIn(valueRange), valueRange, steps)
                if (!enabled) {
                    disabled()
                } else {
                    setProgress { target ->
                        if (target.isFinite()) commit(target) else false
                    }
                }
            },
    ) {
        // 层序：默认轨道 → 自定义轨道 → 水波纹 + 默认拇指 → 自定义拇指。
        // 水波纹画在拇指下面一层，压住水波纹的是拇指而不是轨道。
        if (track == null) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawSliderTrack(
                    fraction = fraction,
                    steps = steps,
                    thumbSize = thumbSize,
                    trackHeight = trackHeight,
                    tickSize = tickSize,
                    trackColor = colors.resolveTrackColor(enabled),
                    activeColor = colors.resolveActiveColor(enabled),
                    tickColor = colors.tick,
                    isRtl = isRtl,
                )
            }
        } else {
            SliderTrackSlot(modifier = Modifier.matchParentSize()) { track(scope) }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = thumbSize.toPx() / 2f
            val centerY = size.height / 2f
            val thumbX = sliderThumbX(size.width, radius, fraction, isRtl)
            if (focused && enabled) {
                drawCircle(
                    color = colors.ripple,
                    radius = rippleRadius.toPx(),
                    center = Offset(thumbX, centerY),
                    style = Stroke(width = trackHeight.toPx()),
                )
            }
            val progress = ripple.value
            if (progress > 0f) {
                // 按压/拖动的圆形水波纹：圆心跟着拇指走，从拇指半径扩散到 16dp 半径。
                val ripplePx = rippleRadius.toPx()
                drawCircle(
                    color = colors.ripple,
                    radius = radius + (ripplePx - radius) * progress,
                    center = Offset(thumbX, centerY),
                    alpha = progress,
                )
            }
            if (thumb == null) {
                drawCircle(
                    color = colors.resolveThumbColor(enabled),
                    radius = radius,
                    center = Offset(thumbX, centerY),
                )
            }
        }

        if (thumb != null) {
            SliderThumbSlot(fraction = fraction, isRtl = isRtl, modifier = Modifier.matchParentSize()) {
                thumb(scope)
            }
        }
    }
}

/**
 * 滑块槽位的作用域：自定义轨道 / 拇指需要知道当前位置与档位分布。
 *
 * @param fraction 当前值在区间内的位置（0..1）。
 * @param enabled 是否可交互，自定义内容据此选启用 / 禁用色。
 * @param steps 量化档位数，`0` 表示连续。
 * @param valueRange 取值区间。
 * @param tickFractions 各档位的位置（含两端），`steps == 0` 时为空。
 */
@Immutable
data class SliderScope(
    val fraction: Float,
    val enabled: Boolean,
    val steps: Int,
    val valueRange: ClosedFloatingPointRange<Float>,
    val tickFractions: List<Float> = if (steps > 0) {
        buildList(steps + 2) {
            for (index in 0..steps + 1) add(index.toFloat() / (steps + 1))
        }
    } else {
        emptyList()
    },
)

/** 默认轨道：空轨 + 进度 + 档位点。 */
private fun DrawScope.drawSliderTrack(
    fraction: Float,
    steps: Int,
    thumbSize: Dp,
    trackHeight: Dp,
    tickSize: Dp,
    trackColor: Color,
    activeColor: Color,
    tickColor: Color,
    isRtl: Boolean,
) {
    val radius = thumbSize.toPx() / 2f
    // 拇指行程是「整宽减两端半径」，轨道本身铺满整宽：真机滑块的空轨是一条
    // 通栏胶囊，拇指压在轨道上滑动，而不是轨道被两端各缩进半个拇指。
    val travelStartX = radius
    val travelEndX = (size.width - radius).coerceAtLeast(travelStartX)
    val centerY = size.height / 2f
    val strokeWidth = trackHeight.toPx()
    val thumbX = sliderThumbX(size.width, radius, fraction, isRtl)

    drawLine(
        color = trackColor,
        start = Offset(strokeWidth / 2f, centerY),
        end = Offset(size.width - strokeWidth / 2f, centerY),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
    if (thumbX > 0f) {
        drawLine(
            color = activeColor,
            start = Offset(if (isRtl) size.width - strokeWidth / 2f else strokeWidth / 2f, centerY),
            end = Offset(thumbX, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
    if (steps > 0) {
        // 步进滑块在每一档位置画一个圆点，真机「显示大小」页即 5 个点对应 5 档。
        val tickRadius = tickSize.toPx() / 2f
        val intervals = steps + 1
        for (index in 0..intervals) {
            val offset = travelStartX + (travelEndX - travelStartX) * (index.toFloat() / intervals)
            drawCircle(
                color = tickColor,
                radius = tickRadius,
                center = Offset(offset, centerY),
            )
        }
    }
}

/** 轨道槽位：铺满整行的固定约束里测量，左上角摆放。 */
@Composable
private fun SliderTrackSlot(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val placeable = measurables.first().measure(Constraints.fixed(width, height))
        layout(width, height) { placeable.place(0, 0) }
    }
}

/** 拇指槽位：按 [fraction] 平移到轨道位置，垂直居中。 */
@Composable
private fun SliderThumbSlot(
    fraction: Float,
    isRtl: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val thumbSize = SliderDefaults.ThumbSize
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val placeable = measurables.first().measure(
            Constraints(maxWidth = width, maxHeight = height),
        )
        val radius = thumbSize.toPx() / 2f
        val thumbX = sliderThumbX(width.toFloat(), radius, fraction, isRtl)
        layout(width, height) {
            placeable.place(
                (thumbX - placeable.width / 2f).roundToInt(),
                ((height - placeable.height) / 2f).roundToInt(),
            )
        }
    }
}

/** 拇指中心相对整行左边的 x（像素）。 */
private fun sliderThumbX(width: Float, radius: Float, fraction: Float, isRtl: Boolean): Float {
    val start = radius
    val end = (width - radius).coerceAtLeast(start)
    val visualFraction = if (isRtl) 1f - fraction else fraction
    return start + (end - start) * visualFraction.coerceIn(0f, 1f)
}

/** 键盘以逻辑值调节；水平箭头随 RTL 镜像，Home / End 始终表示区间端点。 */
internal fun sliderKeyTarget(
    value: Float,
    key: Key,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    isRtl: Boolean,
): Float? {
    val intervals = if (steps > 0) steps + 1 else 100
    val delta = (valueRange.endInclusive - valueRange.start) / intervals
    val page = (intervals / 10).coerceAtLeast(1)
    val target = when (key) {
        Key.DirectionRight -> value + if (isRtl) -delta else delta
        Key.DirectionLeft -> value + if (isRtl) delta else -delta
        Key.DirectionUp -> value + delta
        Key.DirectionDown -> value - delta
        Key.MoveHome -> valueRange.start
        Key.MoveEnd -> valueRange.endInclusive
        Key.PageUp -> value + delta * page
        Key.PageDown -> value - delta * page
        else -> return null
    }
    return snapSliderValue(target, valueRange, steps)
}

object SliderDefaults {
    val TrackHeight: Dp
        @Composable get() = OxygenTheme.spacing.sliderTrackHeight

    val ThumbSize: Dp
        @Composable get() = OxygenTheme.spacing.sliderThumbSize

    /** 按压水波纹半径：直径 32dp，比 15dp 拇指大一圈，压在轨道上。 */
    val RippleRadius: Dp
        @Composable get() = OxygenTheme.spacing.sliderRippleRadius

    /** 步进滑块的档位圆点直径，steps > 0 时自动绘制。 */
    val TickSize: Dp
        @Composable get() = OxygenTheme.spacing.sliderTickSize

    /** 与开关共用同一套状态动效，保证两种控件的按压反馈手感一致。 */
    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.switchToggle()

    @Composable
    fun colors(
        activeTrack: Color = OxygenTheme.colorScheme.primary,
        inactiveTrack: Color = OxygenTheme.colorScheme.surfaceContainerHighest,
        thumb: Color = OxygenTheme.colorScheme.primary,
        ripple: Color = OxygenTheme.colorScheme.statePressed,
        tick: Color = OxygenTheme.colorScheme.onSurface,
        disabledTrack: Color = OxygenTheme.colorScheme.surfaceDisabled,
        disabledThumb: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): SliderColors = remember(
        activeTrack,
        inactiveTrack,
        thumb,
        ripple,
        tick,
        disabledTrack,
        disabledThumb,
    ) {
        SliderColors(
            activeTrack = activeTrack,
            inactiveTrack = inactiveTrack,
            thumb = thumb,
            ripple = ripple,
            tick = tick,
            disabledTrack = disabledTrack,
            disabledThumb = disabledThumb,
        )
    }
}

@Immutable
data class SliderColors(
    val activeTrack: Color,
    val inactiveTrack: Color,
    val thumb: Color,
    val ripple: Color,
    val tick: Color,
    val disabledTrack: Color,
    val disabledThumb: Color,
) {
    @Stable
    internal fun resolveTrackColor(enabled: Boolean): Color = if (enabled) inactiveTrack else disabledTrack

    @Stable
    internal fun resolveActiveColor(enabled: Boolean): Color = if (enabled) activeTrack else disabledTrack

    @Stable
    internal fun resolveThumbColor(enabled: Boolean): Color = if (enabled) thumb else disabledThumb
}

/** 值 → 0..1 位置；区间退化时返回 0。 */
internal fun valueToFraction(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
): Float {
    val span = valueRange.endInclusive - valueRange.start
    if (span <= 0f) return 0f
    return ((value - valueRange.start) / span).coerceIn(0f, 1f)
}

/** 0..1 位置 → 值，并按 `steps` 量化。 */
internal fun fractionToValue(
    fraction: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
): Float {
    val span = valueRange.endInclusive - valueRange.start
    return snapSliderValue(valueRange.start + span * fraction.coerceIn(0f, 1f), valueRange, steps)
}

/** 把任意值夹到区间内并吸附到最近的量化档位。 */
internal fun snapSliderValue(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
): Float {
    val clamped = value.coerceIn(valueRange.start, valueRange.endInclusive)
    if (steps <= 0) return clamped
    val span = valueRange.endInclusive - valueRange.start
    if (span <= 0f) return valueRange.start
    val stepSize = span / (steps + 1)
    val index = ((clamped - valueRange.start) / stepSize).roundToInt()
    return (valueRange.start + index * stepSize).coerceIn(valueRange.start, valueRange.endInclusive)
}
