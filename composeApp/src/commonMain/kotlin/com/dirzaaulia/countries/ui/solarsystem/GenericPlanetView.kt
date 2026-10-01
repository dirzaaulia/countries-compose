package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryCatalog
import com.dirzaaulia.countries.platform.Planet3DPlatformView
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DAY_MILLIS = 86_400_000L

@Composable
fun GenericPlanetView(
    planetId: PlanetId,
    state: GlobeState,
    onBackToSolarSystem: () -> Unit,
    modifier: Modifier = Modifier,
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
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

    var isLive by remember { mutableStateOf(true) }
    var nowMillis by remember { mutableLongStateOf(currentEpochMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            if (isLive) nowMillis = currentEpochMillis()
        }
    }

    val liveMinuteOfDay =
        remember(nowMillis) {
            (((nowMillis % DAY_MILLIS) + DAY_MILLIS) % DAY_MILLIS / 60_000L).toInt()
        }
    var scrubbedMinute by remember { mutableIntStateOf(liveMinuteOfDay) }

    LaunchedEffect(liveMinuteOfDay, isLive) {
        if (isLive) scrubbedMinute = liveMinuteOfDay
    }

    val effectiveSunPosition =
        remember(isLive, scrubbedMinute, nowMillis, sunPosition) {
            if (isLive) {
                sunPosition
            } else {
                val dayStart = nowMillis - (nowMillis % DAY_MILLIS)
                val simulatedEpoch = dayStart + scrubbedMinute.toLong() * 60_000L
                AstronomyMath.calculateSunPosition(simulatedEpoch)
            }
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF020408))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (pan != Offset.Zero) {
                            val newX = (state.rotationX + pan.y * 0.35f).coerceIn(-89.5f, 89.5f)
                            val newY = (state.rotationY - pan.x * 0.35f) % 360f
                            scope.launch { state.snapTo(newX, newY, state.zoom) }
                        }
                        if (zoom != 1.0f) {
                            val newZoom = (state.zoom * zoom).coerceIn(0.6f, 4.5f)
                            scope.launch { state.snapZoom(newZoom) }
                        }
                    }
                },
    ) {
        Planet3DPlatformView(
            planetId = planetId,
            state = state,
            sunPosition = effectiveSunPosition,
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

        PlanetScrubberHud(
            planet = planet,
            isLive = isLive,
            minuteOfDay = scrubbedMinute,
            onMinuteChanged = { min ->
                isLive = false
                scrubbedMinute = min
            },
            onResetLive = {
                isLive = true
                scrubbedMinute = liveMinuteOfDay
            },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
        )
    }
}
