package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.dirzaaulia.countries.domain.solarsystem.PlanetId

private data class PlanetPalette(
    val dayColor: Color,
    val nightColor: Color,
)

private val PLANET_PALETTES =
    mapOf(
        PlanetId.MERCURY to PlanetPalette(Color(0xFFCBD5E1), Color(0xFF0F172A)),
        PlanetId.VENUS to PlanetPalette(Color(0xFFF59E0B), Color(0xFF78350F)),
        PlanetId.EARTH to PlanetPalette(Color(0xFF38BDF8), Color(0xFF082F49)),
        PlanetId.MOON to PlanetPalette(Color(0xFFFFD54F), Color(0xFF78350F)),
        PlanetId.MARS to PlanetPalette(Color(0xFFEF4444), Color(0xFF4C0519)),
        PlanetId.JUPITER to PlanetPalette(Color(0xFFF59E0B), Color(0xFF451A03)),
        PlanetId.SATURN to PlanetPalette(Color(0xFFFDE047), Color(0xFF713F12)),
        PlanetId.URANUS to PlanetPalette(Color(0xFF2DD4BF), Color(0xFF134E4A)),
        PlanetId.NEPTUNE to PlanetPalette(Color(0xFF818CF8), Color(0xFF1E1B4B)),
    )

internal fun DrawScope.drawShadedPlanet(
    planetId: PlanetId,
    center: Offset,
    radius: Float,
    isSelected: Boolean,
) {
    val palette = PLANET_PALETTES[planetId] ?: PlanetPalette(Color.White, Color(0xFF1E293B))

    if (isSelected) drawSelectionReticle(center, radius, palette.dayColor)
    drawGlowAura(center, radius, palette.dayColor)

    // Bold Outer Outline Ring Circle
    val ringStrokeWidth = (radius * 0.18f).coerceIn(3.5f, 12f)
    drawCircle(
        color = palette.dayColor,
        radius = radius,
        center = center,
        style = Stroke(width = ringStrokeWidth),
    )

    // Saturn 3D Orbital Rings
    if (planetId == PlanetId.SATURN) {
        drawSaturn3DRings(center, radius)
    }
}

private fun DrawScope.drawGlowAura(
    center: Offset,
    radius: Float,
    color: Color,
) {
    drawCircle(
        brush =
            Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.30f), Color.Transparent),
                center = center,
                radius = radius * 2.0f,
            ),
        radius = radius * 2.0f,
        center = center,
    )
}

private fun DrawScope.drawSelectionReticle(
    center: Offset,
    radius: Float,
    color: Color,
) {
    drawCircle(color = color.copy(alpha = 0.25f), radius = radius * 2.8f, center = center)
    drawCircle(color = Color.White, radius = radius * 2.0f, center = center, style = Stroke(width = 2.0f))
}

private fun DrawScope.drawSaturn3DRings(
    center: Offset,
    radius: Float,
) {
    val rx = radius * 2.4f
    val ry = rx * 0.28f
    drawOval(
        color = Color(0xAAFDE047),
        topLeft = Offset(center.x - rx, center.y - ry),
        size =
            androidx.compose.ui.geometry
                .Size(rx * 2f, ry * 2f),
        style = Stroke(width = (radius * 0.18f).coerceIn(2.0f, 6.0f)),
    )
}
