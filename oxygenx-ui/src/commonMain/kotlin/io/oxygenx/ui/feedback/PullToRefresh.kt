// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.feedback

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import io.oxygenx.motion.OxygenLoopSpec
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.OxygenTheme
import kotlinx.coroutines.launch

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
    val refreshingState = rememberUpdatedState(isRefreshing)
    val onRefreshState = rememberUpdatedState(onRefresh)

    fun specFor(target: Float): AnimationSpec<Float> = if (target >= distance) motion.enter else motion.exit

    suspend fun settle(target: Float) {
        animate(initialValue = distance, targetValue = target, animationSpec = specFor(target)) { value, _ ->
            distance = value
        }
    }

    val connection = remember(thresholdPx, maxPx, dragScale, enabled) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 已经拉出一段且手指开始上滑：先把这段下拉量收回去，别让内容先滚。
                if (available.y < 0f && distance > 0f) {
                    val next = (distance + available.y).coerceAtLeast(0f)
                    val consumed = next - distance
                    distance = next
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // 内容已经到顶、还有剩余下拉量，才轮到刷新容器吸收。
                if (!enabled || refreshingState.value || available.y <= 0f) return Offset.Zero
                val next = (distance + available.y * dragScale).coerceAtMost(maxPx)
                val gained = next - distance
                distance = next
                // 返回的是「被消费的原始位移」，因此要除以阻尼比例还原。
                return Offset(0f, if (dragScale > 0f) gained / dragScale else 0f)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (distance <= 0f) return Velocity.Zero
                if (!refreshingState.value && distance >= thresholdPx) {
                    settle(thresholdPx)
                    onRefreshState.value()
                } else {
                    settle(if (refreshingState.value) thresholdPx else 0f)
                }
                // 下拉这一段的速度全部吃掉，避免交给内容继续 fling。
                return available
            }
        }
    }

    // 宿主翻转 isRefreshing 时把指示器送到位 / 收回；下拉中途翻转也不会跳变。
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            if (distance < thresholdPx) settle(thresholdPx)
        } else if (distance > 0f) {
            settle(0f)
        }
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
