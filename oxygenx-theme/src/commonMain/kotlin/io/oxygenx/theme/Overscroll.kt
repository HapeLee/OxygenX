// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import io.oxygenx.theme.generated.GeneratedTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

/**
 * 三次阻尼曲线：`(x - x² + x³/3) * range`，`x ∈ [0, 1]` 是「等效触摸量 / range」。
 *
 * 等价写法 `range/3 × (1 - (1-x)³)`，三个性质正是过冲需要的：
 * - **起点斜率为 1**：刚越界时位移跟手（实际跟手比例 = `dragScale`）；
 * - **单调渐沉**：越往后同样的手指位移带来越少位移，且永远到不了 `range/3`；
 * - **可解析求逆**：[overscrollTouchDistance] 是它的反函数，回弹被手指接管时能把位移
 *   换算回等效触摸量继续算，曲线不会跳变。
 *
 * [range] 取「位移上限 × 3」（见 [OxygenOverscrollEffect.MAX_DISPLACEMENT_TOUCH_RANGE]）：
 * `x = 1` 时位移正好等于位移上限 `space.overscroll.max`。
 */
internal fun overscrollDampingDistance(x: Float, range: Float): Float {
    if (range <= 0f) return 0f
    val t = x.coerceIn(0f, 1f).toDouble()
    val damped = t - t * t + t * t * t / 3.0
    return (damped * range).toFloat()
}

/**
 * 上式的反函数：给定 0..range/3 的位移，返回 0..range 的等效触摸量。
 *
 * 由 `u = x - x² + x³/3 = (1 - (1-x)³) / 3` 解出 `x = 1 - ∛(1 - 3u)`；位移超过 `range/3`
 * （曲线本来是渐近的，只有数值误差才会到这）时按满值处理。
 */
internal fun overscrollTouchDistance(offset: Float, range: Float): Float {
    if (range <= 0f) return 0f
    val u = (abs(offset) / range).coerceIn(0f, 1f / 3f).toDouble()
    // 1f / 3f 在 float 里略大于 1/3，3 * u 会溢出一点点；底数必须夹到 0 以上，
    // 否则 pow 收到负数直接给 NaN。
    val x = 1.0 - (1.0 - 3.0 * u).coerceAtLeast(0.0).pow(1.0 / 3.0)
    return (x * range).toFloat()
}

/**
 * 滚动过冲效果：内容滚到任意一端后继续拖，整块内容跟着位移（**横向 / 纵向各自独立**），
 * 松手用一条紧凑弹簧弹回，甩得越快回得越快。
 *
 * 实现的是官方的 [OverscrollEffect]，因此可以挂到任意可滚动容器上（`LazyColumn`、
 * `verticalScroll`、`HorizontalPager` 的纵向分量、`LazyRow` 的横向分量），而不是只能包一个
 * 专用容器：
 * - `applyToScroll` 把「内容没吃掉的越界位移」按 [overscrollDampingDistance] 吸收成位移；
 * - `applyToFling` 在抬手那一刻就把位移与速度交给弹簧，**并保留甩动惯性**：位移方向上的
 *   速度整段交给弹簧（内容不再滑），反向的甩动保留一部分给内容（`1 / 2.13333`），
 *   `performFling` 的剩余速度再打 `1 / 1.53333` 喂回弹簧；
 * - 手指重新按下或内容重新能滚动时，位移按反函数换算回等效触摸量继续跟手，不会跳。
 *
 * 位移画在 [node] 的 draw 阶段（`translate`），逐帧只重绘不重组。
 * **不能**用 `effectModifier`：Compose 1.12 起它被标记为 `DeprecationLevel.ERROR`，
 * 渲染契约换成了 `node`，只覆写 `effectModifier` 会编译通过但**什么也不画**。
 */
internal class OxygenOverscrollEffect(
    private val maxPx: Float,
    private val dragScale: Float,
    private val settle: AnimationSpec<Float>,
    private val scope: CoroutineScope? = null,
) : OverscrollEffect {
    /** 曲线对应的位移尺度：`x = 1` 时位移正好到 [maxPx]。 */
    private val displacementRange: Float = maxPx * MAX_DISPLACEMENT_TOUCH_RANGE

    /**
     * 曲线对应的触摸尺度：拽到这个距离就把位移拉满。
     *
     * 与 [displacementRange] 差一个 [dragScale]，所以「起点跟手比例 = `dragScale`」这条
     * 令牌语义不变（`dragScale` 越小越沉，需要拖更远才到上限）。
     */
    private val touchRange: Float = if (dragScale > 0f) displacementRange / dragScale else 0f

    // 原始触摸累计量是唯一真源：位移由它经曲线算出来，因此「往回拖 / 回弹被接管」都能
    // 直接在这条轴上加减，不必和位移互相反解（只在接管弹簧时才反解一次）。
    private var rawX = 0f
    private var rawY = 0f

    internal var offsetX by mutableFloatStateOf(0f)
        private set

    internal var offsetY by mutableFloatStateOf(0f)
        private set

    private var settleJobX: Job? = null
    private var settleJobY: Job? = null

    override val isInProgress: Boolean
        get() = offsetX != 0f || offsetY != 0f

    override val node: DelegatableNode = object : Modifier.Node(), DrawModifierNode {
        override fun ContentDrawScope.draw() {
            val dx = offsetX
            val dy = offsetY
            if (dx == 0f && dy == 0f) {
                drawContent()
            } else {
                translate(left = dx, top = dy) { this@draw.drawContent() }
            }
        }
    }

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        val userInput = source == NestedScrollSource.UserInput
        // 手指重新按下：立刻接管，正在跑的回弹让位（位移先换算回等效触摸量，接着算不会跳）。
        if (userInput) {
            cancelSettle()
            syncRawFromOffsets()
        }

        var consumed = Offset.Zero
        var remaining = delta

        // 1) 已经拉出过冲、且这一下是往回拖：先把过冲按曲线收回去，内容原地不动。
        val pullBackY = pullBack(raw = rawY, delta = remaining.y)
        if (pullBackY != 0f) {
            setRawY(rawY + pullBackY)
            consumed += Offset(0f, pullBackY)
            remaining -= Offset(0f, pullBackY)
        }
        val pullBackX = pullBack(raw = rawX, delta = remaining.x)
        if (pullBackX != 0f) {
            setRawX(rawX + pullBackX)
            consumed += Offset(pullBackX, 0f)
            remaining -= Offset(pullBackX, 0f)
        }

        // 2) 剩下的交给内容自己滚。契约要求恰好调用一次，剩余量为 0 时也要调。
        val scrollConsumed = performScroll(remaining)
        consumed += scrollConsumed

        // 3) 内容已经到边界、还有剩余：吸收成过冲位移。
        val leftover = remaining - scrollConsumed
        val inertia = !userInput
        // 回弹已经把位移拉走了一半，惯性剩下的部分直接拒收，别和回弹抢同一个位移。
        // 注意必须先看位移非零：回弹刚归零、job 还在收尾的那一小段时间里，若照旧拒收，
        // 紧接着的惯性第一帧就被判「滚不动」，表现得像「没有惯性」。
        val yAvailable = !(inertia && offsetY != 0f && settleJobY?.isActive == true)
        val xAvailable = !(inertia && offsetX != 0f && settleJobX?.isActive == true)

        if (leftover.y != 0f && yAvailable) {
            consumed = consumeOrSettle(
                axisY = true,
                raw = setRawY(rawY + leftover.y),
                leftover = leftover.y,
                inertia = inertia,
                consumed = consumed,
            )
        }
        if (leftover.x != 0f && xAvailable) {
            consumed = consumeOrSettle(
                axisY = false,
                raw = setRawX(rawX + leftover.x),
                leftover = leftover.x,
                inertia = inertia,
                consumed = consumed,
            )
        }
        return consumed
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity,
    ) {
        // 新的 fling 开始：上一次回弹即使还在收尾也作废，否则它会把这次的惯性吃掉。
        cancelSettle()
        var toFling = velocity

        // 位移方向上的速度整段交给弹簧（内容不再滑）；反向甩动留一部分给内容。
        if (offsetY != 0f && velocity.y != 0f) {
            startSettle(axisY = true, initialVelocity = velocity.y)
            toFling = Velocity(toFling.x, contentVelocity(velocity.y, offsetY))
        }
        if (offsetX != 0f && velocity.x != 0f) {
            startSettle(axisY = false, initialVelocity = velocity.x)
            toFling = Velocity(contentVelocity(velocity.x, offsetX), toFling.y)
        }

        val remaining = performFling(toFling)
        // 惯性撞到边界后剩下的速度继续喂给弹簧：这就是「甩出去还有余势」的来源。
        if (remaining.y != 0f) {
            startSettle(axisY = true, initialVelocity = remaining.y / RESIDUAL_VELOCITY_DAMPING)
        }
        if (remaining.x != 0f) {
            startSettle(axisY = false, initialVelocity = remaining.x / RESIDUAL_VELOCITY_DAMPING)
        }
    }

    /** 甩动速度里留给内容的那一份：与位移同向时内容不再滑，反向时按 [INWARD_VELOCITY_DAMPING] 衰减。 */
    private fun contentVelocity(velocity: Float, offset: Float): Float = if (sign(velocity) == sign(offset)) 0f else velocity / INWARD_VELOCITY_DAMPING

    /** 往回拖时该从过冲里收掉多少（返回带符号的消费量，最多收到 0）。 */
    private fun pullBack(raw: Float, delta: Float): Float {
        if (raw == 0f || delta == 0f || sign(delta) == sign(raw)) return 0f
        return if (abs(delta) >= abs(raw)) -raw else delta
    }

    private fun setRawY(value: Float): Float {
        rawY = value.coerceIn(-touchRange, touchRange)
        offsetY = displacementOf(rawY)
        return rawY
    }

    private fun setRawX(value: Float): Float {
        rawX = value.coerceIn(-touchRange, touchRange)
        offsetX = displacementOf(rawX)
        return rawX
    }

    /** 等效触摸量 → 位移（含符号）。 */
    private fun displacementOf(raw: Float): Float {
        if (touchRange <= 0f) return 0f
        return sign(raw) * overscrollDampingDistance(abs(raw) / touchRange, displacementRange)
    }

    /** 位移 → 等效触摸量（含符号）；只在接管弹簧时用。 */
    private fun rawOf(offset: Float): Float {
        if (touchRange <= 0f || displacementRange <= 0f) return 0f
        val normalized = overscrollTouchDistance(abs(offset), displacementRange) / displacementRange
        return sign(offset) * normalized * touchRange
    }

    private fun syncRawFromOffsets() {
        if (offsetY != 0f) rawY = rawOf(offsetY)
        if (offsetX != 0f) rawX = rawOf(offsetX)
    }

    /**
     * 惯性把位移推到上限附近时不再消费并立刻回弹。
     *
     * 曲线后段增长极慢，fling 只要还有一丝增长就会继续消耗速度，两者互相拖着，
     * 观感就是「顶到极点后停顿一下」。拖拽（`UserInput`）始终算消费，否则手势会漏给
     * 父级容器（下拉刷新会抢）。
     */
    private fun consumeOrSettle(
        axisY: Boolean,
        raw: Float,
        leftover: Float,
        inertia: Boolean,
        consumed: Offset,
    ): Offset {
        val saturated = inertia && abs(raw) >= touchRange * FLING_SATURATION_RATIO
        if (!saturated) {
            return if (axisY) consumed + Offset(0f, leftover) else consumed + Offset(leftover, 0f)
        }
        // 不能等 fling 收尾：框架在 content fling 结束之后还要分发 post-fling，
        // 顶栏收拢之类的长动画就在那里，等它跑完再回弹也会「停顿」。
        startSettle(axisY = axisY, initialVelocity = 0f)
        return consumed
    }

    private fun cancelSettle() {
        settleJobX?.cancel()
        settleJobY?.cancel()
    }

    private fun startSettle(axisY: Boolean, initialVelocity: Float) {
        val scope = scope ?: return
        val offset = if (axisY) offsetY else offsetX
        if (offset == 0f && initialVelocity == 0f) return
        val running = if (axisY) settleJobY else settleJobX
        if (running?.isActive == true) return
        val job = scope.launch { settleToRest(axisY = axisY, initialVelocity = initialVelocity) }
        if (axisY) settleJobY = job else settleJobX = job
        // 完成后立刻清掉引用：留着已完成的 job 会让下一次越界滚动误判「回弹仍在进行」。
        job.invokeOnCompletion {
            if (axisY) {
                if (settleJobY === job) settleJobY = null
            } else {
                if (settleJobX === job) settleJobX = null
            }
        }
    }

    /** 把当前位移弹回 0；弹簧自带过零回弹，初始速度就是甩动的余势。 */
    private suspend fun settleToRest(axisY: Boolean, initialVelocity: Float) {
        val start = if (axisY) offsetY else offsetX
        if (start == 0f && initialVelocity == 0f) return
        animate(
            initialValue = start,
            targetValue = 0f,
            initialVelocity = initialVelocity,
            animationSpec = settle,
        ) { value, _ ->
            if (axisY) offsetY = value else offsetX = value
        }
        // 弹簧可能过零后被拉回，收尾统一归零，并把原始触摸量一起清掉。
        if (axisY) {
            offsetY = 0f
            rawY = 0f
        } else {
            offsetX = 0f
            rawX = 0f
        }
    }

    private companion object {
        /** 三次曲线在 `x = 1` 时的取值是 `range / 3`，所以位移尺度取上限的三倍。 */
        const val MAX_DISPLACEMENT_TOUCH_RANGE = 3f

        /**
         * 惯性把原始触摸量推到上限的这个比例时就停止消费并立刻回弹。
         *
         * 不取 1.0：曲线后段每帧只挪零点几像素，fling 只要还有一丝增长就会继续消耗速度。
         */
        const val FLING_SATURATION_RATIO = 0.9f

        /**
         * 与位移反向的甩动：内容仍保留一部分惯性。
         *
         * 这两个系数取自 miuix 的 `MiuixOverscrollEffect`（2.13333 / 1.53333），是它在真机上
         * 校过的手感基线；我们只借比值，弹簧与曲线仍是本项目的。
         */
        const val INWARD_VELOCITY_DAMPING = 2.13333f
        const val RESIDUAL_VELOCITY_DAMPING = 1.53333f
    }
}

/**
 * 构造交给 `LocalOverscrollFactory` 的工厂。每个可滚动容器都会向它要一个新的
 * [OxygenOverscrollEffect]，因此工厂自身必须无状态。
 */
@Composable
internal fun rememberOxygenOverscrollFactory(
    spacing: OxygenSpacing,
    interaction: OxygenInteraction,
): OverscrollFactory {
    val density = LocalDensity.current
    val maxPx = with(density) { spacing.overscrollMax.toPx() }
    val dragScale = interaction.overscrollDragScale
    // 回弹要能在 fling 收尾之前就启动（否则位移会在极点上等 post-fling 的顶栏动画）。
    val scope = rememberCoroutineScope()
    return remember(density, maxPx, dragScale, scope) {
        // 与本项目 `MOT-SCROLL-001`（`spec.scroll.overscroll`）同源 token：
        // 阻尼 0.6 是「紧凑但会回弹一次」的关键，阈值单位是 px 而不是 0..1 的进度。
        val settle = spring<Float>(
            dampingRatio = GeneratedTokens.SPRING_OVERSCROLL_DAMPING_RATIO,
            stiffness = GeneratedTokens.SPRING_OVERSCROLL_STIFFNESS,
            visibilityThreshold = GeneratedTokens.SPRING_OVERSCROLL_VISIBILITY_THRESHOLD,
        )
        object : OverscrollFactory {
            override fun createOverscrollEffect(): OverscrollEffect = OxygenOverscrollEffect(
                maxPx = maxPx,
                dragScale = dragScale,
                settle = settle,
                scope = scope,
            )

            // OverscrollFactory 把 equals / hashCode 也声明成了抽象成员，
            // 匿名实现必须显式给出；按引用相等即可，工厂本来就不可交比。
            override fun equals(other: Any?): Boolean = this === other

            override fun hashCode(): Int = 1
        }
    }
}

/**
 * 全局注入滚动过冲：把本项目的弹簧回弹效果交给子树里所有可滚动容器。
 *
 * 平台默认给的是拉伸 / 光晕（Android）或 bounce（iOS），这里整体替换成同一套
 * `MOT-SCROLL-001` 回弹，观感在两端一致。[OxygenTheme] 已经默认包了一层，
 * 因此**只有**「刻意脱离 `OxygenTheme` 的子树」才需要自己再包一次。
 *
 * @param spacing 过冲最大位移的来源，默认取当前主题。
 * @param interaction 过冲阻尼的来源，默认取当前主题。
 * @param content 需要应用过冲效果的子树。
 */
@Composable
fun OverscrollProvider(
    spacing: OxygenSpacing = OxygenTheme.spacing,
    interaction: OxygenInteraction = OxygenTheme.interaction,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalOverscrollFactory provides rememberOxygenOverscrollFactory(spacing, interaction),
        content = content,
    )
}
