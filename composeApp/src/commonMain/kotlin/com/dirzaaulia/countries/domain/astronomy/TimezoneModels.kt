package com.dirzaaulia.countries.domain.astronomy

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

val MAJOR_FINANCIAL_MARKETS: List<FinancialMarket> =
    listOf(
        FinancialMarket("nyse", "New York", "US", "NYSE / NASDAQ", -5.0, 9.5, 16.0, 40.7128, -74.0060),
        FinancialMarket("lse", "London", "GB", "LSE (London)", 0.0, 8.0, 16.5, 51.5074, -0.1278),
        FinancialMarket("tse", "Tokyo", "JP", "TSE (Tokyo)", 9.0, 9.0, 15.0, 35.6762, 139.6503),
        FinancialMarket("sse", "Shanghai", "CN", "SSE (Shanghai)", 8.0, 9.5, 15.0, 31.2304, 121.4737),
        FinancialMarket("hkex", "Hong Kong", "HK", "HKEX (Hong Kong)", 8.0, 9.5, 16.0, 22.3193, 114.1694),
        FinancialMarket("sgx", "Singapore", "SG", "SGX (Singapore)", 8.0, 9.0, 17.0, 1.3521, 103.8198),
        FinancialMarket("fwb", "Frankfurt", "DE", "FWB (Xetra)", 1.0, 9.0, 17.5, 50.1109, 8.6821),
        FinancialMarket("euronext", "Paris", "FR", "Euronext Paris", 1.0, 9.0, 17.5, 48.8566, 2.3522),
        FinancialMarket("six", "Zurich", "CH", "SIX Swiss", 1.0, 9.0, 17.5, 47.3769, 8.5417),
        FinancialMarket("tsx", "Toronto", "CA", "TSX (Toronto)", -5.0, 9.5, 16.0, 43.6532, -79.3832),
        FinancialMarket("nse", "Mumbai", "IN", "NSE India", 5.5, 9.25, 15.5, 19.0760, 72.8777),
        FinancialMarket("asx", "Sydney", "AU", "ASX (Sydney)", 10.0, 10.0, 16.0, -33.8688, 151.2093),
        FinancialMarket("dfm", "Dubai", "AE", "DFM (Dubai)", 4.0, 10.0, 15.0, 25.2048, 55.2708),
        FinancialMarket("krx", "Seoul", "KR", "KRX (Korea)", 9.0, 9.0, 15.5, 37.5665, 126.9780),
        FinancialMarket("b3", "São Paulo", "BR", "B3 Brasil", -3.0, 10.0, 17.0, -23.5505, -46.6333),
    )
