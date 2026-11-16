# M3 vs OxygenX `oxygenx-ui`：组件易用性对比

> 对比口径：Material 3（`androidx.compose.material3` 1.3/1.4 的公开 API 契约） ↔ 本仓库 `oxygenx-ui`（仅 `foundation` + 全自绘，规范禁止用 Material 组件当外观）。
> 方法：静态阅读 38 个源文件的公开签名、`docs/spec/component-api.md`、`catalog/shared` 的真实调用点。**未运行构建**，结论只针对 API 形态与使用成本，不含渲染效果评价。

## 一句话结论

**基础件（主题、令牌、状态模型、动效）比 M3 更好上手；表达力（内容槽位、变体、覆盖面）只到 M3 的六成左右。**
作为「复刻 OxygenOS 的自有应用界面库」够用且好用；作为「给第三方用的通用组件库」还差一层：没有可组合槽位、没有公开构件、缺一整套组件族。

评分（10 分制，权重见末节）：

| 维度 | OxygenX | M3 | 说明 |
| --- | --- | --- | --- |
| 默认值即可用（少写参数） | **9** | 8 | `Button(onClick, text)` 直接出正确外观；M3 也接近，但变体选择多一步 |
| 与 M3 心智模型一致 | 8 | 10 | 角色名、`LocalContentColor`、`Text` 解析顺序都对齐；签名形态有分歧 |
| 表达力 / 可组合性 | **4** | 9 | 文本一律 `String`，没有 `content` / 槽位逃生口 |
| 组件覆盖面 | **5** | 9 | 约 40 个入口 vs M3 约 60+，缺 badge/tooltip/menu/sheet/segmented 等 |
| 状态与受控模型 | 9 | 9 | 无状态优先、`XxxState` + `rememberXxxState`、`Scaffold(topBarState)` 一体接线 |
| 无障碍默认值 | 7 | 8 | Role/语义齐；16dp 控件自身触摸目标不足，依赖宿主行兜底 |
| 可发现性（预览/文档/示例） | **4** | 8 | 无 `@Preview`、文档站 P4 未落地 |
| 作为依赖被复用 | **2** | 10 | 无 publish 约定、无版本号、全部 `draft` |
| **加权总分** | **6.1** | **8.8** | 内部自用 6.1 已够；公共库视角约 4 |

---

## 1. 比 M3 好用的地方（有实证）

### 1.1 默认值全部走令牌，调用点几乎零视觉参数
`Button` 的 6 个参数里只有 2 个必填：

```40:54:d:/Project/OxygenX/oxygenx-ui/src/commonMain/kotlin/io/oxygenx/ui/basic/Button.kt
Button(onClick = { clicks++ }, text = "主按钮")
Button(onClick = {}, text = "禁用", enabled = false)
```

M3 里要拿到「品牌正确、深浅色都对」的按钮，同样一行也可以，但只要想换底色就必须同时给 `containerColor` / `contentColor` / `disabledContainerColor` / `disabledContentColor` 四个值。这里 `ButtonDefaults.colors()` 已经四色成对，`secondaryColors()` / `PillShape` 是语义化预设，改一个维度不用碰其余三个。

### 1.2 主题支持「部分覆写」，比 `MaterialTheme` 的整包替换更省事
```84:105:d:/Project/OxygenX/oxygenx-theme/src/commonMain/kotlin/io/oxygenx/theme/OxygenTheme.kt
fun OxygenTheme(
    darkTheme: Boolean = false,
    colorScheme: OxygenColorScheme? = null,
    spacing: OxygenSpacing? = null,
    ...
```
传 `null` 就继承外层。M3 想只换配色得自己 `MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = …))`，而 `Typography` / `Shapes` 没有 `copy` 之外的「继承式」覆写。这里在页面内套一层只换 `colorScheme` 即可，其余令牌不动。

### 1.3 动效是组件参数，且 reduced-motion 由内核统一降级
`Switch` / `Slider` / `Checkbox` / `RadioButton` / `TabRow` / `NavigationBar` / `ProgressIndicator` 都有 `motion: OxygenMotionSpec`。M3 的这些组件根本不开放动画规格（`Switch` 只能换颜色），要换节奏得自己重写。

### 1.4 大顶栏不需要调用方接线
```68:80:d:/Project/OxygenX/oxygenx-ui/src/commonMain/kotlin/io/oxygenx/ui/layout/Scaffold.kt
fun Scaffold(
    topBar: @Composable () -> Unit = {},
    ...
    topBarState: TopAppBarState? = null,
```
传一个 `rememberTopAppBarState()` 给 `Scaffold` 与顶栏就自动收拢。M3 需要 `rememberTopAppBarScrollBehavior()` + `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` + `TopAppBar(scrollBehavior = …)` 三处手动串起来，忘一处就「顶栏不动」。

### 1.5 有一整套 M3 没有的设置页组件族
`SettingsItem` / `SettingsSwitchItem` / `SettingsSplitItem` / `SettingsRadioItem` / `SettingsCheckItem` / `SettingsGroupTitle`（`oxygenx-ui/.../settings/`）。M3 只有 `ListItem`，`ListItem` 的行尾是 `trailingContent` 自由槽，不给「整行一个 Switch 语义节点」「左点击 + 右开关两个独立点击区」这类真机形态——这两点在 M3 里都要手写 20~40 行。

### 1.6 Snackbar 只有一条路径
`SnackbarHostState.showSnackbar(message, actionLabel, withDismissAction, duration): SnackbarResult`，没有 M3 那个可写的 `currentSnackbarData`，也没有 `SnackbarVisuals` 接口。少一层抽象 = 少一类误用。

---

## 2. 不如 M3 好用的地方（按影响排序）

### 2.1 文本参数一律 `String`，没有内容槽位 ← 最大问题
| 组件 | OxygenX | M3 |
| --- | --- | --- |
| `Button` | `text: String` + 内部 `BasicText` | `content: @Composable RowScope.() -> Unit` |
| `Chip` | `label: String` | `label: @Composable () -> Unit` |
| `TopAppBar` / `LargeTopAppBar` / `PagerTopBar` | `title: String`, `subtitle: String?` | `title: @Composable () -> Unit` |
| `TabRow` / `SecondaryTabRow` | `tabs: List<String>` | `tabs: @Composable () -> Unit`（自己放 `Tab`，`Tab` 有 `text` + `icon`） |
| `NavigationBar` | `items: List<NavigationBarItem>`（`label: String` + `icon`） | `content: @Composable RowScope.() -> Unit` + `NavigationBarItem(label, icon)` 槽 |

后果（都是日常会撞到的）：
- 按钮里放「图标 + 文案」、放 spinner、放富文本 → 做不到，只能放弃组件自己拼 `Row`，于是水波纹、禁用色、触摸目标全部重写一次。
- 页签不能带角标 / 图标 / 计数；顶栏标题不能带换行样式或内嵌图标。
- 导航栏不能加未读角标（`NavigationBarItem` 只有 `label` / `icon` / `enabled`）。

### 2.2 变体与槽位逃生口缺失
- `Button` 只有「填充」一种视觉（`TextButton` 独立文件），没有 `OutlinedButton` / `ElevatedButton` / `FilledTonalButton`；没有 `contentPadding`、没有 `border`。
- `IconButton` 只有基础形态，KDoc 明说「需要底色时用 `Surface(shape = CircleShape, onClick = …)` 包一层」——即把装配成本转给调用方。
- `Card` 的 `shape` 写死 `cornerCard`、无 `elevation`/`border` 参数（`Card.kt:56`）；想换圆角只能改用 `Surface`。
- `TabRow` 的指示器不可替换（无 `indicator` 槽）、无 `divider` 槽。
- `Switch` 没有 `thumbContent`（M3 可把图标塞进拇指）。
- `Slider` 没有 `interactionSource`、没有 `thumb`/`track` 槽（`Slider.kt:57-68`）。
- `Text` 只吃 `style`，没有 `fontSize` / `fontWeight` / `letterSpacing` / `onTextLayout` 等单维度覆写，也不读 `LocalTextStyle`（全仓搜不到 `LocalTextStyle` / `ProvideTextStyle`）——所以容器无法下发默认排版，每个叶子都得写 `OxygenTheme.typography.caption.copy(color = …)`。catalog 里这个写法反复出现了十几次。

### 2.3 覆盖面缺口（相对 M3）
M3 有、这里没有：`Badge`、`Tooltip`、`DropdownMenu`/`ExposedDropdownMenuBox`、`ModalBottomSheet`、`SegmentedButton`、`SearchBar`、`DatePicker`/`TimePicker`、`NavigationDrawer`、`Carousel`、`TriStateCheckbox`、`SliderState`、`TopAppBar` 的 `windowInsets`、`ListItem` 的完整形态、`AlertDialog` / `BasicAlertDialog`。
→ 一旦做表单、菜单、底部弹层这类「非设置页」界面，很快就要破库自己写，设计一致性随之漂移。

### 2.4 没有 `AlertDialog` 便利形态
`Dialog` 只给「卡片 + Column 插槽 + `DialogScope.dismiss()`」：
```97:103:d:/Project/OxygenX/oxygenx-ui/src/commonMain/kotlin/io/oxygenx/ui/basic/Dialog.kt
fun Dialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: DialogAlignment = DialogAlignment.Bottom,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable DialogScope.() -> Unit,
```
最常见的「标题 + 一段文字 + 两个按钮」要自己写 `Column` + 两处 `spacing.dialogPadding` + 两个 `TextButton`。M3 的 `AlertDialog(title = …, text = …, confirmButton = …)` 一行搞定。逐像素还原的确需要低层接口，但缺高层封装就是缺。

### 2.5 同名重载有踩坑面：`Switch`
```110:118:d:/Project/OxygenX/oxygenx-ui/src/commonMain/kotlin/io/oxygenx/ui/basic/Switch.kt
fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressed: Boolean = false,
    ...
```
漏写 `onCheckedChange` 不会报「缺参数」，而是静默命中「纯展示」重载 → 生成一个不能点的开关，只有靠 `pressed`（`Boolean = false`）这种参数名才看得出区别。M3 里展示态是 `Switch` 的 `onCheckedChange = null`，同样是可识别的一次行为改变，但不会被误认为另一个控件。

### 2.6 小控件自身触摸目标不足
`Checkbox` / `RadioButton` 的根是 `Canvas` + `.size(16dp)`，`clickable` 挂在 16dp 上（`Checkbox.kt:64-77`、`RadioButton.kt:56-66`），且 `checked` 只有 `Boolean`，没有三态。项目内靠 `SettingsChoiceItem` 整行兜底，但从库里单独取用这两个组件就低于 48dp 目标。M3 用 `TriStateCheckbox` + 最小交互尺寸兜住了。

### 2.7 可发现性与复用成本
- `oxygenx-ui` 全仓 **0 个 `@Preview`**；组件长什么样只能跑 catalog 或真机。
- 文档站是 P4，未落地；`docs/spec/` 是**规则**文档，不是「怎么用」文档。
- `build-logic/` 没有 `publish` 约定脚本，也没有版本号 —— 外部项目目前无法 `implementation(...)`，只能 copy 源码。
- 组件 KDoc 的 `Status` 全是 `draft`，`Baseline` 大量是 `<version> / <device> / <date>` 占位；单测 30 个且集中在校验数学函数（`SliderMathTest`、`SwitchMathTest`…），没有一条是「组件交互 + 无障碍」的 UI 测试。作为 API 契约，这个保证强度低于 M3 的稳定契约。

---

## 3. 逐组件速查表

| 类别 | OxygenX | 与 M3 的关系 | 易用性判断 |
| --- | --- | --- | --- |
| 主题入口 | `OxygenTheme(darkTheme, colorScheme?, …)` | 同 `MaterialTheme`，多了「部分继承」 | ✅ 更好 |
| 文本 | `Text(text, style, color, …)` | 少了字号/字重/`onTextLayout`，无 `LocalTextStyle` | ⚠️ 更差 |
| Surface / Card | `Surface(…border, onClick?)` / `Card(onClick?)` | 去掉了 elevation（有意），Card 无法换形 | ⚠️ 略差 |
| Button | `(onClick, text, …)` | 无 content 槽、无 outlined/elevated/tonal | ❌ 更差 |
| TextButton | `(onClick, text, …)` | 参数对齐 | ✅ 接近 |
| IconButton | `(onClick, icon, contentDescription)` | 无 filled/tonal/outlined | ⚠️ 更差 |
| FAB | 4 个入口（regular/small/large/extended） | 分档对齐 M3，圆形为有意差异，无收起动画 | ✅ 接近 |
| Chip | `Chip(label, onClick, selected, …)` + 3 套配色 | 一个组件吸收 Assist/Filter/Input/Suggestion | ✅ 更省事 / ⚠️ 少了 `elevation`、无 `onClick = null` 形态 |
| Switch | 交互重载 + 展示重载 | 少了 `thumbContent` | ⚠️ 同名重载有误用面 |
| Checkbox / RadioButton | `(checked, …, onClick: (() -> Unit)?)` | 无三态；16dp 目标 | ⚠️ 略差 |
| Slider | `(value, onValueChange, steps, …)` | 无 `interactionSource`、无 thumb/track 槽 | ⚠️ 略差 |
| TextField | 参数逐一对应 M3（含 `TextFieldValue` 重载） | label 不上浮、`supportingText` 错误色需自己给 | ✅ 接近（M3 忠实复刻） |
| Dialog | `Dialog(onDismissRequest, alignment, content)` | 无 `AlertDialog` 高层封装 | ⚠️ 更麻烦 |
| TopAppBar 族 | `TopAppBar` / `LargeTopAppBar` / `PagerTopBar` | 标题是 String；滚动接线比 M3 简单 | ✅ 接线更好 / ❌ 槽位更差 |
| TabRow | `TabRow(tabs: List<String>, selectedIndex, …)` | 索引式；无 indicator/divider 槽 | ⚠️ 简单场景更快，复杂场景做不到 |
| NavigationBar | `NavigationBar(items, selectedIndex, onSelect)` | 索引式；无角标 | ⚠️ 同上 |
| Scaffold | `(topBar, bottomBar?, fab?, snackbarHost?, topBarState?, …)` | 插槽可空（比 M3 的 `{}` 更明确），多了 `topBarState` | ✅ 更好 |
| Progress | `progress: Float? = null`（`null` = 不确定态） | M3 用两个重载 + `() -> Float` | ✅ 更省事（但少了 `strokeCap`/`gap` 微调） |
| PullToRefresh | `PullToRefreshBox(isRefreshing, onRefresh, …)` | 对齐 M3 1.3+；指示器不可替换 | ✅ 接近 |
| Snackbar | `SnackbarHostState.showSnackbar(...)` | 只有一条路径，更简单 | ✅ 更简单 / ⚠️ 无自定义 visuals |
| 设置页族 | 6 个 `Settings*` 组件 | M3 无对应物 | ✅ 本项目独有优势 |

---

## 4. 要变「好用」的最短路径（按性价比排序）

**P0（不做会持续付出代价）**
1. 给 `Button` / `Chip` / `TopAppBar` / `TabRow` 族加 `content` / `title` 槽位，保留现有 `String` 便捷重载（内部转发）。这是唯一能一次消掉大量「自己拼 Row」需求的动作。
2. 加 `AlertDialog(title, text, confirmButton, dismissButton)` 高层封装，底层 `Dialog` 不动。
3. 补 `OxygenTheme` 的 `LocalTextStyle` + `ProvideTextStyle`，让 `Text` 的 `style` 默认值改成 `LocalTextStyle.current`；顺带补 `fontSize`/`fontWeight` 的直传参数。

**P1（补齐通用性）**
4. `Card` 开放 `shape` / `border`；`TabRow` 开放 `indicator`；`IconButton` 补 `FilledIconButton` / `TonalIconButton`。
5. `Checkbox` 补三态；`Checkbox`/`RadioButton` 外层撑 48dp（项目内已有 Chip 的「外层 Box 撑触摸目标」写法可复用）。
6. 把展示态开关改名为 `SwitchThumb`/`SwitchIndicator` 或 `Switch(onCheckedChange = null)`，消掉同名重载的误用面。

**P2（工程化）**
7. `build-logic` 加 `publish` 约定 + 版本号，让库能被外部消费。
8. 每个组件补 `@Preview`（光/暗 + 禁用 + 大字体三态），比写文档便宜且直接。
9. 补交互 + 语义 UI 测试（现在只有数学函数测试）。

---

## 5. 结论再表述

- **如果你在本仓库内、目标就是复刻 OxygenOS 的设置类界面**：这套 API 比 M3 更省事。默认值即可用、动效可覆盖、大顶栏零接线、设置行有现成群组，几乎不用写视觉参数。易用性 ≈ 8.5/10。
- **如果你打算把它当 M3 的替代品做通用应用**：目前会频繁撞到「文本不能组合」「没有菜单/弹层/角标」「按钮只有一种视觉」。易用性 ≈ 4/10，主要不是「难写」，而是「写不出来」。
- **中间态（同一设计语言的多个应用）**：先做上面 P0 三条，易用性提升最明显；尤其 `Button.content` 与 `AlertDialog` 两项，能覆盖绝大部分「被迫绕开组件」的场景。

> 证据等级说明：本文结论均基于源码签名与 catalog 调用点，未做编译/真机验证；组件 `Status` 多为 `draft`，API 仍可能变动。
