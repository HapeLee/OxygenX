# M3 排版与菜单动效（2026-11-18）

用户指定采用 Material 排版语义与菜单动效；状态 draft，不作为 OxygenOS 真机参数。

- 对齐目标工程 Material3 1.5.0-beta01 的 15 个基础字体角色、字号、行高、字重与字距。
- 保留 OxygenTypography 的旧字段 / 13 参数构造及 copy 的 JVM 入口；通过不可变扩展角色与 M3 命名工厂补齐全套角色。
- 原有按钮、菜单改用 labelLarge，导航使用 labelMedium，大顶栏展开使用 headlineMedium；旧语义名称继续作为兼容入口。
- 可选 material3 模块提供完整 Typography → OxygenTypography 投影。宿主保留 M3 / Legado 的原字体角色，避免拿 listItemTitle / appbarTitle 错覆 titleSmall 等角色。
- Dropdown 增加可配置的空间 / 透明度 spring、锚点变换中心、关闭后保留至动画结束；快速反向保持当前值。旧函数签名委派默认动效。
- 先固化关闭动画缺失，再完成虚拟帧 / 中断 / 渲染、字体全角色和书源列表标题回归；同步文档、catalog 与分发快照。

来源：目标版本 sources.jar 的 TypeScaleTokens、StandardMotionTokens、Menu；https://m3.material.io/styles/typography/type-scale-tokens 。

## 完成结果

- 关闭动画先复现失败，日志 `build/material-type-menu-before.log`；修复后退出保留 / 中断 / 重开通过。
- 根类型 15 基础角色与目标版本 TypeScaleTokens 对齐，15 角色投影保留完整 TextStyle；新角色参与相等性，旧构造 / copy / copy$default 的 JVM 调用均通过反射执行回归。
- 框架字体值替代旧 OEM 默认值。按钮 / 菜单 labelLarge 14sp，导航 labelMedium 12sp，分组 titleSmall，大顶栏 headlineMedium → titleLarge；catalog 加入 15 角色示例。
- 6 张文字基线变化先触发失败，逐张复核后定向更新；其余 6 张未改。全部 129 项 Desktop、Wasm、API、Spotless、Android 签名 catalog 与 Maven 发布通过，15 对双语文档通过。
- 独立 Maven 消费示例验证 M3 命名工厂、新动效重载与原菜单调用，APK 构建通过。
- 宿主不再将 titleSmall 等角色映射成旧大字号。Core 44 项、Comic 7 项零失败 / 零跳过；包括真实书源共用 SelectionItemCardContent 在 Material / Oxygen 间切换的 14sp / 20sp 与所有 Legado 角色一致，以及自定义逐角色字体保留。
- 双产品编译 / Debug APK、架构、AAR 元数据与重复类门禁通过；原有配色、图标和菜单回归保持通过。按用户约定仅自动验证，本轮未重复宿主全量 Lint、真机或性能测试。
- 日志：`build/material-type-library-final.log`、`build/material-type-consumer.log`；目标工程 `build/oxygenx-material-type-menu-final.log`。原生软件渲染预览为目标工程 `app/build/outputs/oxygenx-source-typography.png`，已复核。
- 本地未公开发布 0.0.1 快照 SHA-256：`a90a0dc9b479763624af47586bbe11e7050a5550a03565769f11ae82a7ee1744`。
