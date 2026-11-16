// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.oxygenx.theme.generated.GeneratedTokens
import androidx.compose.animation.core.spring as composeSpring

/**
 * 动效规格：一个 `Spec ID` 对应 enter / exit 两份可执行参数。
 * 组件通过 `motion` 参数暴露此类型，catalog 可据此调参。
 */
@Stable
class OxygenMotionSpec(
    val specId: String,
    val enter: AnimationSpec<Float>,
    val exit: AnimationSpec<Float>,
)

object OxygenMotion {
    fun spring(
        dampingRatio: Float,
        stiffness: Float,
        visibilityThreshold: Float = 0.001f,
    ): SpringSpec<Float> = composeSpring(
        dampingRatio = dampingRatio,
        stiffness = stiffness,
        visibilityThreshold = visibilityThreshold,
    )

    /** 解析 `0.2,0.0,0.0,1.0` 形式的四控制点曲线；不合法立即抛错。 */
    fun cubicBezier(curve: String): Easing {
        val points = curve.split(',').map { it.trim() }
        require(points.size == 4) { "曲线必须由 4 个控制点组成：$curve" }
        val (x1, y1, x2, y2) = points.map { it.toFloat() }
        return CubicBezierEasing(x1, y1, x2, y2)
    }

    fun fade(
        durationMillis: Long,
        curve: String = GeneratedTokens.EASING_STANDARD_CURVE,
    ): TweenSpec<Float> = TweenSpec(
        durationMillis = durationMillis.toInt().coerceAtLeast(1),
        easing = cubicBezier(curve),
    )
}

object OxygenMotionDefaults {
    private val switchToggleSpec: OxygenMotionSpec by lazy {
        val spring = OxygenMotion.spring(
            dampingRatio = GeneratedTokens.SPRING_SNAPPY_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_SNAPPY_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_SNAPPY_VISIBILITY_THRESHOLD,
        )
        OxygenMotionSpec(
            specId = "MOT-STATE-002",
            enter = spring,
            exit = spring,
        )
    }

    /** 开关拇指位移与状态切换（`spec.switch.toggle`）。 */
    fun switchToggle(): OxygenMotionSpec = switchToggleSpec

    private val appBarCollapseSpec: OxygenMotionSpec by lazy {
        val spring = OxygenMotion.spring(
            dampingRatio = GeneratedTokens.SPRING_SNAPPY_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_SNAPPY_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_SNAPPY_VISIBILITY_THRESHOLD,
        )
        OxygenMotionSpec(
            specId = "MOT-STATE-003",
            enter = spring,
            exit = spring,
        )
    }

    /** 大顶栏收拢/展开的收尾动画（`spec.appbar.collapse`）。 */
    fun appBarCollapse(): OxygenMotionSpec = appBarCollapseSpec

    private val dialogSlideSpec: OxygenMotionSpec by lazy {
        // 弹窗进出场用快弹簧 spring.dialog（2400 / 0.95 / 0.006，约 150ms 走完）。
        // 复用 spring.snappy 时 400 的刚度和 0.001 的阈值会把尾巴拖到 600ms 量级。
        val spring = OxygenMotion.spring(
            dampingRatio = GeneratedTokens.SPRING_DIALOG_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_DIALOG_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_DIALOG_VISIBILITY_THRESHOLD,
        )
        OxygenMotionSpec(
            specId = "MOT-ENTER-001",
            enter = spring,
            exit = spring,
        )
    }

    /** 弹窗下滑入场 / 下滑退场（`spec.dialog.slide`）。 */
    fun dialogSlide(): OxygenMotionSpec = dialogSlideSpec

    private val progressLinearLoopSpec: OxygenLoopSpec by lazy {
        OxygenLoopSpec(
            specId = "MOT-PROGRESS-001",
            durationMillis = GeneratedTokens.DURATION_PROGRESS_LINEAR_CYCLE_MILLIS,
            easing = LinearEasing,
        )
    }

    /** 线性不确定态进度：一轮循环内条从头出现、右移、尾部收尽（`duration.progress.linear.cycle`）。 */
    fun progressLinearLoop(): OxygenLoopSpec = progressLinearLoopSpec

    private val progressCircularLoopSpec: OxygenLoopSpec by lazy {
        OxygenLoopSpec(
            specId = "MOT-PROGRESS-002",
            durationMillis = GeneratedTokens.DURATION_PROGRESS_CIRCULAR_ROTATION_MILLIS,
            easing = LinearEasing,
        )
    }

    /** 圆环不确定态进度：同一周期内匀速旋转一整圈，弧长同时往返伸缩（`duration.progress.circular.rotation`）。 */
    fun progressCircularLoop(): OxygenLoopSpec = progressCircularLoopSpec

    private val pullRefreshSpec: OxygenMotionSpec by lazy {
        val spring = OxygenMotion.spring(
            dampingRatio = GeneratedTokens.SPRING_SNAPPY_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_SNAPPY_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_SNAPPY_VISIBILITY_THRESHOLD,
        )
        OxygenMotionSpec(
            specId = "MOT-STATE-004",
            enter = spring,
            exit = spring,
        )
    }

    /** 下拉刷新松手后的回弹与收尾（`spec.switch.toggle` 同级弹簧，手感与开关一致）。 */
    fun pullRefresh(): OxygenMotionSpec = pullRefreshSpec

    private val tabSwitchSpec: OxygenMotionSpec by lazy {
        val spring = OxygenMotion.spring(
            dampingRatio = GeneratedTokens.SPRING_SNAPPY_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_SNAPPY_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_SNAPPY_VISIBILITY_THRESHOLD,
        )
        OxygenMotionSpec(
            specId = "MOT-STATE-005",
            enter = spring,
            exit = spring,
        )
    }

    /** Tab 指示器滑动与文字颜色过渡（`spec.tab.switch`）。 */
    fun tabSwitch(): OxygenMotionSpec = tabSwitchSpec

    private val scrollOverscrollSpec: OxygenMotionSpec by lazy {
        // 过冲回弹（`spring.overscroll`：900 / 0.6 / 0.5）。阻尼 0.6 是「紧凑但会回弹一次」
        // 的关键：阻尼 1.0 只会单调归位、看不到回弹，0.6 则过零弹回一次后迅速静止。
        // 阈值单位是 px——这里动画的值就是位移像素，不是 0..1 的进度。
        val spring = OxygenMotion.spring(
            dampingRatio = GeneratedTokens.SPRING_OVERSCROLL_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_OVERSCROLL_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_OVERSCROLL_VISIBILITY_THRESHOLD,
        )
        OxygenMotionSpec(
            specId = "MOT-SCROLL-001",
            enter = spring,
            exit = spring,
        )
    }

    /** 滚动到边界后的过冲回弹（`spec.scroll.overscroll`）。 */
    fun scrollOverscroll(): OxygenMotionSpec = scrollOverscrollSpec
}

/**
 * 循环动画规格：用于「没有终点、按固定周期重复」的不确定态动效（进度条 / 圆环 / 刷新中的旋转）。
 *
 * 与 [OxygenMotionSpec] 分开：后者表达 enter / exit 的往返语义，把循环塞进去会让
 * 「进度回退」与「下一轮开始」混为一谈，可中断语义也不成立。
 */
@Immutable
class OxygenLoopSpec(
    val specId: String,
    val durationMillis: Long,
    val easing: Easing,
)

/** 以 [spec] 的周期循环产出 `0f..1f` 的相位，每轮结束立即从头开始。 */
@Composable
fun rememberOxygenLoop(spec: OxygenLoopSpec): Float {
    val transition = rememberInfiniteTransition(label = spec.specId)
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = spec.durationMillis.toInt().coerceAtLeast(1),
                easing = spec.easing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = spec.specId,
    )
    return phase
}

/**
 * 可在任意时刻被打断并接续当前值的动画句柄。
 * 不使用 `LaunchedEffect(key)` 重建动画，因此重复触发不会回到起点。
 */
@Stable
class OxygenAnimation internal constructor(
    private val animatable: Animatable<Float, AnimationVector1D>,
    private val spec: OxygenMotionSpec,
) {
    val value: Float get() = animatable.value

    val isRunning: Boolean get() = animatable.isRunning

    suspend fun animateTo(target: Float) {
        val base = when {
            target >= animatable.value -> spec.enter
            else -> spec.exit
        }
        animatable.animateTo(target, base)
    }

    suspend fun snapTo(target: Float) {
        animatable.snapTo(target)
    }
}

@Composable
fun rememberOxygenAnimation(
    spec: OxygenMotionSpec,
    initialValue: Float = 0f,
): OxygenAnimation {
    val animatable = remember { Animatable(initialValue) }
    return remember(animatable, spec) {
        OxygenAnimation(animatable, spec)
    }
}
