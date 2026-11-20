// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

/** 横划顶栏，沿用收拢时淡出副标题的默认行为。 */
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
) = PagerTopBar(
    pagerState = pagerState,
    title = title,
    modifier = modifier,
    subtitle = subtitle,
    showSubtitle = showSubtitle,
    hideSubtitleOnCollapse = true,
    onNavigateBack = onNavigateBack,
    navigationContentDescription = navigationContentDescription,
    state = state,
    actions = actions,
)

/**
 * 横划顶栏：配合 [androidx.compose.foundation.pager.HorizontalPager] 使用，复刻一加图库首页。
 *
 * 本质是「大顶栏 + 上方一条横划指示器」，不是另一套顶栏：栏顶先是一条由分段指示器占据的
 * `size.appbar.pager.indicator.band`（20dp，参照包 TabBarView 独立成行的高度；N 段等宽、
 * 2dp 高、8dp 间隔、两端圆角，上沿走 `space.appbar.pager.top`），栏头之下的内容区里是与大顶栏
 * 同一条令牌的大标题（`type.appbar.expanded.title`）与副标题。操作区随收拢从右下移到右中，
 * 底部分割线淡入。收拢态用横划栏自己的栏高令牌，指示器始终留在栏顶那一条里。
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
 * `size.appbar.height`（74dp）：指示器仍贴在最上沿，展开时返回键停在栏头垂直中线上，
 * 标题块让开栏头。收拢时返回键与操作区移到栏内中心，标题块右移让开返回键。
 *
 * 纵向是**固定高度 + 垂直居中**：栏高只由令牌决定（展开 `size.appbar.large.height` 265dp，
 * 与大顶栏同一个值；收拢 `size.appbar.pager.collapsed.height` 98dp），不随文字内容伸缩。栏顶
 * 那条栏头（只有指示器时是 20dp 的指示器带，有返回键时是一整行 `size.appbar.height` 74dp）
 * 先占掉固定高度，剩下的内容区里把「标题 + 副标题」整块**垂直居中**。
 *
 * 对齐单位是这一**整块**，不是标题行：块高固定为「标题行盒 + 一行副标题行盒」，这一页有没有
 * 副标题都一样，所以换页时块中心、标题与右侧操作区都不会上下跳。块内再按实际画出来的内容
 * 居中：有副标题时标题贴块顶，没有副标题时标题落在块正中，预留出来的空行上下均分。
 * `space.appbar.pager.title.gap` 只当「指示器带下沿 → 块顶」的最小间距兜底——真机图库的收拢态
 * 正好落在这个下限上（块顶 42dp = 20dp 带 + 22dp 间距 @450dpi），栏更高时按内容区中心居中。
 * 副标题可以在收拢前半程淡出。
 *
 * - Spec ID: `CMP-APPBAR-PAGER`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Gallery 3.6 & com.oneplus.gallery 4.0.182 / 2026-11-20`
 * - Reference: `catalog/reference/CMP-APPBAR/`
 * - Motion: `MOT-STATE-003`（与大顶栏共用收拢动画）
 * - Status: `draft`
 *
 * @param pagerState 横划容器的状态，指示器与标题都从它取滚动位置；段数 = `pagerState.pageCount`。
 * @param title 每页的大标题内容槽，形参是页下标；槽位内下发的排版与大顶栏同一条按
 *   `collapsedFraction` 插值的样式，所以写 `Text("照片", maxLines = 1)` 即得逐像素验证过的形态。
 * @param hideSubtitleOnCollapse 收拢时是否淡出副标题；传 `true` 时收拢栏里只显示一行标题
 *   （与 [LargeTopAppBar] 同名同义，副标题行盒仍预留，标题与操作区落位不变）。传 `false`
 *   时副标题保留在两行块里。
 * @param modifier 外部 Modifier。
 * @param subtitle 每页的副标题内容槽，形参是页下标；已下发 `type.body.medium`。
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
    hideSubtitleOnCollapse: Boolean,
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

    // 有返回键时展开栏头占一整行，收拢时只为指示器带留出顶部空间。
    // 指示器带高度取参照包 design_tab_bar_height（20dp）：大顶栏内容区相应让出这一段，总高不变。
    val indicatorHeaderHeight = spacing.appBarPagerIndicatorBand
    val metrics = largeTopBarMetrics(
        headerHeight = if (onNavigateBack != null) spacing.appBarHeight else indicatorHeaderHeight,
        titleGap = 0.dp,
    )
    // 栏头下沿：横划指示器（有返回键时还要再加上它那一行）先占住栏顶这一段固定高度，
    // 内容区从这条线往下算。指示器因此永远在标题上方，而不会和标题抢位置。
    val headerBottomPx = with(density) { lerp(metrics.headerHeight, indicatorHeaderHeight, fraction).toPx() }
    // `space.appbar.pager.title.gap` 只当「栏头下沿 → 标题块顶」的最小间距兜底：
    // 真机图库收拢态正好落在这个下限上（指示器下沿 139px、块顶 42dp @450dpi），
    // 栏更高时块按内容区中心居中，不受这个下限影响。
    val titleGapPx = with(density) { spacing.appBarPagerTitleGap.toPx() }
    val compactOffsetPx = with(density) { CompactAppBarContentOffset.toPx() }
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
        expanded = typography.displayMedium.expandedAppBarTitle().copy(color = colors.onSurface),
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
                .coerceAtLeast(spacing.appBarHeight)
        }
        // 收拢高度用横划栏自己的令牌（100dp）：收拢后仍留指示器 + 一小段标题，
        // 所以比标准顶栏（74dp）高，与真机图库 275px @450dpi 对齐。
        val barHeight = lerp(expandedHeight, spacing.appBarPagerCollapsedHeight, fraction)
        val barHeightPx = with(density) { barHeight.toPx() }
        // 固定的对齐框架：栏高扣掉栏头那一段之后是内容区，内容中心是它唯一的位置依据——
        // 标题块按自己的高度（标题行盒 + 预留的一行副标题）在上面居中，栏高与中心都不会被
        // 文字撑开或压偏。
        val contentCenterPx = (headerBottomPx + barHeightPx) / 2f + compactOffsetPx * fraction
        // 居中值低于「栏头下沿 + 最小间距」时取后者：栏被压得很矮时先保住标题不贴到指示器上。
        val blockTopFloorPx = headerBottomPx + titleGapPx
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
                // 1: 各页标题占完整栏高，横划淡出时图层仍覆盖标题和副标题的实际位置。
                PagerTitles(
                    pageCount = pagerState.pageCount,
                    title = title,
                    subtitle = subtitle?.takeIf { showSubtitle },
                    pagePosition = pagePosition,
                    pageWidthPx = pageWidthPx,
                    titleStyle = titleStyle,
                    subtitleTighten = spacing.appBarSubtitleTighten * fraction,
                    startInset = titleStartInset,
                    endInset = titleEndInset,
                    contentCenterPx = contentCenterPx,
                    blockTopFloorPx = blockTopFloorPx,
                    subtitleAlpha = if (hideSubtitleOnCollapse) (1f - fraction * 2f).coerceAtLeast(0f) else 1f,
                )
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
            val titlesPlaceable = measurables[1].measure(Constraints.fixed(widthPx, heightPx))
            val lowerActionsPlaceable = measurables[2].measure(Constraints())
            val upperActionsPlaceable = measurables[3].measure(Constraints())
            // 两套操作区宽度可能不同（图标数量不一样），对齐用的高度取高的那一套。
            val actionsHeight = maxOf(lowerActionsPlaceable.height, upperActionsPlaceable.height)
            val backPlaceable = measurables.getOrNull(4)?.measure(Constraints())
            val expandedActionsTopPx = heightPx - with(density) { spacing.gapLarge.toPx() } - actionsHeight
            // 收拢态操作区对齐的是「标题 + 副标题」这一整块的中心，不是栏心、也不是标题行心：
            // 真机图库图标中心 286px、标题块中心 280px @450dpi，两者同一口径。
            val collapsedActionsTopPx = (contentCenterPx - actionsHeight / 2f).coerceAtLeast(0f)
            val actionsTopPx = lerp(expandedActionsTopPx.coerceAtLeast(0f), collapsedActionsTopPx, fraction)
            val actionsEndPx = with(density) { metrics.endInset.toPx() }
            val backHeightPx = backPlaceable?.height ?: 0
            val expandedBackTopPx = (with(density) { spacing.appBarHeight.toPx() } - backHeightPx) / 2f
            // 收拢态返回键与操作区同一口径：跟着内容中心走，而不是栏心。
            val collapsedBackTopPx = contentCenterPx - backHeightPx / 2f
            val backTopPx = lerp(expandedBackTopPx, collapsedBackTopPx, fraction)
            layout(widthPx, heightPx) {
                headerPlaceable.place(0, 0)
                titlesPlaceable.place(0, 0)
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
    pagePosition: Float,
    pageWidthPx: Float,
    titleStyle: TextStyle,
    subtitleTighten: Dp,
    startInset: Dp,
    endInset: Dp,
    contentCenterPx: Float,
    blockTopFloorPx: Float,
    subtitleAlpha: Float,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        for (index in 0 until pageCount) {
            // 只画当前页与相邻页，远处的不进组合。
            if (abs(index - pagePosition) >= 1f) continue
            PagerTitleBlock(
                page = index,
                pagePosition = pagePosition,
                pageWidthPx = pageWidthPx,
                titleStyle = titleStyle,
                subtitleTighten = subtitleTighten,
                startInset = startInset,
                endInset = endInset,
                contentCenterPx = contentCenterPx,
                blockTopFloorPx = blockTopFloorPx,
                subtitleAlpha = subtitleAlpha,
                title = title,
                subtitle = subtitle,
            )
        }
    }
}

/**
 * 一页的标题块，随页面横移、淡出。
 *
 * 纵向落位由调用方给的固定框架决定：[contentCenterPx] 是内容区中心，整块以它为中心摆；
 * [blockTopFloorPx] 是「栏头下沿 + 最小间距」的兜底，栏比内容还矮时才会用到。块高始终按
 * 「标题行盒 + 一行副标题行盒」算——这一页没有副标题也照样预留，所以换页时看不到标题或
 * 按钮上下跳；块内只做一次「实际画出来的内容居中」，缺副标题时把预留的那一行上下均分掉。
 */
@Composable
private fun PagerTitleBlock(
    page: Int,
    pagePosition: Float,
    pageWidthPx: Float,
    titleStyle: TextStyle,
    subtitleTighten: Dp,
    startInset: Dp,
    endInset: Dp,
    contentCenterPx: Float,
    blockTopFloorPx: Float,
    subtitleAlpha: Float,
    title: @Composable (page: Int) -> Unit,
    subtitle: (@Composable (page: Int) -> Unit)?,
) {
    val colors = OxygenTheme.colorScheme
    val tightenPx = with(LocalDensity.current) { subtitleTighten.toPx() }
    // 这一页没有副标题（或整个关掉）时，块里仍按同一档位的行盒预留一行。
    val reservedSubtitleLinePx = with(LocalDensity.current) { CompactAppBarSubtitleLineHeight.toPx() }
    Layout(
        modifier = Modifier
            .fillMaxSize()
            // 内距在标题内部：整块（含边距）一起平移，边距不会变成裁切边界。
            .padding(start = startInset, end = endInset)
            .graphicsLayer {
                translationX = pagerTitleTranslation(page, pagePosition, pageWidthPx)
                alpha = pagerTitleAlpha(page, pagePosition)
            },
        content = {
            // 两个槽位各自成块，才量得到「这一页到底有没有副标题」。
            TitleBlockSlot {
                ProvideContentStyle(contentColor = colors.onSurface, textStyle = titleStyle) {
                    title(page)
                }
            }
            if (subtitle != null) {
                TitleBlockSlot(modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }) {
                    val subtitleStyle = OxygenTheme.typography.bodyMedium.compactAppBarSubtitle()
                    ProvideContentStyle(contentColor = colors.onSurfaceVariant, textStyle = subtitleStyle) {
                        subtitle(page)
                    }
                }
            }
        },
    ) { measurables, constraints ->
        val titlePlaceable = measurables[0].measure(Constraints(maxWidth = constraints.maxWidth))
        val subtitlePlaceable = measurables.getOrNull(1)?.measure(Constraints(maxWidth = constraints.maxWidth))
        // 槽位存在但这一页没有副标题文字时高度是 0，按「没有副标题」处理、补一行预留。
        val measuredSubtitlePx = subtitlePlaceable?.height?.toFloat() ?: 0f
        val hasSubtitleText = measuredSubtitlePx > 0f
        val contentSubtitle = measuredSubtitlePx * subtitleAlpha
        // 块高固定为「标题行盒 + 一行副标题行盒」：这一页有没有副标题都一样，块心不跳。
        val blockHeightPx = titlePlaceable.height +
            if (hasSubtitleText) measuredSubtitlePx else reservedSubtitleLinePx
        // 整块在内容区里垂直居中；栏太矮时才退回「栏头下沿 + 最小间距」。
        val blockTopPx = (contentCenterPx - blockHeightPx / 2f).coerceAtLeast(blockTopFloorPx)
        // 块内居中：把真正画出来的内容（标题 + 可见副标题）在这块里居中。有副标题时标题
        // 落在块顶；这页没有副标题（或副标题已淡出）时，预留的那一行上下均分。
        val drawnHeightPx = titlePlaceable.height + contentSubtitle -
            if (hasSubtitleText) tightenPx * subtitleAlpha else 0f
        val inBlockOffsetPx = ((blockHeightPx - drawnHeightPx) / 2f).coerceAtLeast(0f)
        val titleTopPx = blockTopPx + inBlockOffsetPx
        layout(constraints.maxWidth, constraints.maxHeight) {
            titlePlaceable.place(0, titleTopPx.roundToInt())
            subtitlePlaceable?.place(0, (titleTopPx + titlePlaceable.height - tightenPx).roundToInt())
        }
    }
}

/**
 * 标题块里的一个槽位：多根节点按纵向依次排列，与原先 `Column` 的堆叠行为一致，
 * 同时保证外层一次 `measure` 就能拿到该槽位的整体高度（用来判断有没有副标题）。
 */
@Composable
private fun TitleBlockSlot(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.sumOf { it.height }
        layout(width, height) {
            var top = 0
            placeables.forEach { placeable ->
                placeable.place(0, top)
                top += placeable.height
            }
        }
    }
}
