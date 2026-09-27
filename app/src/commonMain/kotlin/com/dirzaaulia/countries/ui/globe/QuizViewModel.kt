package com.dirzaaulia.countries.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.countries.domain.country.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class QuizUiState(
    val isQuizMode: Boolean = false,
    val quizTargetCountry: Country? = null,
    val quizScore: Int = 0,
    val quizStreak: Int = 0,
    val quizFeedback: String? = null,
    val quizIsCorrect: Boolean? = null,
)

class QuizViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Backward-compatibility delegating flows
    val isQuizMode: StateFlow<Boolean> =
        _uiState
            .map { it.isQuizMode }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.isQuizMode)

    val quizTargetCountry: StateFlow<Country?> =
        _uiState
            .map { it.quizTargetCountry }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.quizTargetCountry)

    val quizScore: StateFlow<Int> =
        _uiState
            .map { it.quizScore }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.quizScore)

    val quizStreak: StateFlow<Int> =
        _uiState
            .map { it.quizStreak }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.quizStreak)

    val quizFeedback: StateFlow<String?> =
        _uiState
            .map { it.quizFeedback }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.quizFeedback)

    val quizIsCorrect: StateFlow<Boolean?> =
        _uiState
            .map { it.quizIsCorrect }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.quizIsCorrect)

    fun toggleQuizMode(countries: List<Country>) {
        val next = !_uiState.value.isQuizMode
        _uiState.value = _uiState.value.copy(isQuizMode = next)
        if (next) {
            generateNextQuizQuestion(countries)
        }
    }

    fun generateNextQuizQuestion(countries: List<Country>) {
        if (countries.isNotEmpty()) {
            _uiState.value =
                _uiState.value.copy(
                    quizTargetCountry = countries.random(),
                    quizFeedback = null,
                    quizIsCorrect = null,
                )
        }
    }

    fun handleQuizTap(
        clickedId: String,
        countries: List<Country>,
    ) {
        val target = _uiState.value.quizTargetCountry ?: return
        val tapped = countries.find { it.id == clickedId } ?: return

        if (clickedId == target.id) {
            val newScore = _uiState.value.quizScore + 100 + _uiState.value.quizStreak * 25
            val newStreak = _uiState.value.quizStreak + 1
            _uiState.value =
                _uiState.value.copy(
                    quizScore = newScore,
                    quizStreak = newStreak,
                    quizFeedback = "Correct! That is ${target.name}!",
                    quizIsCorrect = true,
                )
        } else {
            _uiState.value =
                _uiState.value.copy(
                    quizStreak = 0,
                    quizFeedback = "Wrong! That is ${tapped.name}. Keep looking for ${target.name}.",
                    quizIsCorrect = false,
                )
        }
    }
}
