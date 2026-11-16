// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.publish.maven.MavenPublication

// 发布约定：把 KMP 库模块发到本地文件仓库（rootProject/build/maven-repo）与 mavenLocal，
// 供外部项目（例如 Android App 的 pinnedUiForks 流程）按坐标消费。
//
// 只由库模块应用；catalog 与 build-logic 不发布。
// 版本走 `-PoxygenxVersion=x.y.z`；默认 `0.1.0-dev` **故意不是 SNAPSHOT**：
// 本地文件仓库里 SNAPSHOT 会写成带时间戳的路径 + 每次都变的 maven-metadata，
// 外部项目（尤其手工配置的 pinned repo）很容易缓存住旧快照；固定 dev 版本可以原地覆盖。

plugins {
    id("maven-publish")
}

group = "io.oxygenx"
version = providers.gradleProperty("oxygenxVersion").getOrElse("0.1.0-dev")

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
        description.set("OxygenX design system: ${project.name}")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
    }
}
