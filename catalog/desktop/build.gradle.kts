// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("module.spotless")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":catalog:shared"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "io.oxygenx.catalog.desktop.MainKt"
    }
}
