package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.repository.CountryRepository
import com.dirzaaulia.countries.domain.repository.EclipseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GlobeUiState(
    val countries: List<Country> = emptyList(),
    val selectedCountryId: String? = null,
    val showCountryDossier: Boolean = false,
    val eclipseFeed: EclipseFeed? = null,
    val isEclipseFeedLoading: Boolean = true,
)

class GlobeViewModel(
    private val countryRepository: CountryRepository,
    private val eclipseRepository: EclipseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GlobeUiState())
    val uiState: StateFlow<GlobeUiState> = _uiState.asStateFlow()

    // Backward-compatibility delegating flows
    val countries: StateFlow<List<Country>> =
        _uiState
            .map { it.countries }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.countries)

    val selectedCountryId: StateFlow<String?> =
        _uiState
            .map { it.selectedCountryId }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.selectedCountryId)

    val showCountryDossier: StateFlow<Boolean> =
        _uiState
            .map { it.showCountryDossier }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.showCountryDossier)

    val eclipseFeed: StateFlow<EclipseFeed?> =
        _uiState
            .map { it.eclipseFeed }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.eclipseFeed)

    val isEclipseFeedLoading: StateFlow<Boolean> =
        _uiState
            .map { it.isEclipseFeedLoading }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.isEclipseFeedLoading)

    fun flyTo(country: Country) = selectCountry(country.id)

    suspend fun loadInitialData() {
        val loadedCountries = countryRepository.loadCountries()
        _uiState.value = _uiState.value.copy(countries = loadedCountries)
    }

    fun loadEclipseFeed() {
        if (_uiState.value.eclipseFeed != null || !_uiState.value.isEclipseFeedLoading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEclipseFeedLoading = true)
            val feed = eclipseRepository.fetchEclipseFeed()
            _uiState.value =
                _uiState.value.copy(
                    eclipseFeed = feed,
                    isEclipseFeedLoading = false,
                )
        }
    }

    fun selectCountry(
        countryId: String?,
        isQuizMode: Boolean = false,
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedCountryId = countryId,
                showCountryDossier = false,
            )
    }

    fun setShowCountryDossier(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCountryDossier = show)
    }
}
