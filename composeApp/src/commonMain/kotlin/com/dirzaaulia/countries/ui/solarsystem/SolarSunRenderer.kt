package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders a physically-inspired Sun with limb darkening and pulsing corona rays.
 *
 * Limb darkening: I(mu) = I0 * (1 - u * (1 - mu)) where u~0.6 gives hot white
 * centre and deeper golden rim — canonical stellar appearance.
 *
 * [coronaPhase] drives the dual-frequency pulsing flare animation [0f, 1f].
 */
internal fun DrawScope.drawProcSun(
    center: Offset,
    radius: Float,
    coronaPhase: Float,
) {
    drawSunCorona(center, radius, coronaPhase)
    drawSunDisc(center, radius)
    drawSunHighlight(center, radius)
}

private fun DrawScope.drawSunDisc(
    center: Offset,
    radius: Float,
) {
    // Limb-darkened radial gradient: white core -> yellow -> amber rim
    drawCircle(
        brush =
            Brush.radialGradient(
                colorStops =
                    arrayOf(
                        0.00f to Color(0xFFFFFFFF),
                        0.35f to Color(0xFFFEF9C3),
                        0.65f to Color(0xFFFBBF24),
                        0.85f to Color(0xFFF59E0B),
                        1.00f to Color(0xFFD97706),
                    ),
                center = center,
                radius = radius,
            ),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawSunHighlight(
    center: Offset,
    radius: Float,
) {
    // Inner hotspot — simulates optical core brightness
    drawCircle(color = Color(0x80FFFFFF), radius = radius * 0.28f, center = center)
}

private fun DrawScope.drawSunCorona(
    center: Offset,
    radius: Float,
    phase: Float,
) {
    // Outer glow aura — 3-layer atmospheric haze
    drawCircle(
        brush =
            Brush.radialGradient(
                colors = listOf(Color(0x55FCD34D), Color(0x22F59E0B), Color.Transparent),
                center = center,
                radius = radius * 5.5f,
            ),
        radius = radius * 5.5f,
        center = center,
    )
    // Pulsing corona rays: 8 primary + 8 secondary at two different frequencies
    drawCoronaRays(center, radius, phase, count = 8, lengthScale = 1.9f, alpha = 0.22f, freqMult = 1f)
    drawCoronaRays(center, radius, phase, count = 8, lengthScale = 1.45f, alpha = 0.14f, freqMult = 1.618f)
}

private fun DrawScope.drawCoronaRays(
    center: Offset,
    radius: Float,
    phase: Float,
    count: Int,
    lengthScale: Float,
    alpha: Float,
    freqMult: Float,
) {
    val baseAngle = phase * PI.toFloat() * 2f * freqMult
    for (i in 0 until count) {
        val angle = baseAngle + i * (2f * PI.toFloat() / count)
        val rayLen = radius * (lengthScale + 0.25f * sin(angle * 3f + phase * 5f))
        val x = center.x + rayLen * cos(angle)
        val y = center.y + rayLen * sin(angle)
        drawLine(
            brush =
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFBBF24).copy(alpha = alpha), Color.Transparent),
                    start = center,
                    end = Offset(x, y),
                ),
            start = center,
            end = Offset(x, y),
            strokeWidth = radius * 0.12f,
        )
    }
}
