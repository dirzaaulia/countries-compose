package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.globe.rememberGlobeState
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.platform.formatLocalTime

@Composable
fun PlanetaryExplorerRoute() {
    val models = rememberExplorerModels()
    val globe by models.globe.uiState.collectAsState()
    val dossier by models.dossier.uiState.collectAsState()
    val hazards by models.hazard.uiState.collectAsState()
    val iss by models.iss.uiState.collectAsState()
    val flight by models.flight.uiState.collectAsState()
    val quiz by models.quiz.uiState.collectAsState()
    val spaceWeather by models.spaceWeather.uiState.collectAsState()
    val satellite by models.satellite.uiState.collectAsState()
    val comparison by models.comparison.uiState.collectAsState()
    val timezone by models.timezone.uiState.collectAsState()
    val features =
        ExplorerFeatures(
            globe = globe,
            dossier = dossier,
            hazards = hazards,
            iss = iss,
            flight = flight,
            quiz = quiz,
            spaceWeather = spaceWeather,
            satellite = satellite,
            comparison = comparison,
            timezone = timezone,
        )
    var controls by remember { mutableStateOf(ExplorerControls(currentTimeMillis = currentEpochMillis())) }
    val camera = rememberGlobeState()
    val moonCamera = rememberGlobeState()
    val marsCamera = rememberGlobeState()
    val pager = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()

    androidx.compose.runtime.LaunchedEffect(controls.currentTimeMillis) {
        models.timezone.updateTime(controls.currentTimeMillis)
    }

    ExplorerEffects(features, controls, pager.currentPage, camera, models) {
        controls = it(controls)
    }
    val actions =
        ExplorerActions(
            scope = scope,
            camera = camera,
            globeVm = models.globe,
            hazardVm = models.hazard,
            issVm = models.iss,
            quizVm = models.quiz,
            flightVm = models.flight,
            satelliteVm = models.satellite,
            comparisonVm = models.comparison,
            timezoneVm = models.timezone,
            tectonicVm = models.tectonic,
            features = features,
        ) {
            controls = it(controls)
        }
    PlanetaryExplorerScreen(
        features = features,
        controls = controls,
        astronomy = rememberAstronomy(controls.currentTimeMillis),
        camera = camera,
        moonCamera = moonCamera,
        marsCamera = marsCamera,
        pager = pager,
        actions = actions,
    )
}

@Composable
private fun rememberAstronomy(epoch: Long): ExplorerAstronomy {
    val sun = remember(epoch) { AstronomyMath.calculateSunPosition(epoch) }
    val moon = remember(epoch) { AstronomyMath.calculateMoonInfo(epoch) }
    val utc =
        remember(epoch / 1000L) {
            val millis = ((epoch % 86400000L) + 86400000L) % 86400000L
            val hours = (millis / 3600000L).toString().padStart(2, '0')
            val minutes = ((millis % 3600000L) / 60000L).toString().padStart(2, '0')
            "$hours:$minutes UTC"
        }
    val local = remember(epoch / 1000L) { formatLocalTime(epoch) }
    return ExplorerAstronomy(sun, moon, local, utc)
}
