package com.dirzaaulia.countries.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toRadians
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawAuroralOval(
    kpIndex: Double,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
) {
    if (kpIndex < 0.1) return

    val poles =
        listOf(
            80.5 to -72.5, // Geomagnetic North
            -80.5 to 107.5, // Geomagnetic South
        )

    val baseRadiusDeg = 14.0 + (kpIndex / 9.0) * 18.0
    val baseRadius = currentRadius * 1.022f
    val crownRadius = currentRadius * 1.045f

    poles.forEach { (poleLat, poleLng) ->
        val basePath = Path()
        val crownPath = Path()
        val rayPoints = mutableListOf<Pair<Offset, Offset>>()

        var firstBase = true
        var firstCrown = true
        val steps = 60

        for (i in 0..steps) {
            val angleRad = (i.toDouble() / steps) * 2.0 * PI
            // Sinusoidal harmonic folds mimicking realistic auroral curtain undulations
            val curtainWiggle = sin(angleRad * 6.0) * 1.5 + cos(angleRad * 11.0) * 0.8
            val ringDeg = baseRadiusDeg + curtainWiggle

            val dLat = ringDeg * cos(angleRad)
            val dLng = (ringDeg * sin(angleRad)) / cos(poleLat.toRadians)

            val lat = (poleLat + dLat).coerceIn(-90.0, 90.0)
            var lng = poleLng + dLng
            if (lng > 180.0) lng -= 360.0
            if (lng < -180.0) lng += 360.0

            // Base curtain point (100km altitude)
            val pBase = latLngToCartesian(lat, lng, baseRadius.toDouble())
            val rotBase = rotateX(rotateY(pBase, cosY, sinY), cosX, sinX)

            // Crown curtain point (250km altitude)
            val pCrown = latLngToCartesian(lat, lng, crownRadius.toDouble())
            val rotCrown = rotateX(rotateY(pCrown, cosY, sinY), cosX, sinX)

            if (rotBase.z > 0.0) {
                val sxB = canvasCenter.x + rotBase.x.toFloat()
                val syB = canvasCenter.y - rotBase.y.toFloat()
                val posB = Offset(sxB, syB)

                if (firstBase) {
                    basePath.moveTo(sxB, syB)
                    firstBase = false
                } else {
                    basePath.lineTo(sxB, syB)
                }

                if (rotCrown.z > 0.0) {
                    val sxC = canvasCenter.x + rotCrown.x.toFloat()
                    val syC = canvasCenter.y - rotCrown.y.toFloat()
                    val posC = Offset(sxC, syC)

                    if (firstCrown) {
                        crownPath.moveTo(sxC, syC)
                        firstCrown = false
                    } else {
                        crownPath.lineTo(sxC, syC)
                    }

                    if (i % 3 == 0) {
                        rayPoints.add(posB to posC)
                    }
                }
            } else {
                firstBase = true
                firstCrown = true
            }
        }

        // Draw vertical atmospheric rays connecting base to crown
        rayPoints.forEach { (posB, posC) ->
            drawLine(
                color = Color(0x4434D399),
                start = posB,
                end = posC,
                strokeWidth = 3.0f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Color(0x55C084FC),
                start = posB,
                end = posC,
                strokeWidth = 1.5f,
                cap = StrokeCap.Round,
            )
        }

        // Base green curtain glow
        if (!basePath.isEmpty) {
            drawPath(
                path = basePath,
                color = Color(0x3334D399),
                style = Stroke(width = 32f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                path = basePath,
                color = Color(0x552DD4BF),
                style = Stroke(width = 18f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                path = basePath,
                color = Color(0xEEA7F3D0),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }

        // Upper violet/magenta crown fringe
        if (!crownPath.isEmpty) {
            drawPath(
                path = crownPath,
                color = Color(0x44C084FC),
                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
                path = crownPath,
                color = Color(0xAA8B5CF6),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}
