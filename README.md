# OxygenX

**Unofficial / 非官方**：独立的 OxygenOS 风格 Compose Multiplatform UI 库，与 OnePlus / OxygenOS 无从属或授权关系。

当前为 **P3 实验版**：基础组件、设置项、导航、弹出层、主题和动效内核已落地，提供 Android / Desktop catalog、M3 配色桥接、ABI 基线和本地 Maven 发布。所有组件仍为 `draft`，API 可能变化；OxygenOS 动效逐帧校准和性能验收尚未完成。

## 文档

- [中文使用文档](docs/zh_CN/README.md) / [English documentation](docs/README.md)
- [接入与本地发布](docs/zh_CN/getting-started.md)
- [平台与验证边界](docs/zh_CN/platforms.md)
- [迁移说明](docs/zh_CN/migration.md)
- [本轮修复与未验证项](docs/ui-library-hardening.md)

## 平台

| 平台 | 当前承诺 |
| --- | --- |
| Android | 第一优先平台，minSdk 26 / compileSdk 37；有可构建 catalog |
| Desktop JVM | 快速预览与 UI 回归；当前本机验证 Windows / JDK 21 |
| Web WasmJs | 库编译与逻辑测试；没有 Web catalog 入口 |
| iOS / macOS Native | 暂不支持 |

## 本地验证

需要 JDK 21、Android SDK Platform 37 / Build Tools 37.0.0 和供 Wasm 浏览器测试使用的 Chrome。通过 `local.properties` 的 `sdk.dir` 或 `ANDROID_HOME` 配置 SDK。依赖版本集中在 `gradle/libs.versions.toml`。

```powershell
.\gradlew.bat :oxygenx-theme:generateTokens
.\gradlew.bat spotlessCheck
.\gradlew.bat compileKotlinDesktop :catalog:android:assembleDebug
.\gradlew.bat check
python tools/check_docs.py
```

Linux / macOS 使用 `./gradlew`。图像基线更新见 [golden 说明](catalog/golden/README.md)。CI 配置在 [.github/workflows/ci.yml](.github/workflows/ci.yml)，远端执行状态以 GitHub Actions 为准。

## 模块

| 模块 | 用途 |
| --- | --- |
| `oxygenx-ui` | Foundation 自绘组件 |
| `oxygenx-theme` | 主题、颜色、排版、间距与滚动过冲 |
| `oxygenx-motion` | 参数化、可中断的动效规格 |
| `oxygenx-core` | 平台能力声明 |
| `oxygenx-effects` | 能力感知的模糊入口 |
| `oxygenx-icons` / `oxygenx-icons-material` | 自绘图标 / Material Symbols |
| `oxygenx-material3` | 可选的 M3 → Oxygen 配色桥接、Oxygen → M3 内容样式桥接 |
| `oxygenx-bom` | 本地发布版本约束 |
| `catalog` | 公开 API 示例与人工验收入口 |
| `design/tokens` | 设计与动效 YAML 真源 |

## 贡献与许可

协作规则见 [AGENT.md](AGENT.md) 与 [docs/spec/](docs/spec/README.md)。只复刻可观察规律，不分发厂商专有资产；参考材料不进入发布制品。Apache-2.0，见 [LICENSE](LICENSE)。

Android catalog 启动 profile 与 Macrobenchmark 入口见 [性能文档](docs/zh_CN/performance.md)。此入口不代表全组件性能已验收。

Legado MD3 的真实构建验证与待适配项见 [接入检查](docs/spec/legado-integration-readiness.md)。
