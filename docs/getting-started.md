# Setup and local publishing

Unofficial / experimental. Public Maven Central distribution is not configured. Use JDK 21 and Android SDK Platform 37 / Build Tools 37.0.0.

## Publish locally

Run `./gradlew publishAllPublicationsToLocalRepoRepository '-PoxygenxVersion=0.0.1-local.1'`; Windows uses `.\gradlew.bat`. Quote the entire property argument in PowerShell. The repository is written to `build/maven-repo`, including eight libraries with Android / Desktop / Wasm variants and a BOM. Use a fresh version for every shared build to avoid stale dependency caches.

## Dependencies

In the consumer's settings file, add the local repository, replacing the absolute path:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("D:/Project/OxygenX/build/maven-repo") }
    }
}
```

Enable the Compose compiler plugin in the Android module, then add:

```kotlin
dependencies {
    implementation(platform("io.oxygenx:oxygenx-bom:0.0.1-local.1"))
    implementation("io.oxygenx:oxygenx-ui")
}
```

For Compose Multiplatform, add the versioned dependency to commonMain:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.oxygenx:oxygenx-ui:0.0.1-local.1")
        }
    }
}
```

## Render a component

Public Compose types are exported through the library's API dependencies. The isolated Android fixture at `tools/consumer-smoke` compiles using only the published BOM and UI coordinates. After publishing, run `./gradlew -p tools/consumer-smoke assembleDebug '-PoxygenxVersion=0.0.1-local.1'`; SDK configuration must also be visible to that standalone build.

Import from `io.oxygenx.theme` and `io.oxygenx.ui.basic`. The host selects theme mode.

```kotlin
@Composable
fun SaveAction(dark: Boolean, onSave: () -> Unit) {
    OxygenTheme(darkTheme = dark) {
        Button(onClick = onSave) { Text("Save") }
    }
}
```

## Optional M3 colors

Add `io.oxygenx:oxygenx-material3` at the same version and import `io.oxygenx.material3.toOxygenColorScheme`. This bridge converts colors only, not behavior or typography.

```kotlin
OxygenTheme(colorScheme = materialColors.toOxygenColorScheme()) {
    Text("OxygenX")
}
```

Release versions default to root version.properties; see [versioning](versioning.md) for GitHub signing and distribution.
