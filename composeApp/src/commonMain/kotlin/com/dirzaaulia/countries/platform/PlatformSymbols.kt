package com.dirzaaulia.countries.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformCountryFlag(
    iso2: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

expect fun platformCountryMarker(iso2: String): String
