package com.dirzaaulia.countries.ui.dossier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.repository.CountryDetailRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DossierUiState(
    val selectedCountry: Country? = null,
    val liveDetails: LiveCountryDetails? = null,
    val isFetchingLive: Boolean = false,
    val isDossierOpen: Boolean = false,
)

class DossierViewModel(
    private val repository: CountryDetailRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DossierUiState())
    val uiState: StateFlow<DossierUiState> = _uiState.asStateFlow()

    private var liveFetchJob: Job? = null

    fun selectCountry(country: Country?) {
        liveFetchJob?.cancel()
        if (country == null) {
            _uiState.value = DossierUiState()
            return
        }
        _uiState.value =
            _uiState.value.copy(
                selectedCountry = country,
                liveDetails = null,
                isFetchingLive = true,
            )
        liveFetchJob =
            viewModelScope.launch {
                val details = repository.fetchLiveDetails(country)
                _uiState.value =
                    _uiState.value.copy(
                        liveDetails = details,
                        isFetchingLive = false,
                    )
            }
    }

    fun openDossier(country: Country) {
        _uiState.value =
            _uiState.value.copy(
                selectedCountry = country,
                isDossierOpen = true,
            )
        if (_uiState.value.liveDetails == null) {
            selectCountry(country)
        }
    }

    fun setDossierOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isDossierOpen = open)
    }

    fun closeDossier() {
        liveFetchJob?.cancel()
        _uiState.value = _uiState.value.copy(isDossierOpen = false)
    }

    fun clear() {
        liveFetchJob?.cancel()
        _uiState.value = DossierUiState()
    }
}
