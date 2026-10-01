package com.dirzaaulia.countries.platform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId

/**
 * Web/WASM implementation of Globe3DPlatformView.
 * Renders full photorealistic 3D Earth using WebGL 1.0/2.0 shaders and 2K textures,
 * with atmospheric limb glow and real-time solar terminator shading.
 */
@Composable
actual fun Globe3DPlatformView(
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        PlanetWebGLRenderer.ensureInitialized(scope)
    }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition, isPageActive) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(planetType = 0.0f, isMoon = false)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(planetType = 0.0f, isMoon = false)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }

        // 1. Clear destination pixels inside sphere disc so WebGL 3D globe shines through
        drawCircle(
            color = Color.Transparent,
            radius = currentRadius,
            center = canvasCenter,
            blendMode = BlendMode.Clear,
        )

        // 2. Deep cosmic outer atmosphere glow (Rayleigh scattering halo)
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            Color(0xFF3892FF).copy(alpha = 0.30f),
                            Color(0xFF1E60CC).copy(alpha = 0.16f),
                            Color(0xFF0F3277).copy(alpha = 0.05f),
                            Color.Transparent,
                        ),
                    center = canvasCenter,
                    radius = currentRadius * 1.18f,
                ),
            radius = currentRadius * 1.18f,
            center = canvasCenter,
        )

        // 3. Subtle razor-thin atmospheric limb edge
        drawCircle(
            color = Color(0xFF64B5F6).copy(alpha = 0.40f),
            radius = currentRadius,
            center = canvasCenter,
            style = Stroke(width = 1.5f),
        )
    }
}

/**
 * Web/WASM implementation of Moon3DPlatformView.
 * Renders full photorealistic 3D Moon using WebGL 1.0/2.0 shaders and NASA LRO 2K texture,
 * with real-time astronomical phase terminator shading.
 */
@Composable
actual fun Moon3DPlatformView(
    state: GlobeState,
    phaseAngle: Double,
    subsolarLatitude: Double,
    librationLatitude: Double,
    librationLongitude: Double,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        PlanetWebGLRenderer.ensureInitialized(scope)
    }

    LaunchedEffect(
        state.rotationX,
        state.rotationY,
        state.zoom,
        phaseAngle,
        subsolarLatitude,
        librationLatitude,
        librationLongitude,
        isPageActive,
    ) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(
                planetType = 1.0f,
                isMoon = true,
                phaseAngle = phaseAngle,
                subsolarLatitude = subsolarLatitude,
                librationLatitude = librationLatitude,
                librationLongitude = librationLongitude,
            )
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(
                planetType = 1.0f,
                isMoon = true,
                phaseAngle = phaseAngle,
                subsolarLatitude = subsolarLatitude,
                librationLatitude = librationLatitude,
                librationLongitude = librationLongitude,
            )
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }

        // 1. Clear destination pixels inside sphere disc so WebGL 3D moon shines through
        drawCircle(
            color = Color.Transparent,
            radius = currentRadius,
            center = canvasCenter,
            blendMode = BlendMode.Clear,
        )

        // 2. Soft lunar outer starlight halo
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            Color(0x30F1F5F9),
                            Color(0x12CBD5E1),
                            Color(0x0494A3B8),
                            Color.Transparent,
                        ),
                    center = canvasCenter,
                    radius = currentRadius * 1.25f,
                ),
            radius = currentRadius * 1.25f,
            center = canvasCenter,
        )

        // 3. Subtle lunar limb contour
        drawCircle(
            color = Color(0x55E2E8F0),
            radius = currentRadius,
            center = canvasCenter,
            style = Stroke(width = 1.2f),
        )
    }
}

/**
 * Web/WASM implementation of Mars3DPlatformView.
 * Renders full photorealistic 3D Mars using WebGL 1.0/2.0 shaders and 2K texture,
 * with real-time astronomical phase terminator shading.
 */
@Composable
actual fun Mars3DPlatformView(
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        PlanetWebGLRenderer.ensureInitialized(scope)
    }

    LaunchedEffect(
        state.rotationX,
        state.rotationY,
        state.zoom,
        sunPosition,
        isPageActive,
    ) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(
                planetType = 2.0f,
                isMoon = false,
                isMars = true,
            )
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(
                planetType = 2.0f,
                isMoon = false,
                isMars = true,
            )
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }

        drawCircle(
            color = Color.Transparent,
            radius = currentRadius,
            center = canvasCenter,
            blendMode = BlendMode.Clear,
        )

        // Soft Martian outer atmosphere glow
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            Color(0x30E67E22), // Orange glow
                            Color(0x15D35400),
                            Color(0x04873600),
                            Color.Transparent,
                        ),
                    center = canvasCenter,
                    radius = currentRadius * 1.25f,
                ),
            radius = currentRadius * 1.25f,
            center = canvasCenter,
        )

        // Subtle Martian limb contour
        drawCircle(
            color = Color(0x66E67E22),
            radius = currentRadius,
            center = canvasCenter,
            style = Stroke(width = 1.2f),
        )
    }
}

@Composable
actual fun Planet3DPlatformView(
    planetId: PlanetId,
    state: GlobeState,
    sunPosition: SunPosition,
    isPageActive: Boolean,
    modifier: Modifier,
) {
    if (planetId == PlanetId.EARTH) {
        Globe3DPlatformView(state, sunPosition, isPageActive, modifier)
        return
    }
    if (planetId == PlanetId.MOON) {
        val planetTypeFloat =
            when (planetId) {
                PlanetId.EARTH -> 0f
                PlanetId.MOON -> 1f
                PlanetId.MARS -> 2f
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

        LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition, isPageActive, planetTypeFloat) {
            if (isPageActive) {
                PlanetWebGLRenderer.setPlanetMode(planetType = planetTypeFloat, isMoon = true)
                PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
                PlanetWebGLRenderer.setSunPosition(sunPosition)
            }
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasCenter = center
            val baseRadius = minOf(size.width, size.height) * 0.38f
            val currentRadius = baseRadius * state.zoom

            if (isPageActive) {
                PlanetWebGLRenderer.setPlanetMode(planetType = planetTypeFloat, isMoon = true)
                PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
                PlanetWebGLRenderer.setSunPosition(sunPosition)
                PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
            }

            drawCircle(
                color = Color.Transparent,
                radius = currentRadius,
                center = canvasCenter,
                blendMode = BlendMode.Clear,
            )
        }
        return
    }
    if (planetId == PlanetId.MARS) {
        Mars3DPlatformView(state, sunPosition, isPageActive, modifier)
        return
    }

    val planetTypeFloat =
        when (planetId) {
            PlanetId.EARTH -> 0f
            PlanetId.MOON -> 1f
            PlanetId.MARS -> 2f
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

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, sunPosition, isPageActive, planetTypeFloat) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(planetType = planetTypeFloat, isMoon = true)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(planetType = planetTypeFloat, isMoon = true)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.setSunPosition(sunPosition)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }

        drawCircle(
            color = Color.Transparent,
            radius = currentRadius,
            center = canvasCenter,
            blendMode = BlendMode.Clear,
        )
    }
}
