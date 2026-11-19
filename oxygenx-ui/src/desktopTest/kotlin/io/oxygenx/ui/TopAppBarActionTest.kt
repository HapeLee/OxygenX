// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.LargeTopAppBar
import io.oxygenx.ui.navigation.PagerTopBar
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.navigation.TopAppBarAction
import io.oxygenx.ui.navigation.rememberTopAppBarState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalTestApi::class)
class TopAppBarActionTest {
    @Test
    fun actionIconsKeepTheOriginalCatalogCenterSpacing() = runComposeUiTest {
        setContent {
            OxygenTheme {
                Column(Modifier.width(360.dp)) {
                    TopAppBar(title = { Text("Standard") }, actions = { Actions("standard") })
                    LargeTopAppBar(title = { Text("Large") }, actions = { Actions("large") })
                    PagerTopBar(
                        pagerState = rememberPagerState { 1 },
                        title = { Text("Pager") },
                        actions = { Actions("pager") },
                    )
                }
            }
        }
        for (kind in listOf("standard", "large", "pager")) {
            val first = onNodeWithTag("$kind-first").getUnclippedBoundsInRoot()
            val second = onNodeWithTag("$kind-second").getUnclippedBoundsInRoot()
            assertEquals(48.dp, first.right - first.left, kind)
            assertEquals(0.dp, second.left - first.right, kind)
            val searchIcon = onNodeWithTag("$kind-search-icon", useUnmergedTree = true).getUnclippedBoundsInRoot()
            val moreIcon = onNodeWithTag("$kind-more-icon", useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertEquals(24.dp, searchIcon.right - searchIcon.left, kind)
            assertEquals(24.dp, moreIcon.right - moreIcon.left, kind)
            val searchCenter = (searchIcon.left + searchIcon.right) / 2
            val moreCenter = (moreIcon.left + moreIcon.right) / 2
            assertEquals(48.dp, moreCenter - searchCenter, kind)
        }
    }

    @Test
    fun compactBarsAlignTitlesAndNavigationWithoutShrinkingTheTouchTarget() = runComposeUiTest {
        setContent {
            OxygenTheme {
                Column(Modifier.width(360.dp)) {
                    TopAppBar(
                        title = { Text("Settings", Modifier.testTag("standard-title")) },
                        modifier = Modifier.testTag("standard-bar"),
                        onNavigateBack = {},
                        navigationContentDescription = "standard-back",
                    )
                    LargeTopAppBar(
                        title = { Text("Settings", Modifier.testTag("collapsed-title")) },
                        modifier = Modifier.testTag("collapsed-bar"),
                        onNavigateBack = {},
                        navigationContentDescription = "collapsed-back",
                        state = rememberTopAppBarState(initialCollapsedFraction = 1f),
                    )
                }
            }
        }
        for (kind in listOf("standard", "collapsed")) {
            val bar = onNodeWithTag("$kind-bar").getUnclippedBoundsInRoot()
            val title = onNodeWithTag("$kind-title").getUnclippedBoundsInRoot()
            val glyph = onNodeWithContentDescription("$kind-back", useUnmergedTree = true).getUnclippedBoundsInRoot()
            val target = onNodeWithContentDescription("$kind-back").getUnclippedBoundsInRoot()
            assertEquals(74.dp, bar.bottom - bar.top)
            assertEquals(48.dp, title.left - bar.left)
            assertEquals(16.dp, glyph.right - glyph.left)
            assertEquals(48.dp, target.right - target.left)
            assertEquals(48.dp, target.bottom - target.top)
            assertEquals(41f, ((title.top + title.bottom) / 2 - bar.top).value, 0.5f, kind)
            assertEquals(41f, ((glyph.top + glyph.bottom) / 2 - bar.top).value, 0.5f, kind)
        }
    }

    @Test
    fun pressRippleExtendsBeyondTheTouchAreaWithOrWithoutLongClick() = runComposeUiTest {
        setContent {
            OxygenTheme {
                Column {
                    for (longClick in listOf(false, true)) {
                        Box(
                            modifier = Modifier.size(112.dp).background(Color.White).testTag("panel-$longClick"),
                            contentAlignment = Alignment.Center,
                        ) {
                            TopAppBarAction(
                                onClick = {},
                                onLongClick = if (longClick) ({}) else null,
                                modifier = Modifier.testTag("action-$longClick"),
                            ) {}
                        }
                    }
                }
            }
        }
        mainClock.autoAdvance = false
        for (longClick in listOf(false, true)) {
            val panel = onNodeWithTag("panel-$longClick")
            val action = onNodeWithTag("action-$longClick")
            val before = panel.captureToImage().toPixelMap()
            val y = before.height / 2
            val inside = before.width / 2 + before.width * 30 / 112
            val outside = before.width / 2 + before.width * 40 / 112
            action.performTouchInput { down(center) }
            mainClock.advanceTimeBy(350)
            val pressed = panel.captureToImage().toPixelMap()
            assertNotEquals(before[inside, y], pressed[inside, y], "Ripple must extend past 24dp")
            assertEquals(before[outside, y], pressed[outside, y], "Ripple must remain inside 40dp")
            action.performTouchInput { up() }
            mainClock.advanceTimeBy(350)
        }
    }

    @Composable
    private fun RowScope.Actions(kind: String) {
        TopAppBarAction(onClick = {}, modifier = Modifier.testTag("$kind-first")) {
            Icon(OxygenIcons.Search, null, modifier = Modifier.testTag("$kind-search-icon"))
        }
        TopAppBarAction(onClick = {}, modifier = Modifier.testTag("$kind-second")) {
            Icon(MaterialSymbols.MoreVert, null, modifier = Modifier.testTag("$kind-more-icon"))
        }
    }
}
