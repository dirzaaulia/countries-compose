package com.dirzaaulia.countries.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId

@Composable
expect fun Globe3DPlatformView(
    state: GlobeState,
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
)

@Composable
expect fun Moon3DPlatformView(
    state: GlobeState,
    phaseAngle: Double = 0.0,
    subsolarLatitude: Double = 0.0,
    librationLatitude: Double = 0.0,
    librationLongitude: Double = 0.0,
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
)

@Composable
expect fun Mars3DPlatformView(
    state: GlobeState,
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
)

@Composable
expect fun Planet3DPlatformView(
    planetId: PlanetId,
    state: GlobeState,
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
)
