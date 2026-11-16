// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.contentColorFor
import io.oxygenx.ui.navigation.TopAppBarState

/** 悬浮按钮的横向位置；纵向始终贴在内容区底部（底栏之上）。 */
enum class FabPosition {
    Start,
    Center,
    End,
}

/**
 * 页面骨架：铺底色、按 `safeDrawing` 让开状态栏与挖孔，并承载顶栏、底栏、
 * 悬浮按钮与 Snackbar 宿主。
 *
 * 与 Miuix `Scaffold` 对齐的插槽划分：顶栏/底栏由调用方组合，Scaffold 只负责
 * 尺寸与 insets 分配。底部安全区不写进页面 padding，而是作为 [content] 的
 * `PaddingValues` 交给内容自行应用（内容通常是可滚动列表），这样列表滚到底部
 * 时能延伸到导航条下方；有 [bottomBar] 时底部安全区由底栏自己承担，内容不再重复让位。
 *
 * 传入 [topBarState] 时，内容区会挂上它的嵌套滚动连接，使大顶栏随滚动收拢/展开。
 *
 * - Spec ID: `CMP-SCAFFOLD`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-SCAFFOLD/`
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param topBar 顶栏，由调用方选择 [io.oxygenx.ui.navigation.TopAppBar] 或大标题形态。
 * @param bottomBar 底部栏（通常放导航栏或页面主操作）；传 `null` 表示没有底栏。
 * @param floatingActionButton 悬浮按钮；传 `null` 表示没有。
 * @param floatingActionButtonPosition 悬浮按钮的横向位置。
 * @param snackbarHost Snackbar 容器；Scaffold 不提供 Snackbar 组件本身，只负责定位。
 * @param topBarState 大顶栏的滚动联动状态；普通顶栏传 `null`。
 * @param containerColor 页面底色，默认 `color.surface`。
 * @param contentColor 内容区的内容色；`Color.Unspecified` 时按 [containerColor] 反查。
 * @param contentWindowInsets 内容区让开的窗口 insets，默认 `safeDrawing`。
 * @param content 页面内容；参数是内容需要自行应用的内边距（无底栏时为底部安全区）。
 */
@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: (@Composable () -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    snackbarHost: (@Composable () -> Unit)? = null,
    topBarState: TopAppBarState? = null,
    containerColor: Color = OxygenTheme.colorScheme.surface,
    contentColor: Color = Color.Unspecified,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    content: @Composable (PaddingValues) -> Unit,
) {
    val spacing = OxygenTheme.spacing
    // 内容区的内容色：显式传入优先，否则按底色反查（与 M3 Scaffold 同口径）。
    val resolvedContentColor = if (contentColor.isSpecified) {
        contentColor
    } else {
        val mapped = OxygenTheme.colorScheme.contentColorFor(containerColor)
        if (mapped.isSpecified) mapped else OxygenTheme.colorScheme.onSurface
    }
    val bottomInset = contentWindowInsets.asPaddingValues().calculateBottomPadding()
    val contentNestedScroll = if (topBarState != null) {
        Modifier.nestedScroll(topBarState.nestedScrollConnection)
    } else {
        Modifier
    }
    val overlayAlignment = when (floatingActionButtonPosition) {
        FabPosition.Start -> Alignment.Start
        FabPosition.Center -> Alignment.CenterHorizontally
        FabPosition.End -> Alignment.End
    }
    // 有底栏时底部安全区由底栏承担，内容再用底部内边距会让位两次。
    val contentBottomInset = if (bottomBar == null) bottomInset else 0.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
            .windowInsetsPadding(
                contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            ),
    ) {
        topBar()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(contentNestedScroll)
                // 过冲位移由各滚动容器自己画，裁在内容区内才不会盖到顶栏 / 底栏。
                .clipToBounds(),
        ) {
            // 页面内容统一拿到内容色：容器色换掉时，里面的文字 / 图标自动跟着换。
            CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
                content(PaddingValues(bottom = contentBottomInset))
            }
            if (snackbarHost != null || floatingActionButton != null) {
                // 两者都在内容之上、底栏之下；同时存在时 Snackbar 在悬浮按钮上方，避免互相遮挡。
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gutter, vertical = spacing.gapLarge),
                    horizontalAlignment = overlayAlignment,
                ) {
                    if (snackbarHost != null) {
                        snackbarHost()
                    }
                    if (floatingActionButton != null) {
                        Box(
                            modifier = Modifier.padding(
                                top = if (snackbarHost != null) spacing.gapSmall else 0.dp,
                            ),
                        ) {
                            floatingActionButton()
                        }
                    }
                }
            }
        }
        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(contentWindowInsets.only(WindowInsetsSides.Bottom)),
            ) {
                bottomBar()
            }
        }
    }
}
