// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon

/**
 * 设置项共用默认值：行高、内边距、竖分割线尺寸与颜色集合。
 *
 * 尺寸取自 `space.list.*` / `size.settings.*` 令牌，其中行内边距与开关单元格
 * 来自设置 APK 资源真值（`op_preference_material` 20dp 行内边距、
 * `op_switch_preference_material` 45dp 开关单元格），行文字左缘 20dp、
 * 开关轨道右缘约 14.5dp 均由这两项推出，与真机量值一致。
 */
object SettingsDefaults {
    /** 行最小高度，单行与双行设置项共用。 */
    val RowMinHeight: Dp
        @Composable get() = OxygenTheme.spacing.listRowMinHeight

    /** 行首（文字）与行尾内边距，两侧同为 20dp。 */
    val ItemInset: Dp
        @Composable get() = OxygenTheme.spacing.listItemInset

    /** 行尾开关单元格边长：开关在 45dp 方框内居中，右侧留白由方框自身产生。 */
    val SwitchCell: Dp
        @Composable get() = OxygenTheme.spacing.settingsSwitchCell

    /** 行尾带开关时的右侧内边距，比文字行更靠外，开关才贴近屏幕右缘。 */
    val SwitchInset: Dp
        @Composable get() = OxygenTheme.spacing.settingsSwitchInset

    /** 行内上下内边距。 */
    val VerticalPadding: Dp
        @Composable get() = OxygenTheme.spacing.gapSmall

    /** 点击区与开关之间的竖分割线高度。 */
    val DividerHeight: Dp
        @Composable get() = OxygenTheme.spacing.settingsDividerHeight

    /** 竖分割线到相邻内容的间距。 */
    val DividerGap: Dp
        @Composable get() = OxygenTheme.spacing.settingsDividerGap

    val DividerThickness: Dp
        @Composable get() = OxygenTheme.spacing.dividerThickness

    /** 设置项内嵌开关的动效，走开关自身的 `MOT-STATE-002`。 */
    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.switchToggle()

    @Composable
    fun colors(
        title: Color = OxygenTheme.colorScheme.onSurface,
        summary: Color = OxygenTheme.colorScheme.onSurfaceVariant,
        value: Color = OxygenTheme.colorScheme.onSurfaceVariant,
        divider: Color = OxygenTheme.colorScheme.outlineVariant,
        disabled: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): SettingsColors = remember(title, summary, value, divider, disabled) {
        SettingsColors(
            title = title,
            summary = summary,
            value = value,
            divider = divider,
            disabled = disabled,
        )
    }
}

@Immutable
data class SettingsColors(
    val title: Color,
    val summary: Color,
    val value: Color,
    val divider: Color,
    val disabled: Color,
) {
    @Stable
    internal fun resolveTitleColor(enabled: Boolean): Color = if (enabled) title else disabled

    @Stable
    internal fun resolveSummaryColor(enabled: Boolean): Color = if (enabled) summary else disabled

    @Stable
    internal fun resolveValueColor(enabled: Boolean): Color = if (enabled) value else disabled

    @Stable
    internal fun resolveDividerColor(enabled: Boolean): Color = if (enabled) divider else disabled
}

/**
 * 行首文本块：标题 + 可选副标题，标题与副标题之间不留额外间距
 * （真机两行字心距 18dp，等于 20dp 行高与 16dp 行高的一半之和）。
 */
@Composable
internal fun SettingsItemText(
    title: String,
    summary: String?,
    enabled: Boolean,
    colors: SettingsColors,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        BasicText(
            text = title,
            style = OxygenTheme.typography.bodyLarge.copy(color = colors.resolveTitleColor(enabled)),
        )
        if (summary != null) {
            BasicText(
                text = summary,
                style = OxygenTheme.typography.bodyMedium.copy(color = colors.resolveSummaryColor(enabled)),
            )
        }
    }
}

/** 行尾导航箭头：RTL 下自动镜像，禁用态走 `color.on.surface.disabled`。 */
@Composable
internal fun SettingsItemChevron(
    enabled: Boolean,
    colors: SettingsColors,
) {
    Icon(
        imageVector = OxygenIcons.ChevronRight,
        contentDescription = null,
        tint = colors.resolveValueColor(enabled),
        size = OxygenTheme.icons.small,
        mirrorInRtl = true,
    )
}
