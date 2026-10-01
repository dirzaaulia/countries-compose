package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.LivePlanetPosition
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.solarsystem.SolarCameraState
import com.dirzaaulia.countries.domain.solarsystem.SolarKeplerianMath
import com.dirzaaulia.countries.domain.solarsystem.projectCelestialPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
internal fun SolarOrbitCanvas(
    planets: List<LivePlanetPosition>,
    selectedPlanet: LivePlanetPosition?,
    cameraState: SolarCameraState,
    modifier: Modifier = Modifier,
    starTwinkle: Float = 0.5f,
    coronaPhase: Float = 0f,
) {
    val orbitPaths =
        remember {
            PlanetId.entries
                .filter { it != PlanetId.SUN }
                .associateWith { SolarKeplerianMath.computeOrbitPoints(it) }
        }

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val screenCenter = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = (size.minDimension * 0.42f) * cameraState.zoomScale
        val yawRad = cameraState.yawDeg * (PI.toFloat() / 180f)
        val pitchRad = cameraState.pitchDeg * (PI.toFloat() / 180f)

        val focusX = cameraState.focusX
        val focusY = cameraState.focusY
        val focusZ = cameraState.focusZ

        val sunScreenPos = projectCelestialPoint(0.0, 0.0, 0.0, focusX, focusY, focusZ, screenCenter, baseRadius, yawRad, pitchRad, cameraState.panOffset)

        drawSolarStarfield(starTwinkle)
        drawMilkyWayLane(coronaPhase)

        drawAsteroidBelt(sunScreenPos, baseRadius, yawRad, pitchRad)
        drawOrbits3D(orbitPaths, focusX, focusY, focusZ, screenCenter, baseRadius, yawRad, pitchRad, cameraState.panOffset)

        val sunRadius = (22f * cameraState.zoomScale.coerceIn(0.6f, 2.5f)).coerceIn(12f, 36f)
        drawProcSun(sunScreenPos, sunRadius, coronaPhase)

        drawPlanets3D(planets, selectedPlanet, focusX, focusY, focusZ, screenCenter, baseRadius, yawRad, pitchRad, cameraState.zoomScale, cameraState.panOffset, textMeasurer)
    }
}

private fun DrawScope.drawOrbits3D(
    orbitPaths: Map<PlanetId, List<Pair<Double, Double>>>,
    focusX: Double,
    focusY: Double,
    focusZ: Double,
    screenCenter: Offset,
    baseRadius: Float,
    yawRad: Float,
    pitchRad: Float,
    panOffset: Offset,
) {
    orbitPaths.forEach { (planetId, points) ->
        val path = Path()
        var first = true
        points.forEach { (xAu, yAu) ->
            val pos = projectCelestialPoint(xAu, yAu, 0.0, focusX, focusY, focusZ, screenCenter, baseRadius, yawRad, pitchRad, panOffset)
            if (first) {
                path.moveTo(pos.x, pos.y)
                first = false
            } else {
                path.lineTo(pos.x, pos.y)
            }
        }
        path.close()

        val orbitColor =
            when (planetId) {
                PlanetId.MERCURY -> Color(0x6694A3B8)
                PlanetId.VENUS -> Color(0x66F59E0B)
                PlanetId.EARTH -> Color(0x8838BDF8)
                PlanetId.MOON -> Color(0x66FFD54F)
                PlanetId.MARS -> Color(0x66F43F5E)
                PlanetId.JUPITER -> Color(0x66D97706)
                PlanetId.SATURN -> Color(0x66FDE047)
                PlanetId.URANUS -> Color(0x662DD4BF)
                PlanetId.NEPTUNE -> Color(0x666366F1)
                else -> Color(0x3394A3B8)
            }

        drawPath(path = path, color = orbitColor, style = Stroke(width = 1.8f))
    }
}

private fun DrawScope.drawAsteroidBelt(
    sunCenter: Offset,
    baseRadius: Float,
    yawRad: Float,
    pitchRad: Float,
) {
    val beltDistance = 2.7

    for (i in 0 until 120) {
        val angle = (i / 120.0) * 2.0 * PI
        val rVariation = beltDistance + (sin(i * 13.0) * 0.45)
        val xAu = rVariation * cos(angle)
        val yAu = rVariation * sin(angle)
        // Asteroids use absolute coordinate system around sun, focus is (0,0,0)
        val pos = projectCelestialPoint(xAu, yAu, 0.0, 0.0, 0.0, 0.0, sunCenter, baseRadius, yawRad, pitchRad, Offset.Zero)
        drawCircle(color = Color(0x44CBD5E1), radius = 1.0f, center = pos)
    }
}

private fun DrawScope.drawPlanets3D(
    planets: List<LivePlanetPosition>,
    selectedPlanet: LivePlanetPosition?,
    focusX: Double,
    focusY: Double,
    focusZ: Double,
    screenCenter: Offset,
    baseRadius: Float,
    yawRad: Float,
    pitchRad: Float,
    zoomScale: Float,
    panOffset: Offset,
    textMeasurer: TextMeasurer,
) {
    val maxRadius = maxOf(size.width, size.height) * 2.5f
    val baseScale = zoomScale.coerceAtLeast(1.0f).pow(0.85f)

    planets.forEach { livePos ->
        val pos = projectCelestialPoint(livePos.xAu, livePos.yAu, livePos.zAu, focusX, focusY, focusZ, screenCenter, baseRadius, yawRad, pitchRad, panOffset)
        val isSelected = selectedPlanet?.planet?.id == livePos.planet.id

        val baseSize =
            when (livePos.planet.id) {
                PlanetId.MERCURY -> 12f
                PlanetId.VENUS -> 16f
                PlanetId.EARTH -> 18f
                PlanetId.MOON -> 10f
                PlanetId.MARS -> 15f
                PlanetId.JUPITER -> 28f
                PlanetId.SATURN -> 24f
                PlanetId.URANUS -> 20f
                PlanetId.NEPTUNE -> 20f
                else -> 16f
            }

        val dynamicScale =
            if (isSelected && zoomScale > 1.0f) {
                baseScale * (1.0f + (zoomScale - 1.0f) / 28.0f * 18.0f)
            } else {
                baseScale
            }

        val radius = (baseSize * dynamicScale).coerceIn(10f, maxRadius)

        drawShadedPlanet(
            planetId = livePos.planet.id,
            center = pos,
            radius = radius,
            isSelected = isSelected,
        )

        // Draw Planet Name Label next to the ring
        val labelText = livePos.planet.name.uppercase()
        val textResult =
            textMeasurer.measure(
                text = labelText,
                style = TextStyle(color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold),
            )
        val labelX = pos.x + radius + 8f
        val labelY = pos.y - textResult.size.height / 2f
        drawText(textResult, topLeft = Offset(labelX, labelY))
    }
}
