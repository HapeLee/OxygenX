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

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":oxygenx-theme"))
            api(project(":oxygenx-motion"))
            api(project(":oxygenx-effects"))
            api(project(":oxygenx-icons"))
            api(project(":oxygenx-core"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.animation)
            // 仅取 Material 的水波纹 indication（触摸态），不用其组件外观。
            implementation(libs.compose.material.ripple)
            implementation(libs.kotlinx.collections.immutable)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
