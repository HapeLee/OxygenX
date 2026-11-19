// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "io.oxygenx.consumer"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 26 }
}

val oxygenxVersion = providers.gradleProperty("oxygenxVersion").get()

dependencies {
    implementation(platform("io.oxygenx:oxygenx-bom:$oxygenxVersion"))
    implementation("io.oxygenx:oxygenx-ui")
}
