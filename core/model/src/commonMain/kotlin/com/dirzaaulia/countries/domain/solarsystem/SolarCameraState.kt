package com.dirzaaulia.countries.domain.solarsystem

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class SolarCameraState(
    val pivotTarget: PlanetId = PlanetId.SUN,
    val yawDeg: Float = 0f,
    val pitchDeg: Float = 52f,
    val zoomScale: Float = 1.0f,
    val panOffset: Offset = Offset.Zero,
    val flightProgress: Float = 0f,
    val focusX: Double = 0.0,
    val focusY: Double = 0.0,
    val focusZ: Double = 0.0,
)

fun projectCelestialPoint(
    xAu: Double,
    yAu: Double,
    zAu: Double,
    focusX: Double,
    focusY: Double,
    focusZ: Double,
    screenCenter: Offset,
    baseRadius: Float,
    yawRad: Float,
    pitchRad: Float,
    panOffset: Offset = Offset.Zero,
): Offset {
    val relX = xAu - focusX
    val relY = yAu - focusY
    val relZ = zAu - focusZ

    val cosYaw = cos(yawRad.toDouble())
    val sinYaw = sin(yawRad.toDouble())
    val rotX = relX * cosYaw - relY * sinYaw
    val rotY = relX * sinYaw + relY * cosYaw

    val cosPitch = cos(pitchRad.toDouble())
    val sinPitch = sin(pitchRad.toDouble())
    val projY = rotY * cosPitch - relZ * sinPitch
    val projZ = rotY * sinPitch + relZ * cosPitch

    val rAu = sqrt(rotX * rotX + projY * projY + projZ * projZ)
    if (rAu < 0.00001) return Offset(screenCenter.x + panOffset.x, screenCenter.y + panOffset.y)

    val scaleFactor = (baseRadius / sqrt(rAu)).toFloat()
    val finalX = (rotX * scaleFactor).toFloat()
    val finalY = (projY * scaleFactor).toFloat()

    return Offset(
        screenCenter.x + panOffset.x + finalX,
        screenCenter.y + panOffset.y - finalY,
    )
}

fun interpolateHermite(
    p0: Double,
    p1: Double,
    t: Float,
): Double {
    val tDouble = t.toDouble()
    val t2 = tDouble * tDouble
    val t3 = t2 * tDouble
    return (2 * t3 - 3 * t2 + 1) * p0 + (-2 * t3 + 3 * t2) * p1
}

fun findPlanetAtScreenPos(
    tapOffset: Offset,
    canvasSize: androidx.compose.ui.unit.IntSize,
    planets: List<LivePlanetPosition>,
    selectedPlanet: LivePlanetPosition?,
    zoomFactor: Float,
    tiltAngleX: Float,
    rotationZ: Float,
    panOffset: Offset,
): LivePlanetPosition? {
    val screenCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val baseRadius = (minOf(canvasSize.width, canvasSize.height) * 0.42f) * zoomFactor
    val yawRad = rotationZ * (kotlin.math.PI.toFloat() / 180f)
    val pitchRad = tiltAngleX * (kotlin.math.PI.toFloat() / 180f)

    val focusX = selectedPlanet?.xAu ?: 0.0
    val focusY = selectedPlanet?.yAu ?: 0.0
    val focusZ = selectedPlanet?.zAu ?: 0.0

    val maxTouchRadiusPx = 48.0f

    // Check if the tap is within the circle or within the extended label area to the right
    return planets
        .filter { livePos ->
            val screenPos =
                projectCelestialPoint(
                    xAu = livePos.xAu,
                    yAu = livePos.yAu,
                    zAu = livePos.zAu,
                    focusX = focusX,
                    focusY = focusY,
                    focusZ = focusZ,
                    screenCenter = screenCenter,
                    baseRadius = baseRadius,
                    yawRad = yawRad,
                    pitchRad = pitchRad,
                    panOffset = panOffset,
                )
            val dx = tapOffset.x - screenPos.x
            val dy = tapOffset.y - screenPos.y

            // Hit box: original circle + an extended rectangular area to the right for the text label
            val hitCircle = (dx * dx + dy * dy) <= maxTouchRadiusPx * maxTouchRadiusPx
            val hitLabel = (dx >= 0f && dx <= 120f && dy >= -maxTouchRadiusPx && dy <= maxTouchRadiusPx)

            hitCircle || hitLabel
        }.minByOrNull { livePos ->
            val screenPos =
                projectCelestialPoint(
                    xAu = livePos.xAu,
                    yAu = livePos.yAu,
                    zAu = livePos.zAu,
                    focusX = focusX,
                    focusY = focusY,
                    focusZ = focusZ,
                    screenCenter = screenCenter,
                    baseRadius = baseRadius,
                    yawRad = yawRad,
                    pitchRad = pitchRad,
                    panOffset = panOffset,
                )
            // Still compute closest planet by distance to the origin center of the planet
            val dx = screenPos.x - tapOffset.x
            val dy = screenPos.y - tapOffset.y
            (dx * dx + dy * dy).toDouble()
        }
}
