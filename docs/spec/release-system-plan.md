# Versioning and GitHub distribution

2026-11-17；初始版本 0.0.1；Unofficial / 非官方。

1. 参照 Matter：version.properties 维护显示版本 / 预发布开关，Git 提交数派生 Android versionCode；BOM、所有库与 catalog 共用版本入口。
2. 支持本地忽略的签名配置和 GitHub Actions Secrets；验证用户提供 keystore 的证书，不把私钥、口令或 base64 写入版本控制。
3. 手动 Auto Build 默认只构建；Release 在 main 上由仓库 owner 手动触发，验证后创建不可复用的版本 tag 与 GitHub Release。
4. 发布附件包含签名 APK、R8 mapping、该版本完整 Maven 仓库 zip、构建信息与 SHA-256。
5. 版本解析 / 防重复发布 / 产物收集有回归；本地验证版本元数据、签名、Maven 消费者、完整门禁，远端配置与实际发布分别记录。

进度：已核对 Matter 与 GitHub 官方 Secrets / Release 文档；本地实现、签名构建与分发包验证已完成，独立消费者验证结果见下方。远端工作流仍待代码提交、推送后实际运行。

用户优先事项：先修 Overscroll 中间惯性误回弹。已用 5 项虚拟帧用例复现 4 处失败，修正消费速度契约后通过；补部分余速与真实 LazyColumn 回归。修复将一并进入初始 0.0.1，发布构建与 Maven 包需在此后重新生成。

过冲验证：6 项虚拟帧 + 1 项真实 LazyColumn 回归通过；全量 check 通过，Desktop 107 项 / 零失败，Wasm 测试通过。OnePlus 9R 录屏确认中间首项 41→46 始终无过冲，顶部与底部向外拖动显示过冲且松手归零；证据位于忽略的 build/overscroll-*.mp4 / *-status.png。

GitHub：HapeLee/OxygenX 为 private，当前账号 admin；4 项 Actions signing Secrets 已通过 gh 配置（不输出值）。连接器返回 404，使用本机 gh 完成授权配置。actionlint v1.7.12 校验三个 workflow 通过；未自动提交 / 推送工作区，未触发远端工作流或创建 tag / Release。

最终构建：spotlessCheck / compileKotlinDesktop / Android debug + signed release / 全平台 Maven 发布 / check 同次通过（build/release-final-build.log）；Desktop 107 项 / 零失败，Wasm 浏览器测试通过。发布脚本 15 项通过，覆盖 Build Tools 37 的 V2 / V3 签名输出、错误证书与错误版本拦截。

本地分发验证：0.0.1 / versionCode 2；实际 APK 证书与 signing-cert.sha256 一致。build/distribution/0.0.1 含签名 APK、R8 mapping、Maven zip、release-info.json 与 SHA256SUMS.txt，四份附件的 SHA-256 全部匹配。Maven zip 714 个文件，均限定在 io/oxygenx 下的 0.0.1，无旧版本或签名私钥。当前工作区尚未提交，release-info 明确记录 dirty=true；这是本地验证包，正式流程强制干净检出。

2026-11-18 独立消费者：最终 Maven zip 解压到 build/release-consumer-repo，仅通过 exclusiveContent 仓库读取 io.oxygenx 坐标；tools/consumer-smoke assembleDebug 通过（build/release-consumer.log）。远端发布流程也按先打包、再解压最终 zip 验证消费者的顺序执行。

2026-11-18 接入增量：NavigationBar 槽位 API 与兼容列表实现通过门禁，Desktop 共 108 项；已重新生成 0.0.1 signed catalog / Maven zip（仍为本地 dirty 快照）。MD3 固定该 zip 的 SHA-256，两个产品的编译 / Debug APK、22 项主题与组件回归、架构门禁、双产品 Lint 通过；未创建远端 Release。
