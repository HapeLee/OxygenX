# UI 库可靠性修复计划 / UI library hardening

Unofficial / 非官方。2026-11-17；所有受影响组件保持 `draft`。

本文记录首轮可靠性修复；后续状态输入与内容样式增量见 [接入计划](spec/state-input-content-plan.md)。

This records the initial hardening pass; see the [integration plan](spec/state-input-content-plan.md) for subsequent state-input and content-style work.

| 项目 / Work item | 状态 / Status |
| --- | --- |
| 主题继承、文本颜色、Snackbar 取消清理 / Theme inheritance, text color, Snackbar cancellation | 已实现并本地验证 / Implemented and locally verified |
| Slider RTL、键盘、无障碍结束回调 / Slider RTL, keyboard, accessibility completion | 已实现；Desktop 契约与 Android 触摸/键盘通过 / Desktop contract and Android touch/keyboard passed |
| TextField 令牌动效与中断验证 / Token-based TextField motion and interruption tests | 自定义时钟与绘制测试通过 / Custom-clock and drawing tests passed |
| Desktop UI 回归矩阵与 catalog 复现入口 / Desktop UI regression matrix and catalog reproduction | 已完成代表性矩阵与四张 Slider 基线 / Representative matrix and four Slider goldens passed |
| 双语接入、迁移、平台说明与 CI / Bilingual setup, migration, platform documentation and CI | 九对文档检查通过；CI 远端未运行 / Nine documentation pairs checked; remote CI not run |
| 公开 Compose 依赖传递与独立接入 / Public Compose dependencies and isolated consumer | API 依赖已修正；独立 Android APK 构建通过 / API dependencies fixed; isolated Android APK build passed |
| 真机读屏、逐帧校准、高刷性能 / Device accessibility, frame calibration, high-refresh performance | 未验证 / Not verified |

验证顺序：令牌生成 → 格式 → Desktop / Android 编译 → ABI 与测试 → catalog 人工验收 → 性能。

Validation order: token generation → formatting → Desktop / Android compilation → ABI and tests → manual catalog review → performance.

保留工作区原有底栏、底部面板和过冲改动；没有设备证据的项目不得标为通过或 `verified`。

Preserve existing navigation, sheet, and overscroll edits. Device-dependent items require actual evidence before claiming a pass or `verified` status.

## 本地验证 / Local validation

- 修复前新增七项契约测试有五项失败，修复后通过。当前 Desktop 总计 76 项、零失败；Wasm common 逻辑浏览器测试已执行通过。
- Five of seven initial regression tests failed before the fixes and passed afterward. Current Desktop total: 76 tests, zero failures; Wasm common logic browser tests also executed successfully.
- `spotlessCheck`、Desktop / Android 编译、APK 构建、`apiCheck`、`check`、本地 Maven 发布与九对双语文档检查通过。CI 配置执行同样门禁，并在发布后构建独立 Android 使用方；远端没有运行记录。
- Formatting, Desktop / Android compilation, APK build, ABI/check, local Maven publication and nine bilingual documentation pairs passed locally. CI runs these gates and an isolated published-artifact consumer; remote execution has not been observed.
- Android 真机：OnePlus PLK110 / Android 17 / API 37。Snackbar 显示 → 取消协程后移除 → 下一条显示；RTL 滑块左侧触摸提交 75%，Right 键减为并提交 50%；TextField 实际输入回写成功；深色嵌套主题文字和强调色已截图核对。
- Device: OnePlus PLK110 / Android 17 / API 37. Snackbar display → cancellation removal → next display passed. RTL touch committed 75%, then Right committed 50%. TextField accepted real input; nested dark-theme text and style color were visually checked.
- 本机证据位于 `build/hardening-*.log`、`build/device-*.json`、`build/hardening-*-device.png`；这些是本机调试证据，不作为厂商逐帧 reference 发布。
- Local evidence is in `build/hardening-*.log`, `build/device-*.json`, and `build/hardening-*-device.png`. These debugging artifacts are not published as vendor frame references.
- 独立 Android 工程先复现 `Composable` / `RowScope` 缺失的发布元数据问题；改正公开 API 的依赖可见性、重新发布后，`tools/consumer-smoke` 按 `0.1.0-local.1` 坐标完成 APK 构建，未额外添加 Compose 组件依赖。
- An isolated Android build first reproduced missing Composable / RowScope types. After correcting API dependency visibility and republishing, `tools/consumer-smoke` built an APK from version 0.1.0-local.1 without adding Compose component dependencies separately.

## 兼容性与未验收项 / Compatibility and remaining acceptance

`OxygenTheme.darkTheme` 改为 `Boolean? = null`，TextField 两个重载新增默认 `motion`；必须重新编译依赖方。现有显式 Boolean 调用仍有效。参考 [中文迁移说明](zh_CN/migration.md) / [English migration notes](migration.md)。

`OxygenTheme.darkTheme` becomes `Boolean? = null`; both TextField overloads add a defaulted motion parameter. Recompile dependents; existing explicit Boolean calls remain valid. See the migration pages above.

完整 TalkBack 矩阵、全组件 / Android golden、OxygenOS 动效逐帧校准、高刷性能、其余组件文档、文档站、公开发布与远端 CI 验收尚未完成；不将上述项目标为通过，不将组件升级为 `verified`。

Full TalkBack coverage, all-component / Android goldens, OxygenOS frame calibration, high-refresh performance, remaining component documentation, a documentation site, public publishing and remote CI acceptance remain unfinished. No pass or `verified` claim is made for those items.
