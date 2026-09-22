package com.dirzaaulia.countries

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Web/WASM implementation of Globe3DPlatformView.
 * Renders a high-fidelity space sphere with atmospheric glow,
 * spherical depth gradients, and day/night terminator shading on Web.
 */
@Composable
actual fun Globe3DPlatformView(
    state: GlobeState,
    modifier: Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasCenter = center
        val baseRadius = minOf(size.width, size.height) * 0.38f
        val currentRadius = baseRadius * state.zoom

        // 1. Deep cosmic outer atmosphere glow (Rayleigh scattering halo)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF3892FF).copy(alpha = 0.40f),
                    Color(0xFF1E60CC).copy(alpha = 0.22f),
                    Color(0xFF0F3277).copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = canvasCenter,
                radius = currentRadius * 1.18f
            ),
            radius = currentRadius * 1.18f,
            center = canvasCenter
        )

        // 2. Realistic 3D ocean sphere with spherical depth shading
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2267AB), // Upper illuminated shallow waters
                    Color(0xFF113D6E), // Deep ocean blue
                    Color(0xFF081C33)  // Dark limb depth
                ),
                center = Offset(
                    canvasCenter.x - currentRadius * 0.25f,
                    canvasCenter.y - currentRadius * 0.25f
                ),
                radius = currentRadius * 1.25f
            ),
            radius = currentRadius,
            center = canvasCenter
        )

        // 3. Soft day / night terminator shadow gradient across the sphere
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF020712).copy(alpha = 0.06f),
                    Color(0xFF020712).copy(alpha = 0.32f),
                    Color(0xFF020712).copy(alpha = 0.65f)
                ),
                start = Offset(canvasCenter.x - currentRadius, canvasCenter.y - currentRadius),
                end = Offset(canvasCenter.x + currentRadius, canvasCenter.y + currentRadius)
            ),
            radius = currentRadius,
            center = canvasCenter
        )

        // 4. Subtle razor-thin atmospheric limb edge
        drawCircle(
            color = Color(0xFF64B5F6).copy(alpha = 0.35f),
            radius = currentRadius,
            center = canvasCenter,
            style = Stroke(width = 1.5f)
        )
    }
}
