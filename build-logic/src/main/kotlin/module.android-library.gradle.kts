// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

// :oxygenx-core -> io.oxygenx.core / :catalog:shared -> io.oxygenx.catalog.shared
val androidNamespace = "io.oxygenx." + path
    .split(':')
    .filter { it.isNotEmpty() }
    .joinToString(".") { it.removePrefix("oxygenx-").replace('-', '.') }

kotlin {
    android {
        namespace = androidNamespace
        compileSdk {
            version = release(37)
        }
        minSdk = 26
    }
}
