// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("module.kmp-library")
    id("module.android-library")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("module.spotless")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":oxygenx-ui"))
            // 图标总览页遍历常用图标全集；oxygenx-icons（精选 14 个）已由 oxygenx-ui 传递。
            implementation(project(":oxygenx-icons-material"))
            // 「关于」页的 M3 配色开关：验证 M3 ColorScheme → OxygenColorScheme 映射链路。
            implementation(project(":oxygenx-material3"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            // nav3：runtime 管 NavKey / 返回栈 / entryProvider DSL；
            // NavDisplay 用 JetBrains 的 KMP 发行版（desktop / wasm 有真实实现，
            // androidx 组同名模块在这两个平台只有 jvmStubs）。
            implementation(libs.navigation3.runtime)
            implementation(libs.navigation3.ui)
        }
    }
}
