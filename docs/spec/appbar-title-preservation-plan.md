# 顶栏直接使用 M3 字体角色（2026-11-18）

当时的决定：不需要为旧 46 / 18sp 单独覆写，直接使用 M3 令牌。此决定取代上一轮保留原字号的方案。

- 页面 / 展开大标题直接使用 displayMedium：默认 45sp / 52sp。
- 普通 / 收拢顶栏直接使用 titleMedium：默认 16sp / 24sp。
- 删除上一轮新增的两个组件配方扩展、专用 YAML 令牌与对应生成 API，不保留别名或 copy 覆写。
- TopAppBar、LargeTopAppBar、PagerTopBar 直接消费主题角色，保留自定义字体属性及展开 / 收拢插值；书源列表不变。
- 同步 ABI、双语文档、独立消费示例与阅读项目依赖快照。尚未公开发布的 0.0.1 API 收回仅新增的配方，不改原有字体构造 / JVM 兼容桥。

验证计划：真实顶栏测试先按新角色断言复现失败；修改后运行格式 / API / Desktop / Wasm 门禁、签名 catalog、独立 Maven 消费示例及阅读项目定向测试和双产品 APK。仅自动化验证，不重复真机或性能验收。结果见下。

## 已完成验证

- 默认 45 / 16sp 的两项真实顶栏回归先失败，移除覆写后通过。
- 自定义 displayMedium / titleMedium 使用不同字体族、字号、行高、字重和字距，发现旧插值固定使用展开字体族；改用 Compose TextStyle 插值后，普通 / 展开 / 收拢端点全部通过。
- 删除原配方的两项公共测试，保留两项实际顶栏 UI 测试。Desktop 共 131 项零失败 / 零跳过，Wasm、API、Spotless、签名 catalog 和 Maven 发布通过；独立 Maven 消费示例构建通过。12 张现有基线未改，15 对双语文档通过。
- 收回上一轮未发布的两个配方扩展及专用生成常量；标准 M3 角色、旧构造与 JVM copy 兼容桥保留。
- 本地快照 SHA-256：`759d5821fd19afd4c1c2c2e70f6256190c2809337561780dd7e124fdc6442684`。宿主 Core 45 项 / Comic 8 项定向回归全部通过；末次仅清理旧字号注释后更新最终快照，双产品 6 项字体验证、双 APK、架构 / AAR 元数据 / 重复类门禁再次通过。预览已复核；日志见目标工程 build/oxygenx-appbar-m3-final.log 和 oxygenx-appbar-m3-final-snapshot.log。
- 日志：build/appbar-m3-before.log、appbar-m3-custom-before.log、appbar-m3-library-final.log、appbar-m3-consumer.log。


## 折叠字号复核

对照 M3 迁移前的提交 2696093，普通顶栏与大顶栏的收拢端点使用 type.appbar.title（18sp / 24sp、500、零字距）。切换到 M3 titleMedium 后两端都变成了 16sp / 24sp，catalog 与阅读项目共用该实现。

恢复已有 type.appbar.title，通过组件内部样式与主题 titleMedium 的字体及平台属性组合。普通、大、横划顶栏共享该档位；展开端点保持 displayMedium。完整 M3 15 角色及旧字体数据构造保留。

桌面两项顶栏测试先复现 16sp，再通过 18sp 基线；库侧共 136 项 Desktop、Wasm、API、Spotless、签名 catalog 与独立 Maven 消费成功。宿主 Core / Comic 各 4 项定向自动化测试、两份 Debug APK 与架构门禁通过，折叠态原生软件预览已复核。


## M3 强调大标题

普通与收拢标题改用 titleLargeEmphasized。以 Android M3 1.5.0-beta01 的 TypeScaleTokens 为准，默认 22sp / 28sp、500、零字距；字重高于普通 titleLarge 的 400。展开端点使用 displayMedium。OxygenX 通过已有 type.appbar.title 生成默认指标。跨平台 M3 依赖的该属性是内部 API，适配器新增接受完整强调 TextStyle 的重载，Android 宿主从公开角色传入实际样式。保留此前工厂、构造和 JVM copy 入口。

库侧 Desktop 136 项、Wasm、Spotless、API、签名 catalog 和独立 Maven 消费通过，既有 JVM 构造和 copy 描述符已核对。Core 和 Comic 各 4 项定向回归与双 APK、架构门禁通过。Android M3 的旧 Typography() 构造会把强调角色填为普通样式，宿主改用接收 fontFamily 的构造读取正式强调角色；默认 500 和自定义样式均已验证。


## 改用常规字重

按最新决定，普通与折叠顶栏使用 M3 titleLarge，默认 22sp / 28sp、400，展开态使用 displayMedium。撤回上一轮未公开发布的强调角色适配，宿主通过原有 15 角色转换传递 titleLarge 及用户字体。

库侧 136 项 Desktop、Wasm、API、格式、签名 catalog 与独立 Maven 消费构建通过。Core / Comic 各 3 项定向验证通过，默认渲染读取到 400 字重；两套 Debug APK 和架构门禁通过。
