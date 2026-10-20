package com.dirzaaulia.countries.data.timezone

import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.FinancialMarketsBundleDto
import com.dirzaaulia.countries.domain.astronomy.MAJOR_FINANCIAL_MARKETS
import com.dirzaaulia.countries.domain.astronomy.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class FinancialMarketRepository(
    private val json: Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        },
) {
    private val _marketsFlow = MutableStateFlow<List<FinancialMarket>>(MAJOR_FINANCIAL_MARKETS)
    val marketsFlow: StateFlow<List<FinancialMarket>> = _marketsFlow.asStateFlow()

    fun parseMarketsFromJson(jsonString: String): List<FinancialMarket> =
        runCatching {
            val bundle = json.decodeFromString<FinancialMarketsBundleDto>(jsonString)
            bundle.markets.map { it.toDomain() }
        }.getOrDefault(emptyList())

    fun updateMarketsFromRemoteConfig(jsonString: String?): List<FinancialMarket> {
        if (jsonString.isNullOrBlank()) {
            return _marketsFlow.value
        }
        val parsed = parseMarketsFromJson(jsonString)
        if (parsed.isNotEmpty()) {
            _marketsFlow.value = parsed
            return parsed
        }
        return _marketsFlow.value
    }

    fun getCurrentMarkets(): List<FinancialMarket> = _marketsFlow.value
}
