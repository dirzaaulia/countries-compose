package com.dirzaaulia.countries.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.rotateX
import com.dirzaaulia.countries.domain.globe.rotateY
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun DrawScope.drawAuroralOval(
    kpIndex: Double,
    sunVector: Point3D,
    waveProgress: Float,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    isDragging: Boolean = false,
) {
    if (kpIndex < 0.1) return

    val normS = sqrt(sunVector.x * sunVector.x + sunVector.y * sunVector.y + sunVector.z * sunVector.z)
    val sNorm =
        if (normS > 0.001) {
            Point3D(sunVector.x / normS, sunVector.y / normS, sunVector.z / normS)
        } else {
            Point3D(0.0, 0.0, 1.0)
        }

    val poles =
        listOf(
            80.5 to -72.5, // Geomagnetic North
            -80.5 to 107.5, // Geomagnetic South
        )

    val baseRadiusDeg = 14.5 + (kpIndex / 9.0) * 16.5
    val wavePhase = (waveProgress.toDouble() * 2.0 * PI)
    val stormFactor = (0.45f + (kpIndex.toFloat() / 9f) * 0.55f).coerceIn(0.4f, 1.0f)

    poles.forEach { (poleLat, poleLng) ->
        val (displacedLat, displacedLng) = computeDisplacedPole(poleLat, poleLng, sNorm, kpIndex)
        val geometry =
            buildCurtainGeometry(
                centerLat = displacedLat,
                centerLng = displacedLng,
                baseRadiusDeg = baseRadiusDeg,
                wavePhase = wavePhase,
                currentRadius = currentRadius,
                canvasCenter = canvasCenter,
                cosX = cosX,
                sinX = sinX,
                cosY = cosY,
                sinY = sinY,
                sNorm = sNorm,
            )

        drawCurtainPaths(geometry, stormFactor, isDragging)
        if (!isDragging) {
            drawAtmosphericRays(geometry.rayPillars, wavePhase, stormFactor)
        }
    }
}

private fun computeDisplacedPole(
    poleLat: Double,
    poleLng: Double,
    sNorm: Point3D,
    kpIndex: Double,
): Pair<Double, Double> {
    val p = latLngToCartesian(poleLat, poleLng, 1.0)
    val antiSolar = Point3D(-sNorm.x, -sNorm.y, -sNorm.z)
    val dot = antiSolar.x * p.x + antiSolar.y * p.y + antiSolar.z * p.z
    val perpX = antiSolar.x - dot * p.x
    val perpY = antiSolar.y - dot * p.y
    val perpZ = antiSolar.z - dot * p.z
    val perpLen = sqrt(perpX * perpX + perpY * perpY + perpZ * perpZ)

    if (perpLen < 0.01) return poleLat to poleLng

    val shiftRad = (3.5 + (kpIndex / 9.0) * 2.5).toRadians
    val cosShift = cos(shiftRad)
    val sinShift = sin(shiftRad)
    val uX = perpX / perpLen
    val uY = perpY / perpLen
    val uZ = perpZ / perpLen

    val cX = p.x * cosShift + uX * sinShift
    val cY = p.y * cosShift + uY * sinShift
    val cZ = p.z * cosShift + uZ * sinShift

    val shiftedLat = asin(cY.coerceIn(-1.0, 1.0)).toDegrees
    val shiftedLng = atan2(cX, cZ).toDegrees
    return shiftedLat to shiftedLng
}

private fun calculateWavyCurtainCoords(
    angleRad: Double,
    baseRadiusDeg: Double,
    wavePhase: Double,
    centerLat: Double,
    centerLng: Double,
    isSecondary: Boolean = false,
): Pair<Double, Double> {
    val phaseOffset = if (isSecondary) 0.35 else 0.0
    val radiusOffset = if (isSecondary) 1.8 else 0.0
    val wave =
        sin((angleRad + phaseOffset) * 4.0 + wavePhase) * 3.2 +
            cos(angleRad * 8.0 - wavePhase * 1.4) * 1.8 +
            sin(angleRad * 16.0 + wavePhase * 2.1) * 0.8 +
            sin(angleRad * 28.0 - wavePhase * 0.7) * 0.35
    val ringDeg = baseRadiusDeg + radiusOffset + wave

    val dLat = ringDeg * cos(angleRad)
    val dLng = (ringDeg * sin(angleRad)) / cos(centerLat.toRadians).coerceAtLeast(0.01)
    val lat = (centerLat + dLat).coerceIn(-90.0, 90.0)
    var lng = centerLng + dLng
    if (lng > 180.0) lng -= 360.0
    if (lng < -180.0) lng += 360.0
    return lat to lng
}

private data class AuroralGeometry(
    val basePath: Path,
    val ribbonPath: Path,
    val crownPath: Path,
    val secondaryRibbonPath: Path,
    val rayPillars: List<Pair<Offset, Offset>>,
)

private fun buildCurtainGeometry(
    centerLat: Double,
    centerLng: Double,
    baseRadiusDeg: Double,
    wavePhase: Double,
    currentRadius: Float,
    canvasCenter: Offset,
    cosX: Double,
    sinX: Double,
    cosY: Double,
    sinY: Double,
    sNorm: Point3D,
): AuroralGeometry {
    val basePath = Path()
    val ribbonPath = Path()
    val crownPath = Path()
    val secRibbonPath = Path()
    val rayPillars = mutableListOf<Pair<Offset, Offset>>()

    val rBase = currentRadius * 1.015f
    val rRibbon = currentRadius * 1.026f
    val rCrown = currentRadius * 1.045f

    var firstPrimary = true
    var firstSec = true
    val steps = 72

    for (i in 0..steps) {
        val angleRad = (i.toDouble() / steps) * 2.0 * PI
        val (lat, lng) = calculateWavyCurtainCoords(angleRad, baseRadiusDeg, wavePhase, centerLat, centerLng)
        val u = latLngToCartesian(lat, lng, 1.0)
        val sunDot = u.x * sNorm.x + u.y * sNorm.y + u.z * sNorm.z

        if (sunDot < -0.05) {
            val rotRibbon = rotateX(rotateY(latLngToCartesian(lat, lng, rRibbon.toDouble()), cosY, sinY), cosX, sinX)
            if (rotRibbon.z > 0.0) {
                val rotBase = rotateX(rotateY(latLngToCartesian(lat, lng, rBase.toDouble()), cosY, sinY), cosX, sinX)
                val crownLat = lat + sin(angleRad * 5.0 + wavePhase) * 0.8
                val rotCrown = rotateX(rotateY(latLngToCartesian(crownLat, lng, rCrown.toDouble()), cosY, sinY), cosX, sinX)

                val pB = Offset(canvasCenter.x + rotBase.x.toFloat(), canvasCenter.y - rotBase.y.toFloat())
                val pR = Offset(canvasCenter.x + rotRibbon.x.toFloat(), canvasCenter.y - rotRibbon.y.toFloat())
                val pC = Offset(canvasCenter.x + rotCrown.x.toFloat(), canvasCenter.y - rotCrown.y.toFloat())

                if (firstPrimary) {
                    basePath.moveTo(pB.x, pB.y)
                    ribbonPath.moveTo(pR.x, pR.y)
                    crownPath.moveTo(pC.x, pC.y)
                    firstPrimary = false
                } else {
                    basePath.lineTo(pB.x, pB.y)
                    ribbonPath.lineTo(pR.x, pR.y)
                    crownPath.lineTo(pC.x, pC.y)
                }

                if (i % 2 == 0) {
                    rayPillars.add(pB to pC)
                }
            } else {
                firstPrimary = true
            }

            val (secLat, secLng) = calculateWavyCurtainCoords(angleRad, baseRadiusDeg, wavePhase, centerLat, centerLng, isSecondary = true)
            val rotSec = rotateX(rotateY(latLngToCartesian(secLat, secLng, rRibbon.toDouble()), cosY, sinY), cosX, sinX)
            if (rotSec.z > 0.0) {
                val pSec = Offset(canvasCenter.x + rotSec.x.toFloat(), canvasCenter.y - rotSec.y.toFloat())
                if (firstSec) {
                    secRibbonPath.moveTo(pSec.x, pSec.y)
                    firstSec = false
                } else {
                    secRibbonPath.lineTo(pSec.x, pSec.y)
                }
            } else {
                firstSec = true
            }
        } else {
            firstPrimary = true
            firstSec = true
        }
    }

    return AuroralGeometry(basePath, ribbonPath, crownPath, secRibbonPath, rayPillars)
}

private fun DrawScope.drawCurtainPaths(
    geometry: AuroralGeometry,
    stormFactor: Float,
    isDragging: Boolean,
) {
    if (!isDragging && !geometry.crownPath.isEmpty) {
        drawPath(
            path = geometry.crownPath,
            color = Color(0xFFF43F5E).copy(alpha = 0.28f * stormFactor),
            style = Stroke(width = 18f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }

    if (!geometry.basePath.isEmpty) {
        drawPath(
            path = geometry.basePath,
            color = Color(0xFFC084FC).copy(alpha = 0.45f * stormFactor),
            style = Stroke(width = if (isDragging) 6f else 12f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }

    if (!isDragging && !geometry.secondaryRibbonPath.isEmpty) {
        drawPath(
            path = geometry.secondaryRibbonPath,
            color = Color(0xFF2DD4BF).copy(alpha = 0.50f * stormFactor),
            style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }

    if (!geometry.ribbonPath.isEmpty) {
        if (!isDragging) {
            drawPath(
                path = geometry.ribbonPath,
                color = Color(0xFF10B981).copy(alpha = 0.32f * stormFactor),
                style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
        drawPath(
            path = geometry.ribbonPath,
            color = Color(0xFF34D399).copy(alpha = 0.75f * stormFactor),
            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawPath(
            path = geometry.ribbonPath,
            color = Color(0xFFA7F3D0).copy(alpha = 0.95f * stormFactor),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

private fun DrawScope.drawAtmosphericRays(
    rayPillars: List<Pair<Offset, Offset>>,
    wavePhase: Double,
    stormFactor: Float,
) {
    rayPillars.forEachIndexed { index, (posB, posC) ->
        val shimmer = (0.55f + 0.45f * sin(index * 1.5 + wavePhase * 3.0)).toFloat().coerceIn(0.2f, 1.0f)
        drawLine(
            color = Color(0x3334D399).copy(alpha = 0.40f * stormFactor * shimmer),
            start = posB,
            end = posC,
            strokeWidth = 2.5f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color(0x44C084FC).copy(alpha = 0.35f * stormFactor * shimmer),
            start = posB,
            end = posC,
            strokeWidth = 1.2f,
            cap = StrokeCap.Round,
        )
    }
}
