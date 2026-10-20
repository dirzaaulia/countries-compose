package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallManager
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.globe.rememberGlobeState
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.platform.formatLocalTime

@Composable
fun PlanetaryExplorerRoute() {
    val installManager = remember { FeatureInstallManager() }
    val models = rememberExplorerModels()
    val globe by models.globe.uiState.collectAsState()
    val dossier by models.dossier.uiState.collectAsState()
    val hazard by models.hazard.uiState.collectAsState()
    val iss by models.iss.uiState.collectAsState()
    val flight by models.flight.uiState.collectAsState()
    val quiz by models.quiz.uiState.collectAsState()
    val spaceWeather by models.spaceWeather.uiState.collectAsState()
    val satellite by models.satellite.uiState.collectAsState()
    val comparison by models.comparison.uiState.collectAsState()
    val economy by models.economy.uiState.collectAsState()
    val hazards by models.hazards.uiState.collectAsState()
    val features =
        ExplorerFeatures(
            globe = globe,
            dossier = dossier,
            hazards = hazard,
            iss = iss,
            flight = flight,
            quiz = quiz,
            spaceWeather = spaceWeather,
            satellite = satellite,
            comparison = comparison,
            economy = economy,
            tectonic = hazards,
        )
    var controls by remember { mutableStateOf(ExplorerControls(currentTimeMillis = currentEpochMillis())) }
    val camera = rememberGlobeState()
    val moonCamera = rememberGlobeState()
    val marsCamera = rememberGlobeState()
    var activePage by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(controls.currentTimeMillis) {
        models.economy.updateTime(controls.currentTimeMillis)
        if (!controls.isTimeMachineLive) {
            models.satellite.refreshTelemetry(controls.currentTimeMillis)
        }
    }

    ExplorerEffects(features, controls, activePage, camera, models) {
        controls = it(controls)
    }
    val actions =
        ExplorerActions(
            scope = scope,
            camera = camera,
            moonCamera = moonCamera,
            marsCamera = marsCamera,
            globeVm = models.globe,
            hazardVm = models.hazard,
            issVm = models.iss,
            quizVm = models.quiz,
            flightVm = models.flight,
            satelliteVm = models.satellite,
            comparisonVm = models.comparison,
            economyVm = models.economy,
            hazardsVm = models.hazards,
            features = features,
            update = { controls = it(controls) },
            onSelectPage = { page -> activePage = page },
            checkFeatureInstalled = { fid -> installManager.isFeatureInstalled(fid) },
        )
    PlanetaryExplorerScreen(
        features = features,
        controls = controls,
        astronomy = rememberAstronomy(controls.currentTimeMillis),
        camera = camera,
        moonCamera = moonCamera,
        marsCamera = marsCamera,
        activePage = activePage,
        actions = actions,
        installManager = installManager,
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
