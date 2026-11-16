# OxygenX

> **Unofficial / 非官方**：独立的第三方实现，与 OnePlus / OxygenOS 无任何从属或授权关系。
> **Phase 1 已完成；Android target 与 P2 动效内核基础已落地**：真机可运行、动效内核有单测；组件与完整 catalog 仍在后续阶段。

以可测量、可回归的方式复刻 OxygenOS 的视觉设计与动效，并提供一套 Compose Multiplatform 组件库与 catalog 示例应用。

**协作前请先读 [AGENT.md](./AGENT.md)**；细则在 [docs/spec/](./docs/spec/README.md)。

## 目标

- **设计复刻**：颜色、排版、形状、间距、图标、层级与状态，全部收敛到可追溯的设计令牌。
- **动效复刻**：状态反馈、进出场、共享元素转场、手势驱动、系统级场景，统一由 `oxygenx-motion` 的弹簧/缓动规格驱动，可中断、可逐帧比对。
- **可验证**：每个组件与动效都有 `Spec ID`、`targetBaseline`、reference 与证据等级；「看起来差不多」不算完成。
- **可复用**：组件库与 catalog 分离，catalog 既是示例也是唯一的可视化验收面。

## 平台

| 平台 | 定位 |
| --- | --- |
| Android | **第一优先与最终验收平台**（compileSdk 37 / minSdk 26），视觉、动效与触觉的完整实现 |
| Desktop (JVM) | 快速预览与动效调试，不作为验收依据 |
| Web (WasmJs) | 编译已通，应用入口延后 |
| iOS / macOS | 暂不承诺 |

平台能力差异通过 `LocalOxygenCapabilities` 降级，详见 `AGENT.md` 与 `docs/spec/component-api.md`。

## 模块

P1 已建立全部 `oxygenx-*` 库模块与 `catalog/shared`、`catalog/desktop`；`benchmark/`、文档站、Android/Web catalog 入口待后续阶段。

| 模块 | 说明 |
| --- | --- |
| `oxygenx-core` | 平台能力抽象与工具 |
| `oxygenx-theme` | 设计令牌与 `OxygenTheme` |
| `oxygenx-motion` | 弹簧、缓动、编排与可访问性降级 |
| `oxygenx-effects` | 模糊、发光等效果及降级 |
| `oxygenx-icons` | 自绘图标，可独立使用 |
| `oxygenx-ui` | 组件主体 |
| `catalog` | 示例应用（shared + 平台入口） |
| `design/tokens` | 令牌单一真源（YAML） |
| `benchmark` | 帧率与动效性能基准 |

## 路线图

`P0 规范（已完成）` → `P1 工程骨架 + 令牌真源（已完成）` → `P2 动效内核` → `P3 组件` → `P4 catalog + 中英文档 + Android/Web 入口` → `P5 逐帧与性能验收`

## 快速开始（本机已验证）

```powershell
.\gradlew.bat :oxygenx-theme:generateTokens
.\gradlew.bat check
.\gradlew.bat :catalog:android:assembleDebug
.\gradlew.bat :catalog:android:installDebug
.\gradlew.bat :catalog:desktop:run
```

Android 是主验收平台：`installDebug` 需要连接设备，本机已用真机（PLK110）验证安装、启动与动效交互；Desktop 仅作快速预览。

Windows 用 `.\gradlew.bat`，其他平台用 `./gradlew`。完整命令表与状态见 `AGENT.md` 第 3 节。

`design/tokens/*.yaml` 是设计/动效令牌的唯一真源，当前 15 个值全部是 `E0` / `draft` 占位，用于验证生成管线，不是 OxygenOS 的复刻参数。

## 规范索引

| 文档 | 内容 |
| --- | --- |
| `AGENT.md` | 唯一权威规则：状态、结构、命令、代码风格、API/动效红线、提交规范 |
| `docs/spec/README.md` | 优先级、术语、证据等级、合规边界、规范变更流程 |
| `docs/spec/design-tokens.md` | 静态设计令牌 |
| `docs/spec/motion.md` | 动效规范 |
| `docs/spec/component-api.md` | 组件 API 与代码约定 |
| `docs/spec/catalog.md` | 示例应用与验收 |
| `docs/spec/workflow.md` | 交付清单与门禁 |

## 合规

只复刻可观察的视觉规律与动效行为，不搬运字体、图标、壁纸、音效、系统二进制等专有资产；参考录屏仅用于本地验收。详见 `docs/spec/README.md`。

采用 Apache-2.0 许可，全文见 `LICENSE`；所有 `.kt` / `.kts` 文件带 SPDX 许可证头，由 Spotless 校验。
