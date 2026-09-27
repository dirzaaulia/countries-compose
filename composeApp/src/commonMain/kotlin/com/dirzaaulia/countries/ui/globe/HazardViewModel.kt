package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.domain.repository.HazardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HazardUiState(
    val globalHazards: List<NasaNaturalEvent> = emptyList(),
    val selectedHazard: NasaNaturalEvent? = null,
    val isLoading: Boolean = false,
)

class HazardViewModel(
    private val repository: HazardRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HazardUiState())
    val uiState: StateFlow<HazardUiState> = _uiState.asStateFlow()

    // Backward-compatibility delegating flows
    val globalHazards: StateFlow<List<NasaNaturalEvent>> =
        _uiState
            .map { it.globalHazards }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.globalHazards)

    val selectedHazard: StateFlow<NasaNaturalEvent?> =
        _uiState
            .map { it.selectedHazard }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.selectedHazard)

    suspend fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val events = repository.fetchGlobalNasaEvents()
        _uiState.value =
            _uiState.value.copy(
                globalHazards = events,
                isLoading = false,
            )
    }

    fun selectHazard(hazard: NasaNaturalEvent?) {
        _uiState.value = _uiState.value.copy(selectedHazard = hazard)
    }
}
