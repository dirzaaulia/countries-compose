package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.data.repository.AdministrativeRepository
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdministrativeUiState(
    val administrativeDivisions: List<AdministrativeDivision> = emptyList(),
    val isFetchingAdministrativeDivisions: Boolean = false,
    val selectedAdministrativeDivision: AdministrativeDivision? = null,
    val administrativeLevel: AdminLevel = AdminLevel.ADM1,
    val selectedAdm1Division: AdministrativeDivision? = null,
)

class AdministrativeViewModel(
    private val repository: AdministrativeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdministrativeUiState())
    val uiState: StateFlow<AdministrativeUiState> = _uiState.asStateFlow()

    // Backward-compatibility delegating flows
    val administrativeDivisions: StateFlow<List<AdministrativeDivision>> =
        _uiState
            .map { it.administrativeDivisions }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.administrativeDivisions)

    val isFetchingAdministrativeDivisions: StateFlow<Boolean> =
        _uiState
            .map { it.isFetchingAdministrativeDivisions }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.isFetchingAdministrativeDivisions)

    val selectedAdministrativeDivision: StateFlow<AdministrativeDivision?> =
        _uiState
            .map { it.selectedAdministrativeDivision }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.selectedAdministrativeDivision)

    val administrativeLevel: StateFlow<AdminLevel> =
        _uiState
            .map { it.administrativeLevel }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.administrativeLevel)

    val selectedAdm1Division: StateFlow<AdministrativeDivision?> =
        _uiState
            .map { it.selectedAdm1Division }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.selectedAdm1Division)

    fun loadAdmDivisions(
        country: Country,
        level: AdminLevel = AdminLevel.ADM1,
    ) {
        _uiState.value =
            _uiState.value.copy(
                administrativeLevel = level,
                isFetchingAdministrativeDivisions = true,
            )
        viewModelScope.launch {
            val divisions =
                repository.fetchAdministrativeDivisions(
                    country.id.ifEmpty { country.iso2 },
                    level,
                )
            _uiState.value =
                _uiState.value.copy(
                    administrativeDivisions = divisions,
                    isFetchingAdministrativeDivisions = false,
                )
        }
    }

    fun selectAdministrativeDivision(division: AdministrativeDivision?) {
        _uiState.value =
            _uiState.value.copy(
                selectedAdministrativeDivision = division,
                selectedAdm1Division = if (division?.level == AdminLevel.ADM1) division else _uiState.value.selectedAdm1Division,
            )
    }

    fun resetAdmState() {
        _uiState.value = AdministrativeUiState()
    }
}
