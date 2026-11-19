# NavigationBar

非官方 / draft。条目等宽，选中色使用动效令牌并提供 Tab 语义；系统栏 Insets 由宿主负责。

## 槽位

```kotlin
NavigationBar {
    NavigationBarItem(
        selected = selected,
        onClick = onSelect,
        label = { Text("Books") },
        icon = { Icon(bookIcon, contentDescription = null) },
    )
}
```

条目是 RowScope 扩展，下发内容色与 caption 排版，支持禁用。宿主可隐藏标签、提供自定义图标；需要同时导入 NavigationBar 与 NavigationBarItem 扩展。

原有列表重载和 NavigationBarItem 数据类继续可用，内部使用相同槽位。槽位条目暴露 colors / motion。混用 M3 时，应在 M3 容器 provider 下方、Oxygen 底栏边界包 ProvideMaterial3Content，使保留的 M3 槽位内容读取 Oxygen 样式。

## 验证

Desktop 回归覆盖选中、禁用语义与单次点击只回调一次。OxygenOS 逐帧拟合与组件性能验收仍待完成。
