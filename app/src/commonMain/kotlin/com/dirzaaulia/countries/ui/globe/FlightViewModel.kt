package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FlightUiState(
    val isFlightMode: Boolean = false,
    val flightOrigin: Country? = null,
    val flightDestination: Country? = null,
    val flightRoute: List<LatLng>? = null,
    val flightDistanceKm: Double = 0.0,
)

class FlightViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    // Backward-compatibility delegating flows
    val isFlightMode: StateFlow<Boolean> =
        _uiState
            .map { it.isFlightMode }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.isFlightMode)

    val flightOrigin: StateFlow<Country?> =
        _uiState
            .map { it.flightOrigin }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.flightOrigin)

    val flightDestination: StateFlow<Country?> =
        _uiState
            .map { it.flightDestination }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.flightDestination)

    val flightRoute: StateFlow<List<LatLng>?> =
        _uiState
            .map { it.flightRoute }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.flightRoute)

    val flightDistanceKm: StateFlow<Double> =
        _uiState
            .map { it.flightDistanceKm }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.flightDistanceKm)

    fun toggleFlightMode(countries: List<Country>) {
        val nextMode = !_uiState.value.isFlightMode
        if (nextMode) {
            _uiState.value = _uiState.value.copy(isFlightMode = true)
            generateRandomFlightRoute(countries)
        } else {
            _uiState.value =
                _uiState.value.copy(
                    isFlightMode = false,
                    flightRoute = null,
                )
        }
    }

    fun generateRandomFlightRoute(countries: List<Country>) {
        if (countries.size >= 2) {
            val origin = countries.random()
            val destination = countries.filter { it.id != origin.id }.random()
            setFlightRoute(origin, destination)
        }
    }

    fun setFlightOrigin(country: Country) {
        val destination = _uiState.value.flightDestination
        if (destination != null) {
            setFlightRoute(country, destination)
        } else {
            _uiState.value = _uiState.value.copy(flightOrigin = country)
        }
    }

    fun setFlightDestination(country: Country) {
        val origin = _uiState.value.flightOrigin
        if (origin != null) {
            setFlightRoute(origin, country)
        } else {
            _uiState.value = _uiState.value.copy(flightDestination = country)
        }
    }

    private fun setFlightRoute(
        origin: Country,
        destination: Country,
    ) {
        val route = AstronomyMath.calculateGreatCircleArc(origin.center, destination.center, 50)
        val distance = AstronomyMath.calculateGreatCircleDistance(origin.center, destination.center)
        _uiState.value =
            _uiState.value.copy(
                flightOrigin = origin,
                flightDestination = destination,
                flightRoute = route,
                flightDistanceKm = distance,
            )
    }
}
