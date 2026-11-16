// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.material3

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import io.oxygenx.theme.OxygenColorScheme

/**
 * M3 色板 → 本仓库色板的单向映射。
 *
 * 方向：**M3 是来源，Oxygen 是目标**。外部 App（例如已经用 material-kolor 由种子色生成
 * 整套 M3 `ColorScheme` 的项目）不需要为了接入本设计系统再实现一套取色算法，只要在边界上
 * 调一次 [toOxygenColorScheme]，然后把结果交给 `OxygenTheme(colorScheme = …)`。
 * 反向（Oxygen → M3）暂不提供：本仓库的角色集是 M3 的子集 + 扩展项，反向映射会丢信息。
 *
 * 三类处理：
 * 1. **直连**：角色语义一致的一律原值搬过来，不重算（`primary` / `secondaryContainer` /
 *    `surfaceContainer*` / `errorContainer` / `outline*` / `scrim` …）。
 * 2. **合成**：M3 没有的扩展角色按 M3 角色叠透明度得出，比例固定，见各私有常量；
 *    这些比例就是我们 `color.*` 令牌里登记的 E0 取值（12% 按压层、16%/35% 开关轨道等），
 *    所以从 M3 进来和从本仓库令牌进来观感一致。
 * 3. **丢弃**：M3 有、本仓库刻意不引入的角色 —— `tertiary*`（没有第三强调色）、
 *    `background` / `onBackground`（页面底一律用 `surface`）、`surfaceVariant`
 *    （由 `onSurfaceVariant` 与 `surfaceContainer*` 档位分摊）、`surfaceTint`
 *    （层级靠容器档位与细边框表达，不做色调叠加）。丢弃是显式设计，不是遗漏。
 *
 * 明暗判定不看外部传入的标记，而是按 [ColorScheme.surface] 的亮度推断（`< 0.5` 视为深色），
 * 因为 M3 的 `ColorScheme` 自己不携带 `isDark`，这样也顺带覆盖了 AMOLED / 高对比度色板。
 */
fun ColorScheme.toOxygenColorScheme(): OxygenColorScheme {
    val dark = surface.luminance() < 0.5f
    return OxygenColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        surface = surface,
        surfaceContainer = surfaceContainer,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainerLowest = surfaceContainerLowest,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        surfaceBright = surfaceBright,
        surfaceDim = surfaceDim,
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        outline = outline,
        outlineVariant = outlineVariant,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        // 扩展角色：M3 用 onSurface 的透明度表达禁用 / 按压层，比例取本仓库 E0 令牌。
        surfaceDisabled = onSurface
            .copy(alpha = if (dark) DISABLED_CONTAINER_DARK else DISABLED_CONTAINER_LIGHT)
            .compositeOver(surface),
        onSurfaceDisabled = onSurface.copy(alpha = DISABLED_CONTENT),
        inputIndicatorUnfocused = onSurface.copy(alpha = UNFOCUSED_INDICATOR),
        statePressed = onSurface.copy(alpha = if (dark) PRESSED_DARK else PRESSED_LIGHT),
        switchTrackOn = primary.copy(alpha = if (dark) SWITCH_TRACK_ON_DARK else SWITCH_TRACK_ON_LIGHT),
        switchThumbOff = onSurface.copy(alpha = if (dark) THUMB_OFF_DARK else THUMB_OFF_LIGHT),
        switchTrackDisabled = onSurface.copy(
            alpha = if (dark) TRACK_DISABLED_DARK else TRACK_DISABLED_LIGHT,
        ),
        switchThumbDisabled = onSurface.copy(
            alpha = if (dark) THUMB_DISABLED_DARK else THUMB_DISABLED_LIGHT,
        ),
        // 单选内点 / 复选对勾在深浅两套令牌里都是白，与 M3 的 onPrimary 不同（深色下 onPrimary 是墨色）。
        controlDot = Color.White,
        scrim = scrim,
        isDark = dark,
    )
}

// 以下比例与 `design/tokens/color.yaml` 的 E0 取值同源：
// color.surface.disabled 0xFFEAEAEF / 0xFF2A2A2F → onSurface @7% / @11% 叠在 surface 上。
private const val DISABLED_CONTAINER_LIGHT = 0.07f
private const val DISABLED_CONTAINER_DARK = 0.11f

// color.on.surface.disabled 0xFFA5A5AE / 0xFF6C6C75 → onSurface @38%。
private const val DISABLED_CONTENT = 0.38f

// color.input.indicator.unfocused 0x1F000000 / 0x1FFFFFFF → 12%。
private const val UNFOCUSED_INDICATOR = 0.12f

// color.state.pressed 0x1F000000（12%）/ 0x29FFFFFF（16%）。
private const val PRESSED_LIGHT = 0.12f
private const val PRESSED_DARK = 0.16f

// color.switch.track.on 0x29C41442（16%）/ 0x59FF6B85（35%）。
private const val SWITCH_TRACK_ON_LIGHT = 0.16f
private const val SWITCH_TRACK_ON_DARK = 0.35f

// color.switch.thumb.off 0x2D000000（18%）/ 0x4CFFFFFF（30%）。
private const val THUMB_OFF_LIGHT = 0.18f
private const val THUMB_OFF_DARK = 0.30f

// color.switch.track.disabled 0x0A000000（4%）/ 0x14FFFFFF（8%）。
private const val TRACK_DISABLED_LIGHT = 0.04f
private const val TRACK_DISABLED_DARK = 0.08f

// color.switch.thumb.disabled 0x0C000000（5%）/ 0x1EFFFFFF（12%）。
private const val THUMB_DISABLED_LIGHT = 0.05f
private const val THUMB_DISABLED_DARK = 0.12f
