// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.IndicationDefaults
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * 文字型 Tab 行：页签下方一条 2dp 细横条指示器，宽度**等于页签宽度**，
 * 相邻页签的指示器首尾相接、中间不留缝（不给指示器加内缩），与顶栏分段指示器同一套细条语言。
 *
 * 页签走 [tabs] 槽位：每个页签是一个 [Tab]（自己带 `selected` / `onClick` / `text` / `icon`），
 * 选中态由调用方持有；TabRow 只负责「等分宽度（或可滚动时的最小宽度）、把指示器滑到选中
 * 页签、把选中页签滚进视口」。所以页签可以带图标、角标或任意内容，TabRow 不需要认识它们。
 *
 * 固定模式按页签数量等分宽度；[scrollable] 模式下每个页签至少 `size.tab.min.width`，
 * 超出屏幕可横向滚动，并自动把选中页签带进视口。
 *
 * - Spec ID: `CMP-TABROW`
 * - Baseline: `OxygenX P3 细条基线 / OnePlus 9R (LE2100) / 2026-11-15`
 * - Reference: `catalog/reference/README.md`
 * - Motion: `MOT-STATE-005`（`spec.tab.switch`），指示器位置/宽度与文字颜色同一条弹簧。可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param scrollable 是否允许横向滚动；`false` 时页签等分整行宽度。
 * @param colors 文字与指示器颜色集合；按压反馈走统一水波纹，不在配色里配置。
 * @param motion 指示器滑动与文字颜色过渡的动效规格。
 * @param tabs 页签内容槽，按显示顺序放 [Tab]；必须是 [TabRow] 的直接子项才会等分 / 参与指示器。
 */
@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    colors: TabRowColors = TabRowDefaults.colors(),
    motion: OxygenMotionSpec = TabRowDefaults.Motion,
    tabs: @Composable () -> Unit,
) {
    val spacing = OxygenTheme.spacing
    val scrollState = rememberScrollState()
    // 只记录「当前选中页签」的包围盒：槽位式页签没有下标，选中者自己上报即可。
    var selectedBounds by remember { mutableStateOf(Rect.Zero) }

    val indicatorLeft = rememberOxygenAnimation(spec = motion)
    val indicatorWidth = rememberOxygenAnimation(spec = motion)

    LaunchedEffect(selectedBounds) {
        if (selectedBounds.width <= 0f) return@LaunchedEffect
        coroutineScope {
            launch { indicatorLeft.animateTo(selectedBounds.left) }
            launch { indicatorWidth.animateTo(selectedBounds.width) }
        }
    }

    LaunchedEffect(selectedBounds, scrollable) {
        if (!scrollable || selectedBounds.width <= 0f) return@LaunchedEffect
        val target = tabScrollTarget(
            tabLeft = selectedBounds.left.toInt(),
            tabWidth = selectedBounds.width.toInt(),
            viewport = scrollState.viewportSize,
            current = scrollState.value,
        )
        if (target != scrollState.value) scrollState.animateScrollTo(target)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (scrollable) Modifier.horizontalScroll(scrollState) else Modifier)
            // 指示器画在内容坐标系里：滚动模式下跟着页签一起走，不需要额外同步滚动偏移。
            .drawBehind {
                val thickness = spacing.tabIndicatorHeight.toPx()
                val width = indicatorWidth.value
                if (thickness <= 0f || width <= 0f) return@drawBehind
                drawRoundRect(
                    color = colors.indicator,
                    topLeft = Offset(indicatorLeft.value, size.height - thickness),
                    size = Size(width, thickness),
                    cornerRadius = CornerRadius(thickness / 2f, thickness / 2f),
                )
            },
    ) {
        Row(modifier = if (scrollable) Modifier else Modifier.fillMaxWidth()) {
            val scope = TabRowScope(
                rowScope = this,
                scrollable = scrollable,
                colors = colors,
                motion = motion,
                onSelectedBounds = { selectedBounds = it },
            )
            CompositionLocalProvider(LocalTabRowScope provides scope, content = tabs)
        }
    }
}

/**
 * 页签：等分（或按最小宽度）由外层 [TabRow] 决定，选中时把自身位置上报给指示器。
 *
 * 文字 / 图标都是槽位，槽位内已下发选中色与 `type.label.medium` 排版，写
 * `Tab(selected = …, onClick = …, text = { Text("推荐") })` 即可。单独改某一个页签的
 * 选中 / 未选中色时传 [selectedContentColor] / [unselectedContentColor]，不必整包换配色。
 *
 * @param selected 是否选中；只影响配色，不持有状态。
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互。
 * @param selectedContentColor 选中内容色覆盖；`Color.Unspecified` 时用 [TabRow] 的配色。
 * @param unselectedContentColor 未选中内容色覆盖；`Color.Unspecified` 时用 [TabRow] 的配色。
 * @param text 文案槽；与 [icon] 同时给时图标在上、文案在下。
 * @param icon 图标槽；单独给时图标居中。
 */
@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = Color.Unspecified,
    unselectedContentColor: Color = Color.Unspecified,
    text: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    val scope = LocalTabRowScope.current
    val spacing = OxygenTheme.spacing
    val baseColors = scope?.colors ?: TabRowDefaults.colors()
    val colors = if (selectedContentColor.isSpecified || unselectedContentColor.isSpecified) {
        baseColors.copy(
            selectedContent = if (selectedContentColor.isSpecified) {
                selectedContentColor
            } else {
                baseColors.selectedContent
            },
            unselectedContent = if (unselectedContentColor.isSpecified) {
                unselectedContentColor
            } else {
                baseColors.unselectedContent
            },
        )
    } else {
        baseColors
    }
    val motion = scope?.motion ?: TabRowDefaults.Motion
    val interactionSource = remember { MutableInteractionSource() }
    val progress = rememberOxygenAnimation(spec = motion, initialValue = if (selected) 1f else 0f)
    LaunchedEffect(selected) { progress.animateTo(if (selected) 1f else 0f) }
    val contentColor = lerp(colors.unselectedContent, colors.selectedContent, progress.value)

    // 位置在布局阶段更新；只有被选中时才把它交给 TabRow 驱动指示器。
    var bounds by remember { mutableStateOf(Rect.Zero) }
    if (selected && scope != null) {
        LaunchedEffect(bounds) { scope.reportSelectedBounds(bounds) }
    }

    Box(
        modifier = modifier
            .then(scope?.tabWidthModifier(spacing.tabMinWidth) ?: Modifier)
            .height(spacing.tabRowHeight)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                // 与 M3 一致：页签的按压反馈就是标准水波纹（bounded，被自己的矩形裁住）。
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            .onGloballyPositioned { bounds = it.boundsInParent() }
            .padding(horizontal = spacing.tabInset),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides OxygenTheme.typography.labelMedium,
        ) {
            if (icon != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    icon()
                    if (text != null) {
                        Spacer(modifier = Modifier.height(spacing.gapXs))
                        text()
                    }
                }
            } else {
                text?.invoke()
            }
        }
    }
}

/**
 * TabRow 与 Tab 之间的布局约定：宽度策略、配色、动效与「选中页签位置」上报。
 *
 * 持有 `RowScope` 是为了在固定模式下给页签加 `weight(1f)`——`weight` 只能在 Row 的子项上
 * 生效，而页签内容由调用方写在槽位里，Tab 自己拿不到那个作用域。
 */
@Stable
internal class TabRowScope(
    private val rowScope: RowScope,
    private val scrollable: Boolean,
    val colors: TabRowColors,
    val motion: OxygenMotionSpec,
    private val onSelectedBounds: (Rect) -> Unit,
) {
    fun tabWidthModifier(minWidth: Dp): Modifier = with(rowScope) {
        if (scrollable) Modifier.widthIn(min = minWidth) else Modifier.weight(1f)
    }

    fun reportSelectedBounds(rect: Rect) = onSelectedBounds(rect)
}

internal val LocalTabRowScope = compositionLocalOf<TabRowScope?> { null }

/**
 * secondary 版 Tab 行：指示器与选中文字都用 `color.secondary`（M3 secondary 角色），
 * 取值是页面墨色，与横划顶栏（`PagerTopBar`）的高亮段同源，不用主色。
 *
 * 用于「内容分区」这类二级切换：页面上已经有一个强调色主操作时，再出现一条品红指示器
 * 会抢焦点；换成前景色细条后，选中态靠墨色对比表达，和横划顶栏的视觉语言一致。
 *
 * - Spec ID: `CMP-TABROW-SECONDARY`
 * - Baseline: `OxygenX P3 细条基线 / OnePlus 9R (LE2100) / 2026-11-15`
 * - Reference: `catalog/reference/README.md`
 * - Motion: 与 [TabRow] 同一条 `MOT-STATE-005`。
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param scrollable 是否允许横向滚动。
 * @param colors 颜色集合，默认 [TabRowDefaults.secondaryColors]。
 * @param motion 指示器滑动与文字颜色过渡的动效规格。
 * @param tabs 页签内容槽，语义同 [TabRow]。
 */
@Composable
fun SecondaryTabRow(
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    colors: TabRowColors = TabRowDefaults.secondaryColors(),
    motion: OxygenMotionSpec = TabRowDefaults.Motion,
    tabs: @Composable () -> Unit,
) {
    TabRow(
        modifier = modifier,
        scrollable = scrollable,
        colors = colors,
        motion = motion,
        tabs = tabs,
    )
}

/**
 * 把 `[tabLeft, tabLeft + tabWidth]` 滚进视口所需的目标滚动位置。
 *
 * 已在视口内就返回 [current]（不做无意义的小幅滚动）；只处理左右两侧露出不足两种情形，
 * 由调用方再用 `ScrollState.maxValue` 收敛。
 */
internal fun tabScrollTarget(tabLeft: Int, tabWidth: Int, viewport: Int, current: Int): Int {
    if (viewport <= 0) return current
    return when {
        tabLeft < current -> tabLeft
        tabLeft + tabWidth > current + viewport -> tabLeft + tabWidth - viewport
        else -> current
    }
}

object TabRowDefaults {
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.tabRowHeight

    val IndicatorHeight: Dp
        @Composable get() = OxygenTheme.spacing.tabIndicatorHeight

    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.tabSwitch()

    @Composable
    fun colors(
        selectedContent: Color = OxygenTheme.colorScheme.onSurface,
        unselectedContent: Color = OxygenTheme.colorScheme.onSurfaceVariant,
        indicator: Color = OxygenTheme.colorScheme.primary,
    ): TabRowColors = remember(selectedContent, unselectedContent, indicator) {
        TabRowColors(
            selectedContent = selectedContent,
            unselectedContent = unselectedContent,
            indicator = indicator,
        )
    }

    /** secondary 配色：指示器与选中文字取 `color.secondary`（墨色，与横划顶栏选中色同源）。 */
    @Composable
    fun secondaryColors(
        selectedContent: Color = OxygenTheme.colorScheme.secondary,
        unselectedContent: Color = OxygenTheme.colorScheme.onSurfaceVariant,
        indicator: Color = OxygenTheme.colorScheme.secondary,
    ): TabRowColors = colors(
        selectedContent = selectedContent,
        unselectedContent = unselectedContent,
        indicator = indicator,
    )
}

@Immutable
data class TabRowColors(
    val selectedContent: Color,
    val unselectedContent: Color,
    val indicator: Color,
)
