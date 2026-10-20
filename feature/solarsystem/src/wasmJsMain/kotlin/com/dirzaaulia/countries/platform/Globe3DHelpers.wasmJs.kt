package com.dirzaaulia.countries.platform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId

@Composable
internal fun MoonPlanetViewWasm(
    planetId: PlanetId,
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    Moon3DPlatformView(
        state = state,
        isPageActive = isPageActive,
        modifier = modifier,
    )
}

@Composable
internal fun GenericPlanetViewWasm(
    planetId: PlanetId,
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    val planetTypeFloat =
        when (planetId) {
            PlanetId.MERCURY -> 3f
            PlanetId.VENUS -> 4f
            PlanetId.JUPITER -> 5f
            PlanetId.SATURN -> 6f
            PlanetId.URANUS -> 7f
            PlanetId.NEPTUNE -> 8f
            else -> 0f
        }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        PlanetWebGLRenderer.ensureInitialized(scope)
    }

    DisposableEffect(isPageActive) {
        PlanetWebGLRenderer.setVisible(isPageActive)
        onDispose {
            PlanetWebGLRenderer.setVisible(false)
        }
    }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition, isPageActive, planetTypeFloat, planetId) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(planetType = planetTypeFloat, isMoon = false)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
            PlanetWebGLRenderer.loadPlanetTexture(scope, planetId)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(planetType = planetTypeFloat, isMoon = false)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }
    }
}
