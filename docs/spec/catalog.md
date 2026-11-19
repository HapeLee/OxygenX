# Catalog 示例应用规范

`catalog/` 是**唯一的可视化验收面**：组件示例、动效对照、令牌总览、回归基线都在这里。它同时是文档站交互 demo 的源码，不另起一套演示代码。

> **当前落地状态**：`catalog/shared` 的索引页是**横划三页**（`PagerTopBar` + `HorizontalPager`）：第 1 页组件清单、第 2 页图标库、第 3 页关于（环境开关 + 项目定位 / 许可 / 规范 / 阶段）。组件清单背后是 `catalogSections()` 注册表（TopBar、PagerTopBar、Scaffold、NavigationBar、Button、IconButton、Text/Icon、Chip、Badge、Tooltip、DropdownMenu、ModalBottomSheet、SwipeToDismiss、NavigationRail、Switch、TextField、SearchBar、Dialog、FAB、Slider、TabRow、Progress、PullToRefresh、Overscroll、Card、RadioButton、Checkbox、设置项，共 28 个 Section），每个 Section 在 `CatalogSectionScreen` 里作为独立页面显示（自带 `Scaffold` 与返回式顶栏）；路由由 nav3 的 `NavDisplay` 承载。顶栏、横划顶栏、下拉刷新这三页用 `CatalogSection.screen` 整页接管——演示对象（顶栏 / 横划容器 / 下拉滚动源）本身就是页面外壳。另有 `settings/` 整页预览（设置首页 + 显示与亮度列表页）与 `scaffold/` 能力页，以及 `catalog/android`（真机可运行，主验收平台）、`catalog/desktop`（快速预览）与 `catalog/reference/`（占位说明）。`motion/` 对比页、`tokens/`、`registry/`、`golden/`、`catalog/web` 按 P3–P5 建立；下述结构是其目标形态，不代表已实现。

## 1. 结构

2026-11-17 增量：`golden/CMP-SLIDER/` 已有 Desktop 的明暗 × LTR/RTL 四张静态回归图，说明见 `catalog/golden/README.md`；它们只保护本库输出，不属于 OxygenOS 真机 reference。Slider 页新增 RTL/提交值，TextField 页新增原生状态过滤 / 显示变换 / 程序化编辑、M3 Text 继承按钮样式和主题/样式颜色，Scaffold 能力页使用公开 SnackbarHost 并可取消调用协程。`golden/CMP-TEXTFIELD/` 补明暗两张输入槽位 Desktop 基线；全组件与 Android golden、逐帧对比模式仍未落地。

```
catalog/
├── shared/src/commonMain/kotlin/io/oxygenx/catalog/
│   ├── CatalogApp.kt           NavDisplay 路由（NavKey 返回栈 + entryProvider）
│   ├── CatalogPage.kt          索引页（横划三页：组件 / 图标 / 关于）+ 唯一注册入口 catalogSections()
│   ├── CatalogSectionScreen.kt 单个 Section 的页面外壳（Scaffold + 返回式顶栏）
│   ├── component/{Name}Section.kt    组件示例，一个组件一个文件
│   ├── icon/IconGalleryPage.kt       索引页第 2 页：图标网格（按符号名首字母分组）
│   ├── motion/{SpecId}Comparison.kt  动效并排对比页
│   ├── tokens/TokensPage.kt          令牌总览与对比度检查
│   ├── registry/                   参考凭据索引与浏览页
│   └── ui/                         示例专用小组件（SmallTitle、DemoCard、PlaybackControls…）
├── android/  desktop/  web/       平台入口，仅做启动与平台装配
├── reference/<spec-id>/           参考片段、关键帧、测量数据（见 motion.md §9）
└── golden/<spec-id>/              被批准的关键帧基线截图
```

平台入口不得包含组件业务逻辑；`shared` 不得引用任何平台专属 API。

## 2. 注册

组件示例统一写成 `LazyListScope` 扩展：

```kotlin
fun LazyListScope.buttonSection() {
    item(key = "cmp-button") {
        SmallTitle(text = "Button / 按钮", specId = "CMP-BUTTON")
        DemoCard {
            // 默认形态、关键参数变体、禁用态
        }
    }
}
```

- 在 `CatalogPage.kt` 的按字母序 import 块中加一行，并在 `catalogSections()` 的列表顺序中插入一条 `CatalogSection`（`id` 用 `Spec ID` 小写形式）；**新增即注册**，不允许有游离的 Section 文件。
- 每个 Section 的 `item(key = …)` 使用 `Spec ID` 小写形式；导航 / deep link / 搜索索引从该 key 生成。
- 一个入口 = 一个 `CatalogSection` = 一个独立页面：索引页只列标题与 `Spec ID`，内容由 `CatalogSectionScreen` 在自己的 `Scaffold` 里渲染，不再把所有示例堆成一张长列表。
- 同一族组件（顶栏、设置项）共用一个 `CatalogSection`、一个索引入口即可，页内按各自的 `SmallTitle` / `item(key = …)` 分块标 `Spec ID`；合并时 `id` 用族名，`specId` 取该族主组件。不要为同一族拆出多行把索引页撑乱。
- 演示对象本身就是页面外壳时（顶栏、Scaffold 这类），用 `CatalogSection.screen` 提供整页渲染，让屏幕自己的外壳就是被演示的组件；不要在外壳里再嵌一个同类的预览容器。底栏是例外：它要在同一页并排对比「四栏 / 三栏 / 禁用 + 配色」几种形态，整页外壳一次只能摆一条，所以留在页内用 `DemoCard` 逐块演示，形态文案写清楚是哪一档。
- 通栏列表类组件（设置项这类行要延伸到屏幕左右边缘的）设 `CatalogSection.fullBleed = true`，页面不加左右 `contentPadding`、也不用 `DemoCard` 包裹；行的点击区与水波纹走满整行，文字离屏幕的距离交给组件自身的 `ItemInset`。页内的分块标题与说明文字仍各留一份 `space.gutter` 左右内距。
- 页面级的左右上下边距只从 `catalog/ui/CatalogPageSpacing.kt` 的 `catalogPageSpacing(fullBleed)` 一处取：左右 = `space.gutter`（16dp，真机设置界面分组容器的边距；注意通栏列表里的文字内距是 `space.list.item.inset` 20dp，两者不是同一个值），顶 = `space.list.top`（9dp），底 = `space.gutter` + 安全区，分块之间 = `space.list.group.gap`（21dp）。`SectionScaffold`、索引页、顶栏 / Scaffold 演示页与设置列表容器都必须走它，不要再各自 `PaddingValues`。
- 示例的 `SmallTitle` 必须显示 `Spec ID` 与 `Status`（`draft` 的示例在页面上要有可见标记）。
- 索引页自己就是一个 `PagerTopBar` + `HorizontalPager` 的三页页面：组件清单 / 图标库 / 关于。三页各自是滚动容器、共用同一个 `rememberTopAppBarState()`，翻页不重置收拢形态；底部安全区由 pager 的 `contentPadding` 统一让开，页内列表不再各扣一次。环境开关（明暗主题：跟随系统 / 固定浅色 / 固定深色，以及「动态取色（M3 桥接）」）属于调试入口，放在「关于」页而不是组件页。打开动态取色后整个 catalog 的色板来自 **Material 3 动态取色**：Android 12+ 直接吃系统壁纸取色（`dynamicLightColorScheme` / `dynamicDarkColorScheme`），desktop / wasm 与 Android 11 及以下用种子色 `#0061A4` 跑一遍取色算法（`catalog/shared` 里的 `rememberSystemDynamicColorScheme` expect/actual + `com.materialkolor`，依赖只加在 catalog），再经 `toOxygenColorScheme()`（`:oxygenx-material3`）映射成 Oxygen 色板；关掉即回到本项目令牌。
- 资源类总览（图标库）不单独登记 Section，它是索引页第 2 页的内容（`icon/IconGalleryPage.kt`，`Spec ID` 仍为 `ICONS-MATERIAL`）：按官方符号名首字母分组的三列网格，`ImageVector.name` 即官方符号名。返回靠系统返回（索引页是根页面，没有导航图标）。

## 3. 演示矩阵

每个组件示例必须覆盖（不适用的项在 Section 注释里写明原因）：

| 维度 | 要求 |
| --- | --- |
| 默认形态 | 与 `ComponentDefaults` 完全一致，不留隐式参数 |
| 关键参数变体 | 每个公开视觉参数至少一个可观察变体 |
| 交互状态 | 按压 / 聚焦 / 选中 / 展开 / 进行中 |
| 禁用态 | `enabled = false`，且能看出不可交互 |
| 明暗主题 | 页面级切换，覆盖 `light` / `dark` 双值；默认跟随系统，可固定成任一档 |
| 强调色覆盖 | 验证 `overridable` 令牌与对比度 |
| RTL / 大字体 | 提供全局开关，验证镜像与不截断 |
| 动效调参 | 曲线、弹簧刚度/阻尼、错峰滑块；实时生效 |
| 代码展示 | 与该示例同步的可复制代码片段，禁止手写过期示例 |

分组用多个 `DemoCard`（统一的水平/底部内边距令牌），不同维度独立成组，避免一个巨型示例。

## 4. 动效对比模式

凡是 `MOT-*` 动效，除了常规演示，还必须有并排对比页：

- 左：reference 片段；右：当前实现。共享时间轴、帧号、暂停与逐帧步进、循环区间（实现侧不再提供时间倍率 / 慢放，逐帧核对只靠暂停与步进）。
- 提供采样曲线（时间 → 位移/缩放/不透明度）叠加显示，差异来自 `measurements.csv`，**不接受「看起来差不多」作为通过结论**。
- 显示 `targetBaseline`、设备/系统版本、帧率、证据等级与 `Status`；`draft` 的对比页必须明确标注「参数未验证」。
- 对比页由 `motion/{SpecId}Comparison.kt` 实现，并在 `CatalogPage.kt` 的动效分组注册。

## 5. 参考凭据与 Golden 基线

- `reference/<spec-id>/` 内容遵循 [motion.md](./motion.md) §9；只保留**最短可复核片段**，遵守 [README.md](./README.md) §5 的合规约束，不进入公开制品。
- `golden/<spec-id>/` 保存被批准的关键帧截图，文件名含帧号与平台，例如 `f012_android_light.png`。
- Golden 更新必须与被批准的 reference、实现改动在**同一提交**中完成；PR 描述列出差异原因。CI（P1 起）校验 golden 与组件清单一一对应，缺失或孤立文件视为失败。
- 截图禁止包含个人信息、通知内容、账号数据或任何专有素材。

## 6. 文档站联动

- 文档站（`docs/`）的交互 demo 直接复用 `catalog/shared` 的演示代码，不复制粘贴第二份实现。
- 每个组件对应 `docs/components/{slug}.md` 与 `docs/zh_CN/components/{slug}.md`，章节一一对应：`iframe` 交互 demo、Import、Basic Usage、Properties 表、Defaults、Colors、Motion、Advanced Usage。
- slug 全小写；demo id 与 `Spec ID` 对应；组件概览表与侧边栏（中英各一份）同步新增一行。
- iframe 容器高度按 demo 实际内容设置，不写死过大/过小的值；中文页相对路径多一层 `../`。

## 7. 平台与验收矩阵

| 检查 | Android | Desktop JVM | Web (WasmJs) |
| --- | --- | --- | --- |
| 组件外观 / 交互 | 完整验收 | 完整验收 | 主要流程 |
| 动效时序与曲线 | 完整验收 + 逐帧 | 完整验收 + 逐帧 | 抽样 |
| 能力降级（模糊/触觉/高刷） | 原生效能 | 降级路径 | 降级路径 |
| 性能基准 | 必需 | 参考 | 不要求 |

Android 是 full-fidelity 基准；Desktop 的快速迭代不得成为跳过 Android 验收的理由。任何平台上的视觉近似都必须保留**相同的时序与曲线**，否则逐帧对比失效。

## 8. 禁止清单

- catalog 内硬编码视觉值、绕过公开 API 调用 `internal`、用平台入口写业务逻辑。
- 提交无法追溯到 reference 的 golden；用主观描述替代测量数据。
- 只更新实现不更新示例，或只更新英文文档不更新中文文档。
- 把参考录屏、专有素材打进发布物；在示例里留 `TODO` 占位却标 `verified`。
- 新增 Section 但不注册，或注册了却没有对应演示文件。

2026-11-17 Text / Icon 增量：新增 `cmp-text-icon`，同时演示原色 / 默认色、Vector / Painter / Bitmap、行内内容、自动字号与字体装饰；明暗 Desktop 图在 `golden/CMP-TEXT-ICON/`。Android 入口对目录页 test tag 启用 resource-id 映射供启动基准等待，Desktop 不依赖 Android 语义扩展。

2026-11-17 过冲回归：Overscroll 演示扩充为 80 项，提供顶部 / 中间 / 底部定位与可见过冲状态；正常中间惯性不触发弹簧。Android 关于页从 BuildConfig 显示真实版本名 / 构建号，开发与 benchmark 使用独立包名。
