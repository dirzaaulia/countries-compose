package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryCatalog
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryFeature
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryInteriorCatalog
import com.dirzaaulia.countries.platform.Planet3DPlatformView
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.platform.isWasmTarget
import kotlinx.coroutines.launch
import kotlin.ranges.coerceIn

@Composable
fun GenericPlanetView(
    planetId: PlanetId,
    state: GlobeState,
    onBackToSolarSystem: () -> Unit,
    modifier: Modifier = Modifier,
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    epochMillis: Long = currentEpochMillis(),
    showTimeMachine: Boolean = false,
    onToggleTimeMachine: () -> Unit = {},
    showControlsCard: Boolean = true,
    onToggleControlsCard: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    isPageActive: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val planet =
        when (planetId) {
            PlanetId.MERCURY -> PlanetaryCatalog.MERCURY
            PlanetId.VENUS -> PlanetaryCatalog.VENUS
            PlanetId.JUPITER -> PlanetaryCatalog.JUPITER
            PlanetId.SATURN -> PlanetaryCatalog.SATURN
            PlanetId.URANUS -> PlanetaryCatalog.URANUS
            PlanetId.NEPTUNE -> PlanetaryCatalog.NEPTUNE
            else -> PlanetaryCatalog.EARTH
        }

    var inputMode by remember { mutableStateOf(if (isWasmTarget) AppInputMode.MOUSE_KEYBOARD else AppInputMode.TOUCH) }
    var showDossier by remember { mutableStateOf(false) }
    var showInterior by remember { mutableStateOf(false) }
    var selectedFeature by remember { mutableStateOf<PlanetaryFeature?>(null) }
    var selectedMoon by remember { mutableStateOf<com.dirzaaulia.countries.domain.solarsystem.MajorMoon?>(null) }

    // Star twinkle — same animation profile as SolarSystemView
    val starTwinkle by rememberInfiniteTransition(label = "planetStarField")
        .animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
            label = "starTwinkle",
        )

    var isTextureLoaded by remember(planetId) {
        mutableStateOf(
            com.dirzaaulia.countries.data.solarsystem.PlanetTextureProvider
                .getCachedTexture(planetId) != null,
        )
    }

    androidx.compose.runtime.LaunchedEffect(epochMillis, showTimeMachine, state.isDragging) {
        if (showTimeMachine && !state.isDragging) {
            val planetRot =
                com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                    .calculatePlanetRotationAngle(planetId, epochMillis)
                    .toFloat()
            state.snapTo(state.rotationX, planetRot, state.zoom)
        }
    }

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
                        onPan = { pan ->
                            val pitchSens = 0.35f / state.zoom
                            val yawSens = 0.45f / state.zoom
                            val newX = (state.rotationX - pan.y * pitchSens).coerceIn(-89f, 89f)
                            val newY = (state.rotationY - pan.x * yawSens) % 360f
                            scope.launch {
                                state.stopAnimations()
                                state.snapTo(newX, newY, state.zoom)
                            }
                        },
                        onOrbit = { pan ->
                            val pitchSens = 0.35f / state.zoom
                            val yawSens = 0.45f / state.zoom
                            val newX = (state.rotationX - pan.y * pitchSens).coerceIn(-89f, 89f)
                            val newY = (state.rotationY - pan.x * yawSens) % 360f
                            scope.launch {
                                state.stopAnimations()
                                state.snapTo(newX, newY, state.zoom)
                            }
                        },
                        onZoom = { factor: Float ->
                            val newZoom = (state.zoom * factor).coerceIn(0.6f, 4.5f)
                            scope.launch { state.snapZoom(newZoom) }
                        },
                        onTap = {},
                        onDoubleTap = {},
                    )
                },
    ) {
        CelestialStarfieldBackground(starTwinkle = starTwinkle)

        Planet3DPlatformView(
            planetId = planetId,
            state = state,
            sunPosition = sunPosition,
            isPageActive = isPageActive,
            onTextureLoaded = { isTextureLoaded = true },
            modifier = Modifier.fillMaxSize(),
        )

        GenericPlanetOverlayCanvas(
            planetId = planetId,
            state = state,
            starTwinkle = starTwinkle,
            selectedFeature = selectedFeature,
            onFeatureSelected = { selectedFeature = it },
            modifier = Modifier.fillMaxSize(),
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasCenter = center
            val baseRadius = minOf(size.width, size.height) * 0.38f
            val currentRadius = baseRadius * state.zoom
            val occludeR2 = currentRadius * currentRadius
            drawDeepSpaceStarfield(
                starTwinkle = starTwinkle,
                occludeR2 = occludeR2,
                canvasCenter = canvasCenter,
                canvasSize = size,
                cameraYaw = state.rotationY,
                cameraPitch = state.rotationX,
            )
        }

        PlanetHeaderBar(
            title = planet.name,
            subtitle = "Celestial Body",
            accentColor = Color(planet.colorHex),
            onBackToSolarSystem = onBackToSolarSystem,
            showControlsCard = showControlsCard,
            onToggleControlsCard = {
                if (!showControlsCard) {
                    showDossier = false
                    showInterior = false
                    selectedFeature = null
                    selectedMoon = null
                }
                onToggleControlsCard()
            },
            onOpenSettings = onOpenSettings,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        com.dirzaaulia.countries.platform.PlatformBackHandler {
            when {
                showDossier -> showDossier = false
                showInterior -> showInterior = false
                selectedFeature != null -> selectedFeature = null
                selectedMoon != null -> selectedMoon = null
                else -> onBackToSolarSystem()
            }
        }

        val isSheetOpen = showDossier || showInterior || selectedFeature != null || selectedMoon != null
        if (!isSheetOpen && !showTimeMachine && showControlsCard) {
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

        if (!showTimeMachine) {
            SolarZoomControlHud(
                zoomScale = state.zoom,
                onZoomIn = { scope.launch { state.zoomIn() } },
                onZoomOut = { scope.launch { state.zoomOut() } },
                onResetZoom = { scope.launch { state.resetZoom() } },
                onResetCamera = { scope.launch { state.snapTo(0f, 0f, 1f) } },
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .navigationBarsPadding()
                        .padding(end = 16.dp),
            )

            PlanetaryActionDock(
                showTimeMachine = showTimeMachine,
                onToggleTimeMachine = onToggleTimeMachine,
                onOpenDossier = { showDossier = true },
                hasInterior = remember(planetId) { PlanetaryInteriorCatalog.getInteriorForPlanet(planetId) != null },
                onOpenInterior = { showInterior = true },
                onOpenSettings = onOpenSettings,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 16.dp, bottom = 16.dp),
            )
        }

        if (!showControlsCard && !isSheetOpen) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = if (showTimeMachine) 320.dp else 16.dp),
            ) {
                MajorMoonsCarousel(
                    planetId = planetId,
                    onMoonSelected = { selectedMoon = it },
                )
            }
        }

        if (showDossier) {
            PlanetaryDossierSheet(
                planetId = planetId,
                onClose = { showDossier = false },
            )
        }

        if (showInterior) {
            val interior = remember(planetId) { PlanetaryInteriorCatalog.getInteriorForPlanet(planetId) }
            if (interior != null) {
                PlanetaryInteriorSheet(
                    interior = interior,
                    onClose = { showInterior = false },
                )
            }
        }

        selectedFeature?.let { feature ->
            GenericPlanetFeatureSheet(
                feature = feature,
                onClose = { selectedFeature = null },
            )
        }

        selectedMoon?.let { moon ->
            MajorMoonDetailSheet(
                moon = moon,
                onClose = { selectedMoon = null },
            )
        }

        CelestialTextureLoadingOverlay(
            planetName = planet.name,
            isLoading = !isTextureLoaded,
        )
    }
}
