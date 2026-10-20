package com.dirzaaulia.countries.domain.astronomy

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class MarsSolarDateInfo(
    val msd: Double,
    val mtcHours: Double,
    val mtcTimeFormatted: String,
    val solNumber: Int,
    val solarLongitudeDeg: Double,
    val seasonName: String,
    val earthDistanceMillionKm: Double,
    val radioDelayMinutes: Double,
    val isDustStormSeason: Boolean,
)

object MarsAstronomyMath {
    fun calculateMarsSolarDate(epochMillis: Long): MarsSolarDateInfo {
        val jdUt = 2440587.5 + (epochMillis / 86400000.0)
        val jdTt = jdUt + (69.184 / 86400.0)
        val msd = (jdTt - 2405522.59270) / 1.02749125170 + 44796.0 - 0.00096
        val solNumber = msd.toInt()

        val mtcHours = (msd % 1.0) * 24.0
        val hours = mtcHours.toInt()
        val minutes = ((mtcHours - hours) * 60.0).toInt()
        val mtcFormatted = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')} MTC"

        val d = (epochMillis - 946728000000L) / 86400000.0
        val mRad = ((19.373 + 0.5240207 * d) % 360.0) * PI / 180.0
        val lsDeg = ((270.389 + 0.5240384 * d + 10.691 * sin(mRad) + 0.623 * sin(2.0 * mRad)) % 360.0 + 360.0) % 360.0

        val season =
            when (lsDeg) {
                in 0.0..90.0 -> "Northern Spring / Southern Autumn"
                in 90.0..180.0 -> "Northern Summer / Southern Winter"
                in 180.0..270.0 -> "Northern Autumn / Southern Spring"
                else -> "Northern Winter / Southern Summer"
            }

        val isDustStormSeason = lsDeg in 180.0..330.0

        val distAu = 1.524 + 0.142 * cos(mRad)
        val distMillionKm = distAu * 149.5978707
        val delayMin = (distMillionKm * 1_000_000.0) / 299792.458 / 60.0

        return MarsSolarDateInfo(
            msd = msd,
            mtcHours = mtcHours,
            mtcTimeFormatted = mtcFormatted,
            solNumber = solNumber,
            solarLongitudeDeg = lsDeg,
            seasonName = season,
            earthDistanceMillionKm = distMillionKm,
            radioDelayMinutes = delayMin,
            isDustStormSeason = isDustStormSeason,
        )
    }
}
