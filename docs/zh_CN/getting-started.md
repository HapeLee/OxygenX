# 接入与本地发布

非官方、实验版。尚未配置 Maven Central 公开分发。需要 JDK 21 与 Android SDK Platform 37 / Build Tools 37.0.0。

## 发布到本地

运行 `./gradlew publishAllPublicationsToLocalRepoRepository '-PoxygenxVersion=0.0.1-local.1'`；Windows 使用 `.\gradlew.bat`，PowerShell 中应给整个属性参数加引号。仓库生成在 `build/maven-repo`，包括八个库的 Android / Desktop / Wasm 变体及 BOM。每次共享构建使用新版本号，避免缓存旧制品。

## 添加依赖

在使用方的 settings 文件添加本地仓库，并替换绝对路径：

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("D:/Project/OxygenX/build/maven-repo") }
    }
}
```

Android 模块启用 Compose 编译器插件后添加：

```kotlin
dependencies {
    implementation(platform("io.oxygenx:oxygenx-bom:0.0.1-local.1"))
    implementation("io.oxygenx:oxygenx-ui")
}
```

Compose Multiplatform 在 commonMain 添加带版本的依赖：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.oxygenx:oxygenx-ui:0.0.1-local.1")
        }
    }
}
```

## 渲染组件

公开 Compose 类型通过库的 API 依赖传递。`tools/consumer-smoke` 是只引用已发布 BOM 与 UI 坐标的独立 Android 工程。发布后可运行 `./gradlew -p tools/consumer-smoke assembleDebug '-PoxygenxVersion=0.0.1-local.1'`；该独立构建也需要可见的 Android SDK 配置。

从 `io.oxygenx.theme` 与 `io.oxygenx.ui.basic` 导入。明暗主题由宿主选择。

```kotlin
@Composable
fun SaveAction(dark: Boolean, onSave: () -> Unit) {
    OxygenTheme(darkTheme = dark) {
        Button(onClick = onSave) { Text("Save") }
    }
}
```

## 可选的 M3 配色

添加同版本的 `io.oxygenx:oxygenx-material3`，导入 `io.oxygenx.material3.toOxygenColorScheme`。桥接只转换颜色，不转换行为或排版。

```kotlin
OxygenTheme(colorScheme = materialColors.toOxygenColorScheme()) {
    Text("OxygenX")
}
```

正式版本默认来自根 version.properties；GitHub 签名与分发见[版本说明](versioning.md)。
