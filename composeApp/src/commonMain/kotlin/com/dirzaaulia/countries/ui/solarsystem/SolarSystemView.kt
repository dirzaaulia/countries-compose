package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.dirzaaulia.countries.domain.solarsystem.LivePlanetPosition
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.solarsystem.SolarCameraState
import com.dirzaaulia.countries.domain.solarsystem.SolarKeplerianMath
import com.dirzaaulia.countries.domain.solarsystem.findPlanetAtScreenPos
import com.dirzaaulia.countries.domain.solarsystem.interpolateHermite
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
private fun rememberSolarCameraFocus(
    sourcePos: LivePlanetPosition?,
    targetPos: LivePlanetPosition?,
    progress: Float,
): Triple<Double, Double, Double> {
    val focusX = interpolateHermite(sourcePos?.xAu ?: 0.0, targetPos?.xAu ?: 0.0, progress)
    val focusY = interpolateHermite(sourcePos?.yAu ?: 0.0, targetPos?.yAu ?: 0.0, progress)
    val focusZ = interpolateHermite(sourcePos?.zAu ?: 0.0, targetPos?.zAu ?: 0.0, progress) + (sin(progress * PI) * 0.5)
    return Triple(focusX, focusY, focusZ)
}

@Composable
private fun rememberSolarShimmer(): Pair<Float, Float> {
    val transition = rememberInfiniteTransition(label = "solarCosmicShimmer")
    val starTwinkle by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
        label = "starTwinkle",
    )
    val coronaPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label = "coronaPhase",
    )
    return Pair(starTwinkle, coronaPhase)
}

@Composable
fun SolarSystemView(
    currentTimeMillis: Long,
    onDiveToPlanet: (PlanetId, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val diveController = remember { SolarDiveController() }
    val livePositions =
        remember(currentTimeMillis) {
            SolarKeplerianMath.calculateLivePlanetPositions(currentTimeMillis)
        }

    var zoomFactor by remember { mutableFloatStateOf(1.0f) }
    var tiltAngleX by remember { mutableFloatStateOf(52f) }
    var rotationZ by remember { mutableFloatStateOf(0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedPlanet by remember { mutableStateOf<LivePlanetPosition?>(null) }
    var previousPlanet by remember { mutableStateOf<LivePlanetPosition?>(null) }

    val (starTwinkle, coronaPhase) = rememberSolarShimmer()

    val sourcePos = livePositions.find { it.planet.id == diveController.sourcePlanet } ?: previousPlanet
    val targetPos = livePositions.find { it.planet.id == diveController.targetPlanet } ?: selectedPlanet
    val progress = diveController.diveProgress.value
    val effectiveZoom = zoomFactor * (1.0f + progress * 28.0f)
    val (focusX, focusY, focusZ) = rememberSolarCameraFocus(sourcePos, targetPos, progress)

    SolarSystemContent(
        livePositions = livePositions,
        selectedPlanet = selectedPlanet,
        targetPos = targetPos,
        cameraState =
            SolarCameraState(
                pivotTarget = selectedPlanet?.planet?.id ?: PlanetId.SUN,
                yawDeg = rotationZ,
                pitchDeg = tiltAngleX,
                zoomScale = effectiveZoom,
                panOffset = panOffset,
                flightProgress = progress,
                focusX = focusX,
                focusY = focusY,
                focusZ = focusZ,
            ),
        starTwinkle = starTwinkle,
        coronaPhase = coronaPhase,
        onOrbit = { pan ->
            rotationZ = (rotationZ - pan.x * 0.45f) % 360f
            tiltAngleX = (tiltAngleX + pan.y * 0.35f) % 360f
        },
        onPan = { pan -> panOffset = Offset(panOffset.x + pan.x, panOffset.y + pan.y) },
        onZoom = { zoom -> zoomFactor = (zoomFactor * zoom).coerceIn(0.05f, 50.0f) },
        onSelectPlanet = { selectedPlanet = it },
        onTapPos = { tapOffset, canvasSize, isDouble ->
            val clicked =
                findPlanetAtScreenPos(
                    tapOffset = tapOffset,
                    canvasSize = canvasSize,
                    planets = livePositions,
                    selectedPlanet = selectedPlanet,
                    zoomFactor = zoomFactor,
                    tiltAngleX = tiltAngleX,
                    rotationZ = rotationZ,
                    panOffset = panOffset,
                )
            if (clicked != null) {
                previousPlanet = selectedPlanet
                selectedPlanet = clicked
                if (isDouble) {
                    selectedPlanet = null
                    scope.launch {
                        diveController.executeDive(
                            fromPlanet = previousPlanet?.planet?.id,
                            toPlanet = clicked.planet.id,
                            onStartHandoff = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                            onComplete = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                        )
                    }
                }
            } else if (!isDouble) {
                selectedPlanet = null
            }
        },
        onDiveToPlanet = { planetId ->
            selectedPlanet = null
            scope.launch {
                diveController.executeDive(
                    fromPlanet = previousPlanet?.planet?.id,
                    toPlanet = planetId,
                    onStartHandoff = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                    onComplete = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun SolarSystemContent(
    livePositions: List<LivePlanetPosition>,
    selectedPlanet: LivePlanetPosition?,
    targetPos: LivePlanetPosition?,
    cameraState: SolarCameraState,
    starTwinkle: Float,
    coronaPhase: Float,
    onOrbit: (Offset) -> Unit,
    onPan: (Offset) -> Unit,
    onZoom: (Float) -> Unit,
    onSelectPlanet: (LivePlanetPosition?) -> Unit,
    onTapPos: (Offset, androidx.compose.ui.unit.IntSize, Boolean) -> Unit,
    onDiveToPlanet: (PlanetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF020408))
                .pointerInput(Unit) {
                    solarSystemGestures(
                        onOrbit = onOrbit,
                        onPan = onPan,
                        onZoom = onZoom,
                        onDoubleTap = { onTapPos(it, size, true) },
                        onTap = { onTapPos(it, size, false) },
                    )
                },
    ) {
        SolarOrbitCanvas(
            planets = livePositions,
            selectedPlanet = targetPos,
            cameraState = cameraState,
            starTwinkle = starTwinkle,
            coronaPhase = coronaPhase,
            modifier = Modifier.fillMaxSize(),
        )

        SolarPlanetTelemetrySheet(
            livePlanet = selectedPlanet,
            onClose = { onSelectPlanet(null) },
            onDiveToPlanet = onDiveToPlanet,
        )
    }
}
