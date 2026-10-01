package com.dirzaaulia.countries.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
    onSatPosCalculated: (String, Offset) -> Unit,
) {
    // 1. Render Ground Track for currently selected satellite
    selectedSatellite?.let { sat ->
        if (sat.groundTrack.isNotEmpty()) {
            val trackPath = Path()
            var firstPoint = true

            sat.groundTrack.forEach { coord ->
                val p3d = latLngToCartesian(coord.lat, coord.lng, currentRadius.toDouble())
                val p = rotateX(rotateY(p3d, cosY, sinY), cosX, sinX)
                if (p.z > 0.0) {
                    val sx = canvasCenter.x + p.x.toFloat()
                    val sy = canvasCenter.y - p.y.toFloat()
                    if (firstPoint) {
                        trackPath.moveTo(sx, sy)
                        firstPoint = false
                    } else {
                        trackPath.lineTo(sx, sy)
                    }
                } else {
                    firstPoint = true
                }
            }

            if (!trackPath.isEmpty) {
                drawPath(
                    path = trackPath,
                    color = Color(0xFF38BDF8).copy(alpha = 0.65f),
                    style =
                        Stroke(
                            width = 4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f),
                        ),
                )
            }
        }
    }

    // 2. Render Satellite Beacons
    fleet.forEach { sat ->
        val isJwst = sat.type == SatelliteType.DEEP_SPACE_OBSERVATORY
        val p3d = latLngToCartesian(sat.lat, sat.lng, currentRadius.toDouble())
        val p = rotateX(rotateY(p3d, cosY, sinY), cosX, sinX)

        if (p.z > 0.0 || isJwst) {
            val sx = canvasCenter.x + p.x.toFloat()
            val sy = canvasCenter.y - p.y.toFloat()
            val pos = Offset(sx, sy)
            onSatPosCalculated(sat.id, pos)

            val isSelected = selectedSatellite?.id == sat.id
            val mainColor =
                when (sat.id) {
                    "ISS" -> Color(0xFF38BDF8) // Cyan
                    "CSS" -> Color(0xFFF59E0B) // Golden Amber
                    "HST" -> Color(0xFFA855F7) // Electric Violet
                    else -> Color(0xFF10B981) // Emerald Green (JWST)
                }

            if (isJwst) {
                // JWST Deep-Space Directional Reticle
                drawCircle(
                    color = mainColor.copy(alpha = strobeAlpha * 0.8f),
                    radius = if (isSelected) 18f else 12f,
                    center = pos,
                    style = Stroke(width = 2.5f),
                )
                drawLine(
                    color = mainColor,
                    start = Offset(pos.x - 16f, pos.y),
                    end = Offset(pos.x + 16f, pos.y),
                    strokeWidth = 2f,
                )
                drawLine(
                    color = mainColor,
                    start = Offset(pos.x, pos.y - 16f),
                    end = Offset(pos.x, pos.y + 16f),
                    strokeWidth = 2f,
                )
            } else {
                // 3-Ring Beacon for LEO Space Stations & Telescopes
                drawCircle(
                    color = mainColor.copy(alpha = strobeAlpha * 0.35f),
                    radius = if (isSelected) 24f else 16f,
                    center = pos,
                )
                drawCircle(
                    color = mainColor.copy(alpha = if (isSelected) 0.9f else 0.6f),
                    radius = if (isSelected) 14f else 9f,
                    center = pos,
                    style = Stroke(width = 2f),
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = pos,
                )
            }
        }
    }
}
