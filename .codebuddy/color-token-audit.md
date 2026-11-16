# 颜色令牌对比清单（2026-11-15）

真源 `design/tokens/color.yaml` → `GeneratedTokens.kt` → `OxygenColorScheme`（`ColorScheme.kt`）。

> **2026-11-15 已按下面的建议实施**。§1–§5 与附录保留为**实施前快照**，便于对照取舍；当前状态以本节与 `design/tokens/color.yaml` 为准。

## 0. 实施结果（令牌 121 → 118，颜色 28 → 25）

1. **删 `color.input.placeholder`**：与 `onSurfaceVariant` 完全同值且仅 1 个消费者，`OxygenTextFieldDefaults.placeholderStyle()` 改用 `onSurfaceVariant`，占位文字按 M3 复用次要文字角色。
2. **`color.track.inactive` → `color.surface.container.highest`**（M3 `surfaceContainerHighest`）：分段指示器底轨、滑块空轨、进度空轨、开关关闭态轨道共用。底轨从 `onSurfaceDisabled`（`#A5A5AE` 实色）改为 12% 中性叠加，是本次唯一可见变化（reference 无底轨取色证据，待真机复核）。
3. **开关 on 态跟随 `primary`**：删 `color.switch.thumb.on`（与 primary 纯重复）、`color.switch.track.off`（并入 `surfaceContainerHighest`）；保留 `color.switch.track.on` 作为 E3 的 16%/35% alpha 基线，`lightColorScheme` / `darkColorScheme` 用当前 primary 色相合成 `switchTrackOn`，`switchThumbOn` 直接取 primary。默认主题下渲染值与改动前**逐位相同**，但动态取色时不再脱钩。
4. **顶栏细线不动**：`outlineVariant`（8% / 10%，实测 235）与 `surfaceContainerHighest`（12% / 12%，本项目 225 / 真机 `#DDDDDD`）按两条独立真机资源保留，不合并。
5. **保留三个零消费者的 M3 角色**：`inverseSurface` / `inverseOnSurface` / `onSecondaryContainer` —— 它们本身就是 M3 角色，按「贴近 M3」保留等 Snackbar / chip 落地，不按 §9.1 删。（2026-11-15 补：CMP-CHIP 落地后 `secondaryContainer` / `onSecondaryContainer` 已由 `OxygenChipDefaults.tonalColors()` 消费，只剩 `inverseSurface` / `inverseOnSurface` 待 Snackbar。）

### 当前 25 个颜色令牌

| 令牌 ID | light | dark | 用途 |
| --- | --- | --- | --- |
| `color.surface` | `0xFFFAFAFA` | `0xFF101012` | 页面背景 |
| `color.surface.container` | `0xFFFFFFFF` | `0xFF1C1C1F` | 卡片与容器背景 |
| `color.surface.container.highest` | `0x1E000000` | `0x1FFFFFFF` | 未激活轨道（M3 `surfaceContainerHighest`） |
| `color.on.surface` | `0xFF18181B` | `0xFFF4F4F5` | 正文、选中段 |
| `color.on.surface.variant` | `0xFF6B6B73` | `0xFFA1A1AA` | 次要文字：占位文字、副标题、未选中页签 |
| `color.primary` | `0xFFC41442` | `0xFFFF6B85` | 一加红主题色、`overridable` |
| `color.on.primary` | `0xFFFFFFFF` | `0xFF101012` | 主色之上的内容 |
| `color.secondary` | `0xFF18181B` | `0xFFF4F4F5` | 次要强调色、`overridable` |
| `color.on.secondary` | `0xFFFAFAFA` | `0xFF101012` | 次要强调色之上的内容 |
| `color.secondary.container` | `0x14000000` | `0x1FFFFFFF` | 次要强调浅底 |
| `color.on.secondary.container` | `0xFF18181B` | `0xFFF4F4F5` | 浅底上的文字（暂无消费者） |
| `color.inverse.surface` | `0xFF18181B` | `0xFFF4F4F5` | 反白容器（暂无消费者） |
| `color.inverse.on.surface` | `0xFFFAFAFA` | `0xFF101012` | 反白容器上的内容（暂无消费者） |
| `color.outline` | `0xFF969696` | `0x66FFFFFF` | 控件描边 |
| `color.outline.variant` | `0x14000000` | `0x19FFFFFF` | 分割线、顶栏细线 |
| `color.error` | `0xFFB3261E` | `0xFFF2B8B5` | 错误态 |
| `color.surface.disabled` | `0xFFEAEAEF` | `0xFF2A2A2F` | 禁用容器与轨道 |
| `color.on.surface.disabled` | `0xFFA5A5AE` | `0xFF6C6C75` | 禁用文字与图标 |
| `color.switch.track.on` | `0x29C41442` | `0x59FF6B85` | 只取 alpha（16% / 35%），运行时与 primary 合成 |
| `color.switch.thumb.off` | `0x2D000000` | `0x4CFFFFFF` | 开关关闭态拇指 |
| `color.switch.track.disabled` | `0x0A000000` | `0x14FFFFFF` | 开关禁用态轨道 |
| `color.switch.thumb.disabled` | `0x0C000000` | `0x1EFFFFFF` | 开关禁用态拇指 |
| `color.state.pressed` | `0x1F000000` | `0x29FFFFFF` | 按压状态层 |
| `color.input.indicator.unfocused` | `0x1F000000` | `0x1FFFFFFF` | 输入框未聚焦指示线 |
| `color.control.dot` | `0xFFFFFFFF` | `0xFFFFFFFF` | 单选内点 / 复选对勾 |

`generateTokens`（118 个令牌）→ `spotlessCheck` → `compileKotlinDesktop` → `check` 全通过。

## 1. `trackInactive` / `inputPlaceholder` 能否并入 `outlineVariant`？

| 字段 | light | dark | 证据 | 用途 |
| --- | --- | --- | --- | --- |
| `outlineVariant` | `0x14000000` 8% 黑 | `0x19FFFFFF` 10% 白 | E3 `op_control_divider_color` | 行间分割线、顶栏细线 |
| `trackInactive` | `0x1E000000` 12% 黑 | `0x1FFFFFFF` 12% 白 | E3 `op_seek_bar_disabled_color_light` | 滑块 / 进度条空轨 |
| `inputPlaceholder` | `0xFF6B6B73` 实色 | `0xFFA1A1AA` 实色 | E0 | 输入框占位文字 |

**`inputPlaceholder` 不能**：它是保证 4.5:1 对比度的不透明文字色，`outlineVariant` 是 8% 半透明叠加层；拿后者画文字会几乎看不见。真正与它**完全同值**的是 `onSurfaceVariant`（`0xFF6B6B73` / `0xFFA1A1AA`），规范里也写明「取值同 `color.on.surface.variant`」。M3 里占位文字本来就复用 `onSurfaceVariant`；当前 `inputPlaceholder` 只有 1 个消费者（`TextField:338`），独立成令牌的理由是「聚焦/错误态能单独调」，但这个分叉至今没发生。若要精简，删它并让 `TextField` 直接用 `onSurfaceVariant` 是零渲染变化的一刀。

**`trackInactive` 不建议**：色值差 12% vs 8%（叠白底约 `#E0E0E0` vs `#EBEBEB`，差 11 级灰），而且来自两条独立的真机资源。按 M3 语义也不同：M3 的 inactive track 属于 `surfaceContainerHighest` 容器层级，`outlineVariant` 属于描边。

**真正接近重复的是「12% 中性叠加」这一组**：`switchTrackOff`、`inputIndicatorUnfocused` 都是 `0x1F000000` / `0x1FFFFFFF`；`trackInactive` 浅色 `0x1E000000` 只差 1/255 alpha；`statePressed` 浅色同值（深色是 `0x29FFFFFF` 16% 白）。要合并应该合并这一组，而不是并进 `outlineVariant`。

## 2. `switch` 应该是 `primary` 吧？

色值上**一半是**，而且这里有个真实缺陷：

- `switchThumbOn` = `0xFFC41442` / `0xFFFF6B85` = `primary`，完全同值 ✅
- `switchTrackOn` = `0x29C41442` / `0x59FF6B85` = primary @16% 浅 / @35% 深
- `switchTrackOff` / `switchThumbOff` / `switch*Disabled` 与 primary 无关（中性灰叠加）

但 `primary` 是 `overridable: true`，而开关这几个是**烧死在 YAML 里的 #C41442 派生值**。调用方 `lightColorScheme(primary = 蓝色)` 时，按钮 / 滑块 / 进度条 / 勾选框 / 页签全变蓝，唯独开关还是红色。这是当前最该修的不一致。

另外一加的开关与 M3 默认**相反**：M3 是「轨道实心 primary + 拇指 onPrimary 白」，真机（E3 资源）是「轨道 primary@16% + 拇指 primary 实心」，所以不能套 `SwitchDefaults.colors()`。可行做法是让 `OxygenSwitchDefaults.colors()`（Switch.kt:260-268）的默认值从主题派生：

```kotlin
trackOn = OxygenTheme.colorScheme.primary.copy(alpha = if (OxygenTheme.colorScheme.isDark) 0.35f else 0.16f)
thumbOn = OxygenTheme.colorScheme.primary
```

这样开关跟随动态取色，`switchTrackOn` / `switchThumbOn` 两个令牌可退成证据基线（或把 alpha 抽成 `effect.*` 令牌）；关闭/禁用态保持独立。

## 3. 当前同值、可讨论合并的组

| 色值 light / dark | 字段 | 备注 |
| --- | --- | --- |
| `0xFF18181B` / `0xFFF4F4F5` | `onSurface`、`secondary`、`onSecondaryContainer`、`inverseSurface` | 后两个**无消费者** |
| `0xFFFAFAFA` / `0xFF101012` | `surface`、`onSecondary`、`inverseOnSurface` | 最后一个**无消费者** |
| `0xFF6B6B73` / `0xFFA1A1AA` | `onSurfaceVariant`、`inputPlaceholder` | 见 §1 |
| `0xFFC41442` / `0xFFFF6B85` | `primary`、`switchThumbOn` | 见 §2 |
| `0x1F000000` / `0x1FFFFFFF` | `switchTrackOff`、`inputIndicatorUnfocused`（`trackInactive` 浅色仅差 1/255） | 12% 中性层 |
| `0x14000000` | `outlineVariant`、`secondaryContainer`（dark 不同：10% vs 12% 白） | 浅色巧合同值 |

**三个完全没有消费者的令牌**：`onSecondaryContainer`、`inverseSurface`、`inverseOnSurface`。当初是为 secondary 按钮的反白底登记的，但 `Button.kt:131-132` 现在实际用 `secondary` + `onSecondary`。按规范 §9.1「禁止近义重复」，这三个是优先清理对象。

保留独立是对的：`onPrimary`（light 白 / dark 墨）与 `controlDot`（双白）语义不同，dot 画在强调色实心块上、深色模式也必须为白；`outline` 是 40% 灰描边、`surfaceDisabled` / `onSurfaceDisabled` 是实色禁用对，都不是低透明叠加层。

## 4. 去哪里对比

catalog 目前**没有**令牌总览页或对比度检查页（规范 §4 提到但尚未实现），各组件页里的颜色都是直接读 `OxygenTheme.colorScheme.*`。要对比色值，最直接的是 `design/tokens/color.yaml`（双主题值 + 证据等级）与 `ColorScheme.kt` 的映射，再看 catalog 对应组件页（例如 Switch / Slider / TextField / TabRow / Button 各自的 `*Section.kt`）。

## 5. 横划顶栏 / 副标题 / 顶栏细线的角色归属

### 当前实际取值

| 位置 | 现在用 | 色值 | M3 应对应 |
| --- | --- | --- | --- |
| 标准顶栏副标题 `TopAppBar:154` | `onSurfaceVariant` | `#6B6B73` / `#A1A1AA` | `onSurfaceVariant` ✅ |
| 横划顶栏副标题 `PagerTopBar:318` | `onSurfaceVariant` | 同上 | `onSurfaceVariant` ✅ |
| 文字页签未选中 `TabRow:256` | `onSurfaceVariant` | 同上 | `onSurfaceVariant` ✅ |
| 分段指示器**底轨（未选中段）** `PagerTopBar:267` | `onSurfaceDisabled` | `#A5A5AE` / `#6C6C75` | M3 `surfaceContainerHighest`（项目未登记）❌ |
| 分段指示器选中段 `:274` | `onSurface` | `#18181B` / `#F4F4F5` | 项目自定（真机选中段是墨色） |
| 顶栏底部分割线 `TopAppBar:106`、`TopAppBar:270`、`PagerTopBar:166` | `outlineVariant` | 8% 黑 / 10% 白 | `outlineVariant` ✅ |
| 单选/复选未选中描边 `Checkbox:184`、`RadioButton:118` | `outline` | `#969696` / 40% 白 | `outline` ✅ |

**副标题、未选中文字、描边、分割线已经全部对上 M3**，不用改。三个角色的分工就是：文字 → `onSurfaceVariant`，边框 → `outline`，细线 → `outlineVariant`。唯一错配的是分段指示器底轨：它借用了「禁用态**文字**色」，但它其实是一条「未激活**轨道**」。

### `inputPlaceholder` 能拿给横划顶栏用吗

不建议，两个原因：

1. 色值上 `inputPlaceholder` == `onSurfaceVariant`（`#6B6B73` / `#A1A1AA`）。换到底轨上会从现在的 `#A5A5AE` **变深**，与「未选中态应该更弱」相反；而且 `onSurfaceVariant` 已经画着副标题了，底轨再用它就跟文字抢层次。
2. 语义上「输入框占位文字」令牌不该跑到顶栏。正确做法是删掉它、所有次要文字统一 `onSurfaceVariant`（与 §1 一致），而不是把它推广成通用文字色。

要让未选中段变淡，该走的是中性叠加层（`trackInactive` 12% → `#E0E0E0`，或 `outlineVariant` 8% → `#EBEBEB`），不是实色文字角色。

**推荐的一组动作**：把 `trackInactive` 正名为 M3 的 `surfaceContainerHighest`（M3 里 tab indicator track 与 slider inactive track 本来共用这个角色），让分段底轨 / 滑块空轨 / 进度空轨三处共用；同时删 `inputPlaceholder`。令牌 28 → 27，语义全对齐。代价是底轨从 `#A5A5AE` 变淡到 `#E0E0E0` 量级——`AGENT.md:107` 只记录了「当时决定用禁用色做底轨」这一实现选择，reference 里**没有**分段底轨的取色证据（E0），所以这处需要真机对照图库再确认，不宜直接照改。

### `trackInactive` 能不能和顶栏那道分割线同色

不能，真机上它们本来就是两个不同颜色，各有独立证据：

| | 资源 | alpha | 叠白底实测 |
| --- | --- | --- | --- |
| 顶栏细线 | `op_control_divider_color` | 8% 黑 / 10% 白 | **235 = `#EBEBEB`**（`AGENT.md:70`，逐像素验证 y=310） |
| 滑块空轨 | `op_seek_bar_disabled_color_light` | 12% 黑 / 12% 白 | **225**（本项目实测叠白底，`AGENT.md:72`）/ 真机取色 **`#DDDDDD` 叠 `#FAFAFA` 页面底**（`catalog/reference/README.md:27`） |

差 10~14 级灰。合并只有两种结果：

- 统一到 8%：滑块 / 进度空轨变淡，放弃 E3 资源真值；
- 统一到 12%：顶栏**常驻**细线变深、更抢眼，`AGENT.md:70` 的逐像素验证失效。

若一定要合并，优先改空轨那侧（顶栏细线的真机证据更硬）。但更推荐保留两个值、只把名字挂到 M3 角色上：`outlineVariant`（分割线）与 `surfaceContainerHighest`（未激活轨道）——语义对齐了，真机基线各自保住。

## 附录：实施前 28 个颜色令牌的完整对照表（历史快照）

位置列统计 `OxygenTheme.colorScheme.<字段>` / `colors.<字段>` 在 `oxygenx-ui` 与 `catalog/shared` 的引用；`ui/` = `oxygenx-ui/src/commonMain/kotlin/io/oxygenx/ui/`，`catalog/` = `catalog/shared/src/commonMain/kotlin/io/oxygenx/catalog/`。

| 字段 | 令牌 ID | light | dark | 消费位置 |
| --- | --- | --- | --- | --- |
| `surface` | `color.surface` | `0xFFFAFAFA` | `0xFF101012` | 6 处：ui/layout/Scaffold:70、catalog/component/ButtonSection:52、catalog/component/TopBarDemoScreen:223、catalog/scaffold/ScaffoldDemoScreen:174、catalog/scaffold/ScaffoldDemoScreen:178、catalog/CatalogPage:284 |
| `surfaceContainer` | `color.surface.container` | `0xFFFFFFFF` | `0xFF1C1C1F` | 10 处：ui/basic/Card:107、ui/basic/Dialog:187、catalog/component/PagerTopBarDemoScreen:156、catalog/component/PullRefreshDemoScreen:51、catalog/scaffold/ScaffoldDemoScreen:123、catalog/settings/SettingsScreens:67、catalog/settings/SettingsScreens:164、catalog/settings/SettingsScreens:243、catalog/settings/SettingsScreens:296、catalog/CatalogPage:181 |
| `onSurface` | `color.on.surface` | `0xFF18181B` | `0xFFF4F4F5` | 31 处：ui/basic/Icon:45、ui/basic/Slider:205、ui/basic/TextField:343、ui/basic/TextField:344、ui/basic/TextField:349、ui/navigation/PagerTopBar:138、ui/navigation/PagerTopBar:139、ui/navigation/PagerTopBar:274、ui/navigation/TabRow:255、ui/navigation/TopAppBar:144、ui/navigation/TopAppBar:232、ui/navigation/TopAppBar:233、ui/navigation/TopAppBar:500、ui/settings/SettingsItemDefaults:66、catalog/component/ButtonSection:51、catalog/component/CardSection:41、catalog/component/CardSection:50、catalog/component/DialogSection:55、catalog/component/DialogSection:111、catalog/component/PagerTopBarDemoScreen:163、catalog/component/PagerTopBarDemoScreen:210、catalog/component/ScaffoldSection:20、catalog/component/SwitchSection:86、catalog/component/TopBarDemoScreen:191、catalog/component/TopBarDemoScreen:224、catalog/scaffold/ScaffoldDemoScreen:87、catalog/scaffold/ScaffoldDemoScreen:97、catalog/scaffold/ScaffoldDemoScreen:166、catalog/ui/SettingsRow:64、catalog/ui/SmallTitle:30、catalog/CatalogPage:285 |
| `onSurfaceVariant` | `color.on.surface.variant` | `0xFF6B6B73` | `0xFFA1A1AA` | 36 处：ui/navigation/PagerTopBar:318、ui/navigation/TabRow:256、ui/navigation/TabRow:272、ui/navigation/TopAppBar:154、ui/navigation/TopAppBar:238、ui/settings/SettingsGroupTitle:38、ui/settings/SettingsItemDefaults:67、ui/settings/SettingsItemDefaults:68、catalog/component/ButtonSection:45、catalog/component/ButtonSection:57、catalog/component/ChoiceSection:89、catalog/component/ChoiceSection:198、catalog/component/DialogSection:127、catalog/component/PagerTopBarDemoScreen:169、catalog/component/PagerTopBarDemoScreen:216、catalog/component/PagerTopBarDemoScreen:229、catalog/component/ProgressSection:92、catalog/component/PullRefreshDemoScreen:81、catalog/component/SettingsItemSection:170、catalog/component/SliderSection:39、catalog/component/SliderSection:48、catalog/component/SliderSection:58、catalog/component/SwitchSection:65、catalog/component/TabRowSection:94、catalog/component/TextFieldSection:80、catalog/component/TextFieldSection:93、catalog/component/TextFieldSection:115、catalog/component/TextFieldSection:161、catalog/component/TopBarDemoScreen:172、catalog/scaffold/ScaffoldDemoScreen:103、catalog/scaffold/ScaffoldDemoScreen:138、catalog/settings/SettingsScreens:315、catalog/ui/SettingsRow:69、catalog/ui/SettingsRow:76、catalog/ui/SmallTitle:34、catalog/CatalogPage:237 |
| `primary` | `color.primary` | `0xFFC41442` | `0xFFFF6B85` | 11 处：ui/basic/Button:144、ui/basic/Checkbox:185、ui/basic/RadioButton:119、ui/basic/Slider:201、ui/basic/Slider:203、ui/basic/TextButton:92、ui/basic/TextField:346、ui/feedback/ProgressIndicator:214、ui/navigation/TabRow:257、catalog/component/TextFieldSection:100、catalog/scaffold/ScaffoldDemoScreen:138 |
| `onPrimary` | `color.on.primary` | `0xFFFFFFFF` | `0xFF101012` | 1 处：ui/basic/Button:145 |
| `surfaceDisabled` | `color.surface.disabled` | `0xFFEAEAEF` | `0xFF2A2A2F` | 5 处：ui/basic/Button:133、ui/basic/Button:146、ui/basic/Slider:206、ui/feedback/ProgressIndicator:217、catalog/component/PagerTopBarDemoScreen:113 |
| `onSurfaceDisabled` | `color.on.surface.disabled` | `0xFFA5A5AE` | `0xFF6C6C75` | 16 处：ui/basic/Button:134、ui/basic/Button:147、ui/basic/Checkbox:187、ui/basic/Checkbox:188、ui/basic/RadioButton:121、ui/basic/RadioButton:122、ui/basic/Slider:207、ui/basic/TextButton:93、ui/basic/TextField:345、ui/feedback/ProgressIndicator:216、ui/navigation/PagerTopBar:267、ui/navigation/TopAppBar:502、ui/settings/SettingsItemDefaults:70、catalog/component/ChoiceSection:103、catalog/component/ChoiceSection:139、catalog/component/PagerTopBarDemoScreen:120 |
| `switchTrackOn` | `color.switch.track.on` | `0x29C41442` | `0x59FF6B85` | 1 处：ui/basic/Switch:262 |
| `switchTrackOff` | `color.switch.track.off` | `0x1F000000` | `0x1FFFFFFF` | 1 处：ui/basic/Switch:263 |
| `switchThumbOn` | `color.switch.thumb.on` | `0xFFC41442` | `0xFFFF6B85` | 1 处：ui/basic/Switch:264 |
| `switchThumbOff` | `color.switch.thumb.off` | `0x2D000000` | `0x4CFFFFFF` | 1 处：ui/basic/Switch:265 |
| `switchTrackDisabled` | `color.switch.track.disabled` | `0x0A000000` | `0x14FFFFFF` | 1 处：ui/basic/Switch:267 |
| `switchThumbDisabled` | `color.switch.thumb.disabled` | `0x0C000000` | `0x1EFFFFFF` | 1 处：ui/basic/Switch:268 |
| `trackInactive` | `color.track.inactive` | `0x1E000000` | `0x1FFFFFFF` | 2 处：ui/basic/Slider:202、ui/feedback/ProgressIndicator:215 |
| `outline` | `color.outline` | `0xFF969696` | `0x66FFFFFF` | 2 处：ui/basic/Checkbox:184、ui/basic/RadioButton:118 |
| `controlDot` | `color.control.dot` | `0xFFFFFFFF` | `0xFFFFFFFF` | 2 处：ui/basic/Checkbox:186、ui/basic/RadioButton:120 |
| `outlineVariant` | `color.outline.variant` | `0x14000000` | `0x19FFFFFF` | 7 处：ui/basic/TextField:350、ui/navigation/PagerTopBar:166、ui/navigation/TopAppBar:106、ui/navigation/TopAppBar:270、ui/settings/SettingsItemDefaults:69、catalog/scaffold/ScaffoldDemoScreen:128、catalog/ui/SettingsRow:94 |
| `statePressed` | `color.state.pressed` | `0x1F000000` | `0x29FFFFFF` | 4 处：ui/basic/IndicationDefaults:34、ui/basic/Slider:204、ui/basic/Switch:266、ui/navigation/TabRow:258 |
| `inputIndicatorUnfocused` | `color.input.indicator.unfocused` | `0x1F000000` | `0x1FFFFFFF` | 1 处：ui/basic/TextField:348 |
| `error` | `color.error` | `0xFFB3261E` | `0xFFF2B8B5` | 3 处：ui/basic/TextField:347、ui/basic/TextField:351、catalog/component/TextFieldSection:115 |
| `secondary` | `color.secondary` | `0xFF18181B` | `0xFFF4F4F5` | 3 处：ui/basic/Button:131、ui/navigation/TabRow:271、ui/navigation/TabRow:273 |
| `onSecondary` | `color.on.secondary` | `0xFFFAFAFA` | `0xFF101012` | 1 处：ui/basic/Button:132 |
| `secondaryContainer` | `color.secondary.container` | `0x14000000` | `0x1FFFFFFF` | 1 处：ui/navigation/TabRow:274 |
| `onSecondaryContainer` | `color.on.secondary.container` | `0xFF18181B` | `0xFFF4F4F5` | **无消费者** |
| `inverseSurface` | `color.inverse.surface` | `0xFF18181B` | `0xFFF4F4F5` | **无消费者** |
| `inverseOnSurface` | `color.inverse.on.surface` | `0xFFFAFAFA` | `0xFF101012` | **无消费者** |
| `inputPlaceholder` | `color.input.placeholder` | `0xFF6B6B73` | `0xFFA1A1AA` | 1 处：ui/basic/TextField:338 |
