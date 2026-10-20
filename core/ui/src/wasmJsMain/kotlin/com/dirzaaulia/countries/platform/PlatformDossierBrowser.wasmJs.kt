package com.dirzaaulia.countries.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.window

@Composable
actual fun PlatformDossierBrowser(
    url: String,
    onClose: () -> Unit,
) {
    LaunchedEffect(url) {
        window.location.href = url
        onClose()
    }
}
