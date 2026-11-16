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
 * 项目图标集：Material Symbols（Rounded 样式，`wght=300`）的静态 [ImageVector]。
 *
 * **来源与许可**：Google Material Symbols，Apache License 2.0，`generated/` 下每个文件都带
 * 各自的来源 URL 与版权头。这里不引入 `material-icons-core` / `material-icons-extended`
 * 依赖：那套库是从静态 drawable 生成的、没有 weight 轴，且 Compose Multiplatform 的对应
 * artifact 不覆盖本项目的 wasmJs target；直接按轴生成静态矢量既拿到 300 的细度，也保持
 * `oxygenx-icons` 只依赖 `compose.ui`。
 *
 * **生成参数**（每个图标都必须一致，否则一整套看起来不像一套）：
 * ```
 * https://fonts.gstatic.com/render/v1/Material+Symbols+Rounded/24dp/<symbol_name>.kt
 *     ?var=opsz,wght,FILL,GRAD,ROND@24,300,0,0,50
 * ```
 * `opsz=24`（24dp 光学尺寸）、`wght=300`（比默认 400 更细）、`FILL=0`（线性轮廓）、
 * `GRAD=0`（不加等级微调）、`ROND=50`（Rounded 样式）。响应是 gzip，`curl --compressed`。
 * 新增图标时下载到 `generated/<PascalName>Glyph.kt`，改 package 与本文件同目录约定、
 * 把 `public val <symbol>` 改名为 `internal val <PascalName>Glyph`，再在这里加一条映射。
 * 每个生成文件自带惰性缓存，未使用的图标可被裁剪。
 *
 * **着色与尺寸**不在本模块：`Icon` 用 `ColorFilter.tint` 按颜色令牌着色、按
 * `icon.size.*` 缩放。生成文件是填充轮廓路径（不是描边），所以没有 `icon.stroke.width`
 * 这回事。方向性图标由 `Icon(mirrorInRtl = true)` 处理，生成文件不带 `autoMirror`。
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
