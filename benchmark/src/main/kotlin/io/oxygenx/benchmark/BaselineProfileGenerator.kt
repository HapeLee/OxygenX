// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.benchmark

import android.os.SystemClock
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** 仅收集 catalog 启动路径；不宣称覆盖全部组件或发布库级 profile。 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun startup() = rule.collect(packageName = targetAppId, includeInStartupProfile = true) {
        pressHome()
        startActivityAndWait()
        awaitCatalog()
        // 部分厂商 ART 在进程启动后延迟首次 profile 保存；只影响采集，不进入启动测量。
        val saveDelay = InstrumentationRegistry.getArguments().getString("profileSaveDelayMs")?.toLong() ?: 0L
        require(saveDelay in 0L..60_000L) { "profileSaveDelayMs must be between 0 and 60000" }
        if (saveDelay > 0) SystemClock.sleep(saveDelay)
    }
}
