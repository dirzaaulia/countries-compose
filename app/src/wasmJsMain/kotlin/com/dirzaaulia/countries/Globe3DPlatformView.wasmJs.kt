package com.dirzaaulia.countries

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Web/WASM implementation of Globe3DPlatformView.
 * Renders full photorealistic 3D Earth using WebGL 1.0/2.0 shaders and 2K textures,
 * with atmospheric limb glow and real-time solar terminator shading.
 */
@Composable
actual fun Globe3DPlatformView(
    state: GlobeState,
    isPageActive: Boolean,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        PlanetWebGLRenderer.ensureInitialized(scope)
    }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, isPageActive) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(isMoon = false)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(isMoon = false)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }

        // 1. Clear destination pixels inside sphere disc so WebGL 3D globe shines through
        drawCircle(
            color = Color.Transparent,
            radius = currentRadius,
            center = canvasCenter,
            blendMode = BlendMode.Clear
        )

        // 2. Deep cosmic outer atmosphere glow (Rayleigh scattering halo)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF3892FF).copy(alpha = 0.30f),
                    Color(0xFF1E60CC).copy(alpha = 0.16f),
                    Color(0xFF0F3277).copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = canvasCenter,
                radius = currentRadius * 1.18f
            ),
            radius = currentRadius * 1.18f,
            center = canvasCenter
        )

        // 3. Subtle razor-thin atmospheric limb edge
        drawCircle(
            color = Color(0xFF64B5F6).copy(alpha = 0.40f),
            radius = currentRadius,
            center = canvasCenter,
            style = Stroke(width = 1.5f)
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
    isPageActive: Boolean,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        PlanetWebGLRenderer.ensureInitialized(scope)
    }

    LaunchedEffect(state.rotationX, state.rotationY, state.zoom, phaseAngle, isPageActive) {
        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(isMoon = true, phaseAngle = phaseAngle)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        if (isPageActive) {
            PlanetWebGLRenderer.setPlanetMode(isMoon = true, phaseAngle = phaseAngle)
            PlanetWebGLRenderer.updateCamera(state.rotationX, state.rotationY, state.zoom)
            PlanetWebGLRenderer.render(size.width.toInt(), size.height.toInt())
        }

        // 1. Clear destination pixels inside sphere disc so WebGL 3D moon shines through
        drawCircle(
            color = Color.Transparent,
            radius = currentRadius,
            center = canvasCenter,
            blendMode = BlendMode.Clear
        )

        // 2. Soft lunar outer starlight halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x30F1F5F9),
                    Color(0x12CBD5E1),
                    Color(0x0494A3B8),
                    Color.Transparent
                ),
                center = canvasCenter,
                radius = currentRadius * 1.25f
            ),
            radius = currentRadius * 1.25f,
            center = canvasCenter
        )

        // 3. Subtle lunar limb contour
        drawCircle(
            color = Color(0x55E2E8F0),
            radius = currentRadius,
            center = canvasCenter,
            style = Stroke(width = 1.2f)
        )
    }
}
