# Dropdown 的 M3 样式优化（2026-11-18）

状态：本轮样式与自动化接入完成 / draft。用户明确允许采用 M3 样式，本轮只调整菜单与接入，不扩展到其他组件。

## 设计

- 参考目标工程 Material3 1.5.0-beta01 的经典 Dropdown：4dp 圆角、3dp 投影、无卡片描边、112–280dp 宽、48dp 最小行高、14sp / 20sp 标签与 12dp 图标间距。
- 使用 Foundation / Oxygen 主题实现，色板继续随 Oxygen 自定义主题开关变化；投影仅为菜单浮层的 M3 样式例外，不修改 Card / Surface。
- 长菜单可滚动，长文字换行，大字体可增高；窗口边缘与 RTL 定位正确。
- 原公开签名保留，新增可传 contentPadding / interactionSource 的条目重载供 App* 适配使用。
- 图标仍使用 OxygenX 现有矢量。catalog、双语文档、明暗基线和回归一起交付。

## 验证

1. 先复现文字样式继承与长菜单不能滚动，再修改实现。
2. 令牌生成、Spotless、Desktop / Android / Wasm、API 门禁与发布快照校验。
3. 更新阅读项目 vendor；检查 App* 菜单的禁用态、槽位颜色、选择 / 关闭、定制内距。
4. 双产品编译、定向回归和 APK；按用户约定只做自动化验证，不声明真机或性能验收。

来源：
- https://developer.android.com/develop/ui/compose/components/menu
- https://github.com/androidx/androidx/blob/androidx-main/compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/Menu.kt
- 已缓存的 `material3-android-1.5.0-beta01-sources.jar`（目标工程同版本）。

## 库侧结果

- 两个真实回归先失败：外层 30sp 排版污染条目、长菜单没有滚动语义；见 `build/dropdown-before.log`。修复后通过。
- 5 个新尺寸 / 形状 / 阴影令牌由 YAML 生成。Shapes 使用新增扩展属性，避免修改原构造与 copy 签名。
- 原 DropdownMenu、原有 DropdownMenuItem 保留；新增显式 contentPadding / 可选 interactionSource 重载。
- Desktop 122 项零失败，含 9 项新增菜单回归；Wasm、API check、Android 编译 / 签名 catalog、33 份 Maven publication 通过。
- 明暗菜单 2 张新图已检查，既有 10 张未改。15 对双语文档与图标来源校验通过。
- 本地 0.0.1 未发布快照已更新，目标 vendor SHA-256：`33544012670bd221120bc4909fb36d588147e2463debf0b68f0da97f288a9fc1`。
- 日志：`build/dropdown-api-compile.log`、`build/dropdown-library-final.log`；没有真机、性能或远端发布结论。

独立消费验证：`tools/consumer-smoke` 仅依赖 BOM + oxygenx-ui 的 Maven 坐标，同时编译原条目调用与 PaddingValues / MutableInteractionSource 新重载，APK 构建通过（`build/dropdown-consumer-smoke.log`）。

## 宿主结果

- App* 菜单容器与条目槽位内重新桥接 Oxygen → M3，真实 AppText / AppIcon 收到 14sp 标签与文字 / 图标局部颜色。禁用态抑制选中 / 自定义文本色，启用后仍保留自定义色；contentPadding / interactionSource 实际透传。
- Core 42 项、Comic 5 项（含两产品各 3 个新菜单回归）零失败 / 零跳过；验证真实 Popup 明暗渲染、选中关闭一次、40 项末项可达、禁用与自定义色 / 内距 / 共享交互源。
- 双产品 Kotlin / Debug APK 与架构门禁通过；目标工程 `build/oxygenx-dropdown-final-snapshot.log` 是最终功能制品验证。两张原生软件渲染图已复核，不冒充真机截图。
- 最后只修正文档表述，重新发布后的 8 个 Android AAR 与已测试制品逐字节一致；最终 vendor 校验值如上，沿用已通过的功能验证结果。
- 全量宿主 Lint、手机验证与性能没有在本轮重复执行，组件仍为 draft / API experimental。
