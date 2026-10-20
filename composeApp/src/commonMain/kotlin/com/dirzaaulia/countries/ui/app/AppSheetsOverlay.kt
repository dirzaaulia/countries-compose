package com.dirzaaulia.countries.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import com.dirzaaulia.countries.ui.comparison.ComparisonUiState
import com.dirzaaulia.countries.ui.comparison.CountryComparisonSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.economy.GlobalMarketSheet
import com.dirzaaulia.countries.ui.economy.GlobalTimezoneSheet
import com.dirzaaulia.countries.ui.economy.WorldBankDashboardSheet
import com.dirzaaulia.countries.ui.hazards.EarthquakeDetailSheet
import com.dirzaaulia.countries.ui.hazards.NasaCrisisMonitorSheet
import com.dirzaaulia.countries.ui.hazards.TectonicPlateSheet
import com.dirzaaulia.countries.ui.hud.CountrySearchSheet
import com.dirzaaulia.countries.ui.hud.MissionLegendSheet
import com.dirzaaulia.countries.ui.hud.PlanetaryTimeMachineSheet
import com.dirzaaulia.countries.ui.solarsystem.SolarSystemView
import com.dirzaaulia.countries.ui.weather.MeteorologyStationSheet
import com.dirzaaulia.countries.ui.weather.SpaceWeatherHudCard
import com.dirzaaulia.countries.ui.weather.SpaceWeatherUiState

@Composable
fun AppSheetsOverlay(
    showLegendSheet: Boolean,
    onCloseLegend: () -> Unit,
    showMeteorologySheet: Boolean,
    onCloseMeteorology: () -> Unit,
    showWorldBankSheet: Boolean,
    onCloseWorldBank: () -> Unit,
    showNasaCrisisSheet: Boolean,
    onCloseNasaCrisis: () -> Unit,
    onFlyToEpicenter: (NasaNaturalEvent) -> Unit,
    showSearchSheet: Boolean,
    onCloseSearch: () -> Unit,
    onSelectSearchCountry: (Country) -> Unit,
    showInfoSheet: Boolean = false,
    onCloseInfo: () -> Unit = {},
    showSettingsSheet: Boolean = false,
    onCloseSettings: () -> Unit = {},
    showOnboardingSheet: Boolean = false,
    onCloseOnboarding: () -> Unit = {},
    installManager: com.dirzaaulia.countries.core.dynamicapi.FeatureInstallManager =
        com.dirzaaulia.countries.core.dynamicapi
            .FeatureInstallManager(),
    showSpaceWeatherSheet: Boolean = false,
    onCloseSpaceWeather: () -> Unit = {},
    spaceWeatherState: SpaceWeatherUiState = SpaceWeatherUiState(),
    onFlyToAuroralCountry: (Double, Double) -> Unit = { _, _ -> },
    showTimeMachine: Boolean,
    currentPage: Int,
    isSheetOpen: Boolean,
    currentTimeMillis: Long,
    isTimeMachineLive: Boolean,
    onScrubStarted: () -> Unit,
    onEpochSelected: (Long) -> Unit,
    onResetTimeLive: () -> Unit,
    onCloseTimeMachine: () -> Unit,
    isSimulationPlaying: Boolean = false,
    timeMultiplier: Float = 1f,
    isReversePlayback: Boolean = false,
    onTogglePlayPause: () -> Unit = {},
    onToggleDirection: () -> Unit = {},
    onSelectSpeedMultiplier: (Float) -> Unit = {},
    selectedCountry: Country?,
    liveDetails: LiveCountryDetails?,
    globalHazards: List<NasaNaturalEvent>,
    countries: List<Country>,
    showComparisonSheet: Boolean = false,
    comparisonUiState: ComparisonUiState = ComparisonUiState(),
    onCloseComparison: () -> Unit = {},
    onSwapComparison: () -> Unit = {},
    onSelectSlotForPicker: (Int) -> Unit = {},
    showCountrySelectorForSlot: Int? = null,
    onSelectPickerCountry: (Country) -> Unit = {},
    onCloseCountryPicker: () -> Unit = {},
    showTimezoneSheet: Boolean = false,
    selectedMeridianOffset: Int? = null,
    onCloseTimezoneSheet: () -> Unit = {},
    onSelectTimezoneCountry: (Country) -> Unit = {},
    showMarketSheet: Boolean = false,
    activeMarkets: List<FinancialMarket> = emptyList(),
    markets: List<FinancialMarket> = com.dirzaaulia.countries.domain.astronomy.MAJOR_FINANCIAL_MARKETS,
    onCloseMarketSheet: () -> Unit = {},
    onFlyToMarket: ((FinancialMarket) -> Unit)? = null,
    selectedPlate: TectonicPlate? = null,
    onClosePlateSheet: () -> Unit = {},
    selectedEarthquake: Earthquake? = null,
    onCloseEarthquakeSheet: () -> Unit = {},
    onFlyToEarthquake: (Earthquake) -> Unit = {},
    showSolarSystem: Boolean = false,
    onCloseSolarSystem: () -> Unit = {},
    onDiveFromSolarSystem: (com.dirzaaulia.countries.domain.solarsystem.PlanetId, Float, Float) -> Unit = { _, _, _ -> },
) {
    if (showLegendSheet && isSheetOpen) {
        MissionLegendSheet(onClose = onCloseLegend)
    }

    if (showMeteorologySheet && selectedCountry != null && isSheetOpen) {
        MeteorologyStationSheet(
            country = selectedCountry,
            liveDetails = liveDetails,
            onClose = onCloseMeteorology,
        )
    }

    if (showWorldBankSheet && selectedCountry != null && isSheetOpen) {
        WorldBankDashboardSheet(
            country = selectedCountry,
            liveDetails = liveDetails,
            onClose = onCloseWorldBank,
        )
    }

    if (showNasaCrisisSheet && isSheetOpen) {
        NasaCrisisMonitorSheet(
            hazards = globalHazards,
            currentCountry = selectedCountry,
            onClose = onCloseNasaCrisis,
            onFlyToEpicenter = onFlyToEpicenter,
        )
    }

    if (showSearchSheet && isSheetOpen) {
        CountrySearchSheet(
            countries = countries,
            onClose = onCloseSearch,
            onSelectCountry = onSelectSearchCountry,
        )
    }

    if (showInfoSheet && isSheetOpen) {
        com.dirzaaulia.countries.ui.components.AppInfoSheet(
            onClose = onCloseInfo,
        )
    }

    if (showSettingsSheet && isSheetOpen) {
        com.dirzaaulia.countries.ui.components.AppSettingsSheet(
            installManager = installManager,
            onClose = onCloseSettings,
        )
    }

    if (showOnboardingSheet && isSheetOpen) {
        com.dirzaaulia.countries.ui.components.AppOnboardingSheet(
            installManager = installManager,
            onCompleteOnboarding = onCloseOnboarding,
        )
    }

    if (showSpaceWeatherSheet && isSheetOpen) {
        SpaceWeatherHudCard(
            uiState = spaceWeatherState,
            onClose = onCloseSpaceWeather,
            onFlyToCountry = onFlyToAuroralCountry,
        )
    }

    if (showTimeMachine && isSheetOpen) {
        PlanetaryTimeMachineSheet(
            epochMillis = currentTimeMillis,
            isLive = isTimeMachineLive,
            onScrubStarted = onScrubStarted,
            onEpochSelected = onEpochSelected,
            onResetLive = onResetTimeLive,
            onClose = onCloseTimeMachine,
            isSimulationPlaying = isSimulationPlaying,
            timeMultiplier = timeMultiplier,
            isReversePlayback = isReversePlayback,
            onTogglePlayPause = onTogglePlayPause,
            onToggleDirection = onToggleDirection,
            onSelectSpeedMultiplier = onSelectSpeedMultiplier,
        )
    }

    if (showComparisonSheet && isSheetOpen) {
        CountryComparisonSheet(
            uiState = comparisonUiState,
            onClose = onCloseComparison,
            onSwap = onSwapComparison,
            onSelectSlotForPicker = onSelectSlotForPicker,
        )
    }

    if (showCountrySelectorForSlot != null && isSheetOpen) {
        CountrySearchSheet(
            countries = countries,
            onClose = onCloseCountryPicker,
            onSelectCountry = onSelectPickerCountry,
        )
    }

    if (showTimezoneSheet && selectedMeridianOffset != null && isSheetOpen) {
        GlobalTimezoneSheet(
            utcOffset = selectedMeridianOffset,
            allCountries = countries,
            onClose = onCloseTimezoneSheet,
            onSelectCountry = onSelectTimezoneCountry,
        )
    }

    if (showMarketSheet && isSheetOpen) {
        GlobalMarketSheet(
            activeMarkets = activeMarkets,
            currentUtcMillis = currentTimeMillis,
            onClose = onCloseMarketSheet,
            allMarkets = markets,
            onFlyToMarket = onFlyToMarket,
        )
    }

    if (selectedPlate != null && isSheetOpen) {
        TectonicPlateSheet(
            plate = selectedPlate,
            onClose = onClosePlateSheet,
        )
    }

    if (selectedEarthquake != null && isSheetOpen) {
        EarthquakeDetailSheet(
            earthquake = selectedEarthquake,
            onClose = onCloseEarthquakeSheet,
            onFlyToEpicenter = { _, _ ->
                onFlyToEarthquake(selectedEarthquake)
            },
        )
    }

    if (showSolarSystem && isSheetOpen) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFF020408)),
        ) {
            SolarSystemView(
                currentTimeMillis = currentTimeMillis,
                onDiveToPlanet = { planetId, pitch, yaw ->
                    onCloseSolarSystem()
                    onDiveFromSolarSystem(planetId, pitch, yaw)
                },
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 12.dp, end = 16.dp),
            ) {
                MinimalistCloseButton(onClick = onCloseSolarSystem)
            }
        }
    }
}
