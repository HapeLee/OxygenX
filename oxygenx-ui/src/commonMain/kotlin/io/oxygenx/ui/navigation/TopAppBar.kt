// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.theme.generated.GeneratedTokens
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.IndicationDefaults
import kotlin.math.roundToInt

/**
 * 标准顶栏：左侧返回、左对齐标题、右侧操作区。适合二级页面，不需要随滚动变化。
 *
 * - Spec ID: `CMP-APPBAR`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-14`
 * - Reference: `catalog/reference/CMP-APPBAR/`
 * - Status: `draft`
 *
 * 高度取 `size.appbar.height`（74dp = 20dp 上边距 + 54dp 内容），底部有一条
 * `size.divider.thickness` 的细线把顶栏与内容分开，真机设置页同样常驻这条线。
 *
 * @param title 标题内容槽：已下发 `type.appbar.title` 与 `onSurface` 内容色，槽位内的
 *   `Text("标题")` 自动是正确档位；需要「标题 + 徽标」时直接在槽位里排。
 * @param modifier 外部 Modifier。
 * @param subtitle 标题下方的副标题内容槽（`type.appbar.subtitle` / `onSurfaceVariant`），
 *   与标题作为一整块居中。
 * @param showSubtitle 是否显示副标题；默认显示，传 `false` 时只留标题。
 * @param onNavigateBack 传入非空时显示内置返回按钮。
 * @param navigationContentDescription 返回按钮的无障碍描述；为空则按钮不可被朗读定位。
 * @param navigationIcon 自定义导航图标（M3 `navigationIcon` 槽口径）；非空时优先于
 * [onNavigateBack]，可用来放「关闭」「菜单」等。内置返回按钮的起始内边距不会自动加到
 * 自定义内容上，需要跟标准栏对齐时自行加 `spacing.appBarTitleInset` 量级的左内距。
 * @param showDivider 是否画底部分割线；内容直接接在顶栏下方时保留（默认）。
 * @param actions 右侧操作区，按钮紧邻排列。建议使用 [TopAppBarAction]。
 */
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    showSubtitle: Boolean = true,
    onNavigateBack: (() -> Unit)? = null,
    navigationContentDescription: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    showDivider: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    // M3 的 navigationIcon 槽：给了自定义内容就用它，否则沿用 onNavigateBack 的内置返回键。
    val navigation = navigationIcon ?: if (onNavigateBack != null) {
        {
            TopAppBarAction(
                onClick = onNavigateBack,
            ) {
                Icon(
                    imageVector = OxygenIcons.Back,
                    contentDescription = navigationContentDescription,
                    size = LocalTopAppBarNavigationIconSize.current ?: OxygenTheme.icons.medium,
                    mirrorInRtl = true,
                )
            }
        }
    } else {
        null
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.appBarHeight)
            .drawBehind {
                if (showDivider) {
                    val thickness = spacing.dividerThickness.toPx()
                    drawRect(
                        color = colors.outlineVariant,
                        topLeft = Offset(0f, size.height - thickness),
                        size = Size(size.width, thickness),
                    )
                }
            }
            .padding(top = CompactAppBarContentOffset * 2, end = spacing.gapSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (navigation != null) {
            // 水波纹比按钮本身大，允许盖到标题上，因此导航槽要画在标题之后。
            Box(modifier = Modifier.zIndex(1f)) {
                CompositionLocalProvider(LocalTopAppBarNavigationIconSize provides CompactAppBarNavigationIconSize, content = navigation)
            }
        }
        Column(
            // 一加时钟的顶栏标题紧贴返回键左对齐；没有返回键时才从标题内边距起始。
            // 有副标题时标题与副标题作为一整块在 74dp 栏里垂直居中。
            modifier = Modifier
                .weight(1f)
                .then(
                    if (navigation != null) {
                        Modifier
                    } else {
                        Modifier.padding(start = spacing.appBarTitleInset)
                    },
                )
                .padding(end = spacing.gapSmall),
            verticalArrangement = Arrangement.Center,
        ) {
            ProvideContentStyle(
                contentColor = colors.onSurface,
                textStyle = OxygenTheme.typography.titleLarge.compactAppBarTitle(),
                content = title,
            )
            val subtitleSlot = subtitle?.takeIf { showSubtitle }
            if (subtitleSlot != null) {
                ProvideContentStyle(
                    contentColor = colors.onSurfaceVariant,
                    textStyle = OxygenTheme.typography.bodyMedium.compactAppBarSubtitle(),
                ) {
                    Box(
                        // 行盒上下余量会把副标题推离标题，上提 `space.appbar.subtitle.tighten`
                        // 才是真机那种紧贴（收拢态墨迹间距 16px @450dpi）。
                        modifier = Modifier.offset(y = -spacing.appBarSubtitleTighten),
                    ) {
                        subtitleSlot()
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}

/**
 * 大顶栏：展开时占据约三分之一屏高（`size.appbar.large.height` 265dp），标题取
 * `type.appbar.expanded.title`（参照包 `op_app_bar_expand_text_size` 的 48sp，其余属性继承
 * displayMedium），操作区贴右下；
 * 随内容滚动收拢为标准单行顶栏：标题始终是同一条文本，字号按进度连续缩到
 * `type.appbar.title` 并移到收拢位，副标题在收拢前半程淡出，高度收到
 * `size.appbar.collapsed.height`。有返回键时，展开态顶部是一段与标准顶栏同高
 * （`size.appbar.height`）的独立栏头：返回键在其垂直中线上、不随展开高度浮动，
 * 大标题与副标题排在栏头下方。滚动联动由 [TopAppBarState] 提供，
 * 需要把它同时传给 `Scaffold` 的 `topBarState` 才会生效。
 *
 * - Spec ID: `CMP-APPBAR-LARGE`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-APPBAR/`
 * - Motion: `MOT-STATE-003`。
 * - Status: `draft`
 *
 * @param title 标题内容槽：槽位内下发的排版是按 `collapsedFraction` 从
 *   `type.appbar.expanded.title`（48sp）连续插值到 `type.appbar.title`（18sp）的那一条，
 *   所以槽位里写 `Text("设置", maxLines = 1, overflow = Ellipsis)` 就是逐像素验证过的连续过渡；
 *   需要图形标题时槽位接受任意内容，但不再有字号插值。
 * @param modifier 外部 Modifier。
 * @param subtitle 标题下方的副标题内容槽（例如城市与时间），已下发 `type.appbar.subtitle`。
 * @param showSubtitle 是否显示副标题；默认显示，传 `false` 时完全不画（与标准顶栏同名同义）。
 * @param hideSubtitleOnCollapse 收拢时是否隐藏副标题；默认隐藏（收拢前半程淡出）。
 * 传 `false` 时副标题保留，标题与副标题作为一整块在收拢栏里垂直居中。
 * @param onNavigateBack 传入非空时显示内置返回按钮。
 * @param navigationContentDescription 返回按钮的无障碍描述。
 * @param navigationIcon 自定义导航图标（M3 `navigationIcon` 槽口径），非空时优先于
 * [onNavigateBack]；图标可读取 [LocalTopAppBarNavigationIconSize]。
 * @param state 收拢进度来源；为 `null` 时固定展开。
 * @param actions 操作区，展开态贴右下、收拢态贴右中；按钮紧邻排列。
 */
@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    showSubtitle: Boolean = true,
    hideSubtitleOnCollapse: Boolean = true,
    onNavigateBack: (() -> Unit)? = null,
    navigationContentDescription: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    state: TopAppBarState? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val spacing = OxygenTheme.spacing
    val layout = OxygenTheme.layout
    val colors = OxygenTheme.colorScheme
    val typography = OxygenTheme.typography
    val density = LocalDensity.current
    val fraction = state?.collapsedFraction ?: 0f
    val navigationIconSize = appBarNavigationIconSize(fraction)
    // 导航槽只占一个子项：内置返回键与自定义图标走同一条测量/摆放路径。
    val navigation = navigationIcon ?: if (onNavigateBack != null) {
        {
            TopAppBarAction(
                onClick = onNavigateBack,
            ) {
                Icon(
                    imageVector = OxygenIcons.Back,
                    contentDescription = navigationContentDescription,
                    size = LocalTopAppBarNavigationIconSize.current ?: OxygenTheme.icons.medium,
                    mirrorInRtl = true,
                )
            }
        }
    } else {
        null
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // 展开高度是固定值（真机 265dp，与屏高无关），窗口更矮时收敛到可用高度。
        val expandedHeight = if (maxHeight == Dp.Infinity) {
            layout.appBarLargeHeight
        } else {
            minOf(layout.appBarLargeHeight, maxHeight)
                .coerceAtLeast(spacing.appBarCollapsedHeight)
        }
        val barHeight = lerp(expandedHeight, spacing.appBarCollapsedHeight, fraction)
        if (state != null) {
            // 收拢进度按 `size.appbar.collapse.distance` 折算，而不是按高度差：
            // 真机滚过 171dp 时大标题已完全收起，剩余高度差由收尾动画补完。
            val rangePx = with(density) { spacing.appBarCollapseDistance.toPx() }
            SideEffect { state.collapseRangePx = rangePx }
        }

        // 标题只有一条：按收拢进度从 displayMedium 连插值到标准顶栏标题，
        // 位置同步从展开位挪到收拢位，全程不交叉淡入，两态之间是同一个字形在缩小。
        val titleStyle = lerpAppBarTitleStyle(
            expanded = typography.displayMedium.expandedAppBarTitle().copy(color = colors.onSurface),
            collapsed = typography.titleLarge.compactAppBarTitle().copy(color = colors.onSurface),
            fraction = fraction,
        )
        // [showSubtitle] 为 false 时整段不画，后续索引与居中计算都按「没有副标题」走。
        val subtitleSlot = subtitle?.takeIf { showSubtitle }
        val subtitleStyle = typography.bodyMedium.compactAppBarSubtitle().copy(color = colors.onSurfaceVariant)
        // 副标题跟着标题下沿走：默认在收拢前半程淡出，避免和标题缩放抢视觉；
        // [hideSubtitleOnCollapse] 为 false 时整段保留，与标题一起在收拢栏里整体居中。
        val subtitleAlpha = if (hideSubtitleOnCollapse) {
            1f - (fraction * 2f).coerceAtMost(1f)
        } else {
            1f
        }
        val expandedStartInset = spacing.appBarTitleInset
        // 收拢态标题起点与标准顶栏对齐：有返回键时紧贴导航图标之后，
        // 没有时从 appBarTitleInset 起。
        val collapsedStartInset = if (navigation != null) {
            spacing.touchTargetMin
        } else {
            spacing.appBarTitleInset
        }
        // 有导航键时，顶部 74dp 是和标准顶栏同高、只有返回键的独立栏头，
        // 大标题/副标题整体排在栏头下方；没有导航键时用参照包的 `expandedTitleMarginTop`（20dp）。
        val metrics = largeTopBarMetrics(
            headerHeight = if (navigation != null) spacing.appBarHeight else 0.dp,
            titleGap = if (navigation != null) 0.dp else spacing.appBarLargeTitleGap,
        )

        Layout(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
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
                // 水平与垂直位置都由下面的 Layout 负责，这里不能再加内边距，否则会重复偏移。
                ProvideContentStyle(
                    contentColor = colors.onSurface,
                    textStyle = titleStyle,
                    content = title,
                )
                if (subtitleSlot != null) {
                    Box(modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }) {
                        ProvideContentStyle(
                            contentColor = colors.onSurfaceVariant,
                            textStyle = subtitleStyle,
                            content = subtitleSlot,
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
                if (navigation != null) {
                    CompositionLocalProvider(LocalTopAppBarNavigationIconSize provides navigationIconSize, content = navigation)
                }
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val heightPx = constraints.maxHeight
            // actions 距屏幕右边与标准顶栏一致（gapSmall）。
            val endPx = with(density) { metrics.endInset.toPx() }
            val startPx = lerp(
                with(density) { expandedStartInset.toPx() },
                with(density) { collapsedStartInset.toPx() },
                fraction,
            )
            // 副标题是否存在会改变后续索引，这里显式算一次偏移。
            val subtitleIndex = if (subtitleSlot != null) 1 else -1
            val actionsIndex = if (subtitleSlot != null) 2 else 1
            val backPlaceable = measurables.getOrNull(actionsIndex + 1)?.measure(Constraints())
            val actionsPlaceable = measurables[actionsIndex].measure(Constraints())
            // 收拢后标题要让开右侧操作区，和标准顶栏一样不会被按钮压住；
            // 展开态操作区贴右下、和大标题不重叠，因此这段让位随进度淡入。
            val actionsReservePx = if (actionsPlaceable.width > 0) {
                lerp(0f, actionsPlaceable.width + with(density) { spacing.gapSmall.toPx() }, fraction)
            } else {
                0f
            }
            val titleMaxWidth = (width - startPx - endPx - actionsReservePx).toInt().coerceAtLeast(0)
            val titlePlaceable = measurables[0].measure(Constraints(maxWidth = titleMaxWidth))
            val subtitlePlaceable = if (subtitleIndex >= 0) {
                measurables[subtitleIndex].measure(Constraints(maxWidth = titleMaxWidth))
            } else {
                null
            }

            val expandedTextTopPx = with(density) { metrics.titleTop.toPx() }
            val expandedBottomInsetPx = with(density) { spacing.gapLarge.toPx() }
            // 收拢后字号变小，行盒上下余量占比变大，副标题会被推离标题：间距按进度收掉，
            // 再按进度叠加 `space.appbar.subtitle.tighten` 上提——收拢完成时这套参数与
            // 标准顶栏的副标题落位完全一致（同为 14px 墨迹间距 @450dpi）。
            val subtitleGapPx = with(density) { metrics.subtitleGap.toPx() } * (1f - fraction)
            val subtitleTightenPx = with(density) { spacing.appBarSubtitleTighten.toPx() } * fraction

            // 导航图标属于栏头，始终停在标准顶栏（74dp）的垂直中线上，
            // 不随大顶栏的展开高度上下浮动。
            val compactOffsetPx = with(density) { CompactAppBarContentOffset.toPx() }
            val backTopPx = (with(density) { spacing.appBarHeight.toPx() } - (backPlaceable?.height ?: 0)) / 2f +
                compactOffsetPx * fraction
            // 收拢态把「标题 + 副标题」当成一整块居中，而不是只让标题居中（否则副标题会把
            // 视觉重心拉低）。副标题若在收拢时淡出，它的高度按透明度折算，避免文字已经看不见
            // 还把标题顶偏。
            val subtitleBlockPx = if (subtitlePlaceable != null) {
                (subtitlePlaceable.height + subtitleGapPx - subtitleTightenPx) * subtitleAlpha
            } else {
                0f
            }
            val collapsedGroupHeightPx = titlePlaceable.height + subtitleBlockPx

            layout(width, heightPx) {
                // 展开态按设计内边距，收拢态在单行栏里整体居中，中间连续插值。
                val titleTop = lerp(
                    expandedTextTopPx,
                    (heightPx - collapsedGroupHeightPx) / 2f + compactOffsetPx,
                    fraction,
                )
                titlePlaceable.place(startPx.roundToInt(), titleTop.roundToInt())
                subtitlePlaceable?.place(
                    startPx.roundToInt(),
                    (titleTop + titlePlaceable.height + subtitleGapPx - subtitleTightenPx).roundToInt(),
                )

                val actionsTop = lerp(
                    (heightPx - expandedBottomInsetPx - actionsPlaceable.height).coerceAtLeast(0f),
                    (heightPx - actionsPlaceable.height) / 2f + compactOffsetPx,
                    fraction,
                )
                actionsPlaceable.place(
                    (width - endPx - actionsPlaceable.width).roundToInt(),
                    actionsTop.roundToInt(),
                )

                if (backPlaceable != null) {
                    backPlaceable.place(0, backTopPx.roundToInt())
                }
            }
        }
    }
}

/**
 * 大顶栏的滚动联动状态。
 *
 * - `collapsedFraction = 0` 展开，`1` 收拢；由内部嵌套滚动连接驱动，随手指连续变化。
 * - 上滑优先收拢顶栏，下滑且内容已到顶时再展开顶栏；快速滑动结束后按就近原则收尾
 *   （`MOT-STATE-003`）。
 */
@Stable
class TopAppBarState internal constructor(
    private val settleSpec: AnimationSpec<Float>,
    initialCollapsedFraction: Float = 0f,
) {
    var collapsedFraction by mutableFloatStateOf(initialCollapsedFraction.coerceIn(0f, 1f))
        private set

    internal var collapseRangePx by mutableFloatStateOf(0f)

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = applyScroll(available.y, allowCollapse = true, allowExpand = false)

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset = applyScroll(available.y, allowCollapse = false, allowExpand = true)

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val target = if (collapsedFraction >= 0.5f) 1f else 0f
            if (collapseRangePx > 0f && collapsedFraction != target) {
                animate(
                    initialValue = collapsedFraction,
                    targetValue = target,
                    animationSpec = settleSpec,
                ) { value, _ ->
                    collapsedFraction = value
                }
            }
            return Velocity.Zero
        }
    }

    /**
     * 把滚动位移折算成收拢进度，返回真正消费掉的位移。
     *
     * `delta` 是嵌套滚动给的 `available.y`：**负值**表示内容向上滚（收拢），正值表示下拉（展开）。
     */
    internal fun applyScroll(delta: Float, allowCollapse: Boolean, allowExpand: Boolean): Offset {
        if (collapseRangePx <= 0f || delta == 0f) return Offset.Zero
        val target = (collapsedFraction - delta / collapseRangePx).coerceIn(0f, 1f)
        val applied = target - collapsedFraction
        if (applied > 0f && !allowCollapse) return Offset.Zero
        if (applied < 0f && !allowExpand) return Offset.Zero
        if (applied == 0f) return Offset.Zero
        collapsedFraction = target
        return Offset(0f, -applied * collapseRangePx)
    }
}

/**
 * 记住一个大顶栏状态。[initialCollapsedFraction] 便于在 catalog 里预览收拢形态。
 */
@Composable
fun rememberTopAppBarState(
    motion: OxygenMotionSpec = OxygenMotionDefaults.appBarCollapse(),
    initialCollapsedFraction: Float = 0f,
): TopAppBarState = remember(motion, initialCollapsedFraction) {
    TopAppBarState(motion.enter, initialCollapsedFraction)
}

/**
 * 顶栏动作按钮：图标视觉尺寸取自 `icon.size.medium`，触摸目标按
 * `space.touch.target.min` 扩展。触摸态是半径 `size.appbar.action.ripple` 的水波纹，
 * 不裁剪在按钮边界内，因此可以盖到相邻的标题上。
 *
 * 内容走 [content] 槽位（放 `Icon` 即标准动作按钮），可换成加载圈或角标；槽位内已下发
 * 内容色（启用 `onSurface` / 禁用 `onSurfaceDisabled`），方向性图标由 `Icon` 自己的
 * `mirrorInRtl` 决定镜像。
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互。
 * @param contentColor 内容色覆盖；`Color.Unspecified` 时按启用态解析。
 * @param content 图标内容槽。
 */
@Composable
fun TopAppBarAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) = TopAppBarAction(
    onClick = onClick,
    onLongClick = null,
    modifier = modifier,
    enabled = enabled,
    contentColor = contentColor,
    content = content,
)

/** 带可选长按的顶栏操作按钮，触摸目标与水波纹沿用原生顶栏样式。 */
@Composable
fun TopAppBarAction(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = Color.Unspecified,
    onLongClickLabel: String? = null,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OxygenTheme.colorScheme
    val resolvedContentColor = when {
        contentColor.isSpecified -> contentColor
        enabled -> colors.onSurface
        else -> colors.onSurfaceDisabled
    }
    val ripple = IndicationDefaults.ripple(
        bounded = false,
        radius = OxygenTheme.spacing.appBarActionRippleRadius,
    )
    CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
        Box(
            modifier = modifier
                .size(OxygenTheme.spacing.touchTargetMin)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onLongClickLabel = onLongClickLabel,
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/**
 * 大顶栏展开态的落位参数：标题、副标题与操作区都从这组值算。
 *
 * 横划顶栏（`PagerTopBar`）本质是「大顶栏 + 横划指示器」，只是把栏头从返回键换成
 * 分段指示器，因此展开态的大标题位置、左内距、副标题间距与操作区右内距都与大顶栏共用这一套，
 * 不另立一份间距令牌。
 */
internal data class LargeTopBarMetrics(
    /** 栏头高度：返回键栏头是 `size.appbar.height`，没有栏头时为 0。 */
    val headerHeight: Dp,
    /** 栏头下沿到大标题行盒顶的间距。 */
    val titleGap: Dp,
    /** 大标题左内距（`space.appbar.title.inset`）。 */
    val titleInset: Dp,
    /** 大标题下沿到副标题行盒顶的间距。 */
    val subtitleGap: Dp,
    /** 操作区右内距，与标准顶栏一致。 */
    val endInset: Dp,
) {
    /** 展开态大标题行盒顶距顶栏顶部的距离。 */
    val titleTop: Dp get() = headerHeight + titleGap
}

@Composable
internal fun largeTopBarMetrics(headerHeight: Dp, titleGap: Dp): LargeTopBarMetrics {
    val spacing = OxygenTheme.spacing
    return LargeTopBarMetrics(
        headerHeight = headerHeight,
        titleGap = titleGap,
        titleInset = spacing.appBarTitleInset,
        subtitleGap = spacing.gapXs,
        endInset = spacing.gapSmall,
    )
}

internal val CompactAppBarContentOffset = GeneratedTokens.SPACE_APPBAR_COMPACT_CONTENT_OFFSET.dp

/** 顶栏副标题的行盒高度；横划顶栏用它预留「这一页没有副标题」的那一行。 */
internal val CompactAppBarSubtitleLineHeight = GeneratedTokens.TYPE_APPBAR_SUBTITLE_LINE_HEIGHT.dp
private val CompactAppBarNavigationIconSize = GeneratedTokens.ICON_SIZE_APPBAR_NAVIGATION_COMPACT.dp

/** 顶栏导航槽的默认图标画布尺寸；槽位之外为 null。触摸目标由按钮组件决定。 */
val LocalTopAppBarNavigationIconSize = compositionLocalOf<Dp?> { null }

@Composable
internal fun appBarNavigationIconSize(fraction: Float): Dp = lerp(OxygenTheme.icons.medium, CompactAppBarNavigationIconSize, fraction.coerceIn(0f, 1f))

internal fun TextStyle.compactAppBarTitle(): TextStyle = copy(
    fontSize = GeneratedTokens.TYPE_APPBAR_TITLE_SIZE.sp,
    lineHeight = GeneratedTokens.TYPE_APPBAR_TITLE_LINE_HEIGHT.sp,
)

/** 展开态大标题：字号取参照包 `op_app_bar_expand_text_size`（96px ÷ 2 = 48sp），其余继承 displayMedium。 */
internal fun TextStyle.expandedAppBarTitle(): TextStyle = copy(
    fontSize = GeneratedTokens.TYPE_APPBAR_EXPANDED_TITLE_SIZE.sp,
    lineHeight = GeneratedTokens.TYPE_APPBAR_EXPANDED_TITLE_LINE_HEIGHT.sp,
)

/** 顶栏副标题：字号取参照包 `op_text_size_body1`（13sp），其余继承 bodyMedium。 */
internal fun TextStyle.compactAppBarSubtitle(): TextStyle = copy(
    fontSize = GeneratedTokens.TYPE_APPBAR_SUBTITLE_SIZE.sp,
    lineHeight = GeneratedTokens.TYPE_APPBAR_SUBTITLE_LINE_HEIGHT.sp,
)

/** 两个主题角色之间按 Compose TextStyle 规则插值，端点保留各自完整的字体属性。 */
internal fun lerpAppBarTitleStyle(
    expanded: TextStyle,
    collapsed: TextStyle,
    fraction: Float,
): TextStyle = androidx.compose.ui.text.lerp(expanded, collapsed, fraction)
