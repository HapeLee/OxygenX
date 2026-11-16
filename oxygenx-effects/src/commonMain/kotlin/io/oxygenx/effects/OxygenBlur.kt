// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.oxygenx.core.LocalOxygenCapabilities

/**
 * 能力感知的模糊：不支持真实模糊的平台保持原样，而不是抛错或静默绘错。
 * 需要「有层级感的半透明玻璃」时，应同时提供不依赖模糊的替代样式。
 */
@Composable
fun Modifier.oxygenBlur(radius: Dp): Modifier {
    val blurSupported = LocalOxygenCapabilities.current.blur
    return if (blurSupported && radius > 0.dp) blur(radius) else this
}
