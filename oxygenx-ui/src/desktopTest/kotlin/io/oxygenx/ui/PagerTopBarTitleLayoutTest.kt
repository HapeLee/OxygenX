// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.CompactAppBarContentOffset
import io.oxygenx.ui.navigation.CompactAppBarSubtitleLineHeight
import io.oxygenx.ui.navigation.PagerTopBar
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.navigation.TopAppBarAction
import io.oxygenx.ui.navigation.compactAppBarTitle
import io.oxygenx.ui.navigation.rememberTopAppBarState
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class PagerTopBarTitleLayoutTest {
    @Composable
    private fun Bar(
        tag: String,
        fraction: Float,
        initialPage: Int,
        keepSubtitle: Boolean = false,
        showSubtitle: Boolean = true,
        firstTitle: String = "标题甲",
        secondTitle: String = "标题乙",
    ) {
        PagerTopBar(
            pagerState = rememberPagerState(initialPage = initialPage) { 2 },
            title = { page -> Text(if (page == 0) firstTitle else secondTitle) },
            subtitle = { page -> if (page == 0) Text("副标题") },
            state = rememberTopAppBarState(initialCollapsedFraction = fraction),
            showSubtitle = showSubtitle,
            hideSubtitleOnCollapse = !keepSubtitle,
            modifier = Modifier.width(360.dp).testTag(tag),
            actions = { page -> TopAppBarAction(onClick = {}, modifier = Modifier.testTag("$tag-action-$page")) {} },
        )
    }

    /**
     * 横划顶栏的纵向框架：栏高只由令牌决定，栏头（指示器那一条）先占掉固定高度，
     * 「标题 + 副标题」这一整块在剩下的内容区里居中。测试按令牌复算，不复用实现里的常量。
     */
    private data class Frame(
        val height: Dp,
        val contentTop: Dp,
        val contentCenter: Dp,
        val blockTop: Dp,
        val blockCenter: Dp,
        val subtitleTighten: Dp,
    )

    @Composable
    private fun frameOf(fraction: Float): Frame {
        val spacing = OxygenTheme.spacing
        val layout = OxygenTheme.layout
        val typography = OxygenTheme.typography
        val fontScale = LocalDensity.current.fontScale

        // 行盒高度是 sp，字体缩放为 1 时与 dp 等值；直接按 sp 值折算，避免依赖 Density 作用域。
        fun lineHeightDp(sp: Float): Dp = (sp * fontScale).dp

        // 栏头 = 指示器带（这一栏没有返回键）。
        val contentTop = spacing.appBarPagerIndicatorBand
        val height = layout.appBarLargeHeight +
            (spacing.appBarPagerCollapsedHeight - layout.appBarLargeHeight) * fraction
        val expandedLine = typography.displayMedium.lineHeight.value
        val collapsedLine = typography.titleLarge.compactAppBarTitle().lineHeight.value
        val titleLine = lineHeightDp(expandedLine + (collapsedLine - expandedLine) * fraction)
        val subtitleLine = CompactAppBarSubtitleLineHeight
        val blockHeight = titleLine + subtitleLine
        val contentCenter = contentTop + (height - contentTop) / 2f + CompactAppBarContentOffset * fraction
        // 居中值低于「栏头下沿 + 最小间距」时取后者，与实现里的兜底同一口径。
        val blockTop = (contentCenter - blockHeight / 2f).coerceAtLeast(contentTop + spacing.appBarPagerTitleGap)
        return Frame(
            height = height,
            contentTop = contentTop,
            contentCenter = contentCenter,
            blockTop = blockTop,
            blockCenter = blockTop + blockHeight / 2f,
            subtitleTighten = spacing.appBarSubtitleTighten,
        )
    }

    @Test
    fun barHeightIsFixedForEveryContentShape() = runComposeUiTest {
        var expanded: Frame? = null
        var collapsed: Frame? = null
        setContent {
            OxygenTheme {
                expanded = frameOf(fraction = 0f)
                collapsed = frameOf(fraction = 1f)
                Column(Modifier.width(360.dp).verticalScroll(rememberScrollState())) {
                    // 有副标题 / 这一页没有副标题 / 整体关掉副标题：三种内容形态栏高一致。
                    Bar("expanded-sub", fraction = 0f, initialPage = 0)
                    Bar("expanded-nosub", fraction = 0f, initialPage = 1, secondTitle = "标题一")
                    Bar(
                        "expanded-show-sub-off",
                        fraction = 0f,
                        initialPage = 0,
                        showSubtitle = false,
                        firstTitle = "标题二",
                    )
                    Bar("collapsed-sub", fraction = 1f, initialPage = 0, keepSubtitle = true)
                    Bar("collapsed-nosub", fraction = 1f, initialPage = 1, secondTitle = "标题三")
                }
            }
        }
        val expandedFrame = requireNotNull(expanded)
        val collapsedFrame = requireNotNull(collapsed)

        fun heightOf(tag: String): Dp {
            val bar = onNodeWithTag(tag).getUnclippedBoundsInRoot()
            return bar.bottom - bar.top
        }

        // 展开栏与大顶栏同高，收拢栏用横划栏自己的令牌：都不随标题 / 副标题的有无变化。
        listOf("expanded-sub", "expanded-nosub", "expanded-show-sub-off").forEach {
            assertClose(expandedFrame.height, heightOf(it), "$it 展开栏高")
        }
        listOf("collapsed-sub", "collapsed-nosub").forEach {
            assertClose(collapsedFrame.height, heightOf(it), "$it 收拢栏高")
        }
        assertTrue(heightOf("expanded-sub") > heightOf("collapsed-sub"), "展开栏应高于收拢栏")
    }

    @Test
    fun expandedBlockIsCenteredInTheContentArea() = runComposeUiTest {
        var frame: Frame? = null
        setContent {
            OxygenTheme {
                frame = frameOf(fraction = 0f)
                Bar("bar", fraction = 0f, initialPage = 0)
            }
        }
        val expected = requireNotNull(frame)
        val bar = onNodeWithTag("bar").getUnclippedBoundsInRoot()
        val title = onNodeWithText("标题甲").getUnclippedBoundsInRoot()
        val subtitle = onNodeWithText("副标题").getUnclippedBoundsInRoot()

        // 整块中心落在内容区中心：上面让开指示器那一条，不是整条栏的几何中心。
        assertClose(
            expected.contentCenter,
            ((title.top + subtitle.bottom) / 2) - bar.top,
            "展开态标题块中心",
        )
        assertTrue((title.top - bar.top).value >= expected.contentTop.value, "标题不应压到指示器带上")
    }

    @Test
    fun collapsedBlockSitsOnTheMinimumGapUnderTheIndicator() = runComposeUiTest {
        var frame: Frame? = null
        setContent {
            OxygenTheme {
                frame = frameOf(fraction = 1f)
                Bar("bar", fraction = 1f, initialPage = 0, keepSubtitle = true)
            }
        }
        val expected = requireNotNull(frame)
        val bar = onNodeWithTag("bar").getUnclippedBoundsInRoot()
        val title = onNodeWithText("标题甲").getUnclippedBoundsInRoot()
        val subtitle = onNodeWithText("副标题").getUnclippedBoundsInRoot()

        // 收拢栏比内容区矮，居中值会碰到「栏头下沿 + 最小间距」，真机图库也停在这条线上；
        // 块内只上提半个 `space.appbar.subtitle.tighten` 来抵消行盒余量之差。
        assertClose(
            expected.blockTop + expected.subtitleTighten / 2f,
            title.top - bar.top,
            "收拢态标题块顶",
        )
        // 副标题只上提 `space.appbar.subtitle.tighten`，块高仍按行盒算，栏高不受影响。
        assertClose(expected.subtitleTighten, title.bottom - subtitle.top, "标题底到副标题顶")
    }

    @Test
    fun pagesWithoutSubtitleCenterTheTitleOnTheContentArea() = runComposeUiTest {
        var expanded: Frame? = null
        var collapsed: Frame? = null
        setContent {
            OxygenTheme {
                expanded = frameOf(fraction = 0f)
                collapsed = frameOf(fraction = 1f)
                Column(Modifier.width(360.dp).verticalScroll(rememberScrollState())) {
                    Bar("expanded-nosub", fraction = 0f, initialPage = 1, secondTitle = "标题一")
                    Bar("collapsed-nosub", fraction = 1f, initialPage = 1, secondTitle = "标题二")
                }
            }
        }
        val expandedFrame = requireNotNull(expanded)
        val collapsedFrame = requireNotNull(collapsed)

        fun titleCenter(tag: String, title: String): Dp {
            val bar = onNodeWithTag(tag).getUnclippedBoundsInRoot()
            val node = onNodeWithText(title).getUnclippedBoundsInRoot()
            return ((node.top + node.bottom) / 2) - bar.top
        }

        // 缺副标题时预留的那一行上下均分：展开态标题就落在内容区中心。
        assertClose(expandedFrame.contentCenter, titleCenter("expanded-nosub", "标题一"), "展开态无副标题标题中心")
        // 收拢态用令牌算的块心（收拢字号下令牌行盒与实测一致）。
        assertClose(collapsedFrame.blockCenter, titleCenter("collapsed-nosub", "标题二"), "收拢态无副标题标题中心")
    }

    @Test
    fun actionsStayOnTheContentCenterAcrossPages() = runComposeUiTest {
        var frame: Frame? = null
        setContent {
            OxygenTheme {
                frame = frameOf(fraction = 1f)
                Column(Modifier.width(360.dp).verticalScroll(rememberScrollState())) {
                    Bar("collapsed-sub", fraction = 1f, initialPage = 0, keepSubtitle = true)
                    Bar("collapsed-nosub", fraction = 1f, initialPage = 1, secondTitle = "标题一")
                }
            }
        }
        val expected = requireNotNull(frame)

        fun actionCenter(tag: String, page: Int): Dp {
            val top = onNodeWithTag(tag).getUnclippedBoundsInRoot().top
            val action = onNodeWithTag("$tag-action-$page").getUnclippedBoundsInRoot()
            return (action.top + action.bottom) / 2 - top
        }

        // 操作区跟着内容区中心走：换页（有无副标题）不会上下跳。
        assertClose(expected.contentCenter, actionCenter("collapsed-sub", 0), "有副标题页的操作区中心")
        assertClose(expected.contentCenter, actionCenter("collapsed-nosub", 1), "无副标题页的操作区中心")
    }

    @Test
    fun collapsedBarHidesSubtitleAndKeepsTheIndicator() = runComposeUiTest {
        var frame: Frame? = null
        setContent {
            OxygenTheme {
                frame = frameOf(fraction = 1f)
                Bar("collapsed-sub", fraction = 1f, initialPage = 0)
            }
        }
        val expected = requireNotNull(frame)
        val bar = onNodeWithTag("collapsed-sub")
        val bounds = bar.getUnclippedBoundsInRoot()
        val title = onNodeWithText("标题甲").getUnclippedBoundsInRoot()

        val pixels = bar.captureToImage().toPixelMap()

        // 背景是透明的：只把真正画出来的深色像素算作墨迹。
        fun isInk(x: Int, y: Int): Boolean {
            val color = pixels[x, y]
            return color.alpha >= 0.5f && color.luminance() < 0.5f
        }

        val titleTop = (title.top - bounds.top).value.toInt()
        assertTrue(
            (0 until titleTop).any { y -> (0 until pixels.width).any { x -> isInk(x, y) } },
            "收拢栏在标题上方仍应绘制横划指示器",
        )
        // 指示器落在栏头那一条里，不会掉到标题下面去。
        assertTrue(
            titleTop.toFloat() >= expected.contentTop.value,
            "标题不应压到指示器带上: titleTop=$titleTop zoneTop=${expected.contentTop}",
        )

        val inkBottom = (title.bottom - bounds.top).value.toInt()
        val dividerRows = 4
        val strayRows = (inkBottom + 3 until pixels.height - dividerRows).filter { y ->
            (0 until pixels.width).any { x -> isInk(x, y) }
        }
        assertTrue(
            strayRows.isEmpty(),
            "收拢栏里仍有副标题墨迹: $strayRows / inkBottom=$inkBottom h=${pixels.height}",
        )
    }

    @Test
    fun retainedSubtitleIsDrawnDuringCollapse() = runComposeUiTest {
        setContent {
            OxygenTheme {
                Bar("bar", fraction = 1f, initialPage = 0, keepSubtitle = true)
            }
        }
        val bar = onNodeWithTag("bar")
        val origin = bar.getUnclippedBoundsInRoot().top.value
        val subtitle = onNodeWithText("副标题").getUnclippedBoundsInRoot()
        val pixels = bar.captureToImage().toPixelMap()
        val subtitleRows = (subtitle.top.value - origin).toInt() until (subtitle.bottom.value - origin).toInt()
        assertTrue(
            subtitleRows.any { y ->
                (0 until pixels.width).any { x ->
                    val color = pixels[x, y]
                    color.alpha >= 0.5f && color.luminance() < 0.5f
                }
            },
            "保留副标题时正文行没有文字像素",
        )
    }

    @Test
    fun partialCollapseKeepsTheFixedFrame() = runComposeUiTest {
        var frame: Frame? = null
        setContent {
            OxygenTheme {
                frame = frameOf(fraction = 0.5f)
                Bar("bar", fraction = 0.5f, initialPage = 0, keepSubtitle = true)
            }
        }
        val expected = requireNotNull(frame)
        val bar = onNodeWithTag("bar").getUnclippedBoundsInRoot()
        // 中间态同样按令牌插值：栏高固定，标题块位置连续变化，不会因为文字高度突变。
        assertClose(expected.height, bar.bottom - bar.top, "半程收拢栏高")
        val title = onNodeWithText("标题甲").getUnclippedBoundsInRoot()
        assertTrue(
            (title.top - bar.top).value > expected.contentTop.value,
            "半程收拢时标题仍在指示器带下方",
        )
    }

    /** 旧用例的「与标准顶栏同高」已不适用：收拢横划栏比标准顶栏高，但收拢态仍要贴合真机图库。 */
    @Test
    fun standardBarStaysShorterThanCollapsedPagerBar() = runComposeUiTest {
        setContent {
            OxygenTheme {
                Column(Modifier.width(360.dp)) {
                    TopAppBar(title = { Text("标题丙") }, modifier = Modifier.testTag("standard"))
                    Bar("collapsed", fraction = 1f, initialPage = 1)
                }
            }
        }
        val standard = onNodeWithTag("standard").getUnclippedBoundsInRoot()
        val bar = onNodeWithTag("collapsed").getUnclippedBoundsInRoot()
        assertTrue(
            (bar.bottom - bar.top) > (standard.bottom - standard.top),
            "收拢横划栏应高于标准顶栏",
        )
    }

    private fun assertClose(expected: Dp, actual: Dp, label: String) {
        assertTrue(abs((expected - actual).value) <= 1f, "$label: 期望 $expected，实际 $actual")
    }
}
