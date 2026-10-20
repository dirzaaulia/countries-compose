package com.dirzaaulia.countries.ui.comparison

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

data class ComparisonUiState(
    val countryA: Country? = null,
    val countryB: Country? = null,
    val detailsA: LiveCountryDetails? = null,
    val detailsB: LiveCountryDetails? = null,
    val isComparing: Boolean = false,
    val showComparisonSheet: Boolean = false,
    val showCountrySelectorForSlot: Int? = null,
)

class ComparisonViewModel(
    private val countryDetailRepository: CountryDetailRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    private var detailsAJob: Job? = null
    private var detailsBJob: Job? = null

    fun startComparison(
        countryA: Country,
        countryB: Country? = null,
    ) {
        val nextCountryB = countryB ?: _uiState.value.countryB
        _uiState.value =
            _uiState.value.copy(
                countryA = countryA,
                countryB = nextCountryB,
                isComparing = nextCountryB != null,
                showComparisonSheet = true,
                showCountrySelectorForSlot = if (nextCountryB == null) 2 else null,
            )
        fetchDetailsA(countryA)
        if (nextCountryB != null) {
            fetchDetailsB(nextCountryB)
        }
    }

    fun setCountryA(country: Country) {
        _uiState.value =
            _uiState.value.copy(
                countryA = country,
                showCountrySelectorForSlot = null,
                isComparing = _uiState.value.countryB != null,
            )
        fetchDetailsA(country)
    }

    fun setCountryB(country: Country) {
        _uiState.value =
            _uiState.value.copy(
                countryB = country,
                showCountrySelectorForSlot = null,
                isComparing = _uiState.value.countryA != null,
            )
        fetchDetailsB(country)
    }

    fun swapCountries() {
        val currentA = _uiState.value.countryA
        val currentB = _uiState.value.countryB
        val currentDetailsA = _uiState.value.detailsA
        val currentDetailsB = _uiState.value.detailsB

        _uiState.value =
            _uiState.value.copy(
                countryA = currentB,
                countryB = currentA,
                detailsA = currentDetailsB,
                detailsB = currentDetailsA,
            )
    }

    fun setSheetOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(showComparisonSheet = open)
    }

    fun setShowCountrySelectorForSlot(slot: Int?) {
        _uiState.value = _uiState.value.copy(showCountrySelectorForSlot = slot)
    }

    fun clearComparison() {
        detailsAJob?.cancel()
        detailsBJob?.cancel()
        _uiState.value = ComparisonUiState()
    }

    private fun fetchDetailsA(country: Country) {
        detailsAJob?.cancel()
        detailsAJob =
            viewModelScope.launch {
                val details = countryDetailRepository.fetchLiveDetails(country)
                _uiState.value = _uiState.value.copy(detailsA = details)
            }
    }

    private fun fetchDetailsB(country: Country) {
        detailsBJob?.cancel()
        detailsBJob =
            viewModelScope.launch {
                val details = countryDetailRepository.fetchLiveDetails(country)
                _uiState.value = _uiState.value.copy(detailsB = details)
            }
    }
}
