// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "OxygenX"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":oxygenx-core")
include(":oxygenx-theme")
include(":oxygenx-motion")
include(":oxygenx-effects")
include(":oxygenx-icons")
include(":oxygenx-icons-material")
include(":oxygenx-ui")
include(":oxygenx-material3")
include(":oxygenx-bom")
include(":catalog:shared")
include(":catalog:android")
include(":catalog:desktop")
