package com.dirzaaulia.countries.platform

import androidx.compose.runtime.Composable

@Composable
@Suppress("FUN_NAME_WITH_UPPERCASE")
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
