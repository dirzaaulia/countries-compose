package com.dirzaaulia.countries.ui.economy

import androidx.lifecycle.ViewModel
import com.dirzaaulia.countries.data.timezone.FinancialMarketRepository
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.MAJOR_FINANCIAL_MARKETS
import com.dirzaaulia.countries.domain.astronomy.calculateActiveMarketOverlap
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EconomyUiState(
    val isTimezoneLayerActive: Boolean = false,
    val isMarketLayerActive: Boolean = false,
    val showMarketCard: Boolean = false,
    val showTimezoneSheet: Boolean = false,
    val selectedMeridianOffset: Int? = null,
    val selectedMarket: FinancialMarket? = null,
    val markets: List<FinancialMarket> = MAJOR_FINANCIAL_MARKETS,
    val activeMarkets: List<FinancialMarket> = emptyList(),
    val currentUtcTimeMillis: Long = 0L,
)

class EconomyViewModel(
    private val financialMarketRepository: FinancialMarketRepository = FinancialMarketRepository(),
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            EconomyUiState(
                markets = financialMarketRepository.getCurrentMarkets(),
            ),
        )
    val uiState: StateFlow<EconomyUiState> = _uiState.asStateFlow()

    init {
        updateTime(currentEpochMillis())
    }

    fun toggleTimezoneLayer() {
        val nextState = !_uiState.value.isTimezoneLayerActive
        _uiState.value = _uiState.value.copy(isTimezoneLayerActive = nextState)
    }

    fun toggleMarketLayer() {
        val nextState = !_uiState.value.isMarketLayerActive
        _uiState.value = _uiState.value.copy(isMarketLayerActive = nextState)
    }

    fun setTimezoneLayerActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isTimezoneLayerActive = active)
    }

    fun toggleMarketCard() {
        val nextState = !_uiState.value.showMarketCard
        _uiState.value =
            _uiState.value.copy(
                showMarketCard = nextState,
                isMarketLayerActive = nextState,
            )
    }

    fun setShowMarketCard(show: Boolean) {
        _uiState.value =
            _uiState.value.copy(
                showMarketCard = show,
                isMarketLayerActive = show,
            )
    }

    fun selectMeridian(utcOffset: Int?) {
        _uiState.value =
            _uiState.value.copy(
                selectedMeridianOffset = utcOffset,
                showTimezoneSheet = utcOffset != null,
            )
    }

    fun selectMarket(market: FinancialMarket?) {
        _uiState.value = _uiState.value.copy(selectedMarket = market)
    }

    fun updateMarketsFromRemote(remoteList: List<FinancialMarket>) {
        if (remoteList.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(markets = remoteList)
            updateTime(_uiState.value.currentUtcTimeMillis)
        }
    }

    fun loadMarketsFromRemoteConfig(jsonString: String?) {
        val updated = financialMarketRepository.updateMarketsFromRemoteConfig(jsonString)
        updateMarketsFromRemote(updated)
    }

    fun setSheetOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(showTimezoneSheet = open)
    }

    fun updateTime(epochMillis: Long) {
        val utcMillis = ((epochMillis % 86_400_000L) + 86_400_000L) % 86_400_000L
        val utcHour = utcMillis / 3_600_000.0
        val active = calculateActiveMarketOverlap(utcHour, _uiState.value.markets)

        _uiState.value =
            _uiState.value.copy(
                currentUtcTimeMillis = epochMillis,
                activeMarkets = active,
            )
    }
}
