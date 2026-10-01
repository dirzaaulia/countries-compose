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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.astronomy.MoonInfo
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.moon.LunarLandmark
import com.dirzaaulia.countries.domain.moon.LunarLandmarkType
import com.dirzaaulia.countries.platform.Moon3DPlatformView
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.ui.hud.MoonDetailSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MoonView(
    moonInfo: MoonInfo,
    modifier: Modifier = Modifier,
    onBackToSolarSystem: () -> Unit = {},
    eclipseFeed: EclipseFeed? = null,
    isEclipseFeedLoading: Boolean = false,
    isPageActive: Boolean = true,
    state: GlobeState = remember { GlobeState(initialRotationX = 0f, initialRotationY = 0f, initialZoom = 1f) },
    sensitivity: Float = 0.38f,
) {
    val scope = rememberCoroutineScope()
    val moonViewState = rememberMoonViewState(moonInfo)
    val isFarSideActive =
        remember(state.rotationY) {
            val normY = ((state.rotationY.toDouble() % 360.0) + 360.0) % 360.0
            normY in 90.0..270.0
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF020408)),
    ) {
        Moon3DPlatformView(
            state = state,
            phaseAngle = moonViewState.displayedMoonInfo.phaseAngle,
            subsolarLatitude = moonViewState.displayedMoonInfo.subsolarLatitude,
            librationLatitude = moonViewState.displayedMoonInfo.librationLatitude,
            librationLongitude = moonViewState.displayedMoonInfo.librationLongitude,
            isPageActive = isPageActive,
            modifier = Modifier.fillMaxSize(),
        )

        IconButton(
            onClick = onBackToSolarSystem,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back to Solar System",
                tint = Color.White,
            )
        }

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

        LunarFarSideBadge(
            isVisible = isFarSideActive,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 58.dp),
        )

        if (moonViewState.selectedLandmark == null) {
            LunarFilterBar(
                selectedCategory = moonViewState.selectedCategory,
                onSelectCategory = { moonViewState.selectedCategory = it },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 190.dp),
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

        if (moonViewState.selectedLandmark == null) {
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
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
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
