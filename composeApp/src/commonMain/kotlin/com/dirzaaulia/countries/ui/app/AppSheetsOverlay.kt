package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import com.dirzaaulia.countries.ui.comparison.ComparisonUiState
import com.dirzaaulia.countries.ui.comparison.CountryComparisonSheet
import com.dirzaaulia.countries.ui.tectonic.EarthquakeDetailSheet
import com.dirzaaulia.countries.ui.tectonic.TectonicPlateSheet
import com.dirzaaulia.countries.ui.timezone.GlobalMarketSheet
import com.dirzaaulia.countries.ui.timezone.GlobalTimezoneSheet
import com.dirzaaulia.countries.ui.dossier.MeteorologyStationSheet
import com.dirzaaulia.countries.ui.dossier.NasaCrisisMonitorSheet
import com.dirzaaulia.countries.ui.dossier.WorldBankDashboardSheet
import com.dirzaaulia.countries.ui.hud.CountrySearchSheet
import com.dirzaaulia.countries.ui.hud.MissionLegendSheet
import com.dirzaaulia.countries.ui.hud.PlanetaryTimeMachineSheet
import com.dirzaaulia.countries.ui.globe.SpaceWeatherHudCard
import com.dirzaaulia.countries.ui.globe.SpaceWeatherUiState

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
    onCloseMarketSheet: () -> Unit = {},
    onFlyToMarket: ((FinancialMarket) -> Unit)? = null,
    selectedPlate: TectonicPlate? = null,
    onClosePlateSheet: () -> Unit = {},
    selectedEarthquake: Earthquake? = null,
    onCloseEarthquakeSheet: () -> Unit = {},
    onFlyToEarthquake: (Earthquake) -> Unit = {},
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

    if (showSpaceWeatherSheet && isSheetOpen) {
        SpaceWeatherHudCard(
            uiState = spaceWeatherState,
            onClose = onCloseSpaceWeather,
            onFlyToCountry = onFlyToAuroralCountry,
        )
    }

    if (showTimeMachine && currentPage == 0 && isSheetOpen) {
        PlanetaryTimeMachineSheet(
            epochMillis = currentTimeMillis,
            isLive = isTimeMachineLive,
            onScrubStarted = onScrubStarted,
            onEpochSelected = onEpochSelected,
            onResetLive = onResetTimeLive,
            onClose = onCloseTimeMachine,
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
}
