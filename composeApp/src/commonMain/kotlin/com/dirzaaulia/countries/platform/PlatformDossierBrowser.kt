package com.dirzaaulia.countries.platform

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformDossierBrowser(
    url: String,
    onClose: () -> Unit,
)
