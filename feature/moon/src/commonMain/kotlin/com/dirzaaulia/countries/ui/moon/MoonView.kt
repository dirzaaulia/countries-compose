package com.dirzaaulia.countries.ui.moon

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.astronomy.MoonInfo
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.moon.LunarLandmark
import com.dirzaaulia.countries.domain.moon.LunarLandmarkType
import com.dirzaaulia.countries.platform.Planet3DPlatformView
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.platform.isWasmTarget
import com.dirzaaulia.countries.ui.solarsystem.AppInputMode
import com.dirzaaulia.countries.ui.solarsystem.CelestialStarfieldBackground
import com.dirzaaulia.countries.ui.solarsystem.PlanetControlsCard
import com.dirzaaulia.countries.ui.solarsystem.PlanetHeaderBar
import com.dirzaaulia.countries.ui.solarsystem.SolarZoomControlHud
import com.dirzaaulia.countries.ui.solarsystem.detectInputModeFromPointer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MoonView(
    moonInfo: MoonInfo,
    modifier: Modifier = Modifier,
    onBackToSolarSystem: () -> Unit = {},
    epochMillis: Long = currentEpochMillis(),
    showTimeMachine: Boolean = false,
    onToggleTimeMachine: () -> Unit = {},
    showControlsCard: Boolean = true,
    onToggleControlsCard: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    eclipseFeed: EclipseFeed? = null,
    isEclipseFeedLoading: Boolean = false,
    isPageActive: Boolean = true,
    state: GlobeState = remember { GlobeState(initialRotationX = 0f, initialRotationY = 0f, initialZoom = 1f) },
    sensitivity: Float = 0.38f,
) {
    val scope = rememberCoroutineScope()
    val moonViewState = rememberMoonViewState(moonInfo)
    var inputMode by remember { mutableStateOf(if (isWasmTarget) AppInputMode.MOUSE_KEYBOARD else AppInputMode.TOUCH) }
    var showLunarPhaseScrubber by remember { mutableStateOf(false) }

    val isFarSideActive =
        remember(state.rotationY) {
            val normY = ((state.rotationY.toDouble() % 360.0) + 360.0) % 360.0
            normY in 90.0..270.0
        }

    var isTextureLoaded by remember {
        mutableStateOf(
            com.dirzaaulia.countries.data.solarsystem.PlanetTextureProvider
                .getCachedTexture(com.dirzaaulia.countries.domain.solarsystem.PlanetId.MOON) != null,
        )
    }

    androidx.compose.runtime.LaunchedEffect(epochMillis, showTimeMachine, state.isDragging) {
        if (showTimeMachine && !state.isDragging) {
            val moonRot =
                com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                    .calculatePlanetRotationAngle(com.dirzaaulia.countries.domain.solarsystem.PlanetId.MOON, epochMillis)
                    .toFloat()
            state.snapTo(state.rotationX, moonRot, state.zoom)
        }
    }

    com.dirzaaulia.countries.platform.PlatformBackHandler {
        when {
            showLunarPhaseScrubber -> showLunarPhaseScrubber = false
            moonViewState.selectedLandmark != null -> moonViewState.selectedLandmark = null
            moonViewState.showMoonDetailSheet -> moonViewState.showMoonDetailSheet = false
            else -> onBackToSolarSystem()
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
                },
    ) {
        CelestialStarfieldBackground(
            starTwinkle = moonViewState.starTwinkle,
        )

        Planet3DPlatformView(
            planetId = com.dirzaaulia.countries.domain.solarsystem.PlanetId.MOON,
            state = state,
            sunPosition =
                com.dirzaaulia.countries.domain.astronomy.AstronomyMath
                    .calculateSunPosition(),
            isPageActive = isPageActive,
            onTextureLoaded = { isTextureLoaded = true },
            modifier = Modifier.fillMaxSize(),
        )

        MoonOverlayCanvas(
            state = state,
            librationLatitude = moonViewState.displayedMoonInfo.librationLatitude,
            librationLongitude = moonViewState.displayedMoonInfo.librationLongitude,
            starTwinkle = moonViewState.starTwinkle,
            selectedCategory = moonViewState.selectedCategory,
            selectedLandmark = moonViewState.selectedLandmark,
            onLandmarkSelected = { moonViewState.selectedLandmark = it },
            sensitivity = sensitivity,
            modifier = Modifier.fillMaxSize(),
        )

        PlanetHeaderBar(
            title = "Moon",
            subtitle = "Lunar Surface",
            accentColor = Color(0xFFFFD54F),
            onBackToSolarSystem = onBackToSolarSystem,
            showControlsCard = showControlsCard,
            onToggleControlsCard = {
                if (!showControlsCard) {
                    moonViewState.selectedLandmark = null
                    moonViewState.showMoonDetailSheet = false
                }
                onToggleControlsCard()
            },
            onOpenSettings = onOpenSettings,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        LunarFarSideBadge(
            isVisible = isFarSideActive,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 58.dp),
        )

        val isSheetOpen = moonViewState.selectedLandmark != null || moonViewState.showMoonDetailSheet
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
                onOpenDossier = { moonViewState.showMoonDetailSheet = true },
                onOpenSettings = onOpenSettings,
                showLunarPhase = showLunarPhaseScrubber,
                onToggleLunarPhase = { showLunarPhaseScrubber = !showLunarPhaseScrubber },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 16.dp, bottom = 16.dp),
            )
        }

        if (!showControlsCard && !isSheetOpen && !showTimeMachine) {
            LunarFilterBar(
                selectedCategory = moonViewState.selectedCategory,
                onSelectCategory = { moonViewState.selectedCategory = it },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
            )
        }

        MoonSheets(
            selectedLandmark = moonViewState.selectedLandmark,
            onCloseLandmarkSheet = { moonViewState.selectedLandmark = null },
            showMoonDetailSheet = moonViewState.showMoonDetailSheet,
            displayedMoonInfo = moonViewState.displayedMoonInfo,
            onCloseDetailSheet = { moonViewState.showMoonDetailSheet = false },
            onFlyToSite = { lat, lng ->
                scope.launch { state.flyTo(lat.toFloat(), -lng.toFloat(), 2.2f) }
            },
        )

        if (showLunarPhaseScrubber && moonViewState.selectedLandmark == null && !showTimeMachine) {
            MoonScrubberHud(
                displayedMoonInfo = moonViewState.displayedMoonInfo,
                currentYear = moonViewState.currentYear,
                selectedDayOfYear = moonViewState.selectedDayOfYear,
                todayDayOfYear = moonViewState.todayDayOfYear,
                totalDaysInYear = moonViewState.totalDaysInYear,
                eclipseFeed = eclipseFeed,
                isEclipseFeedLoading = isEclipseFeedLoading,
                onDaySelected = { moonViewState.selectedDayOfYear = it },
                onShowDetailSheet = { moonViewState.showMoonDetailSheet = true },
                onClose = { showLunarPhaseScrubber = false },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        com.dirzaaulia.countries.ui.solarsystem.CelestialTextureLoadingOverlay(
            planetName = "Moon",
            isLoading = !isTextureLoaded,
        )
    }
}

private class MoonViewState(
    selectedDayOfYear: Int,
) {
    var selectedCategory by mutableStateOf<LunarLandmarkType?>(null)
    var selectedLandmark by mutableStateOf<LunarLandmark?>(null)
    var selectedDayOfYear by mutableStateOf(selectedDayOfYear)
    var showMoonDetailSheet by mutableStateOf(false)
    var currentYear = 0
    var todayDayOfYear = 0
    var totalDaysInYear = 0
    lateinit var displayedMoonInfo: MoonInfo
    var starTwinkle = 0f
}

@Composable
private fun rememberMoonViewState(moonInfo: MoonInfo): MoonViewState {
    var now by remember { mutableStateOf(currentEpochMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3_600_000L)
            now = currentEpochMillis()
        }
    }
    val (year, today, _) = remember(now) { epochMillisToYearAndDay(now) }
    val viewState = remember { MoonViewState(today) }
    val totalDays = if (isLeapYear(year)) 366 else 365
    LaunchedEffect(year, today) {
        viewState.selectedDayOfYear = viewState.selectedDayOfYear.coerceIn(1, totalDays)
    }
    val selectedDay = viewState.selectedDayOfYear.coerceIn(1, totalDays)
    val displayed =
        remember(selectedDay, year, today, moonInfo) {
            if (selectedDay == today) {
                moonInfo
            } else {
                AstronomyMath.calculateMoonInfo(dayOfYearToEpochMillis(year, selectedDay))
            }
        }
    val transition = rememberInfiniteTransition(label = "moonStarTwinkle")
    val twinkle by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Reverse),
        label = "moonStarTwinkle",
    )
    viewState.currentYear = year
    viewState.todayDayOfYear = today
    viewState.totalDaysInYear = totalDays
    viewState.displayedMoonInfo = displayed
    viewState.starTwinkle = twinkle
    return viewState
}

@Composable
private fun MoonSheets(
    selectedLandmark: LunarLandmark?,
    onCloseLandmarkSheet: () -> Unit,
    showMoonDetailSheet: Boolean,
    displayedMoonInfo: MoonInfo,
    onCloseDetailSheet: () -> Unit,
    onFlyToSite: (Double, Double) -> Unit,
) {
    selectedLandmark?.let { landmark ->
        LunarLandmarkSheet(
            landmark = landmark,
            onClose = onCloseLandmarkSheet,
            onFlyToSite = onFlyToSite,
        )
    }

    if (showMoonDetailSheet) {
        MoonDetailSheet(
            moonInfo = displayedMoonInfo,
            onClose = onCloseDetailSheet,
        )
    }
}
