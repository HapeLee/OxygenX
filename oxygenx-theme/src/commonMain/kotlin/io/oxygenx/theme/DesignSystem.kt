// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.generated.GeneratedTokens

@Immutable
data class OxygenSpacing(
    val gutter: Dp,
    val gapXs: Dp,
    val gapSmall: Dp,
    val gapLarge: Dp,
    val cardPadding: Dp,
    val controlMinWidth: Dp,
    val touchTargetMin: Dp,
    val appBarHeight: Dp,
    val appBarTitleInset: Dp,
    val appBarCollapsedHeight: Dp,
    val appBarLargeTitleGap: Dp,
    val appBarCollapseDistance: Dp,
    val appBarActionRippleRadius: Dp,
    val listTopInset: Dp,
    val listGroupGap: Dp,
    val listItemInset: Dp,
    val listRowMinHeight: Dp,
    val settingsSwitchCell: Dp,
    val settingsSwitchInset: Dp,
    val settingsDividerHeight: Dp,
    val settingsDividerGap: Dp,
    val navigationBarHeight: Dp,
    val fabSize: Dp,
    val fabSmall: Dp,
    val fabLarge: Dp,
    val buttonHeight: Dp,
    val buttonHorizontalPadding: Dp,
    val chipHeight: Dp,
    val chipBorderWidth: Dp,
    val switchTrackWidth: Dp,
    val switchTrackHeight: Dp,
    val switchThumbSize: Dp,
    val switchRippleRadius: Dp,
    val sliderTrackHeight: Dp,
    val sliderThumbSize: Dp,
    val sliderRippleRadius: Dp,
    val sliderTickSize: Dp,
    val controlRadioSize: Dp,
    val controlCheckboxSize: Dp,
    val controlBorderWidth: Dp,
    val controlCornerRadius: Dp,
    val controlDotSize: Dp,
    val dividerThickness: Dp,
    val inputHeight: Dp,
    val inputIndicatorThickness: Dp,
    val inputIndicatorThicknessFocused: Dp,
    val dialogInset: Dp,
    val dialogPadding: Dp,
    val appBarPagerIndicatorHeight: Dp,
    val appBarPagerIndicatorBand: Dp,
    val appBarPagerTop: Dp,
    val appBarPagerTitleGap: Dp,
    val appBarPagerCollapsedHeight: Dp,
    val appBarSubtitleTighten: Dp,
    val progressLinearHeight: Dp,
    val progressCircularSize: Dp,
    val progressCircularStroke: Dp,
    val refreshThreshold: Dp,
    val refreshMaxDistance: Dp,
    val overscrollMax: Dp,
    val tabRowHeight: Dp,
    val tabMinWidth: Dp,
    val tabIndicatorHeight: Dp,
    val tabInset: Dp,
    val badgeDot: Dp,
    val badgeHeight: Dp,
    val badgePaddingHorizontal: Dp,
    val tooltipMaxWidth: Dp,
    val tooltipPaddingHorizontal: Dp,
    val tooltipPaddingVertical: Dp,
    val menuMinWidth: Dp,
    val menuItemHeight: Dp,
    val menuPaddingVertical: Dp,
    val menuItemPaddingHorizontal: Dp,
    val sheetHandleWidth: Dp,
    val sheetHandleHeight: Dp,
    val sheetHandleInset: Dp,
    val searchBarHeight: Dp,
    val railWidth: Dp,
    val railItemHeight: Dp,
)

@Immutable
data class OxygenShapes(
    val cornerCard: RoundedCornerShape,
    val cornerButton: RoundedCornerShape,
    val cornerInput: RoundedCornerShape,
    /**
     * 药丸形状按「短边一半」定义，属于形状语义而非设计数值，因此没有对应令牌。
     * 需要圆角令牌的场合仍走 `shape.corner.*`。
     */
    val pill: RoundedCornerShape,
)

/** 菜单独立采用经典 M3 小圆角；扩展属性保留 OxygenShapes 的原构造与 copy 签名。 */
val OxygenShapes.cornerMenu: RoundedCornerShape
    get() = RoundedCornerShape(GeneratedTokens.SHAPE_CORNER_MENU.dp)

@Immutable
data class OxygenIconMetrics(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
)

/** 交互反馈的强度参数（`effect.*` 令牌）。 */
@Immutable
data class OxygenInteraction(
    /** 下拉刷新时位移对手指位移的吸收比例。 */
    val refreshDragScale: Float,
    /** 滚动过冲时位移对手指位移的阻尼比例（再按已拉出比例线性衰减）。 */
    val overscrollDragScale: Float,
    /** 滑动删除的位移阈值比例（占条目宽度的比例，越过即触发删除）。 */
    val dismissThreshold: Float,
)

/**
 * 与具体控件无关的整体布局尺寸（整块区域高度，而不是内部间距）。
 */
@Immutable
data class OxygenLayout(
    val appBarLargeHeight: Dp,
)

internal fun defaultSpacing(): OxygenSpacing = OxygenSpacing(
    gutter = GeneratedTokens.SPACE_GUTTER.dp,
    gapXs = GeneratedTokens.SPACE_GAP_XS.dp,
    gapSmall = GeneratedTokens.SPACE_GAP_SMALL.dp,
    gapLarge = GeneratedTokens.SPACE_GAP_LARGE.dp,
    cardPadding = GeneratedTokens.SPACE_CARD_PADDING.dp,
    controlMinWidth = GeneratedTokens.SPACE_CONTROL_MIN_WIDTH.dp,
    touchTargetMin = GeneratedTokens.SPACE_TOUCH_TARGET_MIN.dp,
    appBarHeight = GeneratedTokens.SIZE_APPBAR_HEIGHT.dp,
    appBarTitleInset = GeneratedTokens.SPACE_APPBAR_TITLE_INSET.dp,
    appBarCollapsedHeight = GeneratedTokens.SIZE_APPBAR_COLLAPSED_HEIGHT.dp,
    appBarLargeTitleGap = GeneratedTokens.SPACE_APPBAR_LARGE_TITLE_GAP.dp,
    appBarCollapseDistance = GeneratedTokens.SIZE_APPBAR_COLLAPSE_DISTANCE.dp,
    appBarActionRippleRadius = GeneratedTokens.SIZE_APPBAR_ACTION_RIPPLE.dp,
    listTopInset = GeneratedTokens.SPACE_LIST_TOP.dp,
    listGroupGap = GeneratedTokens.SPACE_LIST_GROUP_GAP.dp,
    listItemInset = GeneratedTokens.SPACE_LIST_ITEM_INSET.dp,
    listRowMinHeight = GeneratedTokens.SIZE_LIST_ROW_MIN_HEIGHT.dp,
    settingsSwitchCell = GeneratedTokens.SIZE_SETTINGS_SWITCH_CELL.dp,
    settingsSwitchInset = GeneratedTokens.SPACE_SETTINGS_SWITCH_INSET.dp,
    settingsDividerHeight = GeneratedTokens.SIZE_SETTINGS_DIVIDER_HEIGHT.dp,
    settingsDividerGap = GeneratedTokens.SPACE_SETTINGS_DIVIDER_GAP.dp,
    navigationBarHeight = GeneratedTokens.SIZE_NAVIGATION_BAR_HEIGHT.dp,
    fabSize = GeneratedTokens.SIZE_FAB.dp,
    fabSmall = GeneratedTokens.SIZE_FAB_SMALL.dp,
    fabLarge = GeneratedTokens.SIZE_FAB_LARGE.dp,
    buttonHeight = GeneratedTokens.SIZE_BUTTON_HEIGHT.dp,
    buttonHorizontalPadding = GeneratedTokens.SIZE_BUTTON_PADDING_HORIZONTAL.dp,
    chipHeight = GeneratedTokens.SIZE_CHIP_HEIGHT.dp,
    chipBorderWidth = GeneratedTokens.SIZE_CHIP_BORDER_WIDTH.dp,
    switchTrackWidth = GeneratedTokens.SIZE_SWITCH_TRACK_WIDTH.dp,
    switchTrackHeight = GeneratedTokens.SIZE_SWITCH_TRACK_HEIGHT.dp,
    switchThumbSize = GeneratedTokens.SIZE_SWITCH_THUMB.dp,
    switchRippleRadius = GeneratedTokens.SIZE_SWITCH_RIPPLE_RADIUS.dp,
    sliderTrackHeight = GeneratedTokens.SIZE_SLIDER_TRACK_HEIGHT.dp,
    sliderThumbSize = GeneratedTokens.SIZE_SLIDER_THUMB.dp,
    sliderRippleRadius = GeneratedTokens.SIZE_SLIDER_RIPPLE_RADIUS.dp,
    sliderTickSize = GeneratedTokens.SIZE_SLIDER_TICK.dp,
    controlRadioSize = GeneratedTokens.SIZE_CONTROL_RADIO.dp,
    controlCheckboxSize = GeneratedTokens.SIZE_CONTROL_CHECKBOX.dp,
    controlBorderWidth = GeneratedTokens.SIZE_CONTROL_BORDER.dp,
    controlCornerRadius = GeneratedTokens.SIZE_CONTROL_CHECKBOX_CORNER.dp,
    controlDotSize = GeneratedTokens.SIZE_CONTROL_DOT.dp,
    dividerThickness = GeneratedTokens.SIZE_DIVIDER_THICKNESS.dp,
    inputHeight = GeneratedTokens.SIZE_INPUT_HEIGHT.dp,
    inputIndicatorThickness = GeneratedTokens.SIZE_INPUT_INDICATOR_THICKNESS.dp,
    inputIndicatorThicknessFocused = GeneratedTokens.SIZE_INPUT_INDICATOR_THICKNESS_FOCUSED.dp,
    dialogInset = GeneratedTokens.SIZE_DIALOG_INSET.dp,
    dialogPadding = GeneratedTokens.SIZE_DIALOG_PADDING.dp,
    appBarPagerIndicatorHeight = GeneratedTokens.SIZE_APPBAR_PAGER_INDICATOR_HEIGHT.dp,
    appBarPagerIndicatorBand = GeneratedTokens.SIZE_APPBAR_PAGER_INDICATOR_BAND.dp,
    appBarPagerTop = GeneratedTokens.SPACE_APPBAR_PAGER_TOP.dp,
    appBarPagerTitleGap = GeneratedTokens.SPACE_APPBAR_PAGER_TITLE_GAP.dp,
    appBarPagerCollapsedHeight = GeneratedTokens.SIZE_APPBAR_PAGER_COLLAPSED_HEIGHT.dp,
    appBarSubtitleTighten = GeneratedTokens.SPACE_APPBAR_SUBTITLE_TIGHTEN.dp,
    progressLinearHeight = GeneratedTokens.SIZE_PROGRESS_LINEAR_HEIGHT.dp,
    progressCircularSize = GeneratedTokens.SIZE_PROGRESS_CIRCULAR_SIZE.dp,
    progressCircularStroke = GeneratedTokens.SIZE_PROGRESS_CIRCULAR_STROKE.dp,
    refreshThreshold = GeneratedTokens.SPACE_REFRESH_THRESHOLD.dp,
    refreshMaxDistance = GeneratedTokens.SPACE_REFRESH_MAX.dp,
    overscrollMax = GeneratedTokens.SPACE_OVERSCROLL_MAX.dp,
    tabRowHeight = GeneratedTokens.SIZE_TAB_ROW_HEIGHT.dp,
    tabMinWidth = GeneratedTokens.SIZE_TAB_MIN_WIDTH.dp,
    tabIndicatorHeight = GeneratedTokens.SIZE_TAB_INDICATOR_HEIGHT.dp,
    tabInset = GeneratedTokens.SPACE_TAB_INSET.dp,
    badgeDot = GeneratedTokens.SIZE_BADGE_DOT.dp,
    badgeHeight = GeneratedTokens.SIZE_BADGE_HEIGHT.dp,
    badgePaddingHorizontal = GeneratedTokens.SPACE_BADGE_PADDING_HORIZONTAL.dp,
    tooltipMaxWidth = GeneratedTokens.SIZE_TOOLTIP_MAX_WIDTH.dp,
    tooltipPaddingHorizontal = GeneratedTokens.SPACE_TOOLTIP_PADDING_HORIZONTAL.dp,
    tooltipPaddingVertical = GeneratedTokens.SPACE_TOOLTIP_PADDING_VERTICAL.dp,
    menuMinWidth = GeneratedTokens.SIZE_MENU_MIN_WIDTH.dp,
    menuItemHeight = GeneratedTokens.SIZE_MENU_ITEM_HEIGHT.dp,
    menuPaddingVertical = GeneratedTokens.SPACE_MENU_PADDING_VERTICAL.dp,
    menuItemPaddingHorizontal = GeneratedTokens.SPACE_MENU_ITEM_PADDING_HORIZONTAL.dp,
    sheetHandleWidth = GeneratedTokens.SIZE_SHEET_HANDLE_WIDTH.dp,
    sheetHandleHeight = GeneratedTokens.SIZE_SHEET_HANDLE_HEIGHT.dp,
    sheetHandleInset = GeneratedTokens.SPACE_SHEET_HANDLE_INSET.dp,
    searchBarHeight = GeneratedTokens.SIZE_SEARCH_BAR_HEIGHT.dp,
    railWidth = GeneratedTokens.SIZE_RAIL_WIDTH.dp,
    railItemHeight = GeneratedTokens.SIZE_RAIL_ITEM_HEIGHT.dp,
)

internal fun defaultShapes(): OxygenShapes = OxygenShapes(
    cornerCard = RoundedCornerShape(GeneratedTokens.SHAPE_CORNER_CARD.dp),
    cornerButton = RoundedCornerShape(GeneratedTokens.SHAPE_CORNER_BUTTON.dp),
    cornerInput = RoundedCornerShape(GeneratedTokens.SHAPE_CORNER_INPUT.dp),
    pill = RoundedCornerShape(percent = 50),
)

internal fun defaultTypography(): OxygenTypography = OxygenTypography()

internal fun defaultIconMetrics(): OxygenIconMetrics = OxygenIconMetrics(
    small = GeneratedTokens.ICON_SIZE_SMALL.dp,
    medium = GeneratedTokens.ICON_SIZE_MEDIUM.dp,
    large = GeneratedTokens.ICON_SIZE_LARGE.dp,
)

internal fun defaultInteraction(): OxygenInteraction = OxygenInteraction(
    refreshDragScale = GeneratedTokens.EFFECT_REFRESH_DRAG_SCALE,
    overscrollDragScale = GeneratedTokens.EFFECT_OVERSCROLL_DRAG_SCALE,
    dismissThreshold = GeneratedTokens.EFFECT_DISMISS_THRESHOLD,
)

internal fun defaultLayout(): OxygenLayout = OxygenLayout(
    appBarLargeHeight = GeneratedTokens.SIZE_APPBAR_LARGE_HEIGHT.dp,
)

/** 令牌只登记通用字族；自有字体接入前统一映射到平台默认无衬线。 */
internal fun fontFamilyOf(family: String): FontFamily = when (family) {
    "serif" -> FontFamily.Serif
    "monospace" -> FontFamily.Monospace
    else -> FontFamily.Default
}
