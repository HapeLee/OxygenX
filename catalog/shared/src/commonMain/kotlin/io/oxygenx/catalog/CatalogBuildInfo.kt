// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog

import androidx.compose.runtime.staticCompositionLocalOf

/** 平台宿主提供实际构建版本；没有版本元数据的预览不展示伪造的版本号。 */
val LocalCatalogBuildInfo = staticCompositionLocalOf<String?> { null }
