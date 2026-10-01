package com.dirzaaulia.countries.domain.astronomy

import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

data class MarsEnvironmentInfo(
    val solNumber: Long,
    val mtcTimeFormatted: String,
    val solarLongitudeDeg: Double,
    val seasonName: String,
    val earthDistanceMillionKm: Double,
    val radioDelayMinutes: Double,
    val isDustStormSeason: Boolean,
)

object MarsAstronomyMath {
    private const val J2000_JD = 2451545.0

    /**
     * Calculates current Martian Sol Date, Local Mean Time, Season, and Orbital telemetry.
     */
    fun calculateMarsEnvironmentInfo(epochMillis: Long = currentEpochMillis()): MarsEnvironmentInfo {
        // 1. Julian Date
        val jdUt = 2440587.5 + (epochMillis / 86400000.0)
        val jdTt = jdUt + (69.184 / 86400.0)

        // 2. Mars Sol Date (MSD)
        val msd = (jdTt - 2405522.0028779) / 1.0274912517
        val solNumber = floor(msd).toLong()

        // 3. Mars Coordinated Time (MTC)
        val mtcDecimal = 24.0 * (msd % 1.0)
        val hours = floor(mtcDecimal).toInt()
        val minutes = floor((mtcDecimal - hours) * 60).toInt()
        val seconds = floor((((mtcDecimal - hours) * 60) - minutes) * 60).toInt()

        val hh = hours.toString().padStart(2, '0')
        val mm = minutes.toString().padStart(2, '0')
        val ss = seconds.toString().padStart(2, '0')
        val mtcTimeFormatted = "$hh:$mm:$ss MTC"

        // 4. Solar Longitude (Ls)
        val d = jdTt - J2000_JD
        val meanAnomaly = (19.3870 + 0.52402075 * d) % 360.0
        val meanAnomalyRad = meanAnomaly * PI / 180.0
        val equationOfCenter = 10.691 * sin(meanAnomalyRad) + 0.623 * sin(2 * meanAnomalyRad)
        var ls = (250.99 + 0.52402075 * d + equationOfCenter) % 360.0
        if (ls < 0.0) ls += 360.0

        // 5. Martian Seasons
        val seasonName =
            when {
                ls < 90 -> "Northern Spring / Southern Autumn"
                ls < 180 -> "Northern Summer / Southern Winter"
                ls < 270 -> "Northern Autumn / Southern Spring"
                else -> "Northern Winter / Southern Summer"
            }
        val isDustStormSeason = ls in 180.0..330.0

        // 6. Earth-Mars Distance & Signal Latency
        val synodicCycle = (d / 779.94) * 360.0
        val synodicRad = synodicCycle * PI / 180.0
        val earthDistanceMillionKm = 227.8 + 173.2 * cos(synodicRad)
        val earthDistanceKm = earthDistanceMillionKm * 1_000_000.0
        val radioDelayMinutes = earthDistanceKm / (299792.458 * 60.0)

        return MarsEnvironmentInfo(
            solNumber = solNumber,
            mtcTimeFormatted = mtcTimeFormatted,
            solarLongitudeDeg = ls,
            seasonName = seasonName,
            earthDistanceMillionKm = earthDistanceMillionKm,
            radioDelayMinutes = radioDelayMinutes,
            isDustStormSeason = isDustStormSeason,
        )
    }
}
