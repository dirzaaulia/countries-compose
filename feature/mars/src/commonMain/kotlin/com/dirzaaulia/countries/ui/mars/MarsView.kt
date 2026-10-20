package com.dirzaaulia.countries.ui.mars

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.dirzaaulia.countries.domain.mars.MarsLandmark
import com.dirzaaulia.countries.platform.Planet3DPlatformView
import com.dirzaaulia.countries.platform.isWasmTarget
import com.dirzaaulia.countries.ui.mars.components.MarsEnvironmentHudCard
import com.dirzaaulia.countries.ui.solarsystem.AppInputMode
import com.dirzaaulia.countries.ui.solarsystem.CelestialStarfieldBackground
import com.dirzaaulia.countries.ui.solarsystem.PlanetControlsCard
import com.dirzaaulia.countries.ui.solarsystem.PlanetHeaderBar
import com.dirzaaulia.countries.ui.solarsystem.SolarZoomControlHud
import com.dirzaaulia.countries.ui.solarsystem.detectInputModeFromPointer
import kotlinx.coroutines.launch

@Composable
fun MarsView(
    state: GlobeState,
    onBackToSolarSystem: () -> Unit = {},
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    epochMillis: Long =
        com.dirzaaulia.countries.platform
            .currentEpochMillis(),
    showTimeMachine: Boolean = false,
    onToggleTimeMachine: () -> Unit = {},
    showControlsCard: Boolean = true,
    onToggleControlsCard: () -> Unit = {},
    onSetControlsCard: (Boolean) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var selectedLandmark by remember { mutableStateOf<MarsLandmark?>(null) }
    var inputMode by remember { mutableStateOf(if (isWasmTarget) AppInputMode.MOUSE_KEYBOARD else AppInputMode.TOUCH) }

    val starTwinkle by rememberInfiniteTransition(label = "marsStarTwinkle").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
        label = "starTwinkle",
    )
    var showGallery by remember { mutableStateOf(false) }
    var isTextureLoaded by remember {
        mutableStateOf(
            com.dirzaaulia.countries.data.solarsystem.PlanetTextureProvider
                .getCachedTexture(com.dirzaaulia.countries.domain.solarsystem.PlanetId.MARS) != null,
        )
    }

    androidx.compose.runtime.LaunchedEffect(epochMillis, showTimeMachine, state.isDragging) {
        if (showTimeMachine && !state.isDragging) {
            val marsRot = AstronomyMath.calculatePlanetRotationAngle(com.dirzaaulia.countries.domain.solarsystem.PlanetId.MARS, epochMillis).toFloat()
            state.snapTo(state.rotationX, marsRot, state.zoom)
        }
    }

    com.dirzaaulia.countries.platform.PlatformBackHandler {
        when {
            selectedLandmark != null -> selectedLandmark = null
            showGallery -> showGallery = false
            else -> onBackToSolarSystem()
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF03060C))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val detected = detectInputModeFromPointer(event)
                            if (detected != null) inputMode = detected
                        }
                    }
                },
    ) {
        CelestialStarfieldBackground(starTwinkle = starTwinkle)

        Planet3DPlatformView(
            planetId = com.dirzaaulia.countries.domain.solarsystem.PlanetId.MARS,
            state = state,
            sunPosition = sunPosition,
            isPageActive = isPageActive,
            onTextureLoaded = { isTextureLoaded = true },
            modifier = Modifier.fillMaxSize(),
        )

        MarsOverlayCanvas(
            state = state,
            starTwinkle = starTwinkle,
            selectedLandmark = selectedLandmark,
            onLandmarkSelected = {
                selectedLandmark = it
                if (it != null) onSetControlsCard(false)
            },
            modifier = Modifier.fillMaxSize(),
        )

        PlanetHeaderBar(
            title = "Mars",
            subtitle = "Red Planet",
            accentColor = Color(0xFFF97316),
            onBackToSolarSystem = onBackToSolarSystem,
            showControlsCard = showControlsCard,
            onToggleControlsCard = {
                if (!showControlsCard) {
                    selectedLandmark = null
                    showGallery = false
                }
                onToggleControlsCard()
            },
            onOpenSettings = onOpenSettings,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        val isSheetOpen = selectedLandmark != null || showGallery
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

            com.dirzaaulia.countries.ui.solarsystem.PlanetaryActionDock(
                showTimeMachine = showTimeMachine,
                onToggleTimeMachine = onToggleTimeMachine,
                onOpenDossier = { showGallery = true },
                onOpenSettings = onOpenSettings,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 16.dp, bottom = 16.dp),
            )
        }

        if (!showControlsCard && !showTimeMachine && !isSheetOpen) {
            MarsEnvironmentHudCard(
                epochMillis = epochMillis,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        selectedLandmark?.let { landmark ->
            MarsLandmarkSheet(
                landmark = landmark,
                onClose = { selectedLandmark = null },
            )
        }

        if (showGallery) {
            MarsRoverGallerySheet(onClose = { showGallery = false })
        }

        com.dirzaaulia.countries.ui.solarsystem.CelestialTextureLoadingOverlay(
            planetName = "Mars",
            isLoading = !isTextureLoaded,
        )
    }
}
