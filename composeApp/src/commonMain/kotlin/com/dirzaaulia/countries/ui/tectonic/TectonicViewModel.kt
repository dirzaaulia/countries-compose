package com.dirzaaulia.countries.ui.tectonic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.domain.repository.TectonicRepository
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TectonicUiState(
    val plates: List<TectonicPlate> = emptyList(),
    val earthquakes: List<Earthquake> = emptyList(),
    val isTectonicLayerActive: Boolean = false,
    val selectedPlate: TectonicPlate? = null,
    val selectedEarthquake: Earthquake? = null,
    val minMagnitudeFilter: Double = 4.5,
    val isLoading: Boolean = false,
)

class TectonicViewModel(
    private val tectonicRepository: TectonicRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TectonicUiState())
    val uiState: StateFlow<TectonicUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun toggleTectonicLayer() {
        val next = !_uiState.value.isTectonicLayerActive
        _uiState.value = _uiState.value.copy(isTectonicLayerActive = next)
        if (next && _uiState.value.plates.isEmpty()) {
            loadData()
        }
    }

    fun setTectonicLayerActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isTectonicLayerActive = active)
        if (active && _uiState.value.plates.isEmpty()) {
            loadData()
        }
    }

    fun selectPlate(plate: TectonicPlate?) {
        _uiState.value = _uiState.value.copy(selectedPlate = plate)
    }

    fun selectEarthquake(quake: Earthquake?) {
        _uiState.value = _uiState.value.copy(selectedEarthquake = quake)
    }

    fun setMinMagnitude(mag: Double) {
        _uiState.value = _uiState.value.copy(minMagnitudeFilter = mag)
    }

    fun refreshEarthquakes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val quakes = tectonicRepository.fetchLiveEarthquakes(forceRefresh = true)
            _uiState.value = _uiState.value.copy(earthquakes = quakes, isLoading = false)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val plates = tectonicRepository.loadTectonicPlates()
            val quakes = tectonicRepository.fetchLiveEarthquakes()
            _uiState.value =
                _uiState.value.copy(
                    plates = plates,
                    earthquakes = quakes,
                    isLoading = false,
                )
        }
    }
}
