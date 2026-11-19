// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import io.oxygenx.theme.generated.GeneratedTokens

/**
 * 颜色角色表：字段名与 M3 的 `ColorScheme` 角色一一对应（`primary` / `onPrimary` /
 * `surface` / `onSurface` / `onSurfaceVariant` / `outline` / `inverseSurface` …），
 * 组件里直接写 `OxygenTheme.colorScheme.onSurface`，不需要 `content.*` 之类的前缀。
 *
 * 品牌语义：[primary] 是一加红主题色（`color.primary`，可被覆盖），[secondary] 是
 * 次要强调色 —— 取页面墨色而不是第二品牌色，用于「不该跟主色抢焦点」的强调。
 *
 * 易混的两对：
 * - [primary] 是主色本身，[onSurface] 是「正文文字色」（M3 onSurface）；
 * - [onPrimary] 是「主色之上的内容」，[inverseSurface] 是反白容器本身、[inverseOnSurface]
 *   是反白容器之上的内容。
 *
 * [surfaceContainerHighest] 是 M3 的「未激活轨道」角色：分段指示器底轨、滑块/进度空轨、
 * 开关关闭态轨道共用；它与 [outlineVariant]（8% 分割线）虽然都是中性叠加层，但真机取值
 * 不同（12% vs 8%），不合并。
 *
 * [surfaceDisabled] / [onSurfaceDisabled] / `switch*` / `statePressed` /
 * `inputIndicatorUnfocused` / `controlDot` 是 M3 没有对应角色的扩展项，按同一套平铺命名
 * 风格登记（见 `docs/spec/design-tokens.md` §2）。开关开启态不在此列：[switchTrackOn]
 * 由 [primary] 派生，保证动态取色时开关跟随主色。
 *
 * **M3 桥接角色**（`primaryContainer` / `errorContainer` / `surfaceContainerLow|High|Lowest` /
 * `surfaceBright|Dim` / `inversePrimary` / `onError`）：本仓库组件暂时没有消费者，加它们是为了
 * 让外部 M3 色板（`:oxygenx-material3` 的 `fromM3`）有地方落，而不是拿 [primary] / [secondary]
 * 凑近似。M3 的 `tertiary*`、`background`、`surfaceVariant`、`surfaceTint` 不在此列：
 * 前两者在本仓库没有语义（页面底一律用 [surface]），后两者已被 `onSurfaceVariant` /
 * `surfaceContainer*` 档位取代，映射层显式丢弃。
 */
@Immutable
data class OxygenColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    /** M3 的 inversePrimary：反白容器里当强调色用（取另一侧明暗的 [primary]）。 */
    val inversePrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val surface: Color,
    val surfaceContainer: Color,
    val surfaceContainerLow: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val surfaceBright: Color,
    val surfaceDim: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val surfaceDisabled: Color,
    val onSurfaceDisabled: Color,
    val inputIndicatorUnfocused: Color,
    val statePressed: Color,
    val switchTrackOn: Color,
    val switchThumbOff: Color,
    val switchTrackDisabled: Color,
    val switchThumbDisabled: Color,
    val controlDot: Color,
    /** 模态遮罩：底部面板这类自绘暗层用它（Dialog 的遮罩由平台提供）。 */
    val scrim: Color,
    val isDark: Boolean,
)

fun lightColorScheme(
    primary: Color = colorOf(GeneratedTokens.COLOR_PRIMARY_LIGHT),
    secondary: Color = colorOf(GeneratedTokens.COLOR_SECONDARY_LIGHT),
): OxygenColorScheme = OxygenColorScheme(
    primary = primary,
    onPrimary = colorOf(GeneratedTokens.COLOR_ON_PRIMARY_LIGHT),
    primaryContainer = colorOf(GeneratedTokens.COLOR_PRIMARY_CONTAINER_LIGHT),
    onPrimaryContainer = colorOf(GeneratedTokens.COLOR_ON_PRIMARY_CONTAINER_LIGHT),
    inversePrimary = colorOf(GeneratedTokens.COLOR_INVERSE_PRIMARY_LIGHT),
    secondary = secondary,
    onSecondary = colorOf(GeneratedTokens.COLOR_ON_SECONDARY_LIGHT),
    secondaryContainer = colorOf(GeneratedTokens.COLOR_SECONDARY_CONTAINER_LIGHT),
    onSecondaryContainer = colorOf(GeneratedTokens.COLOR_ON_SECONDARY_CONTAINER_LIGHT),
    surface = colorOf(GeneratedTokens.COLOR_SURFACE_LIGHT),
    surfaceContainer = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_LIGHT),
    surfaceContainerLow = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_LOW_LIGHT),
    surfaceContainerLowest = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_LOWEST_LIGHT),
    surfaceContainerHigh = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_HIGH_LIGHT),
    surfaceContainerHighest = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_HIGHEST_LIGHT),
    surfaceBright = colorOf(GeneratedTokens.COLOR_SURFACE_BRIGHT_LIGHT),
    surfaceDim = colorOf(GeneratedTokens.COLOR_SURFACE_DIM_LIGHT),
    onSurface = colorOf(GeneratedTokens.COLOR_ON_SURFACE_LIGHT),
    onSurfaceVariant = colorOf(GeneratedTokens.COLOR_ON_SURFACE_VARIANT_LIGHT),
    inverseSurface = colorOf(GeneratedTokens.COLOR_INVERSE_SURFACE_LIGHT),
    inverseOnSurface = colorOf(GeneratedTokens.COLOR_INVERSE_ON_SURFACE_LIGHT),
    outline = colorOf(GeneratedTokens.COLOR_OUTLINE_LIGHT),
    outlineVariant = colorOf(GeneratedTokens.COLOR_OUTLINE_VARIANT_LIGHT),
    error = colorOf(GeneratedTokens.COLOR_ERROR_LIGHT),
    onError = colorOf(GeneratedTokens.COLOR_ON_ERROR_LIGHT),
    errorContainer = colorOf(GeneratedTokens.COLOR_ERROR_CONTAINER_LIGHT),
    onErrorContainer = colorOf(GeneratedTokens.COLOR_ON_ERROR_CONTAINER_LIGHT),
    surfaceDisabled = colorOf(GeneratedTokens.COLOR_SURFACE_DISABLED_LIGHT),
    onSurfaceDisabled = colorOf(GeneratedTokens.COLOR_ON_SURFACE_DISABLED_LIGHT),
    inputIndicatorUnfocused = colorOf(GeneratedTokens.COLOR_INPUT_INDICATOR_UNFOCUSED_LIGHT),
    statePressed = colorOf(GeneratedTokens.COLOR_STATE_PRESSED_LIGHT),
    switchTrackOn = primary.copy(alpha = colorOf(GeneratedTokens.COLOR_SWITCH_TRACK_ON_LIGHT).alpha),
    switchThumbOff = colorOf(GeneratedTokens.COLOR_SWITCH_THUMB_OFF_LIGHT),
    switchTrackDisabled = colorOf(GeneratedTokens.COLOR_SWITCH_TRACK_DISABLED_LIGHT),
    switchThumbDisabled = colorOf(GeneratedTokens.COLOR_SWITCH_THUMB_DISABLED_LIGHT),
    controlDot = colorOf(GeneratedTokens.COLOR_CONTROL_DOT_LIGHT),
    scrim = colorOf(GeneratedTokens.COLOR_SCRIM_LIGHT),
    isDark = false,
)

fun darkColorScheme(
    primary: Color = colorOf(GeneratedTokens.COLOR_PRIMARY_DARK),
    secondary: Color = colorOf(GeneratedTokens.COLOR_SECONDARY_DARK),
): OxygenColorScheme = OxygenColorScheme(
    primary = primary,
    onPrimary = colorOf(GeneratedTokens.COLOR_ON_PRIMARY_DARK),
    primaryContainer = colorOf(GeneratedTokens.COLOR_PRIMARY_CONTAINER_DARK),
    onPrimaryContainer = colorOf(GeneratedTokens.COLOR_ON_PRIMARY_CONTAINER_DARK),
    inversePrimary = colorOf(GeneratedTokens.COLOR_INVERSE_PRIMARY_DARK),
    secondary = secondary,
    onSecondary = colorOf(GeneratedTokens.COLOR_ON_SECONDARY_DARK),
    secondaryContainer = colorOf(GeneratedTokens.COLOR_SECONDARY_CONTAINER_DARK),
    onSecondaryContainer = colorOf(GeneratedTokens.COLOR_ON_SECONDARY_CONTAINER_DARK),
    surface = colorOf(GeneratedTokens.COLOR_SURFACE_DARK),
    surfaceContainer = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_DARK),
    surfaceContainerLow = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_LOW_DARK),
    surfaceContainerLowest = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_LOWEST_DARK),
    surfaceContainerHigh = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_HIGH_DARK),
    surfaceContainerHighest = colorOf(GeneratedTokens.COLOR_SURFACE_CONTAINER_HIGHEST_DARK),
    surfaceBright = colorOf(GeneratedTokens.COLOR_SURFACE_BRIGHT_DARK),
    surfaceDim = colorOf(GeneratedTokens.COLOR_SURFACE_DIM_DARK),
    onSurface = colorOf(GeneratedTokens.COLOR_ON_SURFACE_DARK),
    onSurfaceVariant = colorOf(GeneratedTokens.COLOR_ON_SURFACE_VARIANT_DARK),
    inverseSurface = colorOf(GeneratedTokens.COLOR_INVERSE_SURFACE_DARK),
    inverseOnSurface = colorOf(GeneratedTokens.COLOR_INVERSE_ON_SURFACE_DARK),
    outline = colorOf(GeneratedTokens.COLOR_OUTLINE_DARK),
    outlineVariant = colorOf(GeneratedTokens.COLOR_OUTLINE_VARIANT_DARK),
    error = colorOf(GeneratedTokens.COLOR_ERROR_DARK),
    onError = colorOf(GeneratedTokens.COLOR_ON_ERROR_DARK),
    errorContainer = colorOf(GeneratedTokens.COLOR_ERROR_CONTAINER_DARK),
    onErrorContainer = colorOf(GeneratedTokens.COLOR_ON_ERROR_CONTAINER_DARK),
    surfaceDisabled = colorOf(GeneratedTokens.COLOR_SURFACE_DISABLED_DARK),
    onSurfaceDisabled = colorOf(GeneratedTokens.COLOR_ON_SURFACE_DISABLED_DARK),
    inputIndicatorUnfocused = colorOf(GeneratedTokens.COLOR_INPUT_INDICATOR_UNFOCUSED_DARK),
    statePressed = colorOf(GeneratedTokens.COLOR_STATE_PRESSED_DARK),
    switchTrackOn = primary.copy(alpha = colorOf(GeneratedTokens.COLOR_SWITCH_TRACK_ON_DARK).alpha),
    switchThumbOff = colorOf(GeneratedTokens.COLOR_SWITCH_THUMB_OFF_DARK),
    switchTrackDisabled = colorOf(GeneratedTokens.COLOR_SWITCH_TRACK_DISABLED_DARK),
    switchThumbDisabled = colorOf(GeneratedTokens.COLOR_SWITCH_THUMB_DISABLED_DARK),
    controlDot = colorOf(GeneratedTokens.COLOR_CONTROL_DOT_DARK),
    scrim = colorOf(GeneratedTokens.COLOR_SCRIM_DARK),
    isDark = true,
)

/**
 * 与 M3 `ColorScheme.contentColorFor` 同口径：给定容器色，反查它之上应该用的内容色。
 *
 * 用在 `Surface` 这类「铺底 + 下发内容色」的容器里。没有对应关系的颜色返回
 * [Color.Unspecified]，由调用方回退到当前 `LocalContentColor`。
 */
fun OxygenColorScheme.contentColorFor(backgroundColor: Color): Color = when (backgroundColor) {
    primary -> onPrimary
    primaryContainer -> onPrimaryContainer
    secondary -> onSecondary
    secondaryContainer -> onSecondaryContainer
    surface -> onSurface
    surfaceContainer -> onSurface
    surfaceContainerLow -> onSurface
    surfaceContainerLowest -> onSurface
    surfaceContainerHigh -> onSurface
    surfaceContainerHighest -> onSurface
    surfaceBright -> onSurface
    surfaceDim -> onSurface
    inverseSurface -> inverseOnSurface
    error -> onError
    errorContainer -> onErrorContainer
    else -> Color.Unspecified
}

internal fun colorOf(argb: Long): Color = Color(
    alpha = ((argb shr 24) and 0xFFL) / 255f,
    red = ((argb shr 16) and 0xFFL) / 255f,
    green = ((argb shr 8) and 0xFFL) / 255f,
    blue = (argb and 0xFFL) / 255f,
)
