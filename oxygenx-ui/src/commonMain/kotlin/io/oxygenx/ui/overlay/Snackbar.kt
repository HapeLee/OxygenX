// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.IndicationDefaults
import io.oxygenx.ui.basic.Text
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/** Snackbar 停留时长；[Indefinite] 表示只能由 action / dismiss 或外部 [SnackbarHostState.dismiss] 结束。 */
enum class SnackbarDuration {
    Short,
    Long,
    Indefinite,
    ;

    internal val timeoutMillis: Long?
        get() = when (this) {
            Short -> 4_000L
            Long -> 10_000L
            Indefinite -> null
        }
}

/** [SnackbarHostState.showSnackbar] 的返回结果，语义同 M3 `SnackbarResult`。 */
enum class SnackbarResult {
    Dismissed,
    ActionPerformed,
}

/**
 * 一条 Snackbar 的数据：由 [SnackbarHostState.showSnackbar] 创建，交给
 * [Snackbar] 渲染。调用方一般不自己构造。
 */
@Stable
class SnackbarData internal constructor(
    val message: String,
    val actionLabel: String?,
    val withDismissAction: Boolean,
    val duration: SnackbarDuration,
    internal val onAction: () -> Unit,
    internal val onDismiss: () -> Unit,
)

/**
 * Snackbar 宿主状态：`suspend fun showSnackbar(…)` 排队显示，返回用户是点了 action 还是让它超时消失。
 *
 * 与 M3 `SnackbarHostState` 同口径，但只保留 `showSnackbar` 一条路径（M3 的 `currentSnackbarData`
 * 可写版本没有暴露）。配合 `Scaffold(snackbarHost = { SnackbarHost(state) })` 使用。
 */
@Stable
class SnackbarHostState {
    private var entry by mutableStateOf<Entry?>(null)
    private val mutex = Mutex()

    /** 当前正在显示的数据；没有时是 `null`。 */
    val currentSnackbarData: SnackbarData?
        get() = entry?.data

    /**
     * 显示一条 Snackbar 并挂起，直到它消失。
     *
     * @param message 正文。
     * @param actionLabel 行动区文案；`null` 表示没有行动按钮。
     * @param withDismissAction 是否显示右上角关闭图标。
     * @param duration 停留时长。
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ): SnackbarResult = mutex.withLock {
        val deferred = CompletableDeferred<SnackbarResult>()
        val data = SnackbarData(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            onAction = { deferred.complete(SnackbarResult.ActionPerformed) },
            onDismiss = { deferred.complete(SnackbarResult.Dismissed) },
        )
        entry = Entry(data, deferred)
        val timeout = duration.timeoutMillis
        val result = if (timeout == null) {
            deferred.await()
        } else {
            withTimeoutOrNull(timeout) { deferred.await() }
        }
        entry = null
        result ?: SnackbarResult.Dismissed
    }

    /** 立即关闭当前这条（等价于点关闭图标）。 */
    fun dismiss() {
        entry?.data?.onDismiss?.invoke()
    }

    private class Entry(
        val data: SnackbarData,
        val deferred: CompletableDeferred<SnackbarResult>,
    )
}

/**
 * Snackbar 宿主：读取 [state] 并渲染当前那一条，放进 `Scaffold` 的 `snackbarHost` 槽即可。
 *
 * 这里不做进出场动画（M3 的淡入淡出依赖它自己的 `FadeInFadeOutWithScale`）；
 * 需要时在调用方包一层 `AnimatedVisibility`。
 *
 * @param state 宿主状态。
 * @param modifier 外部 Modifier。
 */
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    state.currentSnackbarData?.let { data ->
        Snackbar(data = data, modifier = modifier.fillMaxWidth())
    }
}

/**
 * Snackbar 本体：反白底 + 正文 + 可选行动区 + 可选关闭图标。
 *
 * 视觉取本项目取向（`shapes.cornerCard` 圆角、`space.card.padding` 内距），颜色默认走
 * `inverseSurface` / `inverseOnSurface`，与 M3 一致，保证在浅深两套主题下都足够对比。
 *
 * - Spec ID: `CMP-SNACKBAR`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-SNACKBAR/`
 * - Status: `draft`
 *
 * @param data 要显示的数据，一般来自 [SnackbarHostState.currentSnackbarData]。
 * @param modifier 外部 Modifier。
 * @param containerColor 底色，默认 `color.inverse.surface`。
 * @param contentColor 正文与图标颜色，默认 `color.inverse.on.surface`。
 * @param actionColor 行动区文案颜色，默认同 [contentColor]。
 */
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    containerColor: Color = OxygenTheme.colorScheme.inverseSurface,
    contentColor: Color = OxygenTheme.colorScheme.inverseOnSurface,
    actionColor: Color = contentColor,
) {
    val spacing = OxygenTheme.spacing
    Row(
        modifier = modifier
            .clip(OxygenTheme.shapes.cornerCard)
            .background(containerColor)
            .padding(horizontal = spacing.cardPadding, vertical = spacing.gapLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
    ) {
        Text(
            text = data.message,
            modifier = Modifier.weight(1f),
            style = OxygenTheme.typography.bodyMedium,
            color = contentColor,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        data.actionLabel?.let { label ->
            Text(
                text = label,
                modifier = Modifier
                    .clip(OxygenTheme.shapes.cornerButton)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = IndicationDefaults.ripple(),
                        role = Role.Button,
                        onClick = data.onAction,
                    )
                    .padding(horizontal = spacing.gapSmall, vertical = spacing.gapXs),
                style = OxygenTheme.typography.labelMedium,
                color = actionColor,
                maxLines = 1,
            )
        }
        if (data.withDismissAction) {
            Box(
                modifier = Modifier
                    .size(spacing.touchTargetMin)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = IndicationDefaults.ripple(),
                        role = Role.Button,
                        onClick = data.onDismiss,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = OxygenIcons.Close,
                    contentDescription = "关闭",
                    tint = contentColor,
                    size = OxygenTheme.icons.small,
                )
            }
        }
    }
}
