# 主题继承

非官方 / draft。从 `io.oxygenx.theme` 导入 `OxygenTheme`。

## 配色解析

| 输入 | 结果 |
| --- | --- |
| 显式 colorScheme | 使用该配色，优先于 darkTheme |
| 未指定配色，darkTheme = true | 默认深色配色 |
| 未指定配色，darkTheme = false | 默认浅色配色 |
| 两者都不传 / null | 继承外层配色；根级回退为浅色 |

其他主题参数省略时继承。根主题不会自动跟随系统明暗，由宿主传入模式或配色。

## 局部排版

```kotlin
OxygenTheme(colorScheme = customColors) {
    OxygenTheme(typography = customTypography) {
        Text("Keeps customColors")
    }
}
```

## Material 字体体系

默认值对齐 Material3 1.5.0-beta01 的 15 个基础角色，包括字号、行高、字重和字距。Display（57/45/36）、headline（32/28/24）、title（22/16/14）、body（16/14/12）、label（14/12/11）保持独立语义。LabelMedium 为 12sp；按钮与菜单使用 14sp 的 LabelLarge。大顶栏展开态使用 45sp 的 DisplayMedium，列表标题使用宿主选定的对应角色。

```kotlin
val typography = OxygenTypography(
    titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
)
OxygenTheme(typography = typography) { Text("Source title", style = OxygenTheme.typography.titleSmall) }
```

M3 命名工厂与 getter 和旧语义字段共存。原 13 样式构造、JVM copy / 默认参数入口保留；新角色通过不可变 extras 参与相等性与复制。完整配置建议使用命名工厂或 Typography.toOxygenTypography()，确保逐角色字体定制一并保留。可选 material3 适配器保留源 TextStyle，不再用旧别名改写宿主 M3 / Legado 的字体角色。

## 顶栏标题角色

普通与收拢标题使用校准后的 18sp / 24sp，从 titleLarge 继承字体和字重。LargeTopAppBar 与 PagerTopBar 展开时使用 displayMedium（45sp / 52sp），随进度插值到紧凑标题。普通与折叠栏高 74dp，紧凑导航图标画布为 16dp，点击目标为 48dp。自定义导航图标可读取 LocalTopAppBarNavigationIconSize。

TopAppBar、LargeTopAppBar、PagerTopBar 的 actions 槽将按钮紧邻排列，默认图标操作的中心距为 48dp。图标操作可以使用 TopAppBarAction，默认触摸目标为 48dp，无边界波纹半径为 36dp。需要次要操作时传入 onLongClick，并可用 onLongClickLabel 指定无障碍操作名称。

## 显式明暗

```kotlin
OxygenTheme(darkTheme = true) {
    OxygenTheme(darkTheme = false) {
        Text("Explicit light subtree")
    }
}
```

## 内容样式

容器向槽位下发颜色与文本样式。OxygenX Text / Icon 消费这些值。ProvideTextStyle 将显式属性合并到外层样式。[Text 颜色优先级](components/text.md) 在槽位内同样生效。

## 槽位契约

LocalContentColor 与 LocalTextStyle 由 Oxygen 自己维护。没有显式 provider 时，从读取位置的主题派生 onSurface 与 bodyMedium。嵌套 OxygenTheme 不再重置外层已明确下发的槽位样式。

ProvideTextStyle 只合并传入属性。ProvideContentStyle 同时下发内容色并合并文本样式；只传颜色时保留排版。contentColorFor(backgroundColor) 映射已知主题容器色，遇到未知颜色时继承外层内容色。Surface、Card、Scaffold 使用相同回退规则。

```kotlin
ProvideContentStyle(
    contentColor = OxygenTheme.colorScheme.primary,
    textStyle = TextStyle(fontWeight = FontWeight.Bold),
) {
    Text("Inherits size and family; overrides weight")
}
```

## 可选的 M3 桥接

添加 oxygenx-material3，导入 io.oxygenx.material3.ProvideMaterial3Content 和 toOxygenColorScheme。在 Oxygen 组件上方包一次，M3 Text / Icon 即可读取更深层 Oxygen 槽位的颜色、排版与禁用态。computed local 在使用它的子节点位置求值，不会把根级正文样式固定给所有后代。

```kotlin
MaterialTheme(colorScheme = materialColors) {
    OxygenTheme(colorScheme = materialColors.toOxygenColorScheme(), typography = MaterialTheme.typography.toOxygenTypography()) {
        ProvideMaterial3Content {
            Button(onClick = save) {
                androidx.compose.material3.Text("Save")
            }
        }
    }
}
```

## 桥接边界

桥接为单向 Oxygen → M3，不将两套 key 做别名，也不创建 MaterialTheme 或同步全部设计令牌。子树内显式 M3 provider 仍优先；M3 组件槽位中的 Oxygen Text 不会因此反向继承 M3 样式。色板通过颜色桥接保持同源，不建立互相引用的双向 local。
