# 设计令牌规范

静态视觉值（颜色、排版、形状、间距、层级、图标）的唯一管理方式。**本规范不定义 OxygenOS 的具体数值** —— 数值必须来自可复核的 reference 并登记证据等级（见 [README.md](./README.md) §4）。

## 1. 单一真源

```
design/tokens/*.yaml          ← 唯一可编辑的真源（人工维护、需 review）
        │  ./gradlew :oxygenx-theme:generateTokens
        ▼
oxygenx-theme/src/commonMain/kotlin/io/oxygenx/theme/generated/*.kt   ← 生成物，禁止手改
        │
        ▼
OxygenTheme.colorScheme / typography / shapes / spacing / layout
```

- 生成器、模板与校验脚本放在 `build-logic/`；生成物目录加入 Spotless 排除与 `.gitattributes` 的 generated 标记。
- 生成器必须**失败即报错**：缺字段、ID 重复、非法色值、`verified` 却无 `E2+` 证据、明暗缺一，都不允许静默通过。
- 组件代码只读 `OxygenTheme.*`；**禁止**在组件里写 `Color(0xFF…)`、`16.dp`、`12.sp` 等字面量。

## 2. 令牌 ID 与分类

ID 格式 `<category>.<group>.<name>`，全小写点分；一旦发布不重命名（弃用走 `deprecated: true` + `replacedBy`）。

| 分类 | 前缀 | 示例语义 |
| --- | --- | --- |
| 颜色 | `color.` | `color.surface.container`、`color.primary`、`color.on.primary`、`color.state.pressed` |
| 排版 | `type.` | `type.display.large`、`type.body.medium` |
| 形状 | `shape.` | `shape.corner.card`、`shape.corner.sheet` |
| 间距 | `space.` | `space.gutter`、`space.gap.small`、`space.card.padding` |
| 组件尺寸 | `size.` | `size.appbar.height`、`size.switch.track.width`、`size.divider.thickness` |
| 效果 | `effect.` | `effect.refresh.drag.scale` |
| 图标 | `icon.` | `icon.size.small`、`icon.size.medium` |

> 与早期草案的差异：间距（`space.`）与组件尺寸（`size.`）拆成两类。P3 起组件需要大量控件尺寸（开关轨道、滑块拇指、顶栏高度），继续挂在 `space.` 下会让「页面留白」与「控件尺寸」两种语义混在同一个前缀里。

颜色按**语义角色**命名，不用色值命名（禁止 `red500`）；同一角色必须同时给 `light` / `dark` 值。

颜色角色在 P3 阶段统一向 M3 对齐：令牌 ID 的点分段按 camelCase 拼接，就是 `OxygenTheme.colorScheme` 的字段名，也就是 M3 `ColorScheme` 的角色名（`color.on.surface.variant` → `colorScheme.onSurfaceVariant`）。P3 期间这些令牌全是 `draft`、库尚未发布，按「未发布」处理，改名不走 `deprecated` + `replacedBy` 流程：

| 令牌 ID | `colorScheme` 字段 / M3 角色 | 说明 |
| --- | --- | --- |
| `color.primary` | `primary` | 一加红主题色、`overridable`（原名 `color.accent.primary`） |
| `color.on.primary` | `onPrimary` | 主色之上的内容（原名 `color.content.on.accent`） |
| `color.primary.container` / `color.on.primary.container` | `primaryContainer` / `onPrimaryContainer` | 主色浅底与浅底上的内容；**M3 桥接角色**，本仓库组件暂时没有消费者（见 §2.3） |
| `color.inverse.primary` | `inversePrimary` | 反白容器里的主色（取另一侧明暗的 `primary`）；M3 桥接角色 |
| `color.secondary` | `secondary` | 次要强调色，取页面墨色而非第二品牌色；次级按钮、二级页签，`overridable` |
| `color.on.secondary` | `onSecondary` | 次要强调色之上的内容 |
| `color.secondary.container` / `color.on.secondary.container` | `secondaryContainer` / `onSecondaryContainer` | 次要强调浅底与浅底上的文字；页签按压底，后续 chip / tonal 按钮 |
| `color.surface` | `surface` | 页面背景（原名 `color.surface.background`） |
| `color.surface.container` | `surfaceContainer` | 卡片与容器背景 |
| `color.surface.container.low` / `.high` / `.lowest` | `surfaceContainerLow` / `surfaceContainerHigh` / `surfaceContainerLowest` | 容器层级三档（M3 桥接角色）：页面内分区底、浮层底、最贴近底层的白 / 黑 |
| `color.surface.container.highest` | `surfaceContainerHighest` | 未激活轨道 / 容器最高层：分段指示器底轨、滑块/进度空轨、开关关闭态轨道共用；12% 中性叠加，与 8% 的 `outlineVariant` 分割线是两个真机值，不合并（原名 `color.track.inactive`） |
| `color.surface.bright` / `color.surface.dim` | `surfaceBright` / `surfaceDim` | 亮侧 / 暗侧表面（M3 桥接角色）：比 `surface` 更亮或更暗一档的容器 |
| `color.on.surface` | `onSurface` | 正文文字色，**不是**主色（原名 `color.content.primary`） |
| `color.on.surface.variant` | `onSurfaceVariant` | 次要文字色（原名 `color.content.secondary`） |
| `color.outline` | `outline` | 控件描边（原名 `color.control.border`） |
| `color.outline.variant` | `outlineVariant` | 分割线、禁用态指示线（原名 `color.divider`） |
| `color.inverse.surface` / `color.inverse.on.surface` | `inverseSurface` / `inverseOnSurface` | 反白容器与容器之上的内容 |
| `color.error` | `error` | 错误态文字与指示线 |
| `color.on.error` / `color.error.container` / `color.on.error.container` | `onError` / `errorContainer` / `onErrorContainer` | 错误色之上的内容与错误浅底（M3 桥接角色）：表单错误摘要、错误提示条 |

约定：`colorScheme` 字段一律直接用 M3 角色名，组件里写 `OxygenTheme.colorScheme.onSurface`，不额外加 `content.` / `on.content.` 之类的前缀；画底色用容器角色（`surface` / `surfaceContainer` / `surfaceContainerHighest` / `secondaryContainer`），写在它上面的内容用对应的 `on*` 角色，不要混用。**M3 复用优先于新建令牌**：占位文字、顶栏副标题、未选中页签文字都用 `onSurfaceVariant`，不再单设 `color.input.placeholder`。

开关开启态也不单设色令牌：M3 由 `primary` 派生，而一加真机的画法与 M3 默认相反（轨道是 `primary` @16% 浅 / @35% 深，拇指是实心 `primary`；M3 默认是实心轨道 + `onPrimary` 拇指）。`color.switch.track.on` 只保存 E3 的 alpha 基线，`lightColorScheme` / `darkColorScheme` 用当前 `primary` 的色相与之合成 `switchTrackOn`，`switchThumbOn` 直接取 `primary`，因此动态取色时开关会跟随主色而不是烧死在一加红上。

**扩展角色**（M3 没有对应语义，按同一套平铺命名风格登记，字段名同样是 ID 段的 camelCase）：

| 令牌 ID | `colorScheme` 字段 | 说明 |
| --- | --- | --- |
| `color.surface.disabled` | `surfaceDisabled` | 禁用态容器与轨道（M3 用 `onSurface` 降透明度表达，这里独立成令牌） |
| `color.on.surface.disabled` | `onSurfaceDisabled` | 禁用态文字与图标（原名 `color.content.disabled`） |
| `color.input.indicator.unfocused` | `inputIndicatorUnfocused` | 未聚焦指示线（真机 12% 中性；M3 对应 outlined 输入框的 `outline` 用途，因取值不同暂独立） |
| `color.state.pressed` | `statePressed` | 统一按压叠加层 |
| `color.switch.track.on` | `switchTrackOn`（由 `primary` 派生，只取 alpha） | 开关开启态轨道的 E3 叠加比例 16% / 35%，RGB 仅是证据基线 |
| `color.switch.thumb.off` / `color.switch.track.disabled` / `color.switch.thumb.disabled` | `switchThumbOff` / `switchTrackDisabled` / `switchThumbDisabled` | 开关关闭 / 禁用三色（E3 真值，与 primary 无关） |
| `color.control.dot` | `controlDot` | 单选内点 / 复选对勾（深浅色都为白，与 `onPrimary` 深色取墨不同） |

**尚未引入的 M3 角色**（等对应组件落地再加，避免登记成没有消费者的近义令牌）：

| M3 角色 | 现状 | 等待的组件 |
| --- | --- | --- |
| `background` / `onBackground` | 刻意不引入 | 页面底一律用 `surface`，不保留第二套「背景」角色（映射层把 M3 的 background 丢给 surface） |
| `surfaceVariant` | `color.on.surface.variant` / `color.surface.container.highest` / `color.outline.variant` 分摊了它的用法 | 容器类组件（卡片内分区底） |
| `surfaceTint` | 刻意不引入 | 不做 M3 的色调叠加：层级靠 `color.surface.container` 档位与 `color.outline.variant` 细边框表达，不用阴影也不用色调 |
| `tertiary` 系列 | 刻意不引入 | 没有第三强调色需求（映射层显式丢弃，不做近似） |

### 2.3 M3 桥接（`:oxygenx-material3`）

外部 App 往往已经有一整套 M3 `ColorScheme`（material-kolor 由种子色生成、AMOLED、高对比度、多套预设）。为了不在边界上要求对方重写取色算法，单独开一个桥接模块：

```
androidx.compose.material3.ColorScheme  ──toOxygenColorScheme()──▶  OxygenColorScheme
                                                                     │
                                                              OxygenTheme(colorScheme = …)
```

- **只有这个模块**可以依赖 Material 3（`compose.material3`），其余模块仍然只用 `runtime` / `ui` / `foundation`；它是「把外部色板搬进来」，不是「用 Material 组件充当外观」。
- 三类处理：**直连**（语义一致的 28 个角色原值搬运）/ **合成**（M3 没有的 9 个扩展角色按 `onSurface`、`primary` 叠本仓库 E0 令牌的透明度）/ **丢弃**（`tertiary*`、`background`、`surfaceVariant`、`surfaceTint`，显式设计不是遗漏）。
- 明暗不靠外部标记：M3 的 `ColorScheme` 不携带 `isDark`，桥接按 `surface` 亮度（`luminance() < 0.5`）推断，顺带覆盖 AMOLED / 高对比度色板。
- 反向（Oxygen → M3）不提供：本仓库角色集是 M3 的子集 + 扩展项，反向映射会丢信息。
- catalog 的「关于 → 环境 / 主题」里有「M3 配色（桥接）」开关，打开后整个 catalog 用 Material 3 基线色板映射出来的色板，用来肉眼核对角色有没有漏。

## 3. YAML Schema

字段约定（结构示意，所有取值均为**占位**，不是已验证的 OxygenOS 数值）：

```yaml
# design/tokens/color.yaml
tokens:
  - id: color.surface.container
    kind: color
    light: 0xFFF5F5F5
    dark: 0xFF1B1B1F
    baseline: OxygenOS <version> / <device> / <date>
    source: catalog/reference/<spec-id>/frames/...
    evidence: E2                 # E0 | E1 | E2 | E3
    status: draft                # draft | in-review | verified
    description: 卡片与容器背景
  - id: color.primary
    kind: color
    light: 0xFF0A59F7
    dark: 0xFF7AA7FF
    overridable: true            # 允许强调色覆盖
    baseline: OxygenOS <version> / <device> / <date>
    source: catalog/reference/<spec-id>/frames/...
    evidence: E0
    status: draft
```

排版令牌（`design/tokens/type.yaml`，五个字段缺一不可）：

```yaml
tokens:
  - id: type.body.medium
    kind: typography
    size: 14              # sp
    lineHeight: 20         # sp
    weight: 400            # 1..1000
    letterSpacing: 0       # sp
    family: sans-serif     # 只允许通用字族
    baseline: OxygenOS <version> / <device> / <date>
    source: catalog/reference/README.md
    evidence: E0
    status: draft
```

尺寸令牌的 `unit` 只写 `dp`；比例类令牌（例如大顶栏展开高度占可用屏高的比例）写 `unit: ratio`，生成后由 `OxygenTheme.layout` 暴露，不与 `OxygenSpacing` 的 Dp 字段混放。

**格式约束**（P1 起由零依赖生成器解析，不引入 YAML 库）：只支持扁平 `key: value`；以 `- ` 开头表示新令牌，后续缩进行属于同一令牌；值中不得出现 `: `；`#` 开头为注释；颜色写 `0xAARRGGBB`。需要嵌套结构时拆成独立令牌并用 ID 引用。

必填：`id`、`kind`、取值字段、`baseline`、`source`、`evidence`、`status`。`status: verified` 要求 `evidence >= E2`、`baseline` 无占位符且 `source` 指向真实存在的凭据路径；生成器在 `:oxygenx-theme:generateTokens` 中直接失败报错，CI 再校验生成物无 diff。

动效令牌同样放 `design/tokens/motion.yaml`，字段与校验见 [motion.md](./motion.md)；两类令牌共用同一生成流水线，避免双真源。

## 4. 颜色

- 通过 `OxygenTheme.colorScheme.*` 读取；主题对象用 `CompositionLocal` 提供，支持运行时切换明暗与强调色。
- **状态层**（hover / pressed / focused / selected / disabled）用透明度令牌叠加强调色，不允许为每个组件单独配一套状态色。
- 禁用态由「禁用色令牌 + 内容色令牌」共同表达，不允许用整块 `alpha` 一刀切。
- 文本/背景对比度须满足 WCAG AA（正文 4.5:1、大字 3:1）；生成器对已登记的配对做静态校验， catalog 提供对比度检查页。
- 强调色可覆盖（`overridable: true`），但覆盖后仍须通过上述对比度校验；不允许覆盖破坏语义角色。

## 5. 排版

- 每个 `type.*` 令牌包含：字族、字重、字号、行高、字距；缺一不可，避免组件里临时拼 `TextStyle(...)`。生成器把它展开为 `<NAME>_SIZE` / `_LINE_HEIGHT` / `_WEIGHT` / `_LETTER_SPACING` / `_FAMILY` 五个常量，`OxygenTheme.typography` 再组装成 `TextStyle`。
- `family` 只接受通用字族（`sans-serif` / `serif` / `monospace`），生成器直接拒绝其他取值，避免把厂商专有字体写进令牌。
- 一律使用可缩放单位（`sp`），禁止固定像素高度的文本容器；组件必须在大字体（≥ 1.5×）下不截断、不重叠。
- 字族只使用自有或宽松许可字体，并在 `NOTICE` 登记；不得引用系统专有字体文件。
- 文本样式一律 `OxygenTheme.typography.*`，不直接用 `MaterialTheme.typography` / `LocalTextStyle` 的隐式默认。

## 6. 形状、间距、层级

- 圆角用 `RoundedCornerShape` / 连续圆角（squircle，若能力可用）；裁剪与描边必须使用**同一**半径令牌，避免描边错位。
- 需要真实 `Shape` 值的地方（裁剪、Surface 参数）用形状令牌；需要 `Modifier` 级绘制的场合用统一封装，禁止各组件自己写 `drawRoundRect`。
- 栅格单位、间距、最小内边距全部来自 `space.*`；组件不得写死数值。
- 触摸目标不小于 48dp（无障碍下限，非复刻值）；视觉尺寸可以小于触摸目标，用 `Modifier` 扩展点击区域。
- **层级不用阴影表达**：容器类组件是「容器底色 + 一圈细边框」，边框颜色 `color.outline.variant`、厚度 `size.divider.thickness`，与顶栏分割线同一套视觉；卡片的原 `elevation.card`（唯一一个 `elevation.` 令牌）因此删除，`OxygenTheme.elevation` 整个分组不再存在。模糊半径、下拉阻尼等来自 `effect.*`；按压反馈只有水波纹，没有按压缩放（原 `effect.press.scale` 已删）。平台不支持时按 [component-api.md](./component-api.md) 的 capability 规则降级。

## 7. 图标

- 图标一律 `ImageVector`，一个图标一个文件，按用途分两个模块：
  - `oxygenx-icons/`：精选图标（Material Symbols **Rounded**，`wght=300`），只放组件自身固定需要的少量图标，经 `OxygenIcons.X` 暴露。
  - `oxygenx-icons-material/`：常用图标全集（Material Symbols **Outlined**，`wght=300`，按使用量取前 100），经 `MaterialSymbols.X` 暴露，与前者不共享符号名，消费方按需依赖。
  - 两者都是 Apache-2.0 的 Google Material Symbols，生成文件自带来源 URL 与版权头；不搬运专有图标资源。两套样式不同（Rounded / Outlined），同一页面不要混用。
- 尺寸与默认着色取自 `icon.*` 与颜色令牌；方向性图标必须支持 RTL 镜像（见 component-api.md）。生成的是填充轮廓路径，没有描边宽度这回事。
- 图标文件（`**/icon/**/*.kt`、`**/generated/**/*.kt`）不参与 Spotless 格式化；其余约定（license header、命名）照常适用。

## 8. 使用规则

- 组件内读取主题值必须发生在 composition 中（`@Composable get()` 或 `Defaults` 的 `@Composable` 工厂），不要在 `remember {}` 里缓存一个会随主题变化的 `Color`。
- `Defaults` 中的常量尺寸用 `val`，主题相关默认值用 `@Composable` 工厂 + `remember(keys)`，与 [component-api.md](./component-api.md) 一致。
- 生成代码只暴露只读令牌对象；运行时不允许写回令牌。

## 9. 新增 / 修改令牌

1. 先确认现有令牌无法覆盖（能复用就复用，禁止近义重复）。
2. 在 YAML 登记 ID、双主题值、`baseline`、`source`、`evidence`、`status`；无凭据时标 `E0` + `TODO(E0)`，并保持 `draft`。
3. 运行 `generateTokens` 与 `spotlessCheck`；生成物随源码一起提交。
4. 在 catalog 的令牌总览页确认明暗、强调色覆盖、对比度三项表现。
5. 若改动影响既有组件外观，同步更新受影响组件的 golden 截图（见 [catalog.md](./catalog.md)）。

## 10. 禁止清单

- 组件内硬编码任何视觉字面量；绕过 `OxygenTheme` 直接读平台资源。
- 手改 `generated/`、在生成器里写死设计值、用代码注释当令牌真源。
- 把 `E0/E1` 数值标为 `verified`，或把公开宣传术语当作数值来源。
- 只改亮色或只改暗色；新增语义色却缺少状态层定义。
- 引入无法确认许可的字体、图标或图片。
