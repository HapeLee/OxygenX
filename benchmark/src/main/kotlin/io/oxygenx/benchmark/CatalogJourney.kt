// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

internal val targetAppId: String
    get() = requireNotNull(InstrumentationRegistry.getArguments().getString("targetAppId")) { "Missing targetAppId instrumentation argument" }

/** 等待具体可交互节点，超时必须失败，不能把空白启动计为成功。 */
internal fun MacrobenchmarkScope.awaitCatalog() {
    check(device.wait(Until.hasObject(By.res("oxygenx-catalog-index")), 10_000)) { "Catalog index did not become visible" }
}
