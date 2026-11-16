// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-platform`
    id("module.spotless")
    id("module.publishing")
}

// 平台模块：外部项目引一个 BOM 就锁定整套库的版本，不用逐个抄 artifactId + 版本号。
// 约束写成 `api(project(...))`，Gradle 会按各模块发布出去的坐标（io.oxygenx:oxygenx-*）
// 翻译成 dependencyManagement 条目。
//
// `java-platform` 不像 KMP 插件那样自动建 publication（不建就「一切 up-to-date、产物为空」），
// 所以这里显式从 `javaPlatform` 组件取一次。
dependencies {
    constraints {
        api(project(":oxygenx-core"))
        api(project(":oxygenx-theme"))
        api(project(":oxygenx-motion"))
        api(project(":oxygenx-icons"))
        api(project(":oxygenx-icons-material"))
        api(project(":oxygenx-ui"))
        api(project(":oxygenx-effects"))
        api(project(":oxygenx-material3"))
    }
}

publishing {
    publications {
        create<MavenPublication>("oxygenxBom") {
            from(components["javaPlatform"])
            artifactId = "oxygenx-bom"
        }
    }
}
