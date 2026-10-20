package com.dirzaaulia.countries.ui.app

import com.dirzaaulia.countries.domain.astronomy.MoonInfo
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.comparison.ComparisonUiState
import com.dirzaaulia.countries.ui.dossier.DossierUiState
import com.dirzaaulia.countries.ui.economy.EconomyUiState
import com.dirzaaulia.countries.ui.flight.FlightUiState
import com.dirzaaulia.countries.ui.globe.GlobeUiState
import com.dirzaaulia.countries.ui.globe.HazardUiState
import com.dirzaaulia.countries.ui.globe.IssUiState
import com.dirzaaulia.countries.ui.hazards.HazardsUiState
import com.dirzaaulia.countries.ui.quiz.QuizUiState
import com.dirzaaulia.countries.ui.satellite.SatelliteUiState
import com.dirzaaulia.countries.ui.weather.SpaceWeatherUiState

enum class ExplorerOverlay { LEGEND, METEOROLOGY, WORLD_BANK, NASA_CRISIS, SEARCH, TIME_MACHINE, SPACE_WEATHER, SOLAR_SYSTEM, MISSION_CONTROL, INFO, SETTINGS, ONBOARDING }

data class ExplorerControls(
    val showBorders: Boolean = true,
    val showSatellites: Boolean = true,
    val showHazards: Boolean = true,
    val showAurora: Boolean = false,
    val showControlsGuide: Boolean = true,
    val overlay: ExplorerOverlay? = null,
    val isSupersonicFlight: Boolean = false,
    val currentTimeMillis: Long,
    val isTimeMachineLive: Boolean = true,
    val timeMultiplier: Float = 1f,
    val isSimulationPlaying: Boolean = false,
    val isReversePlayback: Boolean = false,
)

data class ExplorerFeatures(
    val globe: GlobeUiState,
    val dossier: DossierUiState,
    val hazards: HazardUiState,
    val iss: IssUiState,
    val flight: FlightUiState,
    val quiz: QuizUiState,
    val spaceWeather: SpaceWeatherUiState,
    val satellite: SatelliteUiState,
    val comparison: ComparisonUiState = ComparisonUiState(),
    val economy: EconomyUiState = EconomyUiState(),
    val tectonic: HazardsUiState = HazardsUiState(),
) {
    val selectedCountry: Country?
        get() = globe.countries.find { it.id == globe.selectedCountryId }
}

data class ExplorerAstronomy(
    val sun: SunPosition,
    val moon: MoonInfo,
    val localTime: String,
    val utcTime: String,
)
