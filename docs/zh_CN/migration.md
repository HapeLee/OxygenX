# 迁移说明

非官方 / draft。相对于可靠性修复前开发版本的变更，2026-11-17。

## 源码与二进制兼容性

`OxygenTheme.darkTheme: Boolean = false` 改为 `Boolean? = null`。已有显式 Boolean 调用仍可编译；省略时改为继承外层颜色。需要刻意将子树重置成默认浅色时，传 `darkTheme = false`。

TextField 两个重载新增有默认值的 `motion` 参数，已有 Kotlin 源码调用保持可用。主题与 TextField 的 JVM 签名改变：依赖方必须重新编译，所有模块使用相同的新发布版本。ABI 基线同步更新，不承诺兼容之前已编译的开发制品。

## 行为变化

- Text 保留 TextStyle.color，除非显式 color 覆盖。
- Slider 镜像 RTL 手势、轨道和自定义拇指，支持焦点与键盘输入。
- Slider 无障碍调节调用一次 onValueChangeFinished；值未变返回 false，不触发回调。
- Slider 拒绝负 steps、非有限值及非法区间。
- Snackbar 取消后清理当前消息，支持无障碍推荐超时与 live-region 播报。
- TextField 聚焦动效使用令牌，绘制期间不改变布局高度。
- 库以 API 依赖传递公开签名使用的 Compose 类型，使用方可直接获得 runtime、UI 与布局类型，无需碰巧依靠其他依赖补全。

## 回归与待验收项

2026-11-17 状态输入增量：TextField 新增 Foundation TextFieldState 重载，通用编辑参数对齐 M3，旧值重载保留。locals 从读取位置的主题派生默认值，嵌套主题保留显式槽位样式；Card/Scaffold 的未知底色继承外层内容色。TextField 槽位获得默认样式，全部重载保留 textStyle.color。oxygenx-theme 新增 ProvideContentStyle/contentColorFor，可选 ProvideMaterial3Content 将 Oxygen 内容样式桥接到 M3。更新后重新编译依赖方。见 [TextField](components/text-field.md) 与[主题桥接](theme.md)。

catalog 的 Slider、TextField 和 Scaffold 能力页提供复现入口。Desktop 渲染测试与四张 Slider PNG 和两张 TextField 槽位 PNG 保护这些契约。真机 TalkBack、全组件 golden、OxygenOS 逐帧校准、性能基准和远端 CI 仍是独立验收项。

2026-11-17 Text / Icon 增量：Icon 省略 tint 继续继承内容色，显式 Unspecified 改为保留原色；依赖旧含义的调用需删除该参数。Text 增加 autoSize、AnnotatedString.inlineContent、fontFamily 和 textDecoration；使用命名参数，重编译调用方。目录新增 Text / Icon 页，Android 性能入口见 [性能文档](performance.md)。

Text 的 onTextLayout 仍在参数末尾；若旧代码以完整位置参数列表传入该回调，新增参数插入后需要改成 onTextLayout = ...。命名回调和尾随 lambda 调用保持有效。

2026-11-17 过冲修复：按 Foundation 契约正确计算未消费余速，消除列表中间惯性结束后的错误回弹；零速度释放已有过冲也会收回。无公开 API 或令牌变化。

2026-11-18：NavigationBar 新增 RowScope 槽位重载与 NavigationBarItem 扩展，方便宿主原有分派层接入。列表重载保留；消费方重新编译。见 [NavigationBar](components/navigation-bar.md)。

2026-11-18 M3 排版与菜单动效：基础排版角色改用 M3 15 角色，labelMedium 现在为 12sp；按钮 / 菜单用 labelLarge 14sp。原字段、13 样式构造和 JVM copy 入口保留，完整配置优先使用 M3 命名工厂或 Typography.toOxygenTypography()。菜单现在默认带锚点缩放 / 淡入淡出，关闭后会保留至退出完成。旧调用无需改参数；自定义时使用 motion / fadeMotion 重载。宿主保留自身 M3 / Legado 角色，不能以旧别名重写 titleSmall 等字段。

普通与折叠顶栏标题校准为 18sp / 24sp，字体与字重沿用 titleLarge。展开大标题使用 displayMedium（45sp / 52sp）。自定义导航图标可通过 LocalTopAppBarNavigationIconSize 读取槽位尺寸。
