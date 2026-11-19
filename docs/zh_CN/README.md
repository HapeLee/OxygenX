# OxygenX 使用文档

非官方、实验性 UI 库。所有组件保持 `draft`；本文描述当前契约，不承诺 API 稳定。[English](../README.md)。

## 从这里开始

- [接入与本地发布](getting-started.md)
- [平台支持](platforms.md)
- [Android 性能入口](performance.md)
- [迁移说明](migration.md)
- [版本与 GitHub 发布](versioning.md)
- [主题继承](theme.md)

## 组件

| 组件 | 契约 |
| --- | --- |
| [Text](components/text.md) | 颜色优先级 |
| [Icon](components/icon.md) | 内容色、原色与资源重载 |
| [TextField](components/text-field.md) | 受控输入与聚焦动效 |
| [Slider](components/slider.md) | RTL、键盘、触摸与无障碍 |
| [Overscroll](components/overscroll.md) | 仅边界回弹与正常惯性 |
| [NavigationBar](components/navigation-bar.md) | 列表与槽位 API、选中与禁用态 |
| [DropdownMenu](components/dropdown-menu.md) | 经典 M3 样式、宽度限制、滚动与 RTL |
| [Snackbar](components/snackbar.md) | 排队、取消与超时 |

本轮覆盖上述组件。其他组件可通过 Android / Desktop catalog 查看示例，完整文档仍待补齐。贡献规范见 [spec/](../spec/README.md)。
