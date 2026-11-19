# Legado MD3 接入检查

2026-11-18。目标：`D:\Project\legado-with-MD3`；OxygenX `0.0.1`，Unofficial / experimental。

本页前半记录接入前的临时验证。后续已按用户指令开始在 MD3 的 App* 分派层实施：固定 Maven 快照、Oxygen 引擎与主题、内容样式、状态输入及常用组件；当前实现与验收见目标工程 docs/dev/oxygenx-integration.md。

接入前结论：可以开始接入。已用最终 Maven 分发包在目标应用的真实依赖图上编译；仍需要宿主的引擎分支、内容样式与主题覆盖适配，不能只加依赖就认为全应用已切换。

## 验证方式与边界

- Maven 来源是 `build/distribution/0.0.1/OxygenX-0.0.1-maven.zip` 解压后的 `build/release-consumer-repo`，不是 `project(...)` 或源码复合构建。
- 使用忽略的 `build/legado-integration-probe/oxygenx-probe.init.gradle`，仅在本次 Gradle 调用注入专属仓库、BOM / UI / material3 依赖和 debug 编译样例；未修改目标工程的源码、设置或 Gradle 配置。
- 样例覆盖原生 TextFieldState、输入 / 输出变换、IME 动作、布局回调、prefix / suffix / supportingText、Oxygen Button / Switch / Slider，以及 M3 Text / ListItem / DatePicker。
- 不添加设置入口、不替换现有四引擎、不改变业务或 DI。样例没有运行入口，编译与打包不能当作真机交互验收。

| 检查 | 结果 |
| --- | --- |
| Kotlin / AGP | 两边均为 2.4.21 / 9.5.1；Legado wrapper 为 9.8.0 |
| SDK | Legado 当前 compileSdk 37.2 / minSdk 26 满足 OxygenX 的依赖要求 |
| 真实 appCoreDebug 依赖图 | 通过；io.oxygenx 坐标均为 0.0.1 |
| Compose 解析结果 | 保持 Legado 的 runtime / UI / Foundation / Animation 1.13.0-beta01、Material3 1.5.0-beta01，没有被 OxygenX 拉回旧版本 |
| :app:compileAppCoreDebugKotlin | 通过，包含临时样例及应用源码 |
| :app:assembleAppCoreDebug | 通过，含 AAR 元数据、重复类检查与 APK 打包 |
| :app:compileAppComicDebugKotlin | 通过，含临时样例及漫画增强版源码 |

日志位于忽略的 `build/legado-integration-probe/app-compile.log` 与 `app-package.log`。

## 接入前需要做的宿主适配

### 内容样式必须进入 AppText / AppIcon

目标工程的 `AppText` 默认取 `LegadoTheme.typography.bodyMedium` / `LegadoTheme.colorScheme.onSurface`，`AppIcon` 默认 tint 也取主题 onSurface。它们没有消费 Oxygen 或 M3 的槽位 locals。于是放入 Oxygen 主按钮、禁用按钮、顶栏或输入框槽位时，仅包 `ProvideMaterial3Content` 仍不能让这些宿主叶子组件得到正确颜色和字号。

最小适配是在 Oxygen 引擎下让这两个 App 组件转发给 Oxygen Text / Icon，或明确读取 Oxygen 的 LocalContentColor / LocalTextStyle，保留显式参数的优先级。不能仅增加根级颜色 provider。`ProvideAppContentColor` 目前只广播 Material / Miuix / Mochi，也应纳入 Oxygen。Icon 的显式 Unspecified 与“省略 tint”语义要单独映射，避免丢失原彩图标。

### 同源色板、字体与局部覆盖

在 Oxygen 分支中保留现有 MaterialTheme，从同一 M3 ColorScheme 调用 `toOxygenColorScheme()`，再提供 OxygenTheme 与 ProvideMaterial3Content。M3 复用组件继续读取 MaterialTheme，Oxygen 组件读取转换后的色板。此桥只提供颜色 / 内容样式，不自动转换字体、形状、布局或状态。

`AppTheme` 全局入口之外，`ProvideColorSchemeOverride` 和 popup 的 `ProvidePopupThemeOverride` 也要重建 Oxygen 作用域，否则书籍详情、阅读器局部配色或浮层可能继续使用旧色板。自定义字体需映射到 OxygenTypography。

透明主题需要明确验证：当前 `toOxygenColorScheme()` 根据 surface 亮度推断深浅；Legado 可将浅色主题 surface 设为 Transparent，宿主的 isDark 与推断结果可能不同，影响禁用 / 按压等扩展角色。不要把这条特殊主题路径当作已经验收。

### 引擎路由与组件适配

当前 ComposeEngine 只有 Material / Miuix / Mochi / Coui，ThemeResolver 不认识 oxygen，会回退 Material。正式接入需要枚举、持久值解析、设置选项、根主题与局部覆盖入口。当前生产代码有 87 个文件引用 ComposeEngine，其中 57 个位于 widget 目录；这是影响面统计，不代表每个文件都必须重写。

优先适配现有 AppText / AppIcon / AppTextField / Button / Switch / Slider / 设置行，再处理顶栏、导航栏、菜单、Dialog 和 Sheet。尚未适配的组件保留明确的 M3 分支，让一个完整设置页面先形成可运行切片。

State-based TextField 的通用参数可直接映射。宿主的 String label 要转成内容槽，labelPosition 的 Inside / Above 不能直接转发：Oxygen 使用固定标签布局，应给出明确映射，不能声称浮动标签行为一致。

ListItem、DatePicker 和现有数字选择实现可以继续复用；M3 ListItem / DatePicker 本身仍会读取主题色，并不是无颜色组件。保留同源 MaterialTheme 即可，混合槽位优先用所属体系的 Text / Icon，避免假设存在双向 locals 桥接。

AppScaffold 与 AppModalBottomSheet 还承载背景图、overlay、Insets、玻璃采样、窗口与返回处理。首次接入应保留宿主契约；不要机械替换为 Oxygen 同名组件，也不要互换两套 SheetState。OxygenTheme 会替换 LocalOverscrollFactory，只应包在选中的 Oxygen 引擎 / 页面分支内。

## 建议落地顺序

1. 将 0.0.1 Maven zip 固定为可重复获取、校验 SHA-256 的依赖来源；当前实际验证使用本地包，远端 Release 尚未创建，绝对本机路径不能成为 CI 的正式接入方式。
2. 增加 Oxygen 引擎与主题 / 字体 / 内容 locals 适配；保持现有引擎与未适配控件的行为。
3. 完成一个设置页的按钮、开关、滑块和状态输入框；复用 M3 列表 / 选择器。
4. 验证深浅 / 纯黑 / 透明、自定义字体、局部色板、禁用态、IME、滚动边界与返回；之后再扩展全局导航、Dialog / Sheet。

0.0.1 可作为宿主锁定版本；0.x 仍不承诺已编译二进制兼容。当前没有证据表明必须先补 ListItem / NumberPicker / DatePicker 才能开始接入，主要剩余工作在宿主适配。

参考：[Material 3 主题](https://developer.android.com/develop/ui/compose/designsystems/material3)、[CompositionLocal](https://developer.android.com/develop/ui/compose/compositionlocal)。

## 2026-11-18 接入实施结果

目标工程已增加可选择的 OxygenX 引擎与 App* 原生分派，固定 0.0.1 Maven 快照，宿主背景、复杂 Sheet 和 M3 复用能力保留。Core / Comic Debug 编译、双产品 APK、22 项主题 / 组件测试、架构门禁与双产品 Lint 通过（使用原基线，未放宽）。OxygenX 自身 Desktop 108 项 / 零失败，14 对双语文档通过。NavigationBar 的槽位增量已重新签名打包，目标工程 vendor manifest 固定实际压缩包校验和。

目标工程的 app/build/outputs/oxygenx-preview.png 是 Robolectric Native Graphics 的真实 App* 设置页组合预览，已目视检查；没有连接设备，未做本轮真机、IME / TalkBack 或性能验收。API 仍 experimental，没有自动提交 / 推送或远端发布。详细分派矩阵与兼容边界见目标工程 docs/dev/oxygenx-integration.md。
