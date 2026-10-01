package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

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
    LaunchedEffect(controls.isTimeMachineLive) {
        while (isActive && controls.isTimeMachineLive) {
            update { it.copy(currentTimeMillis = currentEpochMillis()) }
            delay(30_000L.milliseconds)
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
    LaunchedEffect(selectedSat?.lat, selectedSat?.lng) {
        if (selectedSat != null) {
            camera.snapTo(selectedSat.lat.toFloat(), -selectedSat.lng.toFloat(), camera.zoom.coerceAtLeast(1.8f))
        }
    }
}
