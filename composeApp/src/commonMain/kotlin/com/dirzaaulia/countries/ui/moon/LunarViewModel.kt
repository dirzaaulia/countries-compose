package com.dirzaaulia.countries.ui.moon

import androidx.lifecycle.ViewModel
import com.dirzaaulia.countries.domain.moon.ALL_LUNAR_LANDMARKS
import com.dirzaaulia.countries.domain.moon.LunarLandmark
import com.dirzaaulia.countries.domain.moon.LunarLandmarkType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LunarUiState(
    val selectedCategory: LunarLandmarkType? = null,
    val selectedLandmark: LunarLandmark? = null,
    val isFarSideActive: Boolean = false,
    val searchQuery: String = "",
)

class LunarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LunarUiState())
    val uiState: StateFlow<LunarUiState> = _uiState.asStateFlow()

    fun selectCategory(type: LunarLandmarkType?) {
        _uiState.value = _uiState.value.copy(selectedCategory = type)
    }

    fun selectLandmark(landmark: LunarLandmark?) {
        _uiState.value = _uiState.value.copy(selectedLandmark = landmark)
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setFarSideActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isFarSideActive = active)
    }

    fun getFilteredLandmarks(): List<LunarLandmark> {
        val cat = _uiState.value.selectedCategory
        val query = _uiState.value.searchQuery.trim().lowercase()

        return ALL_LUNAR_LANDMARKS.filter { landmark ->
            val matchCat = (cat == null || landmark.type == cat)
            val matchQuery =
                query.isEmpty() ||
                    landmark.name.lowercase().contains(query) ||
                    landmark.latinName?.lowercase()?.contains(query) == true ||
                    landmark.significance.lowercase().contains(query)
            matchCat && matchQuery
        }
    }
}
