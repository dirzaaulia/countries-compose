package com.dirzaaulia.countries.domain.astronomy

import kotlinx.serialization.Serializable

enum class MarketStatus {
    OPEN,
    CLOSED,
    OPENING_SOON,
}

enum class TwilightPhase {
    DAY,
    GOLDEN_HOUR,
    BLUE_HOUR,
    NIGHT,
}

@Serializable
data class FinancialMarketConfigDto(
    val id: String,
    val city: String,
    val countryIso2: String,
    val exchange: String,
    val utcOffsetHours: Double,
    val openHourLocal: Double = 9.0,
    val closeHourLocal: Double = 17.0,
    val lat: Double,
    val lng: Double,
)

@Serializable
data class FinancialMarketsBundleDto(
    val version: Int = 1,
    val lastUpdated: String = "2026-10-02",
    val markets: List<FinancialMarketConfigDto> = emptyList(),
)

data class FinancialMarket(
    val id: String,
    val city: String,
    val countryIso2: String,
    val exchange: String,
    val utcOffsetHours: Double,
    val openHourLocal: Double = 9.0,
    val closeHourLocal: Double = 17.0,
    val lat: Double,
    val lng: Double,
)

fun FinancialMarketConfigDto.toDomain(): FinancialMarket =
    FinancialMarket(
        id = id,
        city = city,
        countryIso2 = countryIso2,
        exchange = exchange,
        utcOffsetHours = utcOffsetHours,
        openHourLocal = openHourLocal,
        closeHourLocal = closeHourLocal,
        lat = lat,
        lng = lng,
    )

fun FinancialMarket.toDto(): FinancialMarketConfigDto =
    FinancialMarketConfigDto(
        id = id,
        city = city,
        countryIso2 = countryIso2,
        exchange = exchange,
        utcOffsetHours = utcOffsetHours,
        openHourLocal = openHourLocal,
        closeHourLocal = closeHourLocal,
        lat = lat,
        lng = lng,
    )

val MAJOR_FINANCIAL_MARKETS: List<FinancialMarket> =
    listOf(
        FinancialMarket("nyse", "New York", "US", "NYSE / NASDAQ", -5.0, 9.5, 16.0, 40.7128, -74.0060),
        FinancialMarket("cboe", "Chicago", "US", "Cboe / CME", -6.0, 8.5, 16.0, 41.8781, -87.6298),
        FinancialMarket("tsx", "Toronto", "CA", "TSX (Toronto)", -5.0, 9.5, 16.0, 43.6532, -79.3832),
        FinancialMarket("lse", "London", "GB", "LSE (London)", 0.0, 8.0, 16.5, 51.5074, -0.1278),
        FinancialMarket("euronext", "Paris", "FR", "Euronext Paris", 1.0, 9.0, 17.5, 48.8566, 2.3522),
        FinancialMarket("fwb", "Frankfurt", "DE", "FWB (Xetra)", 1.0, 9.0, 17.5, 50.1109, 8.6821),
        FinancialMarket("tse", "Tokyo", "JP", "TSE (Tokyo)", 9.0, 9.0, 15.0, 35.6762, 139.6503),
        FinancialMarket("sse", "Shanghai", "CN", "SSE (Shanghai)", 8.0, 9.5, 15.0, 31.2304, 121.4737),
        FinancialMarket("hkex", "Hong Kong", "HK", "HKEX (Hong Kong)", 8.0, 9.5, 16.0, 22.3193, 114.1694),
        FinancialMarket("asx", "Sydney", "AU", "ASX (Sydney)", 10.0, 10.0, 16.0, -33.8688, 151.2093),
        FinancialMarket("sgx", "Singapore", "SG", "SGX (Singapore)", 8.0, 9.0, 17.0, 1.3521, 103.8198),
        FinancialMarket("nse", "Mumbai", "IN", "NSE India", 5.5, 9.25, 15.5, 19.0760, 72.8777),
    )
