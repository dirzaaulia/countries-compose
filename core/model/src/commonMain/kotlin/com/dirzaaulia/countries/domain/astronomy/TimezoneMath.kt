package com.dirzaaulia.countries.domain.astronomy

import com.dirzaaulia.countries.domain.globe.Point3D
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.domain.globe.toDegrees
import kotlin.math.asin

fun calculateTwilightPhase(
    lat: Double,
    lng: Double,
    sunVector: Point3D,
): TwilightPhase {
    val p = latLngToCartesian(lat, lng, 1.0)
    val dot = p.x * sunVector.x + p.y * sunVector.y + p.z * sunVector.z
    val elevationDeg = asin(dot.coerceIn(-1.0, 1.0)).toDegrees

    return when {
        elevationDeg > 6.0 -> TwilightPhase.DAY
        elevationDeg >= -4.0 -> TwilightPhase.GOLDEN_HOUR
        elevationDeg >= -6.0 -> TwilightPhase.BLUE_HOUR
        else -> TwilightPhase.NIGHT
    }
}

fun calculateMarketStatus(
    market: FinancialMarket,
    utcHour: Double,
): MarketStatus {
    val localHour = (utcHour + market.utcOffsetHours + 24.0) % 24.0
    return when {
        localHour >= market.openHourLocal && localHour < market.closeHourLocal -> MarketStatus.OPEN
        localHour >= (market.openHourLocal - 1) && localHour < market.openHourLocal -> MarketStatus.OPENING_SOON
        else -> MarketStatus.CLOSED
    }
}

fun calculateActiveMarketOverlap(
    utcHour: Double,
    markets: List<FinancialMarket> = MAJOR_FINANCIAL_MARKETS,
): List<FinancialMarket> =
    markets.filter { market ->
        calculateMarketStatus(market, utcHour) == MarketStatus.OPEN
    }

fun calculateActiveOverlapName(activeMarkets: List<FinancialMarket>): String {
    val cities = activeMarkets.map { it.city.split(" ").first() }
    return when {
        cities.isEmpty() -> "No Active Exchanges"
        cities.size == 1 -> "${cities.first()} Session Active"
        else -> "${cities.joinToString(" & ")} Overlap Active"
    }
}
