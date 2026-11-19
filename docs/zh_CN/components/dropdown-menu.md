# DropdownMenu

Unofficial / draft。导入 `io.oxygenx.ui.overlay.DropdownMenu` 与 `DropdownMenuItem`。

## 用法

```kotlin
Box {
    Button(onClick = { expanded = true }) { Text("Sort") }
    DropdownMenu(expanded, { expanded = false }) {
        DropdownMenuItem(
            text = { Text("By name") },
            onClick = { sortByName(); expanded = false },
        )
        DropdownMenuItem(text = { Text("Unavailable") }, onClick = {}, enabled = false)
    }
}
```

父 Box 是锚点。宿主持有展开、选择状态，选中后自行关闭。点击外部和返回键请求关闭；点击条目只执行它的回调。

## 参数与默认值

| API / 参数 | 默认值 / 行为 |
| --- | --- |
| DropdownMenu expanded / onDismissRequest | 必需的宿主状态与关闭回调 |
| modifier / offset | 面板 Modifier；默认零偏移，水平偏移遵循布局方向 |
| content | ColumnScope 槽位；放普通条目，不嵌套 LazyColumn |
| DropdownMenuItem text / onClick | 必需的可组合文字槽与回调 |
| modifier / enabled | Modifier；true，禁用时不触发 onClick |
| leadingIcon / trailingIcon | 可选槽位，至少预留主题 medium 图标宽度 |
| contentPadding / interactionSource | 新增重载；显式提供 PaddingValues 自定义内距，并可共享交互源 |

## 样式与布局

默认参考经典 M3：菜单独立 4dp 圆角、3dp 投影，无卡片描边，底色 surfaceContainer，排版使用主题 labelLarge（默认 14sp / 20sp）。菜单形状与 Card 分开。菜单投影是库内扁平容器风格的明确例外，Card 与 Surface 保持各自默认值。

宽度在主题 menuMinWidth（112dp）和 280dp 之间随内容变化。菜单行最小 48dp，左右内距 16dp，图标间距 12dp，容器上下留白 8dp。长文字换行，大字体可增高，受限菜单可滚动。菜单优先放在锚点下方，其次上方，再夹入窗口内。RTL 按 start/end 对齐并反转水平偏移。正常窗口保留上下 48dp 安全边距；极小窗口会缩减边距，保证内容可达。

颜色跟随 OxygenTheme，包括禁用文字与图标槽。在混合 M3 宿主中，于分派边界使用 ProvideMaterial3Content，让保留的 AppText / AppIcon 槽位收到菜单 locals。

## 迁移与验证

保留原 DropdownMenu 和原有 DropdownMenuItem 签名。定制内距时通过命名 contentPadding 调用新增重载，然后可传 interactionSource。默认文字不再继承外层无关的排版。

Desktop 回归覆盖换行、2× 字体、禁用槽位、自定义内距、共享交互源、滚到最后一项和单次宿主回调。公共测试覆盖 RTL 偏移、边缘回退和极小窗口。明暗 PNG 使用 Popup 的同一面板渲染实现。这些是库回归，不代表 OxygenOS 复刻、真机或性能验收。

## 动效

默认从锚点交集中心进行 0.8→1 缩放和淡入，关闭时反向，并等待两个通道结束后移除 Popup。快速关闭 / 重开从当前进度接续，不每帧重组。入口缩放与透明度分别使用 OxygenMotionDefaults.menuScale() / menuFade()；可通过显式 motion / fadeMotion 重载覆写，原调用仍带默认动画。宿主需始终组合 DropdownMenu 并传 expanded，不能在 expanded=false 时先把整个组件移除。

虚拟帧回归覆盖进退、中途反向、保留至退出完成与无逐帧重组；标准弹簧参数是 M3 参考，归一化阈值为项目选择，状态仍为 draft。
