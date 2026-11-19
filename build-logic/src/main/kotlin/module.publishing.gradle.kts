// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.publish.maven.MavenPublication

// 发布约定：把 KMP 库模块发到本地文件仓库（rootProject/build/maven-repo）与 mavenLocal，
// 供外部项目（例如 Android App 的 pinnedUiForks 流程）按坐标消费。
//
// 只由库模块应用；catalog 与 build-logic 不发布。
// 默认版本由根工程解析 version.properties；本地验证可用 -PoxygenxVersion 指定唯一版本，
// 同一正式版本不允许重复发布。APK、BOM 与各模块共用该入口。

plugins {
    id("maven-publish")
}

group = "io.oxygenx"
version = rootProject.version

val localRepo = rootProject.layout.buildDirectory.dir("maven-repo")

publishing {
    repositories {
        maven {
            name = "localRepo"
            url = localRepo.get().asFile.toURI()
        }
    }
}

publishing.publications.withType<MavenPublication>().configureEach {
    pom {
        // artifactId 由 Gradle 按 variant 后缀自动拼（如 oxygenx-theme-desktop / -android）。
        name.set(project.name)
        description.set("Unofficial OxygenOS-style Compose Multiplatform UI: ${project.name}")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
    }
}
