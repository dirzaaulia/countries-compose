package com.dirzaaulia.countries.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.domain.satellite.SatelliteType

fun DrawScope.drawSatelliteFleet(
    fleet: List<SatelliteTelemetry>,
    selectedSatellite: SatelliteTelemetry?,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float,
    textMeasurer: TextMeasurer,
    isDragging: Boolean = false,
    onSatPosCalculated: (String, Offset) -> Unit,
) {
    // 1. Render Concurrent 3D Hovering Orbital Trajectories for All Satellites
    fleet.forEach { sat ->
        val isSelected = selectedSatellite?.id == sat.id
        val hasSelection = selectedSatellite != null
        val alphaMult =
            when {
                isSelected -> 1.0f
                !hasSelection -> 0.70f
                else -> 0.28f
            }
        drawHoveringOrbit(
            sat = sat,
            currentRadius = currentRadius,
            canvasCenter = canvasCenter,
            cosX = cosX,
            sinX = sinX,
            cosY = cosY,
            sinY = sinY,
            isDragging = isDragging,
            alphaMultiplier = alphaMult,
            drawTether = isSelected || (!hasSelection && sat.id == "ISS"),
        )
    }

    // 2. Render Satellite Beacons in 3D Space
    fleet.forEach { sat ->
        drawSatelliteBeacon(
            sat = sat,
            isSelected = selectedSatellite?.id == sat.id,
            currentRadius = currentRadius,
            canvasCenter = canvasCenter,
            cosX = cosX,
            sinX = sinX,
            cosY = cosY,
            sinY = sinY,
            strobeAlpha = strobeAlpha,
            textMeasurer = textMeasurer,
            isDragging = isDragging,
            onSatPosCalculated = onSatPosCalculated,
        )
    }
}

private fun DrawScope.drawHoveringOrbit(
    sat: SatelliteTelemetry,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    isDragging: Boolean,
    alphaMultiplier: Float = 1.0f,
    drawTether: Boolean = true,
) {
    if (sat.groundTrack.isEmpty()) return

    val altFactor = getAltitudeFactor(sat.id)
    val orbitRadius = currentRadius * altFactor
    val mainColor = getSatelliteColor(sat.id)
    val orbitPath = Path()
    var firstPoint = true

    sat.groundTrack.forEach { coord ->
        val p3d = latLngToCartesian(coord.lat, coord.lng, orbitRadius.toDouble())
        val p = rotateX(rotateY(p3d, cosY, sinY), cosX, sinX)
        if (p.z > 0.0) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            if (firstPoint) {
                orbitPath.moveTo(sx, sy)
                firstPoint = false
            } else {
                orbitPath.lineTo(sx, sy)
            }
        } else {
            firstPoint = true
        }
    }

    if (!orbitPath.isEmpty) {
        if (!isDragging) {
            drawPath(
                path = orbitPath,
                color = mainColor.copy(alpha = 0.25f * alphaMultiplier),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round),
            )
        }
        drawPath(
            path = orbitPath,
            color = mainColor.copy(alpha = 0.85f * alphaMultiplier),
            style =
                Stroke(
                    width = 1.8f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
                ),
        )
    }

    if (!isDragging && drawTether) {
        drawNadirTether(sat, currentRadius, orbitRadius, canvasCenter, cosX, sinX, cosY, sinY, mainColor)
    }
}

private fun DrawScope.drawNadirTether(
    sat: SatelliteTelemetry,
    currentRadius: Float,
    orbitRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    color: Color,
) {
    val pGround = rotateX(rotateY(latLngToCartesian(sat.lat, sat.lng, currentRadius.toDouble()), cosY, sinY), cosX, sinX)
    val pHover = rotateX(rotateY(latLngToCartesian(sat.lat, sat.lng, orbitRadius.toDouble()), cosY, sinY), cosX, sinX)

    if (pGround.z > 0.0 && pHover.z > 0.0) {
        val groundPos = Offset(canvasCenter.x + pGround.x.toFloat(), canvasCenter.y - pGround.y.toFloat())
        val hoverPos = Offset(canvasCenter.x + pHover.x.toFloat(), canvasCenter.y - pHover.y.toFloat())

        drawLine(
            color = color.copy(alpha = 0.40f),
            start = groundPos,
            end = hoverPos,
            strokeWidth = 1.2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f),
        )
        drawCircle(
            color = color.copy(alpha = 0.35f),
            radius = 3.0f,
            center = groundPos,
        )
    }
}

private fun DrawScope.drawSatelliteBeacon(
    sat: SatelliteTelemetry,
    isSelected: Boolean,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    strobeAlpha: Float,
    textMeasurer: TextMeasurer,
    isDragging: Boolean,
    onSatPosCalculated: (String, Offset) -> Unit,
) {
    val isJwst = sat.type == SatelliteType.DEEP_SPACE_OBSERVATORY
    val satRadius = if (isJwst) currentRadius * 1.35f else currentRadius * getAltitudeFactor(sat.id)
    val p3d = latLngToCartesian(sat.lat, sat.lng, satRadius.toDouble())
    val p = rotateX(rotateY(p3d, cosY, sinY), cosX, sinX)

    if (p.z > 0.0 || isJwst) {
        val sx = canvasCenter.x + p.x.toFloat()
        val sy = canvasCenter.y - p.y.toFloat()
        val pos = Offset(sx, sy)
        onSatPosCalculated(sat.id, pos)

        val mainColor = getSatelliteColor(sat.id)

        // Pulsing Strobe Aura Ring
        if (!isDragging) {
            drawCircle(
                color = mainColor.copy(alpha = strobeAlpha * 0.40f),
                radius = if (isSelected) 22f else 14f,
                center = pos,
            )
        }

        if (isJwst) {
            // JWST Gold Hexagonal Mirror + Sunshield Vector Icon
            val mirrorColor = Color(0xFFF59E0B)
            val shieldColor = Color(0xFFE2E8F0)

            // Diamond/Hexagonal Sunshield Base
            val shieldPath = Path()
            shieldPath.moveTo(pos.x - 14f, pos.y)
            shieldPath.lineTo(pos.x, pos.y - 8f)
            shieldPath.lineTo(pos.x + 14f, pos.y)
            shieldPath.lineTo(pos.x, pos.y + 8f)
            shieldPath.close()
            drawPath(path = shieldPath, color = shieldColor.copy(alpha = 0.6f))
            drawPath(path = shieldPath, color = mirrorColor, style = Stroke(width = 1.5f))

            // Primary Hexagonal Gold Mirror Array
            drawCircle(color = mirrorColor, radius = 5f, center = pos)
            drawCircle(color = Color.White, radius = 2f, center = pos)
        } else {
            // Satellite Icon: Main Bus + Extended Solar Wings
            val wingColor = mainColor.copy(alpha = 0.95f)
            val busColor = Color.White

            // Solar Panel Wings (Left & Right)
            drawLine(
                color = wingColor,
                start = Offset(pos.x - 12f, pos.y),
                end = Offset(pos.x + 12f, pos.y),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round,
            )

            // Spacecraft Main Bus Body
            drawCircle(color = busColor, radius = 3.5f, center = pos)
            drawCircle(color = mainColor, radius = 2.0f, center = pos)
        }

        // Non-intrusive Satellite Name Tag
        val labelText = sat.acronym
        val textResult =
            textMeasurer.measure(
                text = labelText,
                style = TextStyle(color = mainColor, fontSize = 9.sp, fontWeight = FontWeight.Bold),
            )
        drawText(textResult, topLeft = Offset(pos.x + 14f, pos.y - textResult.size.height / 2f))
    }
}

private fun getAltitudeFactor(satelliteId: String): Float =
    when (satelliteId) {
        "ISS" -> 1.066f
        "CSS" -> 1.061f
        "HST" -> 1.085f
        "JWST" -> 1.350f
        else -> 1.070f
    }

private fun getSatelliteColor(satelliteId: String): Color =
    when (satelliteId) {
        "ISS" -> Color(0xFF38BDF8) // Sky Blue
        "CSS" -> Color(0xFFF59E0B) // Amber Gold
        "HST" -> Color(0xFFA855F7) // Royal Purple
        "JWST" -> Color(0xFFEAB308) // Deep Gold
        "TERRA" -> Color(0xFF10B981) // Emerald Green
        "AQUA" -> Color(0xFF06B6D4) // Oceanic Cyan
        "AURA" -> Color(0xFF818CF8) // Indigo
        "LDST8" -> Color(0xFF34D399) // Mint Teal
        "LDST9" -> Color(0xFF2DD4BF) // Cyan Teal
        "ICEST2" -> Color(0xFF67E8F9) // Ice Cyan
        "OCO2" -> Color(0xFFA3E635) // Lime
        "TESS" -> Color(0xFFFB7185) // Rose Pink
        "SMAP" -> Color(0xFFF97316) // Coral
        else -> Color(0xFF38BDF8)
    }
