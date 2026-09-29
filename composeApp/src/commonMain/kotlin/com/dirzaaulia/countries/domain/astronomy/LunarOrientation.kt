package com.dirzaaulia.countries.domain.astronomy

import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal data class LunarOrientation(
    val subsolarLatitude: Double,
    val librationLatitude: Double,
    val librationLongitude: Double,
)

internal fun calculateLunarOrientation(
    daysSinceJ2000: Double,
    lunarLongitude: Double,
    lunarLatitude: Double,
    argumentOfLatitude: Double,
    solarLongitude: Double,
): LunarOrientation {
    val node = 125.04452 - 0.0529538083 * daysSinceJ2000
    val inclination = 1.54242.toRadians
    val moonNodeAngle = (lunarLongitude - node).toRadians
    val latitude = lunarLatitude.toRadians
    val argument = argumentOfLatitude.toRadians
    val opticalLatitude =
        asin(
            (
                -sin(moonNodeAngle) * cos(latitude) * sin(inclination) -
                    sin(latitude) * cos(inclination)
            ).coerceIn(-1.0, 1.0),
        ).toDegrees
    val opticalLongitude =
        (
            atan2(
                sin(moonNodeAngle) * cos(latitude) * cos(inclination) - sin(latitude) * sin(inclination),
                cos(moonNodeAngle) * cos(latitude),
            ) - argument
        ).toDegrees.let { (it + 540.0) % 360.0 - 180.0 }
    val sunNodeAngle = (solarLongitude - node).toRadians
    val subsolarLatitude = asin(-sin(sunNodeAngle) * sin(inclination)).toDegrees
    // Positive eastward sub-Earth longitude needs negative model yaw to face the camera.
    return LunarOrientation(subsolarLatitude, opticalLatitude, -opticalLongitude)
}
