package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable

@Composable
internal fun ExplorerSheets(
    features: ExplorerFeatures,
    controls: ExplorerControls,
    currentPage: Int,
    actions: ExplorerActions,
) {
    val overlay = controls.overlay
    AppSheetsOverlay(
        showLegendSheet = overlay == ExplorerOverlay.LEGEND,
        onCloseLegend = { actions.setOverlay(null) },
        showMeteorologySheet = overlay == ExplorerOverlay.METEOROLOGY,
        onCloseMeteorology = { actions.setOverlay(null) },
        showWorldBankSheet = overlay == ExplorerOverlay.WORLD_BANK,
        onCloseWorldBank = { actions.setOverlay(null) },
        showNasaCrisisSheet = overlay == ExplorerOverlay.NASA_CRISIS,
        onCloseNasaCrisis = { actions.setOverlay(null) },
        onFlyToEpicenter = actions::flyToEpicenter,
        showSearchSheet = overlay == ExplorerOverlay.SEARCH,
        onCloseSearch = { actions.setOverlay(null) },
        onSelectSearchCountry = { actions.selectSearchCountry(it.id) },
        showSpaceWeatherSheet = overlay == ExplorerOverlay.SPACE_WEATHER,
        onCloseSpaceWeather = { actions.setOverlay(null) },
        spaceWeatherState = features.spaceWeather,
        onFlyToAuroralCountry = { lat, lng ->
            actions.setOverlay(null)
            actions.flyTo(lat, lng, 2.0f)
        },
        showTimeMachine = overlay == ExplorerOverlay.TIME_MACHINE,
        currentPage = currentPage,
        isSheetOpen = features.isAnySheetOpen(controls, currentPage),
        currentTimeMillis = controls.currentTimeMillis,
        isTimeMachineLive = controls.isTimeMachineLive,
        onScrubStarted = actions::scrubStarted,
        onEpochSelected = actions::selectEpoch,
        onResetTimeLive = actions::resetLive,
        onCloseTimeMachine = { actions.setOverlay(null) },
        selectedCountry = features.selectedCountry,
        liveDetails = features.dossier.liveDetails,
        globalHazards = features.hazards.globalHazards,
        countries = features.globe.countries,
        showComparisonSheet = features.comparison.showComparisonSheet,
        comparisonUiState = features.comparison,
        onCloseComparison = actions::closeComparison,
        onSwapComparison = actions::swapComparisonCountries,
        onSelectSlotForPicker = actions::selectSlotForCountryPicker,
        showCountrySelectorForSlot = features.comparison.showCountrySelectorForSlot,
        onSelectPickerCountry = { country ->
            val slot = features.comparison.showCountrySelectorForSlot
            if (slot == 1) {
                actions.setComparisonCountryA(country)
            } else {
                actions.setComparisonCountryB(country)
            }
        },
        onCloseCountryPicker = { actions.selectSlotForCountryPicker(null) },
        showTimezoneSheet = features.timezone.showTimezoneSheet,
        selectedMeridianOffset = features.timezone.selectedMeridianOffset,
        onCloseTimezoneSheet = actions::closeTimezoneSheet,
        onSelectTimezoneCountry = { country ->
            actions.closeTimezoneSheet()
            actions.selectSearchCountry(country.id)
        },
        showMarketSheet = features.timezone.showMarketCard,
        activeMarkets = features.timezone.activeMarkets,
        onCloseMarketSheet = actions::toggleMarketCard,
        onFlyToMarket = actions::flyToMarket,
        selectedPlate = features.tectonic.selectedPlate,
        onClosePlateSheet = { actions.selectPlate(null) },
        selectedEarthquake = features.tectonic.selectedEarthquake,
        onCloseEarthquakeSheet = { actions.selectEarthquake(null) },
        onFlyToEarthquake = actions::flyToEarthquake,
    )
}
