// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.icons.icon

import androidx.compose.ui.graphics.vector.ImageVector
import io.oxygenx.icons.icon.generated.AddGlyph
import io.oxygenx.icons.icon.generated.AlarmGlyph
import io.oxygenx.icons.icon.generated.BackGlyph
import io.oxygenx.icons.icon.generated.CheckGlyph
import io.oxygenx.icons.icon.generated.ChevronRightGlyph
import io.oxygenx.icons.icon.generated.CloseGlyph
import io.oxygenx.icons.icon.generated.ClockGlyph
import io.oxygenx.icons.icon.generated.GridGlyph
import io.oxygenx.icons.icon.generated.HourglassGlyph
import io.oxygenx.icons.icon.generated.InfoGlyph
import io.oxygenx.icons.icon.generated.SearchGlyph
import io.oxygenx.icons.icon.generated.SettingsGlyph
import io.oxygenx.icons.icon.generated.StopwatchGlyph
import io.oxygenx.icons.icon.generated.WorldClockGlyph

/**
 * 核心兼容入口：Google Material Symbols Outlined，opsz=24、wght=300、FILL=0、GRAD=0。
 *
 * 保留 Back / Clock / Grid 等原有名称；扩展 MaterialSymbols 按官方符号名重导出同一对象。
 * 14 个矢量只保存在本模块，不需要组件用户依赖完整扩展库。来源为 Google，Apache-2.0。
 * 相比早期 Rounded 版本，本轮统一为 Outlined；方向性矢量声明 autoMirror。
 * 生成来源、轴与 SHA-256 见 design/icons/material-symbols.json；运行
 * `python tools/material_symbols.py --fetch`，不要手改 generated 文件。
 */
object OxygenIcons {
    /**
     * 返回 / 展开箭头；RTL 下需要镜像。
     *
     * 特意用 `arrow_back_ios_new`（居中版）：`arrow_back_ios` 的 glyph 挤在 24dp 网格
     * 左半边（x≈1..11，iOS 风格紧贴后续文字），放进 48dp 返回槽会视觉左偏约 6dp、
     * 与标题间距被拉大。居中版只修正水平位置，尺寸与设置行尾的 `ChevronRight` 层级不变。
     */
    val Back: ImageVector get() = BackGlyph

    /** 列表行尾部箭头；RTL 下需要镜像。 */
    val ChevronRight: ImageVector get() = ChevronRightGlyph

    /** 勾选标记，用于选中态与设置项。 */
    val Check: ImageVector get() = CheckGlyph

    /** 齿轮（设置）。 */
    val Settings: ImageVector get() = SettingsGlyph

    /** 时钟，用于底栏「概览」分栏。 */
    val Clock: ImageVector get() = ClockGlyph

    /** 九宫格，用于底栏「组件」分栏。 */
    val Grid: ImageVector get() = GridGlyph

    /** 信息，用于底栏「关于」分栏。 */
    val Info: ImageVector get() = InfoGlyph

    /** 闹钟，用于时钟主页「闹钟」分栏。 */
    val Alarm: ImageVector get() = AlarmGlyph

    /** 世界时钟，用于时钟主页「世界时钟」分栏。 */
    val WorldClock: ImageVector get() = WorldClockGlyph

    /** 沙漏，用于时钟主页「计时器」分栏。 */
    val Hourglass: ImageVector get() = HourglassGlyph

    /** 秒表，用于时钟主页「秒表」分栏。 */
    val Stopwatch: ImageVector get() = StopwatchGlyph

    /** 加号，用于悬浮操作按钮。 */
    val Add: ImageVector get() = AddGlyph

    /** 放大镜，用于输入框的搜索图标。 */
    val Search: ImageVector get() = SearchGlyph

    /** 关闭 / 移除，用于 InputChip 的尾随操作。 */
    val Close: ImageVector get() = CloseGlyph
}
