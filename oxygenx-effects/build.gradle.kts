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
            api(libs.compose.ui)
        }
    }
}
