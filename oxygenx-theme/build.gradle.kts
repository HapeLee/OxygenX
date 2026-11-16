// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("module.kmp-library")
    id("module.android-library")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("module.tokens")
    id("module.spotless")
    id("module.publishing")
    alias(libs.plugins.binaryCompatibilityValidator)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":oxygenx-core"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            // 过冲回弹的弹簧规格（与 MOT-SCROLL-001 同源 token）。
            implementation(libs.compose.animation)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
