// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("module.kmp-library")
    id("module.android-library")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("module.spotless")
    id("module.publishing")
    alias(libs.plugins.binaryCompatibilityValidator)
}

// 唯一允许依赖 Material 3 的模块：它是「把外部 M3 色板搬进来」的桥，
// 不是用 Material 组件充当外观。其余模块仍然只用 runtime / ui / foundation。
kotlin {
    sourceSets {
        commonMain.dependencies {
            // 映射函数的入参就是 M3 的 ColorScheme，消费方要能自己构造，故用 api。
            api(project(":oxygenx-theme"))
            // material3 的版本由 Compose Multiplatform 插件给出（它与 compose-multiplatform
            // 主版本不总是同位），所以这里用 `compose.material3` 访问器而不是手写坐标。
            api(compose.material3)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
