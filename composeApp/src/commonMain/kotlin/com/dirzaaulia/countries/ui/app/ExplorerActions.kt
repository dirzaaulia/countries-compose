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
import com.dirzaaulia.countries.ui.economy.EconomyViewModel
import com.dirzaaulia.countries.ui.flight.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.hazards.HazardsViewModel
import com.dirzaaulia.countries.ui.quiz.QuizViewModel
import com.dirzaaulia.countries.ui.satellite.SatelliteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun ExplorerFeatures.isAnySheetOpen(
    controls: ExplorerControls,
    page: Int,
): Boolean =
    (selectedCountry != null && globe.showCountryDossier) ||
        controls.overlay != null ||
        hazards.selectedHazard != null ||
        iss.selectedIss != null ||
        comparison.showComparisonSheet ||
        comparison.showCountrySelectorForSlot != null ||
        economy.showTimezoneSheet ||
        economy.showMarketCard ||
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
    private val economyVm: EconomyViewModel,
    private val hazardsVm: HazardsViewModel,
    private val features: ExplorerFeatures,
    private val update: ((ExplorerControls) -> ExplorerControls) -> Unit,
    private val onSelectPage: ((Int) -> Unit)? = null,
    private val checkFeatureInstalled: ((String) -> Boolean)? = null,
    private val onRequestFeatureInstall: ((String, String, () -> Unit) -> Unit)? = null,
) {
    private fun requireFeature(
        featureId: String,
        title: String,
        action: () -> Unit,
    ) {
        val installed = checkFeatureInstalled?.invoke(featureId) ?: true
        if (installed) {
            action()
        } else {
            onRequestFeatureInstall?.invoke(featureId, title, action)
        }
    }

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

    fun setOverlay(overlay: ExplorerOverlay?) {
        when (overlay) {
            ExplorerOverlay.METEOROLOGY, ExplorerOverlay.SPACE_WEATHER -> {
                requireFeature("weather", "Weather & Space Weather") {
                    update { it.copy(overlay = overlay) }
                }
            }
            ExplorerOverlay.WORLD_BANK -> {
                requireFeature("economy", "World Bank & Stock Exchanges") {
                    update { it.copy(overlay = overlay) }
                }
            }
            ExplorerOverlay.NASA_CRISIS -> {
                requireFeature("hazards", "Geology Hazards & NASA Crisis") {
                    update { it.copy(overlay = overlay) }
                }
            }
            else -> update { it.copy(overlay = overlay) }
        }
    }

    fun toggleBorders() = update { it.copy(showBorders = !it.showBorders) }

    fun toggleControlsGuide(show: Boolean? = null) = update { it.copy(showControlsGuide = show ?: !it.showControlsGuide) }

    fun toggleSatellites() {
        requireFeature("satellite", "Orbital Satellite Tracking") {
            update { it.copy(showSatellites = !it.showSatellites) }
        }
    }

    fun toggleHazards() {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            update { it.copy(showHazards = !it.showHazards) }
        }
    }

    fun toggleAurora() {
        requireFeature("weather", "Weather & Space Weather") {
            update { it.copy(showAurora = !it.showAurora) }
        }
    }

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

    fun toggleFlightMode() {
        requireFeature("flight", "Supersonic Flight Simulator") {
            flightVm.toggleFlightMode(features.globe.countries)
        }
    }

    fun randomFlightRoute() {
        requireFeature("flight", "Supersonic Flight Simulator") {
            flightVm.generateRandomFlightRoute(features.globe.countries)
        }
    }

    fun setFlightOrigin(country: Country) {
        requireFeature("flight", "Supersonic Flight Simulator") {
            flightVm.setFlightOrigin(country)
        }
    }

    fun setFlightDestination(country: Country) {
        requireFeature("flight", "Supersonic Flight Simulator") {
            flightVm.setFlightDestination(country)
        }
    }

    fun toggleQuizMode() {
        requireFeature("quiz", "Geography Quiz Challenge") {
            quizVm.toggleQuizMode(features.globe.countries)
        }
    }

    fun nextQuizQuestion() {
        requireFeature("quiz", "Geography Quiz Challenge") {
            quizVm.generateNextQuizQuestion(features.globe.countries)
        }
    }

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
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            setOverlay(null)
            hazardVm.selectHazard(hazard)
            flyTo(hazard.lat, hazard.lng, 2.2f)
        }
    }

    fun scrubStarted() = update { it.copy(isTimeMachineLive = false, isSimulationPlaying = false) }

    fun selectEpoch(epoch: Long) {
        update { it.copy(currentTimeMillis = epoch, isTimeMachineLive = false) }
        economyVm.updateTime(epoch)
    }

    fun resetLive() {
        val now = currentEpochMillis()
        update { it.copy(currentTimeMillis = now, isTimeMachineLive = true, isSimulationPlaying = false) }
        economyVm.updateTime(now)
    }

    fun toggleSimulationPlayPause() = update { it.copy(isSimulationPlaying = !it.isSimulationPlaying, isTimeMachineLive = false) }

    fun toggleSimulationDirection() = update { it.copy(isReversePlayback = !it.isReversePlayback) }

    fun setSimulationSpeedMultiplier(mult: Float) = update { it.copy(timeMultiplier = mult) }

    fun selectHazard(hazard: NasaNaturalEvent?) {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            hazardVm.selectHazard(hazard)
        }
    }

    fun selectIss(telemetry: ISSTelemetry) {
        requireFeature("satellite", "Orbital Satellite Tracking") {
            issVm.selectIss(telemetry)
        }
    }

    fun clearIss() = issVm.selectIss(null)

    fun selectSatellite(sat: SatelliteTelemetry?) {
        requireFeature("satellite", "Orbital Satellite Tracking") {
            satelliteVm.selectSatellite(sat)
        }
    }

    fun toggleLockOnSatellite(enabled: Boolean? = null) {
        requireFeature("satellite", "Orbital Satellite Tracking") {
            satelliteVm.toggleLockOnFollow(enabled)
        }
    }

    fun releaseSatelliteLockOn() = satelliteVm.releaseLockOn()

    fun calculateNextPassForCountry(
        lat: Double,
        lng: Double,
    ) = satelliteVm.calculateNextPassForCountry(lat, lng)

    fun startComparison(
        countryA: Country,
        countryB: Country? = null,
    ) {
        requireFeature("comparison", "Country Comparison & True Size") {
            setDossierOpen(false)
            comparisonVm.startComparison(countryA, countryB)
            if (countryB != null) {
                flyTo(countryB.center.lat, countryB.center.lng, 2.2f)
            }
        }
    }

    fun setComparisonCountryA(country: Country) {
        requireFeature("comparison", "Country Comparison & True Size") {
            comparisonVm.setCountryA(country)
        }
    }

    fun setComparisonCountryB(country: Country) {
        requireFeature("comparison", "Country Comparison & True Size") {
            comparisonVm.setCountryB(country)
            flyTo(country.center.lat, country.center.lng, 2.2f)
        }
    }

    fun swapComparisonCountries() {
        requireFeature("comparison", "Country Comparison & True Size") {
            comparisonVm.swapCountries()
            features.comparison.countryA?.let { countryA ->
                flyTo(countryA.center.lat, countryA.center.lng, 2.2f)
            }
        }
    }

    fun closeComparison() {
        comparisonVm.setSheetOpen(false)
    }

    fun selectSlotForCountryPicker(slot: Int?) {
        comparisonVm.setShowCountrySelectorForSlot(slot)
    }

    fun toggleTimezoneLayer() {
        requireFeature("economy", "World Bank & Stock Exchanges") {
            economyVm.toggleTimezoneLayer()
        }
    }

    fun toggleMarketCard() {
        requireFeature("economy", "World Bank & Stock Exchanges") {
            economyVm.toggleMarketCard()
        }
    }

    fun toggleMarketLayer() {
        requireFeature("economy", "World Bank & Stock Exchanges") {
            economyVm.toggleMarketLayer()
        }
    }

    fun selectMarket(market: FinancialMarket?) {
        requireFeature("economy", "World Bank & Stock Exchanges") {
            economyVm.selectMarket(market)
        }
    }

    fun flyToMarket(market: FinancialMarket) {
        requireFeature("economy", "World Bank & Stock Exchanges") {
            economyVm.selectMarket(market)
            flyTo(market.lat, market.lng, 2.2f)
        }
    }

    fun selectMeridian(utcOffset: Int?) {
        requireFeature("economy", "World Bank & Stock Exchanges") {
            economyVm.selectMeridian(utcOffset)
        }
    }

    fun closeTimezoneSheet() = economyVm.setSheetOpen(false)

    fun updateTimezoneTime(epochMillis: Long) = economyVm.updateTime(epochMillis)

    fun toggleTectonicLayer() {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            hazardsVm.toggleTectonicLayer()
        }
    }

    fun selectPlate(plate: TectonicPlate?) {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            hazardsVm.selectPlate(plate)
        }
    }

    fun selectEarthquake(quake: Earthquake?) {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            hazardsVm.selectEarthquake(quake)
        }
    }

    fun flyToEarthquake(quake: Earthquake) {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            hazardsVm.selectEarthquake(quake)
            flyTo(quake.lat, quake.lng, 2.2f)
        }
    }

    fun setMinMagnitude(mag: Double) {
        requireFeature("hazards", "Geology Hazards & NASA Crisis") {
            hazardsVm.setMinMagnitude(mag)
        }
    }
}
