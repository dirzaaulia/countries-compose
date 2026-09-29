package com.dirzaaulia.countries.ui.satellite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.data.satellite.SatelliteRepository
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SatelliteUiState(
    val fleet: List<SatelliteTelemetry> = emptyList(),
    val selectedSatellite: SatelliteTelemetry? = null,
    val nextPassOverSelectedCountry: String? = null,
    val isSatellitesLayerVisible: Boolean = true,
    val isLoading: Boolean = false
)

class SatelliteViewModel(
    private val repository: SatelliteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SatelliteUiState())
    val uiState: StateFlow<SatelliteUiState> = _uiState.asStateFlow()

    init {
        startTelemetryPolling()
    }

    fun selectSatellite(sat: SatelliteTelemetry?) {
        _uiState.update { it.copy(selectedSatellite = sat) }
    }

    fun toggleSatellitesLayer() {
        _uiState.update { it.copy(isSatellitesLayerVisible = !it.isSatellitesLayerVisible) }
    }

    fun calculateNextPassForCountry(countryLat: Double, countryLng: Double) {
        val selected = _uiState.value.selectedSatellite ?: return
        val pass = repository.calculateNextPass(selected, LatLng(countryLat, countryLng))
        if (pass != null) {
            _uiState.update {
                it.copy(nextPassOverSelectedCountry = "${pass.passTimeFormatted} (Max Elev: ${pass.maxElevationDeg}°)")
            }
        }
    }

    fun refreshTelemetry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val fleet = repository.fetchFleetTelemetry()
            _uiState.update {
                val currentSelected = it.selectedSatellite
                val updatedSelected = currentSelected?.let { s -> fleet.find { f -> f.id == s.id } } ?: currentSelected
                it.copy(
                    fleet = fleet,
                    selectedSatellite = updatedSelected,
                    isLoading = false
                )
            }
        }
    }

    private fun startTelemetryPolling() {
        viewModelScope.launch {
            while (true) {
                refreshTelemetry()
                delay(6000L) // 6-second polling loop for live LEO telemetry
            }
        }
    }
}
