package com.dirzaaulia.countries.ui.mars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.data.repository.MarsRoverRepository
import com.dirzaaulia.countries.domain.mars.MarsPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MarsRoverUiState(
    val rover: String = "perseverance",
    val sol: Long = 1000,
    val photos: List<MarsPhoto> = emptyList(),
    val isLoading: Boolean = false,
    val selectedPhoto: MarsPhoto? = null,
    val errorMessage: String? = null
)

class MarsRoverGalleryViewModel(
    private val repository: MarsRoverRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MarsRoverUiState())
    val uiState: StateFlow<MarsRoverUiState> = _uiState.asStateFlow()

    init {
        loadPhotos(_uiState.value.rover, _uiState.value.sol)
    }

    fun selectRover(rover: String) {
        _uiState.update { it.copy(rover = rover) }
        loadPhotos(rover, _uiState.value.sol)
    }

    fun setSol(sol: Long) {
        _uiState.update { it.copy(sol = sol) }
        loadPhotos(_uiState.value.rover, sol)
    }

    fun selectPhoto(photo: MarsPhoto?) {
        _uiState.update { it.copy(selectedPhoto = photo) }
    }

    private fun loadPhotos(rover: String, sol: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getPhotos(rover, sol).fold(
                onSuccess = { photos ->
                    _uiState.update { it.copy(photos = photos, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
                }
            )
        }
    }
}
