package com.dirzaaulia.countries.platform

import androidx.compose.runtime.Composable

@Composable
@Suppress("FUN_NAME_WITH_UPPERCASE")
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // No-op on WASM target
}
