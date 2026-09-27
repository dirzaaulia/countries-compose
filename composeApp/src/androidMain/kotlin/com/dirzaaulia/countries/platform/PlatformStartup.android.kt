package com.dirzaaulia.countries.platform

import java.util.concurrent.atomic.AtomicBoolean

object StartupState {
    val isReady = AtomicBoolean(false)
}

actual fun markStartupReady() {
    StartupState.isReady.set(true)
}
