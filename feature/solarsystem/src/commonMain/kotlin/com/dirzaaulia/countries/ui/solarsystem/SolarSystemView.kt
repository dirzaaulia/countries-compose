package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
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
    isFeatureInstalled: (PlanetId) -> Boolean = { true },
    showControlsCard: Boolean = true,
    onToggleControlsCard: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
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
    var dossierPlanetId by remember { mutableStateOf<PlanetId?>(null) }

    val (starTwinkle, coronaPhase) = rememberSolarShimmer()

    val sourcePos = livePositions.find { it.planet.id == diveController.sourcePlanet } ?: previousPlanet
    val targetPos = livePositions.find { it.planet.id == diveController.targetPlanet } ?: selectedPlanet
    val progress = diveController.diveProgress.value
    val effectiveZoom = zoomFactor * (1.0f + progress * 28.0f)

    SolarSystemContent(
        livePositions = livePositions,
        selectedPlanet = selectedPlanet,
        dossierPlanetId = dossierPlanetId,
        targetPos = targetPos,
        cameraState =
            SolarCameraState(
                pivotTarget = selectedPlanet?.planet?.id ?: PlanetId.SUN,
                yawDeg = rotationZ,
                pitchDeg = tiltAngleX,
                zoomScale = effectiveZoom,
                panOffset = panOffset,
                flightProgress = progress,
            ),
        starTwinkle = starTwinkle,
        coronaPhase = coronaPhase,
        onOrbit = { rot ->
            rotationZ = (rotationZ + rot.x * 0.45f) % 360f
            tiltAngleX = (tiltAngleX - rot.y * 0.35f).coerceIn(10f, 85f)
        },
        onPan = { pan -> panOffset += pan },
        onZoom = { factor -> zoomFactor = (zoomFactor * factor).coerceIn(0.25f, 6.0f) },
        onResetZoom = { zoomFactor = 1.0f },
        onResetCamera = {
            zoomFactor = 1.0f
            tiltAngleX = 52f
            rotationZ = 0f
            panOffset = Offset.Zero
            selectedPlanet = null
        },
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
                    val installed = isFeatureInstalled(clicked.planet.id)
                    selectedPlanet = null
                    if (installed) {
                        scope.launch {
                            diveController.executeDive(
                                fromPlanet = previousPlanet?.planet?.id,
                                toPlanet = clicked.planet.id,
                                onStartHandoff = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                                onComplete = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                            )
                        }
                    } else {
                        onDiveToPlanet(clicked.planet.id, tiltAngleX, rotationZ)
                    }
                }
            } else if (!isDouble) {
                selectedPlanet = null
            }
        },
        onDiveToPlanet = { planetId ->
            val installed = isFeatureInstalled(planetId)
            selectedPlanet = null
            if (installed) {
                scope.launch {
                    diveController.executeDive(
                        fromPlanet = previousPlanet?.planet?.id,
                        toPlanet = planetId,
                        onStartHandoff = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                        onComplete = { onDiveToPlanet(it, tiltAngleX, rotationZ) },
                    )
                }
            } else {
                onDiveToPlanet(planetId, tiltAngleX, rotationZ)
            }
        },
        onOpenDossier = { dossierPlanetId = it },
        isFeatureInstalled = isFeatureInstalled,
        showControlsCard = showControlsCard,
        onToggleControlsCard = onToggleControlsCard,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}

@Composable
private fun SolarSystemContent(
    livePositions: List<LivePlanetPosition>,
    selectedPlanet: LivePlanetPosition?,
    dossierPlanetId: PlanetId?,
    targetPos: LivePlanetPosition?,
    cameraState: SolarCameraState,
    starTwinkle: Float,
    coronaPhase: Float,
    onOrbit: (Offset) -> Unit,
    onPan: (Offset) -> Unit,
    onZoom: (Float) -> Unit,
    onResetZoom: () -> Unit,
    onResetCamera: () -> Unit,
    onSelectPlanet: (LivePlanetPosition?) -> Unit,
    onTapPos: (Offset, androidx.compose.ui.unit.IntSize, Boolean) -> Unit,
    onDiveToPlanet: (PlanetId) -> Unit,
    onOpenDossier: (PlanetId?) -> Unit,
    isFeatureInstalled: (PlanetId) -> Boolean = { true },
    showControlsCard: Boolean = true,
    onToggleControlsCard: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var inputMode by remember { mutableStateOf(if (com.dirzaaulia.countries.platform.isWasmTarget) AppInputMode.MOUSE_KEYBOARD else AppInputMode.TOUCH) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF020408))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val detected = detectInputModeFromPointer(event)
                            if (detected != null) inputMode = detected
                        }
                    }
                }.pointerInput(Unit) {
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

        // Top-Right Glass Header Bar: Controls Toggle, Info & Settings
        Row(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 12.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onToggleControlsCard,
                shape = CircleShape,
                color = if (showControlsCard) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color(0xF209111E),
                border = BorderStroke(1.dp, if (showControlsCard) Color(0xFF38BDF8) else Color(0x5538BDF8)),
                shadowElevation = 8.dp,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = "Controls Guide",
                        tint = if (showControlsCard) Color(0xFF38BDF8) else Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Surface(
                onClick = onOpenSettings,
                shape = CircleShape,
                color = Color(0xF209111E),
                border = BorderStroke(1.dp, Color(0x5538BDF8)),
                shadowElevation = 8.dp,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Module Settings",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        SolarPlanetTelemetrySheet(
            livePlanet = selectedPlanet,
            isFeatureInstalled = selectedPlanet?.let { isFeatureInstalled(it.planet.id) } ?: true,
            onClose = { onSelectPlanet(null) },
            onDiveToPlanet = onDiveToPlanet,
            onOpenDossier = { onOpenDossier(it) },
        )

        PlanetaryDossierSheet(
            planetId = dossierPlanetId,
            onClose = { onOpenDossier(null) },
        )

        if (selectedPlanet == null && dossierPlanetId == null) {
            if (showControlsCard) {
                PlanetControlsCard(
                    currentMode = inputMode,
                    onClose = onToggleControlsCard,
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .navigationBarsPadding()
                            .padding(start = 16.dp, bottom = 16.dp),
                )
            }

            SolarZoomControlHud(
                zoomScale = cameraState.zoomScale,
                onZoomIn = { onZoom(1.25f) },
                onZoomOut = { onZoom(0.8f) },
                onResetZoom = onResetZoom,
                onResetCamera = onResetCamera,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .navigationBarsPadding()
                        .padding(end = 16.dp),
            )
        }
    }
}
