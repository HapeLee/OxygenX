// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Slider
import kotlin.math.roundToInt

fun LazyListScope.sliderSection() {
    item(key = "cmp-slider") {
        SmallTitle(text = "Slider / 滑块", specId = "CMP-SLIDER")
        DemoCard {
            SliderVariants()
        }
    }
}

@Composable
private fun SliderVariants() {
    val colors = OxygenTheme.colorScheme
    var continuous by remember { mutableFloatStateOf(0.35f) }
    var stepped by remember { mutableFloatStateOf(0.5f) }
    var custom by remember { mutableFloatStateOf(0.4f) }
    var customTrack by remember { mutableFloatStateOf(0.65f) }
    var rtl by remember { mutableFloatStateOf(0.25f) }
    var committed by remember { mutableFloatStateOf(0.25f) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "连续取值：${(continuous * 100).roundToInt()}%",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Slider(
            value = continuous,
            onValueChange = { continuous = it },
            modifier = Modifier.fillMaxWidth(),
        )
        BasicText(
            text = "量化档位：${(stepped * 100).roundToInt()}%",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Slider(
            value = stepped,
            onValueChange = { stepped = it },
            modifier = Modifier.fillMaxWidth(),
            steps = 3,
        )
        BasicText(
            text = "RTL / 键盘 / 读屏：当前 ${(rtl * 100).roundToInt()}%，已提交 ${(committed * 100).roundToInt()}%",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Slider(
                value = rtl,
                onValueChange = { rtl = it },
                steps = 3,
                onValueChangeFinished = { committed = rtl },
            )
        }
        BasicText(
            text = "禁用态",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Slider(
            value = 0.6f,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
        )
        BasicText(
            text = "自定义拇指（thumb 槽）：按 fraction 定位，默认拇指圆不再绘制",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Slider(
            value = custom,
            onValueChange = { custom = it },
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(OxygenTheme.spacing.sliderThumbSize * 1.6f)
                        .clip(CircleShape)
                        .background(colors.secondary),
                )
            },
        )
        BasicText(
            text = "自定义轨道（track 槽）：槽位铺满整行，按 fraction 自己画进度",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Slider(
            value = customTrack,
            onValueChange = { customTrack = it },
            modifier = Modifier.fillMaxWidth(),
            track = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(OxygenTheme.spacing.sliderTrackHeight)
                            .clip(CircleShape)
                            .background(colors.surfaceContainerHighest),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(OxygenTheme.spacing.sliderTrackHeight)
                            .clip(CircleShape)
                            .background(colors.primary),
                    )
                }
            },
        )
    }
}
