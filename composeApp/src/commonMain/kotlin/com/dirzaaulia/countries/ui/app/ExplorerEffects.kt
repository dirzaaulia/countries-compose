package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun ExplorerEffects(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    currentPage: Int,
    camera: GlobeState,
    models: ExplorerModels,
    update: ((ExplorerControls) -> ExplorerControls) -> Unit,
) {
    ExplorerLoadingEffects(models.globe, models.hazard, models.iss, controls.showSatellites)
    LaunchedEffect(
        controls.isTimeMachineLive,
        controls.isSimulationPlaying,
        controls.timeMultiplier,
        controls.isReversePlayback,
    ) {
        if (controls.isSimulationPlaying) {
            val direction = if (controls.isReversePlayback) -1f else 1f
            while (isActive && controls.isSimulationPlaying) {
                val advanceMs = (50f * controls.timeMultiplier * direction).toLong()
                update { it.copy(currentTimeMillis = it.currentTimeMillis + advanceMs, isTimeMachineLive = false) }
                delay(50L)
            }
        } else if (controls.isTimeMachineLive) {
            while (isActive && controls.isTimeMachineLive && !controls.isSimulationPlaying) {
                update { it.copy(currentTimeMillis = currentEpochMillis()) }
                delay(1000L)
            }
        }
    }
    LaunchedEffect(features.selectedCountry) { models.dossier.selectCountry(features.selectedCountry) }
    val isOtherSheetOpen =
        (features.selectedCountry != null && features.globe.showCountryDossier) ||
            (
                controls.overlay != null &&
                    (controls.overlay != ExplorerOverlay.TIME_MACHINE || currentPage == 1)
            ) ||
            features.hazards.selectedHazard != null
    LaunchedEffect(isOtherSheetOpen) { if (isOtherSheetOpen) models.iss.selectIss(null) }
    ExplorerCameraEffect(features, camera)
}

@Composable
private fun ExplorerCameraEffect(
    features: ExplorerFeatures,
    camera: GlobeState,
) {
    LaunchedEffect(features.globe.selectedCountryId, features.globe.showCountryDossier) {
        val country = features.selectedCountry
        if (country != null && !features.quiz.isQuizMode) {
            if (features.globe.showCountryDossier) {
                camera.stopAnimations()
                camera.snapTo(country.center.lat.toFloat(), -country.center.lng.toFloat(), country.zoomLevel)
            } else {
                camera.flyTo(country.center.lat.toFloat(), -country.center.lng.toFloat(), country.zoomLevel, 650)
            }
        }
    }

    val selectedIss = features.iss.selectedIss
    LaunchedEffect(selectedIss?.latitude, selectedIss?.longitude) {
        if (selectedIss != null) {
            camera.snapTo(selectedIss.latitude.toFloat(), -selectedIss.longitude.toFloat(), camera.zoom.coerceAtLeast(1.8f))
        }
    }

    val selectedSat = features.satellite.selectedSatellite
    val isLockOn = features.satellite.isLockOnFollow
    LaunchedEffect(selectedSat?.lat, selectedSat?.lng, isLockOn) {
        if (selectedSat != null && isLockOn) {
            camera.snapTo(selectedSat.lat.toFloat(), -selectedSat.lng.toFloat(), camera.zoom.coerceAtLeast(1.8f))
        }
    }
}
