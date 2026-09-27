package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.ui.dossier.MeteorologyStationSheet
import com.dirzaaulia.countries.ui.dossier.NasaCrisisMonitorSheet
import com.dirzaaulia.countries.ui.dossier.WorldBankDashboardSheet
import com.dirzaaulia.countries.ui.dossier.administration.AdministrativeDivisionSheetHost
import com.dirzaaulia.countries.ui.hud.CountrySearchSheet
import com.dirzaaulia.countries.ui.hud.MissionLegendSheet
import com.dirzaaulia.countries.ui.hud.PlanetaryTimeMachineSheet

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
    showAdministrativeSheet: Boolean,
    onCloseAdministrative: () -> Unit,
    onCenterDivision: (AdministrativeDivision) -> Unit,
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
) {
    if (showAdministrativeSheet && selectedCountry != null && isSheetOpen) {
        AdministrativeDivisionSheetHost(
            country = selectedCountry,
            onClose = onCloseAdministrative,
            onCenterDivision = onCenterDivision,
        )
    }

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
}
