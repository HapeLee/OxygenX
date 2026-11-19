// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.contentColorFor

/**
 * 卡片容器：圆角 + 一圈细边框，无投影；可点击时只有 M3 水波纹，没有按压缩放。
 *
 * 边框与顶栏分割线是同一套视觉：颜色 `color.outline.variant`、厚度 `size.divider.thickness`，
 * 不再另立 `size.card.border.width`（那只会复制同一个值）。因此卡片没有投影——
 * 层级靠底色（`color.surface.container`）与这圈线表达，原来唯一的 `elevation.card`
 * 令牌随之失去消费者并删除。
 *
 * [shape] 与 [border] 都可改：方角卡片传 `RectangleShape`，不要描边传 `border = null`。
 *
 * - Spec ID: `CMP-CARD`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-CARD/`
 * - Motion: 无。触摸反馈只走水波纹（可被 [LocalRippleEnabled] 关闭）。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param onClick 传入非空时整卡可点击。
 * @param colors 卡片底色与边框色集合。
 * @param shape 卡片外形，默认 `shapes.corner.card`。
 * @param border 描边；默认取 `colors.borderColor` 与 `size.divider.thickness` 的细线，传 `null` 去掉。
 * @param contentPadding 内容内边距；列表型卡片传 `PaddingValues()` 以便分割线通栏。
 * @param interactionSource 按压态来源（M3 口径）；不传则内部自建。
 * @param content 卡片内容。
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.colors(),
    shape: Shape = OxygenTheme.shapes.cornerCard,
    border: BorderStroke? = CardDefaults.border(colors.borderColor),
    contentPadding: PaddingValues = CardDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    // M3 口径：外部可观察按压态；不传则内部自己 remember 一个。
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = source,
            indication = IndicationDefaults.ripple(),
            role = Role.Button,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    // 卡片是容器：把内容色下发下去，卡内的 Text / Icon 自动跟随底色。
    val contentColor = contentColorFor(colors.containerColor)
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Column(
            modifier = modifier
                .clip(shape)
                .background(colors.containerColor)
                .then(
                    if (border != null) {
                        Modifier.border(width = border.width, brush = border.brush, shape = shape)
                    } else {
                        Modifier
                    },
                )
                .then(clickableModifier)
                .padding(contentPadding),
            content = content,
        )
    }
}

object CardDefaults {
    val ContentPadding: PaddingValues
        @Composable get() = PaddingValues(OxygenTheme.spacing.cardPadding)

    /** 默认描边：与顶栏分割线同一套厚度（`size.divider.thickness`）。 */
    @Composable
    fun border(color: Color = OxygenTheme.colorScheme.outlineVariant): BorderStroke {
        val width = OxygenTheme.spacing.dividerThickness
        return remember(color, width) { BorderStroke(width, color) }
    }

    @Composable
    fun colors(
        containerColor: Color = OxygenTheme.colorScheme.surfaceContainer,
        borderColor: Color = OxygenTheme.colorScheme.outlineVariant,
    ): CardColors = remember(containerColor, borderColor) {
        CardColors(containerColor = containerColor, borderColor = borderColor)
    }
}

@Immutable
data class CardColors(
    val containerColor: Color,
    /** 细边框颜色，默认与顶栏分割线同色（`color.outline.variant`）。 */
    val borderColor: Color,
)
