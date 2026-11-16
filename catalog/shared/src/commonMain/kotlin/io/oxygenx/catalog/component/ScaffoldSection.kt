// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Text

fun LazyListScope.scaffoldSection(onOpenScaffoldDemo: () -> Unit) {
    item(key = "cmp-scaffold") {
        SmallTitle(text = "Scaffold / 页面骨架", specId = "CMP-SCAFFOLD")
        DemoCard {
            BasicText(
                text = "Scaffold 铺底色、让开 safeDrawing，并可同时承载顶栏、底栏、悬浮按钮与 " +
                    "Snackbar 宿主；把同一个 TopAppBarState 传给 topBarState 后，内容滚动就会驱动大顶栏收拢。",
                style = OxygenTheme.typography.bodyMedium.copy(
                    color = OxygenTheme.colorScheme.onSurface,
                ),
            )
            Button(onClick = onOpenScaffoldDemo) { Text("Scaffold 能力页", maxLines = 1) }
        }
    }
}
