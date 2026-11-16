// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.OxygenTheme

/**
 * 是否启用按压水波纹。
 *
 * 默认 `true`。在子树里提供 `false` 后，所有组件拿到的 indication 都是 `null`——用于
 * 「父级已经有水波纹、子级不该再叠一层」的场合（可点击的 `Card` 里再放一个可点击的 `Chip`），
 * 以及需要完全静态截图的页面。
 */
val LocalRippleEnabled = staticCompositionLocalOf { true }

/**
 * 触摸态指示器。
 *
 * 只借用 Material 的水波纹绘制（`material-ripple` 的 `createRippleModifierNode`），
 * 不引入其组件外观；颜色与强度全部来自令牌（`color.state.pressed`）。
 */
object IndicationDefaults {
    /**
     * @param bounded 是否限制在组件边界内；顶栏导航这类需要盖住相邻内容的场景传 `false`。
     * @param radius 波纹半径；`Dp.Unspecified` 时按组件尺寸推算。
     * @return 关闭了 [LocalRippleEnabled] 时返回 `null`（Compose 的 `clickable` / `indication`
     *   都接受空值，表示不绘制任何触摸态）。
     */
    @Composable
    fun ripple(
        bounded: Boolean = true,
        radius: Dp = Dp.Unspecified,
    ): Indication? {
        if (!LocalRippleEnabled.current) return null
        val pressed = OxygenTheme.colorScheme.statePressed
        // 令牌本身的 alpha 就是「按下」强度，这里把它交给 RippleAlpha 表达，
        // 颜色只提供色相，避免两层 alpha 相乘后过淡。
        val pressedAlpha = pressed.alpha.coerceIn(0f, 1f)
        val alpha = RippleAlpha(
            draggedAlpha = (pressedAlpha * 1.3f).coerceAtMost(1f),
            focusedAlpha = (pressedAlpha * 0.9f).coerceAtMost(1f),
            hoveredAlpha = pressedAlpha * 0.4f,
            pressedAlpha = pressedAlpha,
        )
        val color = pressed.copy(alpha = 1f)
        return remember(color, alpha, bounded, radius) {
            OxygenRippleIndication(color = color, alpha = alpha, bounded = bounded, radius = radius)
        }
    }
}

private class OxygenRippleIndication(
    private val color: Color,
    private val alpha: RippleAlpha,
    private val bounded: Boolean,
    private val radius: Dp,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = createRippleModifierNode(
        interactionSource = interactionSource,
        bounded = bounded,
        radius = radius,
        color = ColorProducer { color },
        rippleAlpha = { alpha },
    )

    override fun equals(other: Any?): Boolean = other is OxygenRippleIndication &&
        other.color == color &&
        other.alpha == alpha &&
        other.bounded == bounded &&
        other.radius == radius

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + alpha.hashCode()
        result = 31 * result + bounded.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }
}
