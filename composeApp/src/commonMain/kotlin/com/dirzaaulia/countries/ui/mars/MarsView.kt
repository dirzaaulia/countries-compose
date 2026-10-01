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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.mars.MarsLandmark
import com.dirzaaulia.countries.platform.Mars3DPlatformView
import com.dirzaaulia.countries.ui.mars.components.MarsEnvironmentHudCard
import com.dirzaaulia.countries.ui.solarsystem.CelestialStarfieldBackground
import com.dirzaaulia.countries.ui.theme.extendedColors

@Composable
fun MarsView(
    state: GlobeState,
    onBackToSolarSystem: () -> Unit = {},
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    epochMillis: Long =
        com.dirzaaulia.countries.platform
            .currentEpochMillis(),
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var selectedLandmark by remember { mutableStateOf<MarsLandmark?>(null) }
    val starTwinkle by rememberInfiniteTransition(label = "marsStarTwinkle").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
        label = "starTwinkle",
    )
    var showGallery by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF03060C)),
    ) {
        CelestialStarfieldBackground(starTwinkle = starTwinkle)

        Mars3DPlatformView(
            state = state,
            sunPosition = sunPosition,
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

        MarsOverlayCanvas(
            state = state,
            starTwinkle = starTwinkle,
            selectedLandmark = selectedLandmark,
            onLandmarkSelected = { selectedLandmark = it },
            modifier = Modifier.fillMaxSize(),
        )

        MarsEnvironmentHudCard(
            epochMillis = epochMillis,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        selectedLandmark?.let { landmark ->
            MarsLandmarkSheet(
                landmark = landmark,
                onClose = { selectedLandmark = null },
            )
        }

        FloatingActionButton(
            onClick = { showGallery = true },
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp),
            containerColor = MaterialTheme.extendedColors.overlayBackground,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(Icons.Outlined.PhotoCamera, contentDescription = "Rover Photos")
        }

        if (showGallery) {
            MarsRoverGallerySheet(onClose = { showGallery = false })
        }
    }
}
