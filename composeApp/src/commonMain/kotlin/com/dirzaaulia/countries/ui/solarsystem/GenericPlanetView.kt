package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import com.dirzaaulia.countries.ui.overlay.drawDeepSpaceStarfield
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
                AstronomyMath.calculateSunPosition(dayStart + scrubbedMinute.toLong() * 60_000L)
            }
        }

    // Star twinkle — same animation profile as SolarSystemView
    val starTwinkle by rememberInfiniteTransition(label = "planetStarField")
        .animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
            label = "starTwinkle",
        )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF020408))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (pan != Offset.Zero) {
                            // Free 360 orbital camera — matches solar system view
                            val pitchSens = 0.35f / state.zoom
                            val yawSens = 0.45f / state.zoom
                            val newX = (state.rotationX - pan.y * pitchSens) % 360f
                            val newY = (state.rotationY - pan.x * yawSens) % 360f
                            val newZoom = if (zoom != 1.0f) state.zoom * zoom else null
                            scope.launch {
                                state.stopAnimations()
                                state.snapTo(newX, newY, newZoom)
                            }
                        } else if (zoom != 1.0f) {
                            val newZoom = (state.zoom * zoom).coerceIn(0.6f, 4.5f)
                            scope.launch { state.snapZoom(newZoom) }
                        }
                    }
                },
    ) {
        CelestialStarfieldBackground(starTwinkle = starTwinkle)

        Planet3DPlatformView(
            planetId = planetId,
            state = state,
            sunPosition = effectiveSunPosition,
            isPageActive = isPageActive,
            modifier = Modifier.fillMaxSize(),
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasCenter = center
            val baseRadius = minOf(size.width, size.height) * 0.38f
            val currentRadius = baseRadius * state.zoom
            val occludeRadius = if (planetId == PlanetId.SATURN) currentRadius * 2.3f else currentRadius
            val occludeR2 = occludeRadius * occludeRadius
            drawDeepSpaceStarfield(starTwinkle, occludeR2, canvasCenter, size)
        }

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
