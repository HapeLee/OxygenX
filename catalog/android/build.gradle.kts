// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("module.spotless")
}

android {
    namespace = "io.oxygenx.catalog.android"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.oxygenx.catalog"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(project(":catalog:shared"))
    implementation(libs.androidx.activity)
}
