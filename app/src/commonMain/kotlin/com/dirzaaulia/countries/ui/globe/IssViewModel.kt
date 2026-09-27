package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.data.repository.IssRepository
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class IssUiState(
    val issTelemetry: ISSTelemetry? = null,
    val selectedIss: ISSTelemetry? = null,
    val isRefreshing: Boolean = false,
)

class IssViewModel(
    private val repository: IssRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(IssUiState())
    val uiState: StateFlow<IssUiState> = _uiState.asStateFlow()

    // Backward-compatibility delegating flows
    val issTelemetry: StateFlow<ISSTelemetry?> =
        _uiState
            .map { it.issTelemetry }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.issTelemetry)

    val selectedIss: StateFlow<ISSTelemetry?> =
        _uiState
            .map { it.selectedIss }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.selectedIss)

    fun selectIss(iss: ISSTelemetry?) {
        _uiState.value = _uiState.value.copy(selectedIss = iss)
    }

    fun refreshISSTelemetry() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            val telemetry = repository.fetchISSTelemetry()
            if (telemetry != null) {
                _uiState.value =
                    _uiState.value.copy(
                        issTelemetry = telemetry,
                        selectedIss = if (_uiState.value.selectedIss != null) telemetry else null,
                        isRefreshing = false,
                    )
            } else {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
}
