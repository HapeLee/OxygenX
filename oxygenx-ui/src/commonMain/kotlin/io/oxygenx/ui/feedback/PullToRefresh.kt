// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.feedback

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.oxygenx.motion.OxygenLoopSpec
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.navigation.TopAppBarState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal val LocalRefreshTopAppBarState = staticCompositionLocalOf<TopAppBarState?> { null }

/**
 * 下拉刷新容器：内容整体跟手下移，顶部冒出一个细描边圆环；松手时超过
 * `space.refresh.threshold` 触发 [onRefresh] 并停在阈值处，直到 [isRefreshing] 变回 `false`。
 *
 * 与 `LinearProgressIndicator` 不是同一个东西：那个只表达进度值，不接管手势；
 * 这里负责嵌套滚动、阻尼、阈值判定与回弹，视觉复用的是同一个细环
 * `CircularProgressIndicator`（不确定态）。
 *
 * - Spec ID: `CMP-PULL-REFRESH`
 * - Baseline: `OxygenX P3 细条基线 / OnePlus 9R (LE2100) / 2026-11-14`
 * - Reference: `catalog/reference/README.md`
 * - Motion: 松手回弹与收尾走 `MOT-STATE-004`；刷新中的圆环走 `MOT-PROGRESS-002`。
 * - Status: `draft`
 *
 * @param isRefreshing 宿主持有的刷新状态；`true` 期间指示器常驻并持续旋转。
 * @param onRefresh 下拉超过阈值后松手时回调一次；不要在回调里再次触发刷新。
 * @param modifier 外部 Modifier；容器需要有确定尺寸（通常是 `fillMaxSize`）。
 * @param enabled 是否允许下拉，`false` 时只透传滚动。
 * @param colors 圆环颜色集合。
 * @param motion 松手回弹与收尾的动效规格。
 * @param loop 刷新中圆环的循环规格。
 * @param content 内容区，通常是一个可滚动的 `LazyColumn`。
 */
@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ProgressColors = ProgressDefaults.colors(),
    motion: OxygenMotionSpec = ProgressDefaults.RefreshMotion,
    loop: OxygenLoopSpec = ProgressDefaults.CircularLoop,
    content: @Composable BoxScope.() -> Unit,
) = PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = onRefresh,
    canRefresh = { true },
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    motion = motion,
    loop = loop,
    content = content,
)

/**
 * [PullToRefreshBox] with a live gesture gate for a collapsing app bar or another scroll parent.
 *
 * @param canRefresh Read at scroll dispatch and release. When false, downward input is passed to
 *   the parent. For a large app bar, use `{ topBarState.collapsedFraction == 0f }`.
 * @param indicatorTopPadding Offset for the indicator when content sits under an overlapping bar.
 */
@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    canRefresh: () -> Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indicatorTopPadding: Dp = 0.dp,
    colors: ProgressColors = ProgressDefaults.colors(),
    motion: OxygenMotionSpec = ProgressDefaults.RefreshMotion,
    loop: OxygenLoopSpec = ProgressDefaults.CircularLoop,
    content: @Composable BoxScope.() -> Unit,
) {
    val spacing = OxygenTheme.spacing
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val thresholdPx = with(density) { spacing.refreshThreshold.toPx() }
    val maxPx = with(density) { spacing.refreshMaxDistance.toPx() }
    // 指示器藏在内容上方：内容下移多少，圆环就从屏幕外冒出来多少。
    val indicatorSlotPx = with(density) { (spacing.progressCircularSize + spacing.gapSmall).toPx() }
    val dragScale = OxygenTheme.interaction.refreshDragScale

    // 位移用普通 state 承载：嵌套滚动回调不是挂起函数，直接赋值才没有一帧延迟；
    // 只有 graphicsLayer 与 derivedStateOf 读它，所以逐帧位移不会引起重组。
    var distance by remember { mutableFloatStateOf(0f) }
    val indicatorVisible by remember { derivedStateOf { distance > 0f } }
    var dragDistance by remember { mutableFloatStateOf(0f) }
    var settleJob by remember { mutableStateOf<Job?>(null) }
    val refreshingState = rememberUpdatedState(isRefreshing)
    val enabledState = rememberUpdatedState(enabled)
    val canRefreshState = rememberUpdatedState(canRefresh)
    val topBarState = rememberUpdatedState(LocalRefreshTopAppBarState.current)
    val onRefreshState = rememberUpdatedState(onRefresh)
    val motionState = rememberUpdatedState(motion)

    suspend fun settle(target: Float) {
        if (target == distance) return
        val spec = motionState.value
        val animation: AnimationSpec<Float> = if (target >= distance) spec.enter else spec.exit
        animate(initialValue = distance, targetValue = target, animationSpec = animation) { value, _ ->
            distance = value.coerceAtLeast(0f)
        }
    }

    fun settleAfterRelease(refresh: Boolean) {
        settleJob?.cancel()
        settleJob = scope.launch {
            if (refresh || refreshingState.value) settle(thresholdPx)
            if (!refreshingState.value) settle(0f)
        }
    }

    val connection = remember(thresholdPx, maxPx, dragScale) {
        object : NestedScrollConnection {
            private fun canPull(): Boolean = enabledState.value && !refreshingState.value &&
                (topBarState.value?.collapsedFraction ?: 0f) <= 0f && canRefreshState.value()

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput || dragDistance <= 0f || available.y >= 0f) {
                    return Offset.Zero
                }
                val next = (dragDistance + available.y * dragScale).coerceAtLeast(0f)
                val consumed = (next - dragDistance) / dragScale
                dragDistance = next
                distance = next
                return Offset(0f, consumed)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // 惯性和程序滚动不能增加刷新距离。外层顶栏未展开时，让它先消费下拉。
                if (source != NestedScrollSource.UserInput || !canPull() || available.y <= 0f || dragScale <= 0f) {
                    return Offset.Zero
                }
                settleJob?.cancel()
                dragDistance = (dragDistance + available.y * dragScale).coerceAtMost(maxPx)
                distance = dragDistance
                // 到达拉伸上限后本手势仍由刷新接管，避免再叠一层列表过冲。
                return Offset(0f, available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (dragDistance <= 0f) return Velocity.Zero
                // 松手就判定并清空本次下拉，避免 post-fling 或收尾动画二次触发。
                val refresh = dragDistance >= thresholdPx && canPull()
                dragDistance = 0f
                if (refresh) onRefreshState.value()
                settleAfterRelease(refresh)
                return Velocity(0f, available.y.coerceAtLeast(0f))
            }
        }
    }

    LaunchedEffect(isRefreshing, enabled, thresholdPx) {
        if (isRefreshing || !enabled) dragDistance = 0f
        if (isRefreshing || dragDistance == 0f) settleAfterRelease(refresh = false)
    }

    Box(modifier = modifier.nestedScroll(connection)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = distance },
        ) {
            content()
        }
        if (indicatorVisible) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = indicatorTopPadding)
                    .graphicsLayer {
                        val pull = (distance / thresholdPx).coerceIn(0f, 1f)
                        translationY = distance - indicatorSlotPx
                        alpha = pull
                        // 冒出来时略微放大，避免小圆环突然出现；0.7 是视觉起点，不是设计令牌。
                        val scale = 0.7f + 0.3f * pull
                        scaleX = scale
                        scaleY = scale
                    },
                progress = null,
                colors = colors,
                loop = loop,
            )
        }
    }
}
