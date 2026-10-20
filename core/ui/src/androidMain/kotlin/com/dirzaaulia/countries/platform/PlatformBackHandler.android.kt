package com.dirzaaulia.countries.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
@Suppress("FUN_NAME_WITH_UPPERCASE")
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
