package com.dirzaaulia.countries

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Globe3DPlatformView(
    state: GlobeState,
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier
)

@Composable
expect fun Moon3DPlatformView(
    state: GlobeState,
    phaseAngle: Double = 0.0,
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier
)
