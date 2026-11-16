// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * 当前内容色：容器（`Surface` / `Card` / `Scaffold` 等）负责下发，
 * 叶子组件（`Text` / `Icon` 等）在没显式传色时读它。
 *
 * 与 M3 `LocalContentColor` 同口径；[Color.Unspecified] 表示「这一层没声明」，
 * 组件再回退到 `colorScheme.onSurface`。
 */
val LocalContentColor = compositionLocalOf { Color.Unspecified }

/**
 * 当前文本样式：容器负责下发，`Text` 在没显式传 `style` 时读它。
 *
 * 与 M3 `LocalTextStyle` 同口径。`OxygenTheme` 把它设为 `typography.bodyMedium`，
 * 组件在自己的内容槽位里用 [ProvideTextStyle] 覆盖（例如按钮下发 `labelMedium`、
 * 顶栏下发 `appbarTitle`），于是槽位里的 `Text("文案")` 自动是正确档位。
 */
val LocalTextStyle = staticCompositionLocalOf { TextStyle.Default }

/**
 * 把 [value] 合并进当前文本样式后下发：只覆盖 [value] 里显式声明过的属性
 * （字号、字重、行高、字距等），颜色仍由 [LocalContentColor] 决定。
 *
 * 与 M3 `ProvideTextStyle` 同口径；用于组件的内容槽位，避免调用方逐处写
 * `style = OxygenTheme.typography.xxx`。
 */
@Composable
fun ProvideTextStyle(value: TextStyle, content: @Composable () -> Unit) {
    val merged = LocalTextStyle.current.merge(value)
    CompositionLocalProvider(LocalTextStyle provides merged, content = content)
}

private val LocalColorScheme = staticCompositionLocalOf { lightColorScheme() }
private val LocalSpacing = staticCompositionLocalOf { defaultSpacing() }
private val LocalShapes = staticCompositionLocalOf { defaultShapes() }
private val LocalTypography = staticCompositionLocalOf { defaultTypography() }
private val LocalIcons = staticCompositionLocalOf { defaultIconMetrics() }
private val LocalLayout = staticCompositionLocalOf { defaultLayout() }
private val LocalInteraction = staticCompositionLocalOf { defaultInteraction() }

object OxygenTheme {
    val colorScheme: OxygenColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalColorScheme.current

    val spacing: OxygenSpacing
        @Composable @ReadOnlyComposable
        get() = LocalSpacing.current

    val shapes: OxygenShapes
        @Composable @ReadOnlyComposable
        get() = LocalShapes.current

    val typography: OxygenTypography
        @Composable @ReadOnlyComposable
        get() = LocalTypography.current

    val icons: OxygenIconMetrics
        @Composable @ReadOnlyComposable
        get() = LocalIcons.current

    val layout: OxygenLayout
        @Composable @ReadOnlyComposable
        get() = LocalLayout.current

    val interaction: OxygenInteraction
        @Composable @ReadOnlyComposable
        get() = LocalInteraction.current

    /** 当前内容色；[Color.Unspecified] 时组件应回退到 [colorScheme]`.onSurface`。 */
    val contentColor: Color
        @Composable @ReadOnlyComposable
        get() = LocalContentColor.current

    /**
     * 当前文本样式：主题默认 `typography.bodyMedium`，进入组件内容槽位后是该组件下发的档位。
     * `Text` 的 `style` 默认值就是它。
     */
    val textStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalTextStyle.current
}

/**
 * 设计系统入口：提供配色、间距、形状、排版、图标尺寸、布局与交互参数，
 * 同时把滚动过冲换成本项目的弹簧回弹。
 *
 * 与 M3 `MaterialTheme` 同口径地支持**局部覆写**：任何参数传 `null` 就继承外层当前值，
 * 所以可以在页面内套一层只换配色的 `OxygenTheme(colorScheme = …)` 而不影响其余令牌。
 * [darkTheme] 只在 [colorScheme] 未提供时生效（它是「整套换明暗」的快捷方式）。
 *
 * @param darkTheme 未指定 [colorScheme] 时按明暗取 `lightColorScheme()` / `darkColorScheme()`。
 * @param colorScheme 配色；`null` 表示按 [darkTheme] 取默认配色。
 * @param spacing 间距；`null` 继承外层。
 * @param shapes 形状；`null` 继承外层。
 * @param typography 排版；`null` 继承外层。
 * @param icons 图标尺寸；`null` 继承外层。
 * @param layout 布局尺寸；`null` 继承外层。
 * @param interaction 交互参数（过冲阻尼等）；`null` 继承外层。
 * @param content 主题作用域内的内容。
 */
@Composable
fun OxygenTheme(
    darkTheme: Boolean = false,
    colorScheme: OxygenColorScheme? = null,
    spacing: OxygenSpacing? = null,
    shapes: OxygenShapes? = null,
    typography: OxygenTypography? = null,
    icons: OxygenIconMetrics? = null,
    layout: OxygenLayout? = null,
    interaction: OxygenInteraction? = null,
    content: @Composable () -> Unit,
) {
    val resolvedColors = colorScheme ?: remember(darkTheme) {
        if (darkTheme) darkColorScheme() else lightColorScheme()
    }
    // 未传的令牌继承外层：这一层读到的就是父级 provider 的值。
    val resolvedSpacing = spacing ?: OxygenTheme.spacing
    val resolvedShapes = shapes ?: OxygenTheme.shapes
    val resolvedTypography = typography ?: OxygenTheme.typography
    val resolvedIcons = icons ?: OxygenTheme.icons
    val resolvedLayout = layout ?: OxygenTheme.layout
    val resolvedInteraction = interaction ?: OxygenTheme.interaction
    CompositionLocalProvider(
        // 滚动过冲在这里统一接管：子树里任何可滚动容器都拿到同一套弹簧回弹，
        // 不需要每个列表（也不需要 Scaffold）各自包一层。
        LocalOverscrollFactory provides rememberOxygenOverscrollFactory(resolvedSpacing, resolvedInteraction),
        LocalColorScheme provides resolvedColors,
        LocalSpacing provides resolvedSpacing,
        LocalShapes provides resolvedShapes,
        LocalTypography provides resolvedTypography,
        LocalIcons provides resolvedIcons,
        LocalLayout provides resolvedLayout,
        LocalInteraction provides resolvedInteraction,
    ) {
        // 内容色 / 文本样式单独一层：它们跟配色与排版走，但不参与上面那组令牌的覆写语义。
        CompositionLocalProvider(
            LocalContentColor provides resolvedColors.onSurface,
            LocalTextStyle provides resolvedTypography.bodyMedium,
            content = content,
        )
    }
}
