package com.dirzaaulia.countries.ui.app

import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.globe.GlobeState
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import com.dirzaaulia.countries.platform.currentEpochMillis
import com.dirzaaulia.countries.ui.comparison.ComparisonViewModel
import com.dirzaaulia.countries.ui.globe.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.globe.QuizViewModel
import com.dirzaaulia.countries.ui.satellite.SatelliteViewModel
import com.dirzaaulia.countries.ui.tectonic.TectonicViewModel
import com.dirzaaulia.countries.ui.timezone.TimezoneViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun ExplorerFeatures.isAnySheetOpen(
    controls: ExplorerControls,
    page: Int,
): Boolean =
    (selectedCountry != null && globe.showCountryDossier) ||
        (controls.overlay != null && (controls.overlay != ExplorerOverlay.TIME_MACHINE || page == 1)) ||
        hazards.selectedHazard != null ||
        iss.selectedIss != null ||
        comparison.showComparisonSheet ||
        comparison.showCountrySelectorForSlot != null ||
        timezone.showTimezoneSheet ||
        timezone.showMarketCard ||
        tectonic.selectedPlate != null ||
        tectonic.selectedEarthquake != null

class ExplorerActions(
    val scope: CoroutineScope,
    private val camera: GlobeState,
    private val moonCamera: GlobeState,
    private val marsCamera: GlobeState,
    private val globeVm: GlobeViewModel,
    private val hazardVm: HazardViewModel,
    private val issVm: IssViewModel,
    private val quizVm: QuizViewModel,
    private val flightVm: FlightViewModel,
    private val satelliteVm: SatelliteViewModel,
    private val comparisonVm: ComparisonViewModel,
    private val timezoneVm: TimezoneViewModel,
    private val tectonicVm: TectonicViewModel,
    private val features: ExplorerFeatures,
    private val update: ((ExplorerControls) -> ExplorerControls) -> Unit,
    private val onSelectPage: ((Int) -> Unit)? = null,
) {
    fun snapCamera(
        planetId: PlanetId,
        pitch: Float,
        yaw: Float,
    ) {
        when (planetId) {
            PlanetId.EARTH -> scope.launch { camera.snapTo(pitch, yaw, camera.zoom) }
            PlanetId.MARS -> scope.launch { marsCamera.snapTo(pitch, yaw, marsCamera.zoom) }
            else -> scope.launch { moonCamera.snapTo(pitch, yaw, moonCamera.zoom) }
        }
    }

    fun selectPage(page: Int) {
        onSelectPage?.invoke(page)
    }

    fun setOverlay(overlay: ExplorerOverlay?) = update { it.copy(overlay = overlay) }

    fun toggleBorders() = update { it.copy(showBorders = !it.showBorders) }

    fun toggleSatellites() = update { it.copy(showSatellites = !it.showSatellites) }

    fun toggleHazards() = update { it.copy(showHazards = !it.showHazards) }

    fun toggleAurora() = update { it.copy(showAurora = !it.showAurora) }

    fun toggleTimeMachine() =
        update {
            it.copy(overlay = if (it.overlay == ExplorerOverlay.TIME_MACHINE) null else ExplorerOverlay.TIME_MACHINE)
        }

    fun toggleSupersonic() = update { it.copy(isSupersonicFlight = !it.isSupersonicFlight) }

    fun selectCountry(id: String?) = globeVm.selectCountry(id, features.quiz.isQuizMode)

    fun setDossierOpen(open: Boolean) = globeVm.setShowCountryDossier(open)

    fun nextCountry(current: Country) {
        features.globe.countries
            .filterNot { it.id == current.id }
            .randomOrNull()
            ?.let { selectCountry(it.id) }
    }

    fun randomCountry() {
        features.globe.countries
            .randomOrNull()
            ?.let { selectCountry(it.id) }
    }

    fun toggleFlightMode() = flightVm.toggleFlightMode(features.globe.countries)

    fun randomFlightRoute() = flightVm.generateRandomFlightRoute(features.globe.countries)

    fun setFlightOrigin(country: Country) = flightVm.setFlightOrigin(country)

    fun setFlightDestination(country: Country) = flightVm.setFlightDestination(country)

    fun toggleQuizMode() = quizVm.toggleQuizMode(features.globe.countries)

    fun nextQuizQuestion() = quizVm.generateNextQuizQuestion(features.globe.countries)

    fun onCountryTap(id: String?) {
        if (features.quiz.isQuizMode && features.quiz.quizTargetCountry != null) {
            if (id != null) quizVm.handleQuizTap(id, features.globe.countries)
        } else {
            selectCountry(id)
        }
    }

    fun selectSearchCountry(id: String) {
        setOverlay(null)
        globeVm.selectCountry(id)
    }

    fun flyTo(
        lat: Double,
        lng: Double,
        zoom: Float,
    ) {
        scope.launch { camera.flyTo(lat.toFloat(), -lng.toFloat(), zoom) }
    }

    fun flyToEpicenter(hazard: NasaNaturalEvent) {
        setOverlay(null)
        hazardVm.selectHazard(hazard)
        flyTo(hazard.lat, hazard.lng, 2.2f)
    }

    fun scrubStarted() = update { it.copy(isTimeMachineLive = false) }

    fun selectEpoch(epoch: Long) {
        update { it.copy(currentTimeMillis = epoch) }
        timezoneVm.updateTime(epoch)
    }

    fun resetLive() {
        val now = currentEpochMillis()
        update { it.copy(currentTimeMillis = now, isTimeMachineLive = true) }
        timezoneVm.updateTime(now)
    }

    fun selectHazard(hazard: NasaNaturalEvent?) = hazardVm.selectHazard(hazard)

    fun selectIss(telemetry: ISSTelemetry) = issVm.selectIss(telemetry)

    fun clearIss() = issVm.selectIss(null)

    fun selectSatellite(sat: SatelliteTelemetry?) = satelliteVm.selectSatellite(sat)

    fun calculateNextPassForCountry(
        lat: Double,
        lng: Double,
    ) = satelliteVm.calculateNextPassForCountry(lat, lng)

    fun startComparison(
        countryA: Country,
        countryB: Country? = null,
    ) {
        setDossierOpen(false)
        comparisonVm.startComparison(countryA, countryB)
        if (countryB != null) {
            flyTo(countryB.center.lat, countryB.center.lng, 2.2f)
        }
    }

    fun setComparisonCountryA(country: Country) {
        comparisonVm.setCountryA(country)
    }

    fun setComparisonCountryB(country: Country) {
        comparisonVm.setCountryB(country)
        flyTo(country.center.lat, country.center.lng, 2.2f)
    }

    fun swapComparisonCountries() {
        comparisonVm.swapCountries()
        features.comparison.countryA?.let { countryA ->
            flyTo(countryA.center.lat, countryA.center.lng, 2.2f)
        }
    }

    fun closeComparison() {
        comparisonVm.setSheetOpen(false)
    }

    fun selectSlotForCountryPicker(slot: Int?) {
        comparisonVm.setShowCountrySelectorForSlot(slot)
    }

    fun toggleTimezoneLayer() = timezoneVm.toggleTimezoneLayer()

    fun toggleMarketCard() = timezoneVm.toggleMarketCard()

    fun toggleMarketLayer() = timezoneVm.toggleMarketLayer()

    fun selectMarket(market: FinancialMarket?) = timezoneVm.selectMarket(market)

    fun flyToMarket(market: FinancialMarket) {
        timezoneVm.selectMarket(market)
        flyTo(market.lat, market.lng, 2.2f)
    }

    fun selectMeridian(utcOffset: Int?) = timezoneVm.selectMeridian(utcOffset)

    fun closeTimezoneSheet() = timezoneVm.setSheetOpen(false)

    fun updateTimezoneTime(epochMillis: Long) = timezoneVm.updateTime(epochMillis)

    fun toggleTectonicLayer() = tectonicVm.toggleTectonicLayer()

    fun selectPlate(plate: TectonicPlate?) = tectonicVm.selectPlate(plate)

    fun selectEarthquake(quake: Earthquake?) = tectonicVm.selectEarthquake(quake)

    fun flyToEarthquake(quake: Earthquake) {
        tectonicVm.selectEarthquake(quake)
        flyTo(quake.lat, quake.lng, 2.2f)
    }

    fun setMinMagnitude(mag: Double) = tectonicVm.setMinMagnitude(mag)
}
