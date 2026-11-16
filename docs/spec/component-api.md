# 组件 API 与代码规范

适用于 `oxygenx-ui/` 及所有对外发布模块的 `@Composable` 代码。组织方式借鉴同类 CMP 组件库，但**只有本仓库 `AGENT.md` 与本目录规范生效**。

## 1. 签名与参数顺序

```kotlin
@Composable
fun ComponentName(
    // 1. 必需参数：函数式回调优先，其次必需数据
    onClick: () -> Unit,
    // 2. Modifier（永远有默认值，永远叫 modifier）
    modifier: Modifier = Modifier,
    // 3. 状态与布尔开关
    enabled: Boolean = true,
    // 4. 视觉参数：默认值一律来自 ComponentDefaults
    colors: ComponentColors = ComponentDefaults.componentColors(),
    cornerRadius: Dp = ComponentDefaults.CornerRadius,
    // 5. 动效参数：会动的组件必须有，值来自 OxygenMotionDefaults
    motion: OxygenMotionSpec = ComponentDefaults.Motion,
    // 6. 交互源（需要观察按压/焦点时）
    interactionSource: MutableInteractionSource? = null,
    // 7. content lambda 永远最后
    content: @Composable () -> Unit,
)
```

- 无 `content` 的组件按同样顺序截尾；不需要的段直接省略，**不改变相对顺序**。
- 单个 `modifier` 参数，禁止 `contentModifier` / `buttonModifier` 这类多 Modifier 参数。
- 不暴露 `internal` 类型、平台类型或 Compose 实现细节到公开签名。

### 1.1 文案一律走槽位，不提供 `String` 便捷重载

需要承载文案的参数（按钮内容、chip 标签、页签文案、顶栏标题 / 副标题）必须是 `@Composable` 槽位，
**不并存 `text: String` 重载**：两套 API 会让「要不要多套一个图标 / 加载态」变成换 API 的成本，
而槽位本身对最简用法没有额外代价。

```kotlin
Button(onClick = save) { Text("保存") }                       // 槽位：内容色与 label.medium 已下发
Button(onClick = save) { Icon(OxygenIcons.Check, null); Text("保存") }
Chip(onClick = pick, label = { Text("图片") })
Tab(selected = index == 0, onClick = { index = 0 }, text = { Text("推荐") })
TopAppBar(title = { Text("设置") }, subtitle = { Text("上海, 19:04") })
```

槽位内必须下发两样东西，槽位里的裸 `Text` 才是正确外观：

| CompositionLocal | 组件负责的值 | 消费者 |
| --- | --- | --- |
| `LocalContentColor` | 该槽位的内容色（选中 / 禁用 / 强调色已解析完） | `Text` / `Icon` / `IconButton` |
| `LocalTextStyle` | 该槽位的排版档位（`labelMedium` / `appbarTitle` / …） | `Text`（`style` 的默认值） |

`Text` 自己的解析顺序是「显式 `style` → `LocalTextStyle` → `type.body.medium`」，
「显式 `color` → `LocalContentColor` → `onSurface`」；容器想批量改默认排版时用
`ProvideTextStyle(value) { … }`（按 M3 口径 `merge`，只覆盖显式声明过的属性）。

**例外：行模板类组件参数用 `String`。** `SettingsItem` / `SettingsSwitchItem` /
`SettingsSplitItem` / `SettingsRadioItem` / `SettingsCheckItem` 的 `title` / `summary` /
`value` 保持 `String`——它们的输入是一行数据（列表直接 map 出来），不是版面；把标题改成
槽位只会逼每个调用点写 `title = { Text(…) }`。这类组件已经用 `leading` / `trailing` 槽位
承接「行尾要放自定义内容」的需求，文案本身不需要。

**例外 2：状态回调可空 = 纯展示。** 交互组件用「回调为空」表达「只展示、不接收交互」，
不另开同名重载（`Switch(checked, onCheckedChange = null, pressed = …)`、
`Checkbox(checked, onCheckedChange = null)`、`RadioButton(selected, onClick = null)`）。
好处是误用会直接编译失败，而不是静默命中另一个重载；纯展示态必须显式传 `null`。

### 1.2 触摸态的开关

按压反馈一律走 `IndicationDefaults.ripple(bounded, radius)`，它读 `LocalRippleEnabled`：

```kotlin
CompositionLocalProvider(LocalRippleEnabled provides false) {
    // 父级已经有涟漪的行 / 卡片里，避免子组件再叠一层
}
```

关闭时 `ripple(...)` 返回 `null`，`clickable` / `toggleable` / `selectable` / `indication`
都接受空值，即「没有触摸态」。组件不得自行构造其他 indication。

## 2. Defaults 与 Colors

```kotlin
object ButtonDefaults {
    // 主题相关默认值一律用 @Composable get()，不要在 object 初始化时静态读取主题
    val MinWidth: Dp
        @Composable get() = OxygenTheme.spacing.controlMinWidth
    val CornerRadius: Dp
        @Composable get() = OxygenTheme.shapes.cornerButton
    @Composable
    fun buttonColors(                          // 颜色工厂必须是 @Composable
        color: Color = OxygenTheme.colorScheme.primary,
        disabledColor: Color = OxygenTheme.colorScheme.surfaceDisabled,
    ): ButtonColors = remember(color, disabledColor) {
        ButtonColors(color = color, disabledColor = disabledColor)
    }
}

@Immutable
data class ButtonColors(
    val color: Color,
    val disabledColor: Color,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}
```

- 不依赖主题的固定常量用普通 `val`，且值必须来自生成令牌；主题相关默认值用 `@Composable get()` 或 `@Composable` 工厂 + `remember(keys)`，不要在 `remember {}` 里缓存会随主题变化的值。
- 颜色/样式数据类用 `@Immutable`（全 `val`、无 lambda、创建后永不变化）；含 lambda / 回调 / `MutableState` 的类用 `@Stable`。
- 状态到颜色的解析优先放在 Colors 类的 `@Stable internal fun`，避免组件主体里散落三元表达式。

## 3. 状态归属

- **无状态优先**：组件展示调用方传入的状态，通过回调请求变更；不在内部偷偷持有权威状态。
- 需要复杂内部状态（拖动、展开、动画时钟）时，提供 `XxxState` 接口 + `rememberXxxState(...)`；状态类可保存时用 `Saver`。
- 受控 / 非受控若同时支持，必须写清优先级与冲突行为，并在 KDoc 中给出两种用法。
- 动画状态由组件或 state holder 独占，调用方只通过 `motion` 覆盖表现（见 [motion.md](./motion.md)）。
- 与滚动联动的组件（如大顶栏）提供 `XxxState` + `rememberXxxState`：state 只暴露只读进度，嵌套滚动连接由容器（`Scaffold` 的 `topBarState`）注入内容区；组件不得自行接管内容滚动，也不允许在组件内部偷偷持有调用方不可见的滚动进度。

## 4. 交互、语义与无障碍

- `enabled = false` 时：不响应点击/拖动、使用禁用色令牌、仍能通过 `semantics` 声明状态；不得只变颜色而仍可交互。
- 正确设置 `Role`（Button / Switch / Checkbox / RadioButton / Tab…）、`stateDescription`、`contentDescription`；`onClick = null` 的非交互形态也要有语义声明。
- 触觉反馈在**点击回调内**触发（长按/开关同理），不在状态变化中触发，避免程序化赋值产生振动；类型与强度走令牌并尊重 capability。
- 按压视觉来自统一的 `Indication` 与 `interactionSource`，不要每个组件自绘一套涟漪。
- **RTL**：方向性图标用 `LocalLayoutDirection` + `graphicsLayer { scaleX = … }` 镜像（`GraphicsLayerScope` 不提供 layoutDirection）；位移、手势方向同理。
- 通过键盘/焦点可达，焦点态有可见样式；文本支持系统字体缩放，触摸目标 ≥ 48dp（视觉尺寸可更小，扩展点击区域）。

## 5. 令牌使用

| 要表达的东西 | 只能来自 |
| --- | --- |
| 颜色 / 状态层 | `OxygenTheme.colorScheme.*` |
| 文本样式 | `OxygenTheme.typography.*` |
| 圆角 / 形状 | `OxygenTheme.shapes.*` |
| 间距 / 尺寸 / 模糊 | `OxygenTheme.spacing.*` / `effect.*`（层级不用阴影，见 design-tokens §6） |
| 时长 / 曲线 / 弹簧 / 编排 | `OxygenMotionDefaults.*` / `motion` 参数 |
| 图标 | `oxygenx-icons` + `icon.*` 令牌 |

禁止硬编码任何上述字面量。需要新值 → 先改 `design/tokens/`（见 [design-tokens.md](./design-tokens.md)）。

## 6. Kotlin / Compose 模式

- `remember(keys)` 用于派生值；key 只包含真正参与计算的值。
- `rememberUpdatedState` **只**用于长期闭包读取最新值（`LaunchedEffect` / `DisposableEffect` 体内、`remember {}` 缓存的 lambda 如包装后的 `Modifier.clickable`）。直接把回调转发给子组件时**不需要**，Compose 的跳过机制会处理。
- `LaunchedEffect` 的 key 只含 effect 体内真正读取的值；不要在 effect 里做 I/O 后再持有锁。
- `@NonRestartableComposable` 仅用于完全委托、自身不读状态的薄包装；内部有多处状态读取的组件不要加（会失去智能重组）。
- `@Immutable` 只给真正不可变的类；含 lambda 字段会破坏其契约，改用 `@Stable`。普通 `List` / `Set` / `Map` 对 Compose 不稳定，公开签名优先用 `kotlinx.collections.immutable` 的 `ImmutableList` / `PersistentList`；用 `@Immutable` 包装裸集合并不能自动变稳定。
- 动画帧只写 layer 属性，使用 `graphicsLayer { }` 的 lambda 形式；避免在动画路径上触发重组 / 重新测量。
- 不要替换已验证的自定义 `Layout` / 测量实现；如必须替换，先补齐溢出、RTL、大字体与 intrinsic 的回归基线。

## 7. KDoc 模板

每个公开组件必须带 KDoc：

```kotlin
/**
 * <一句话说明用途与适用场景>。
 *
 * - Spec ID: `CMP-BUTTON`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-BUTTON/`
 * - Motion: `MOT-…` 规格 ID（由弹簧 / 补间驱动状态过渡时才写，并在参数里暴露 [motion] 覆盖）；没有交互动效的组件写「无」。
 * - Status: `draft | in-review | verified`（与证据等级一致）
 *
 * @param onClick ...
 * @param modifier ...
 * @param motion ...
 */
```

`Spec ID`、`Baseline`、`Motion`、`Status` 四项缺一不可；`verified` 只能出现在门禁全部通过后（见 [workflow.md](./workflow.md)）。

## 8. 平台与能力

- `commonMain` 优先；`expect/actual` 只用于真实平台差异（渲染效果、触觉、系统窗口、刷新率），不为「以后可能要用」提前开平台分支。
- 平台差异通过 `LocalOxygenCapabilities` 查询并降级，禁止在组件里散落平台判断、禁止在 commonMain 直接调用 `java.*` / Android / Skiko 专属 API。
- 降级行为必须写进组件 KDoc 与 catalog 演示；不得静默退化成「什么都不做」。

## 9. 文件组织

- 主 Composable、私有辅助 Composable、`Defaults`、`Colors` 放**同一个文件**；`internal/` 只放跨文件复用的实现。
- 文件名与主组件名一致（`Button.kt`）；一个文件一个公开组件（重载除外）。
- 所有 `.kt` 带 license header（见 `AGENT.md` §5）；图标文件除外格式化规则。
- 公开 API 的破坏性变更必须提供迁移说明与 catalog 前后对照。

## 10. 测试要求

每个组件至少覆盖：默认渲染、主要参数变体、`enabled = false`、回调只触发一次、RTL、大字体；有动效的组件额外覆盖中断/反向与结束值。动画测试使用 `oxygenx-motion` 的虚拟时钟，禁止依赖真实 `delay`。

## 11. 禁止清单

- 用 Material 组件充当外观基底；公开签名暴露平台/内部类型。
- 硬编码颜色、字号、圆角、间距、时长、弹簧参数、触觉强度。
- 在状态变化时触发触觉、在动画路径上造成重组、自建无限循环且无取消。
- 用 `@Immutable` 包装含 lambda 或裸集合的类型并宣称稳定。
- 组件通过 `internal` API 被 catalog 调用；用「以后再补」的方式跳过语义、RTL 或降级分支。
