// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.contentColorFor

/**
 * 铺底容器：画底色（可选描边），并把内容色下发给子树——这是 M3 `Surface` 的核心职责，
 * 内容色下发与其他容器使用同一套主题级回退契约。
 *
 * 与 M3 的差别只在视觉取向：没有 `tonalElevation` / 阴影，需要描边时用 [border] 显式传
 * （卡片那套细边框）。传 [onClick] 即成为可点击容器，水波纹裁在 [shape] 内。
 *
 * - Spec ID: `CMP-SURFACE`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-SURFACE/`
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param shape 外形，默认直角；卡片用 `shapes.cornerCard`。
 * @param color 底色，默认 `colorScheme.surface`。
 * @param contentColor 内容色；`Color.Unspecified` 时按 [color] 反查（见 [contentColorFor]）。
 * @param border 描边；`null` 表示不画。
 * @param onClick 点击回调；`null` 表示纯容器。
 * @param interactionSource 按压态来源；不传则内部自建。
 * @param content 容器内容。
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = OxygenTheme.colorScheme.surface,
    contentColor: Color = Color.Unspecified,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val resolvedContentColor = if (contentColor.isSpecified) contentColor else contentColorFor(color)
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
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .then(clickableModifier),
    ) {
        CompositionLocalProvider(LocalContentColor provides resolvedContentColor, content = content)
    }
}
