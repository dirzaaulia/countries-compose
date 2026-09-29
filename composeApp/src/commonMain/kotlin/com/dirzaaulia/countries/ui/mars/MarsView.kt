package com.dirzaaulia.countries.ui.mars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.mars.MarsLandmark
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.ui.theme.extendedColors
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.ui.mars.components.MarsEnvironmentHudCard
import com.dirzaaulia.countries.platform.Mars3DPlatformView

@Composable
fun MarsView(
    state: GlobeState,
    sunPosition: SunPosition = AstronomyMath.calculateSunPosition(),
    epochMillis: Long = com.dirzaaulia.countries.platform.currentEpochMillis(),
    isPageActive: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var selectedLandmark by remember { mutableStateOf<MarsLandmark?>(null) }
    var starTwinkle by remember { mutableStateOf(1f) } // Or use infinite transition if desired
    var showGallery by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF03060C))
    ) {
        Mars3DPlatformView(
            state = state,
            sunPosition = sunPosition,
            isPageActive = isPageActive,
            modifier = Modifier.fillMaxSize()
        )

        MarsOverlayCanvas(
            state = state,
            starTwinkle = starTwinkle,
            selectedLandmark = selectedLandmark,
            onLandmarkSelected = { selectedLandmark = it },
            modifier = Modifier.fillMaxSize()
        )

        MarsEnvironmentHudCard(
            epochMillis = epochMillis,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        selectedLandmark?.let { landmark ->
            MarsLandmarkSheet(
                landmark = landmark,
                onClose = { selectedLandmark = null }
            )
        }

        FloatingActionButton(
            onClick = { showGallery = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp),
            containerColor = MaterialTheme.extendedColors.overlayBackground,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Outlined.PhotoCamera, contentDescription = "Rover Photos")
        }

        if (showGallery) {
            MarsRoverGallerySheet(onClose = { showGallery = false })
        }
    }
}
