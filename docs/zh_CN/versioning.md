# 版本与 GitHub 发布

非官方 / experimental。初始版本 0.0.1；0.x 版本不代表公共 API 已稳定。

## 版本来源

| 值 | 规则 |
| --- | --- |
| VERSION_MAJOR / MINOR / PATCH | 根 version.properties 手工维护 |
| VERSION_SUFFIX=0 | 正常版本，如 0.0.1 |
| VERSION_SUFFIX=1 | 本地 beta.local；发布时按现有 tag 递增 beta.N |
| Android versionCode | git rev-list --count HEAD，构建不修改计数 |
| BOM / 所有库 / catalog | 共用根版本；开发验证可显式传 oxygenxVersion |
| debug / benchmark 包 | io.oxygenx.catalog.debug / .benchmark，与签名正式包分开 |

只从 main 发布，保留完整 Git 历史、不重写已发布历史；已存在的正式 tag 会拒绝再次发布。同一提交重复构建编号相同。Android 关于页显示实际 BuildConfig 版本。根源码归档没有 Git 时本地构建号回退 1，不作为正式分发依据。

## 本地签名与验证

```powershell
.\gradlew.bat printOxygenVersion
.\gradlew.bat :catalog:android:assembleRelease publishAllPublicationsToLocalRepoRepository
$releaseVersion = python tools/release.py resolve --mode local | ConvertFrom-Json
python tools/release.py package --version $releaseVersion.name --code $releaseVersion.code --sdk D:/Android/sdk
```

本地 signing.properties 被 Git 忽略，使用 RELEASE_STORE_FILE、RELEASE_STORE_PASSWORD、RELEASE_KEY_ALIAS、RELEASE_KEY_PASSWORD；同名环境变量优先。缺少配置可以编译未签名 release，但分发打包会拒绝未签名、错误证书或错误版本的 APK。切勿把私钥或口令写入可提交文件。

默认 APK 与 Maven 版本都来自 version.properties。-PoxygenxVersion 与 -PoxygenxVersionCode 是显式构建覆盖；CI 使用准备阶段算出的值。开发构建请使用唯一后缀，避免覆盖正式坐标。0.x 的破坏性 API 变更应升级 minor 并提供迁移说明。

## GitHub Actions

Auto Build 手动运行默认 publish=false，仅产生已签名构建附件。Release 调用同一流程并启用发布；两者仅允许仓库 owner 在 main 运行签名分发。普通 push / PR 只跑 CI，不需要签名 secrets。发布并发串行，构建失败不会创建 tag。

仓库 Actions Secrets 为 ANDROID_KEYSTORE_BASE64、ANDROID_KEYSTORE_PASSWORD、ANDROID_KEY_ALIAS、ANDROID_KEY_PASSWORD。keystore 只在 runner 临时目录解码，finally 清理；口令通过环境传递，不写入仓库或命令行。公开证书指纹位于 signing-cert.sha256，打包时核对实际 APK 签名。

工作流需要先提交、推送至 GitHub。随后在 Actions 中先运行 Auto Build 验证，再运行 Release 创建 v0.0.1。VERSION_SUFFIX=1 时会创建 pre-release；已有正式版本必须先升基版本。

## 分发附件与接入

附件包含签名 catalog APK、R8 mapping、该版本完整 Maven 仓库 zip、release-info.json 和 SHA256SUMS.txt。压缩包仅收录指定版本的八个库、三类平台变体和 BOM，不混入旧开发版本、签名配置或私钥。CI 解压最终 zip，再用仅依赖 BOM + UI 的独立消费者构建验证。

下载 Maven zip 并解压，将包含 io/oxygenx 的目录配置成 Maven 仓库，使用 io.oxygenx:oxygenx-bom:0.0.1 和 oxygenx-ui；需要桥接时再添加 oxygenx-material3。这是 GitHub 附件分发，不是 Maven Central 或 GitHub Packages 托管；私有仓库的附件需要授权访问。

## 验证范围

本地可验证版本、签名、R8、校验和、仓库归档、独立消费者、API 和测试。远端工作流、Release 与附件下载必须实际运行后才能记为通过；搭建系统不等于已公开发布，也不表示全部组件已 verified。

官方参考：[GitHub Secrets](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets)、[GitHub Releases](https://docs.github.com/en/rest/releases/releases)。
