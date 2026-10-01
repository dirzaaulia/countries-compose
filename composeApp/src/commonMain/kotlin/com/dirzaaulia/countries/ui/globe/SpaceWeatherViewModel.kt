package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.data.repository.AuroralCountry
import com.dirzaaulia.countries.data.repository.SpaceWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SpaceWeatherUiState(
    val kpIndex: Double = 3.6,
    val solarWindSpeed: Double = 420.0,
    val bzGsm: Double = -2.5,
    val isAuroraActive: Boolean = true,
    val affectedCountries: List<AuroralCountry> = emptyList(),
    val isLoading: Boolean = false,
)

class SpaceWeatherViewModel(
    private val repository: SpaceWeatherRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SpaceWeatherUiState())
    val uiState: StateFlow<SpaceWeatherUiState> = _uiState.asStateFlow()

    init {
        refreshSpaceWeather()
    }

    fun refreshSpaceWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val info = repository.fetchSpaceWeather()
            _uiState.value =
                SpaceWeatherUiState(
                    kpIndex = info.kpIndex,
                    solarWindSpeed = info.solarWindSpeed,
                    bzGsm = info.bzGsm,
                    isAuroraActive = info.isAuroraActive,
                    affectedCountries = info.affectedCountries,
                    isLoading = false,
                )
        }
    }
}
