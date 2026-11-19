// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.ui.basic.Icon
import kotlin.math.abs
import kotlin.math.roundToInt

/** 顶栏拖拽时判定「甩动翻页」的速度阈值（按 dp 折算成 px/s，避免不同密度下手感不一致）。 */
private val PagerDragFlingThreshold = 400.dp

/**
 * 分段指示器的一段宽度：`(总宽 - 段间距 × (段数 - 1)) / 段数`。
 *
 * 图库真机三段各 310px、间隔 24px、左右各从 20dp 起，总宽 979px @450dpi。
 */
internal fun pagerIndicatorSegmentWidth(count: Int, widthPx: Float, gapPx: Float): Float {
    if (count <= 0) return 0f
    return ((widthPx - gapPx * (count - 1)) / count).coerceAtLeast(0f)
}

/** 第 [index] 段的起始 x：前面各段宽度 + 间隔。 */
internal fun pagerIndicatorSegmentOffset(index: Int, widthPx: Float, gapPx: Float, count: Int): Float {
    if (index <= 0) return 0f
    return index * (pagerIndicatorSegmentWidth(count, widthPx, gapPx) + gapPx)
}

/**
 * 高亮段在连续页位置上的 x：段等宽且间隔固定，所以位置随 `pagePosition` 线性推进
 * （3.25 表示停在 3 号段往 4 号段滑了四分之一），拖动中跟着手指走。
 */
internal fun pagerIndicatorActiveOffset(pagePosition: Float, widthPx: Float, gapPx: Float, count: Int): Float {
    if (count <= 0) return 0f
    val segment = pagerIndicatorSegmentWidth(count, widthPx, gapPx)
    return pagePosition.coerceIn(0f, (count - 1).toFloat()) * (segment + gapPx)
}

/** 第 [index] 页标题相对当前滚动位置的位移（页宽为单位）：`(index - pagePosition) × 页宽`。 */
internal fun pagerTitleTranslation(index: Int, pagePosition: Float, pageWidthPx: Float): Float = (index - pagePosition) * pageWidthPx

/**
 * 在顶栏上直接拖拽后，松手应该落到哪一页。
 *
 * 规则与 pager 自己的吸附一致：先看拖过半页没有（`offsetFraction` 四舍五入），
 * 再让快速甩动覆盖它——顶栏上的拖拽没有 pager 的 fling 惯性，所以速度只用来判断
 * 「这一甩算不算翻页」。速度正方向是手指右划，对应回上一页。
 */
internal fun pagerDragSettlePage(
    currentPage: Int,
    pageCount: Int,
    offsetFraction: Float,
    dragVelocity: Float,
    flingThresholdPx: Float,
): Int {
    if (pageCount <= 0) return 0
    val step = when {
        dragVelocity <= -flingThresholdPx -> 1
        dragVelocity >= flingThresholdPx -> -1
        else -> offsetFraction.roundToInt()
    }
    return (currentPage + step).coerceIn(0, pageCount - 1)
}

/**
 * 第 [index] 页标题的不透明度：滚到一半时整块消失，滚到位时完全显现。
 *
 * 真机图库在横划中途标题是**全空**的（抽帧里标题区最暗像素 250，等于纯白），
 * 所以这里用 `1 - 2 × |位移|`，而不是随位移线性淡出。
 */
internal fun pagerTitleAlpha(index: Int, pagePosition: Float): Float = (1f - 2f * abs(index - pagePosition)).coerceIn(0f, 1f)

/**
 * 横划顶栏：配合 [androidx.compose.foundation.pager.HorizontalPager] 使用，复刻一加图库首页。
 *
 * 本质是「大顶栏 + 横划指示器」，不是另一套顶栏：栏头由分段指示器占据（N 段等宽、2dp 高、
 * 8dp 间隔、两端圆角，上沿走 `space.appbar.pager.top`），栏头之下的大标题
 * （`type.display.medium`，与大顶栏同一个令牌）与副标题（`type.appbar.subtitle`）沿用
 * [LargeTopAppBar] 的展开态落位参数（[LargeTopBarMetrics]）；展开高度、收拢高度、
 * 标题字号插值、操作区从右下到右中、底部分割线淡入也都与大顶栏同构，只是收拢高度更高
 * （`size.appbar.pager.collapsed.height`，因为收拢态仍保留指示器与副标题）。
 *
 * 指示器是一条 `color.on.surface` 的长条矩形压在 `color.surface.container.highest` 的底轨上，
 * 位置随 `pagePosition` 连续推进：拖动时跟着手指从当前段滑向目标段；标题同样随 pager
 * 连续横移、中途淡出，右侧可挂操作区。
 *
 * 顶栏本身也能横划：在标题区拖动会直接驱动 pager（`dispatchRawDelta`，跟手 1:1），
 * 松手按「拖过半页 / 快速甩动」吸附到相邻页——pager 在栏体下方，手指落在标题上时
 * 事件到不了它，这条拖拽通道就是补这个缺口。标题的左右内距写在标题**内部**、容器本身
 * 不裁切：横划时整块（含边距）随页平移，文字一路滑到屏幕边缘才被栏体裁掉，静止时左边距
 * 仍与大顶栏对齐（不会在半个屏的位置提前被切断）。
 *
 * 有导航图标时（[onNavigateBack] 非空）栏头从「只有指示器」变成与大顶栏同高的
 * `size.appbar.height`（74dp）：指示器仍贴在最上沿，返回键停在栏头垂直中线上，大标题整块
 * 下移到栏头下方（与大顶栏有返回键时同一个落位）。收拢过程中标题升回无导航图标时的位置、
 * 同时右移到「返回键 + 触摸目标」之后让开箭头，因此带不带导航图标的收拢态是同一套几何，
 * 差别只在最左侧多一个返回箭头。
 *
 * - Spec ID: `CMP-APPBAR-PAGER`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Gallery 3.6 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-APPBAR/`
 * - Motion: `MOT-STATE-003`（与大顶栏共用收拢动画）
 * - Status: `draft`
 *
 * @param pagerState 横划容器的状态，指示器与标题都从它取滚动位置；段数 = `pagerState.pageCount`。
 * @param title 每页的大标题内容槽，形参是页下标；槽位内下发的排版与大顶栏同一条按
 *   `collapsedFraction` 插值的样式，所以写 `Text("照片", maxLines = 1)` 即得逐像素验证过的形态。
 * @param modifier 外部 Modifier。
 * @param subtitle 每页的副标题内容槽，形参是页下标；已下发 `type.appbar.subtitle`。
 * @param showSubtitle 是否显示副标题；默认显示，传 `false` 时只留标题（与标准 / 大顶栏同名同义）。
 * @param onNavigateBack 传入非空时显示导航图标（返回），语义与标准 / 大顶栏一致。
 * @param navigationContentDescription 返回按钮的无障碍描述；为空则按钮不可被朗读定位。
 * @param state 收拢进度来源，与 `Scaffold` 的 `topBarState` 传同一个；为 `null` 时固定展开。
 * @param actions 右侧操作区，**按当前页重组**（形参是 `pagerState.currentPage`）：一加图库每页的
 *   右上角按钮不同（照片页搜索 / 设置、相册页新建相册等），所以这里给的是「页 → 操作区」的
 *   工厂而不是一组固定按钮。当前页与相邻页两套同时挂着，按 [PagerState] 的连续位置跟手
 *   平移、过半淡出，因此换页时按钮是滑进来而不是硬切。建议使用 [TopAppBarAction]；
 *   展开态贴右下、收拢态贴右中。
 */
// `actions` / `title` / `subtitle` 槽位故意在同一处复用于多组（当前页 + 相邻页）：每组都是
// 独立的组合实例、各有自己的状态，不存在「同一条代码路径上槽位串味」的问题。
@Suppress("ktlint:compose:content-slot-reused")
@Composable
fun PagerTopBar(
    pagerState: PagerState,
    title: @Composable (page: Int) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable (page: Int) -> Unit)? = null,
    showSubtitle: Boolean = true,
    onNavigateBack: (() -> Unit)? = null,
    navigationContentDescription: String? = null,
    state: TopAppBarState? = null,
    actions: @Composable RowScope.(page: Int) -> Unit = {},
) {
    val spacing = OxygenTheme.spacing
    val layout = OxygenTheme.layout
    val typography = OxygenTheme.typography
    val colors = OxygenTheme.colorScheme
    val density = LocalDensity.current
    val fraction = state?.collapsedFraction ?: 0f
    val navigationIconSize = appBarNavigationIconSize(fraction)

    if (state != null) {
        // 收拢进度按 `size.appbar.collapse.distance` 折算，与大顶栏同口径：
        // 真机滚过 171dp 时标题已缩到标准顶栏字号，剩余高度差由收尾动画补完。
        val rangePx = with(density) { spacing.appBarCollapseDistance.toPx() }
        SideEffect { state.collapseRangePx = rangePx }
    }

    // 横划顶栏 = 大顶栏 + 横划指示器：栏头高 = 指示器上沿 + 指示器高度，标题从栏头下沿
    // 再让 `space.appbar.pager.title.gap`。**指示器**左右仍用大顶栏的内距（真机量的 20dp），
    // 标题则贴屏幕边缘、不吃内距——横划时要能一路滑出屏幕。
    // 有导航图标时栏头撑到标准顶栏高度（74dp），指示器仍贴最上沿、返回键居中于栏头，
    // 标题整块下移到栏头下方——与大顶栏有返回键时同一条规则。
    val metrics = largeTopBarMetrics(
        headerHeight = if (onNavigateBack != null) {
            spacing.appBarHeight
        } else {
            spacing.appBarPagerTop + spacing.appBarPagerIndicatorHeight
        },
        titleGap = if (onNavigateBack != null) 0.dp else spacing.appBarPagerTitleGap,
    )
    // 无导航图标时的标题落位，也是带导航图标收拢完成的落位（两种形态收拢后必须一致）。
    val collapsedTitleTop = spacing.appBarPagerTop + spacing.appBarPagerIndicatorHeight + spacing.appBarPagerTitleGap
    val collapsedStartInset = if (onNavigateBack != null) {
        spacing.touchTargetMin
    } else {
        metrics.titleInset
    }
    // 标题的左右内距写在标题**内部**（不靠整体摆位）：横划时整块含内距一起平移，
    // 文字仍能一路滑出屏幕，静止时又保持与大顶栏一致的左边距。
    val titleStartInset = if (onNavigateBack != null) {
        lerp(metrics.titleInset, collapsedStartInset, fraction)
    } else {
        metrics.titleInset
    }
    // 右侧给操作区留位（与改版前同一口径）：长标题不会钻到操作图标下面。
    val titleEndInset = metrics.endInset + spacing.gapSmall + spacing.touchTargetMin * 2

    // 标题只有一条：展开态复用大顶栏的 displayMedium 角色，按收拢进度连续缩到
    // 标准顶栏标题（与大顶栏同一条插值、同一个样式）。
    val titleStyle = lerpAppBarTitleStyle(
        expanded = typography.displayMedium.copy(color = colors.onSurface),
        collapsed = typography.titleLarge.compactAppBarTitle().copy(color = colors.onSurface),
        fraction = fraction,
    )
    // 连续页位置：3.25 表示停在第 3 页往第 4 页划了四分之一。
    val pagePosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
    // 操作区跟标题同口径按「页」渲染：当前页一套 + 相邻页一套，位移与不透明度都按 pagePosition
    // 连续算，才会跟手滑入滑出；尾页没有下一页，第二套给 null（不画内容但保留槽位）。
    val lastPage = (pagerState.pageCount - 1).coerceAtLeast(0)
    val actionsLowerPage = pagePosition.toInt().coerceIn(0, lastPage)
    val actionsUpperPage = (actionsLowerPage + 1).takeIf { it <= lastPage }

    // 顶栏自己也能横划：手指在标题区拖动时把位移直接交给 pager（`dispatchRawDelta` 不带动画，
    // 跟手 1:1），松手再按「过半 / 甩动」吸附到目标页——顶栏不在 pager 内部，拿不到它的
    // fling 惯性，所以只把速度当作「这一甩算不算翻页」的判断依据。
    val dragState = rememberDraggableState { delta -> pagerState.dispatchRawDelta(-delta) }
    val flingThresholdPx = with(density) { PagerDragFlingThreshold.toPx() }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // 横移以**页宽**（整屏宽）为基准，跟手比例才是 1:1；标题容器因为要让开左右内距
        // 与操作区，比页宽窄。
        val pageWidthPx = constraints.maxWidth.toFloat()
        // 展开高度与大顶栏一致（真机 265dp），窗口更矮时收敛到可用高度。
        val expandedHeight = if (maxHeight == Dp.Infinity) {
            layout.appBarLargeHeight
        } else {
            minOf(layout.appBarLargeHeight, maxHeight)
                .coerceAtLeast(spacing.appBarPagerCollapsedHeight)
        }
        val barHeight = lerp(expandedHeight, spacing.appBarPagerCollapsedHeight, fraction)
        Layout(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        pagerState.animateScrollToPage(
                            pagerDragSettlePage(
                                currentPage = pagerState.currentPage,
                                pageCount = pagerState.pageCount,
                                offsetFraction = pagerState.currentPageOffsetFraction,
                                dragVelocity = velocity,
                                flingThresholdPx = flingThresholdPx,
                            ),
                        )
                    },
                )
                .clipToBounds()
                .drawBehind {
                    if (fraction > 0f) {
                        val thickness = spacing.dividerThickness.toPx()
                        drawRect(
                            color = colors.outlineVariant,
                            topLeft = Offset(0f, size.height - thickness),
                            size = Size(size.width, thickness),
                            alpha = fraction,
                        )
                    }
                },
            content = {
                // 0: 栏头 —— 与 [LargeTopAppBar] 的栏头同构（同一高度语义，内容是指示器）。
                // 指示器贴栏头**上沿**（`space.appbar.pager.top`）：栏头在有导航图标时会被
                // 撑到 74dp，此时它不能跟着栏头底部走，否则会掉到大标题上方。
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(metrics.headerHeight)
                        .padding(top = spacing.appBarPagerTop),
                    contentAlignment = Alignment.TopStart,
                ) {
                    PagerSegments(
                        count = pagerState.pageCount,
                        pagePosition = pagePosition,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = metrics.titleInset),
                    )
                }
                // 1: 标题块（大标题 + 副标题），跟手横移。容器本身不裁切、不摆内距：
                // 内距写进每个标题内部，横划时文字能一路滑出屏幕，静止时边距照样在。
                Box {
                    PagerTitles(
                        pageCount = pagerState.pageCount,
                        title = title,
                        subtitle = subtitle,
                        showSubtitle = showSubtitle,
                        pagePosition = pagePosition,
                        pageWidthPx = pageWidthPx,
                        titleStyle = titleStyle,
                        subtitleTighten = spacing.appBarSubtitleTighten * fraction,
                        startInset = titleStartInset,
                        endInset = titleEndInset,
                    )
                }
                // 2 / 3: 操作区。同时挂「当前页」与「相邻页」两套，按 `pagePosition` 跟手平移、
                // 过半淡出——与标题同一套位移 / 透明度口径，换页不再是图标硬切。
                // 只有一页时第二套传 `null`（不画内容，但保留槽位，Layout 的子项下标才稳定）。
                PagerActions(page = actionsLowerPage, pagePosition = pagePosition, pageWidthPx = pageWidthPx) {
                    actions(actionsLowerPage)
                }
                PagerActions(page = actionsUpperPage, pagePosition = pagePosition, pageWidthPx = pageWidthPx) {
                    // `page` 非空时才会走到这里，直接取非空值送进调用方。
                    actions(actionsUpperPage ?: actionsLowerPage)
                }
                // 3: 导航图标（可选）。放在最后加入，水波纹才能盖在标题上。
                if (onNavigateBack != null) {
                    TopAppBarAction(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            imageVector = OxygenIcons.Back,
                            contentDescription = navigationContentDescription,
                            size = navigationIconSize,
                            mirrorInRtl = true,
                        )
                    }
                }
            },
        ) { measurables, constraints ->
            val widthPx = constraints.maxWidth
            val heightPx = constraints.maxHeight
            val headerPlaceable = measurables[0].measure(Constraints(maxWidth = widthPx))
            // 标题容器按整栏宽测量、摆在 x = 0：内距已经写进标题内部，
            // 横移量又以页宽为基准，容器必须是整栏宽，否则文字会在容器右缘被截断。
            val titlesPlaceable = measurables[1].measure(Constraints(maxWidth = widthPx))
            val lowerActionsPlaceable = measurables[2].measure(Constraints())
            val upperActionsPlaceable = measurables[3].measure(Constraints())
            // 两套操作区宽度可能不同（图标数量不一样），对齐用的高度取高的那一套。
            val actionsHeight = maxOf(lowerActionsPlaceable.height, upperActionsPlaceable.height)
            // 导航图标属于栏头：始终停在 74dp 栏头的垂直中线上，不随展开高度浮动。
            val backPlaceable = measurables.getOrNull(4)?.measure(Constraints())
            // 标题块的位置展开 / 收拢一致（真机收拢后标题仍在栏头下沿 + 间距处），
            // 变的只是字号与顶栏高度；带导航图标时展开态要先让出 74dp 栏头，
            // 收拢后再升回无导航图标时的同一落位。
            val titleTopPx = with(density) { lerp(metrics.titleTop, collapsedTitleTop, fraction).toPx() }
            val expandedActionsTopPx = with(density) {
                (barHeight - spacing.gapLarge).toPx() - actionsHeight
            }
            // 收拢态操作区与标题块**整体对齐**：两者垂直中心相同，而不是各自在栏里居中。
            val collapsedActionsTopPx = titleTopPx + titlesPlaceable.height / 2f - actionsHeight / 2f
            val actionsTopPx = lerp(expandedActionsTopPx.coerceAtLeast(0f), collapsedActionsTopPx, fraction)
            val actionsEndPx = with(density) { metrics.endInset.toPx() }
            // 导航图标：展开态停在 74dp 栏头的垂直中线上（与大顶栏一致）；收拢后 98dp 的
            // 窄栏里标题块本来就偏下（真机实测如此，不是居中），所以箭头要跟着标题块整体
            // 对齐，否则会出现「箭头在上、标题在下」的两行错觉。
            val backHeightPx = backPlaceable?.height ?: 0
            val expandedBackTopPx = (with(density) { spacing.appBarHeight.toPx() } - backHeightPx) / 2f
            val collapsedBackTopPx = with(density) { collapsedTitleTop.toPx() } +
                titlesPlaceable.height / 2f - backHeightPx / 2f
            val backTopPx = lerp(expandedBackTopPx, collapsedBackTopPx, fraction)
            layout(widthPx, heightPx) {
                headerPlaceable.place(0, 0)
                titlesPlaceable.place(0, titleTopPx.roundToInt())
                // 两套操作区都贴右边摆；跟手位移由各自的 graphicsLayer 负责，
                // 相邻页那套因此从右边缘外面滑进来。
                lowerActionsPlaceable.place(
                    (widthPx - actionsEndPx - lowerActionsPlaceable.width).roundToInt(),
                    actionsTopPx.roundToInt(),
                )
                upperActionsPlaceable.place(
                    (widthPx - actionsEndPx - upperActionsPlaceable.width).roundToInt(),
                    actionsTopPx.roundToInt(),
                )
                backPlaceable?.place(0, backTopPx.roundToInt())
            }
        }
    }
}

/**
 * 一页的操作区：跟标题同一套位移 / 淡出口径（按页宽平移、过半淡出）。
 *
 * [page] 为 `null` 时只留一个空槽位——Layout 的子项下标必须稳定，不能按页数增减子项。
 */
@Composable
private fun PagerActions(
    page: Int?,
    pagePosition: Float,
    pageWidthPx: Float,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.graphicsLayer {
            translationX = if (page == null) 0f else pagerTitleTranslation(page, pagePosition, pageWidthPx)
            alpha = if (page == null) 0f else pagerTitleAlpha(page, pagePosition)
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (page != null) content()
    }
}

/**
 * 分段指示器：等宽 N 段的禁用色底轨，上面压一条主色高亮段。
 *
 * 高亮段不是「按页切换」而是按 `pagePosition` 连续定位，拖动时就是一条长条矩形跟着手指
 * 从当前段滑到目标段；松手后 pager 的吸附动画会把它继续带到位。
 */
@Composable
private fun PagerSegments(
    count: Int,
    pagePosition: Float,
    modifier: Modifier = Modifier,
) {
    if (count <= 0) return
    val colors = OxygenTheme.colorScheme
    val thickness = OxygenTheme.spacing.appBarPagerIndicatorHeight
    val gap = OxygenTheme.spacing.gapSmall
    Canvas(modifier = modifier.height(thickness)) {
        val gapPx = gap.toPx()
        val radius = size.height / 2f
        val segment = pagerIndicatorSegmentWidth(count, size.width, gapPx)
        for (index in 0 until count) {
            drawRoundRect(
                color = colors.onSurfaceDisabled,
                topLeft = Offset(pagerIndicatorSegmentOffset(index, size.width, gapPx, count), 0f),
                size = Size(segment, size.height),
                cornerRadius = CornerRadius(radius, radius),
            )
        }
        drawRoundRect(
            color = colors.onSurface,
            topLeft = Offset(pagerIndicatorActiveOffset(pagePosition, size.width, gapPx, count), 0f),
            size = Size(segment, size.height),
            cornerRadius = CornerRadius(radius, radius),
        )
    }
}

/** 每页的大标题 + 副标题叠在同一位置，按滚动位置横移并淡出。 */
@Composable
private fun PagerTitles(
    pageCount: Int,
    title: @Composable (page: Int) -> Unit,
    subtitle: (@Composable (page: Int) -> Unit)?,
    showSubtitle: Boolean,
    pagePosition: Float,
    pageWidthPx: Float,
    titleStyle: TextStyle,
    subtitleTighten: Dp,
    startInset: Dp,
    endInset: Dp,
) {
    val typography = OxygenTheme.typography
    val colors = OxygenTheme.colorScheme
    val subtitleSlot = subtitle?.takeIf { showSubtitle }
    Box(modifier = Modifier.fillMaxWidth()) {
        for (index in 0 until pageCount) {
            // 只画当前页与相邻页，远处的不进组合。
            if (abs(index - pagePosition) >= 1f) continue
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // 内距在标题内部：整块（含边距）一起平移，边距不会变成裁切边界。
                    .padding(start = startInset, end = endInset)
                    .graphicsLayer {
                        translationX = pagerTitleTranslation(index, pagePosition, pageWidthPx)
                        alpha = pagerTitleAlpha(index, pagePosition)
                    },
            ) {
                ProvideContentStyle(
                    contentColor = colors.onSurface,
                    textStyle = titleStyle,
                ) {
                    title(index)
                }
                if (subtitleSlot != null) {
                    Box(modifier = Modifier.offset(y = -subtitleTighten)) {
                        ProvideContentStyle(
                            contentColor = colors.onSurfaceVariant,
                            textStyle = typography.bodyMedium,
                        ) {
                            subtitleSlot(index)
                        }
                    }
                }
            }
        }
    }
}
