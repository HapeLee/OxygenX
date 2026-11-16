// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Slider
import io.oxygenx.ui.feedback.CircularProgressIndicator
import io.oxygenx.ui.feedback.LinearProgressIndicator
import kotlin.math.roundToInt

fun LazyListScope.progressSection() {
    item(key = "cmp-progress") {
        SmallTitle(text = "Progress / 线性进度", specId = "CMP-PROGRESS")
        DemoCard {
            LinearProgressVariants()
        }
    }
    item(key = "cmp-progress-circular") {
        SmallTitle(text = "CircularProgress / 圆环进度", specId = "CMP-PROGRESS-CIRCULAR")
        DemoCard {
            CircularProgressVariants()
        }
    }
}

@Composable
private fun LinearProgressVariants() {
    var value by remember { mutableFloatStateOf(0.35f) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("确定态：${(value * 100).roundToInt()}%（滑块即进度来源）")
        LinearProgressIndicator(progress = value)
        Slider(value = value, onValueChange = { value = it }, modifier = Modifier.fillMaxWidth())
        Label("不确定态：一轮 1750ms，条从头长出、右移、尾部收尽")
        LinearProgressIndicator()
        Label("禁用态：确定 / 不确定")
        LinearProgressIndicator(progress = 0.6f, enabled = false)
        LinearProgressIndicator(enabled = false)
    }
}

@Composable
private fun CircularProgressVariants() {
    val sizes = listOf(0.25f, 0.5f, 0.75f)
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("确定态 25% / 50% / 75% 与不确定态")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            sizes.forEach { progress ->
                CircularProgressIndicator(progress = progress)
            }
            CircularProgressIndicator()
            CircularProgressIndicator(progress = 0.6f, enabled = false)
        }
        Label("尺寸由 diameter / strokeWidth 覆盖，厚度与线性进度同档")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(progress = null)
            CircularProgressIndicator(
                progress = null,
                diameter = OxygenTheme.spacing.progressCircularSize * 2,
                strokeWidth = OxygenTheme.spacing.progressCircularStroke,
            )
        }
    }
}

@Composable
private fun Label(text: String) {
    BasicText(
        text = text,
        style = OxygenTheme.typography.caption.copy(color = OxygenTheme.colorScheme.onSurfaceVariant),
    )
}
