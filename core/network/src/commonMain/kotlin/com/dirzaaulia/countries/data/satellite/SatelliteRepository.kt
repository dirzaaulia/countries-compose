package com.dirzaaulia.countries.data.satellite

import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.globe.toDegrees
import com.dirzaaulia.countries.domain.globe.toRadians
import com.dirzaaulia.countries.domain.satellite.PassOverheadPrediction
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.domain.satellite.SatelliteType
import com.dirzaaulia.countries.platform.currentEpochMillis
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class SatelliteRepository(
    private val apiClient: SatelliteApiClient,
) {
    private var cachedFleet: List<SatelliteTelemetry> = emptyList()
    private val mutex = Mutex()

    suspend fun fetchFleetTelemetry(simulatedEpochMillis: Long? = null): List<SatelliteTelemetry> {
        val now = simulatedEpochMillis ?: currentEpochMillis()
        val isSimulated = simulatedEpochMillis != null
        val fleet = mutableListOf<SatelliteTelemetry>()

        // 1. ISS (NORAD 25544) — International Space Station
        fleet.add(fetchOrPropagate(25544, "ISS", "ISS (Zarya)", "ISS", SatelliteType.SPACE_STATION, 418.0, 27600.0, 92.9, 51.6, 422.0, 414.0, "NASA / Roscosmos / ESA / JAXA", 1998, now, isSimulated))

        // 2. Tiangong / CSS (NORAD 48274) — Chinese Space Station
        fleet.add(fetchOrPropagate(48274, "CSS", "Tiangong Space Station", "CSS", SatelliteType.SPACE_STATION, 385.0, 27700.0, 92.2, 41.5, 390.0, 380.0, "CMSA (China)", 2021, now, isSimulated))

        // 3. Hubble / HST (NORAD 20580) — NASA Great Observatory
        fleet.add(fetchOrPropagate(20580, "HST", "Hubble Space Telescope", "HST", SatelliteType.SPACE_TELESCOPE, 535.0, 27300.0, 95.4, 28.5, 540.0, 530.0, "NASA / ESA", 1990, now, isSimulated))

        // 4. JWST (NORAD 50463) — James Webb Space Telescope (L2 Lagrange Point)
        fleet.add(calculateJwstTelemetry(now))

        // 5. TERRA (NORAD 25994) — NASA Earth Observing System Flagship
        fleet.add(fetchOrPropagate(25994, "TERRA", "EOS Terra Satellite", "TERRA", SatelliteType.EARTH_OBSERVATION, 705.0, 26600.0, 98.8, 98.2, 708.0, 703.0, "NASA Goddard", 1999, now, isSimulated))

        // 6. AQUA (NORAD 27424) — NASA Earth Observing System Water Cycle
        fleet.add(fetchOrPropagate(27424, "AQUA", "EOS Aqua Satellite", "AQUA", SatelliteType.EARTH_OBSERVATION, 705.0, 26600.0, 98.8, 98.2, 707.0, 702.0, "NASA Goddard", 2002, now, isSimulated))

        // 7. AURA (NORAD 28485) — NASA Earth Observing System Atmospheric Chemistry
        fleet.add(fetchOrPropagate(28485, "AURA", "EOS Aura Satellite", "AURA", SatelliteType.EARTH_OBSERVATION, 705.0, 26600.0, 98.8, 98.2, 709.0, 704.0, "NASA Goddard", 2004, now, isSimulated))

        // 8. LANDSAT-8 (NORAD 36585) — NASA / USGS Land Remote Sensing
        fleet.add(fetchOrPropagate(36585, "LDST8", "Landsat 8 Imaging Satellite", "LDST8", SatelliteType.EARTH_OBSERVATION, 705.0, 26600.0, 98.8, 98.2, 706.0, 703.0, "NASA / USGS", 2013, now, isSimulated))

        // 9. LANDSAT-9 (NORAD 49260) — NASA / USGS Advanced Land Imager
        fleet.add(fetchOrPropagate(49260, "LDST9", "Landsat 9 Imaging Satellite", "LDST9", SatelliteType.EARTH_OBSERVATION, 705.0, 26600.0, 98.8, 98.2, 707.0, 704.0, "NASA / USGS", 2021, now, isSimulated))

        // 10. ICESAT-2 (NORAD 43689) — NASA Ice, Cloud, & Land Elevation LiDAR
        fleet.add(fetchOrPropagate(43689, "ICEST2", "ICESat-2 Topography Satellite", "ICEST2", SatelliteType.EARTH_OBSERVATION, 496.0, 27200.0, 94.6, 92.0, 500.0, 490.0, "NASA Goddard", 2018, now, isSimulated))

        // 11. OCO-2 (NORAD 40059) — NASA Orbiting Carbon Observatory-2
        fleet.add(fetchOrPropagate(40059, "OCO2", "Orbiting Carbon Observatory-2", "OCO2", SatelliteType.EARTH_OBSERVATION, 705.0, 26600.0, 98.8, 98.2, 707.0, 703.0, "NASA JPL", 2014, now, isSimulated))

        // 12. TESS (NORAD 43226) — NASA Transiting Exoplanet Survey Satellite
        fleet.add(fetchOrPropagate(43226, "TESS", "TESS Exoplanet Telescope", "TESS", SatelliteType.SPACE_TELESCOPE, 108000.0, 11000.0, 19700.0, 37.0, 375000.0, 108000.0, "NASA MIT", 2018, now, isSimulated))

        // 13. SMAP (NORAD 40376) — NASA Soil Moisture Active Passive Satellite
        fleet.add(fetchOrPropagate(40376, "SMAP", "Soil Moisture Active Passive", "SMAP", SatelliteType.EARTH_OBSERVATION, 685.0, 26700.0, 98.5, 98.1, 688.0, 682.0, "NASA JPL", 2015, now, isSimulated))

        mutex.withLock { cachedFleet = fleet }
        return fleet
    }

    private suspend fun fetchOrPropagate(
        noradId: Int,
        id: String,
        name: String,
        acronym: String,
        type: SatelliteType,
        defaultAlt: Double,
        defaultVel: Double,
        periodMin: Double,
        inclinationDeg: Double,
        apogeeKm: Double,
        perigeeKm: Double,
        operator: String,
        launchYear: Int,
        now: Long,
        isSimulated: Boolean = false,
    ): SatelliteTelemetry {
        val raw = if (isSimulated) null else apiClient.fetchSatelliteRaw(noradId)
        val lat: Double
        val lng: Double
        val alt: Double
        val vel: Double
        val isDaylight: Boolean

        if (raw?.latitude != null && raw.longitude != null) {
            lat = raw.latitude
            lng = raw.longitude
            alt = raw.altitude ?: defaultAlt
            vel = raw.velocity ?: defaultVel
            isDaylight = raw.visibility?.lowercase() == "daylight"
        } else {
            // Offline Keplerian Propagator
            val epochMin = (now / 60000.0) % periodMin
            val theta = (epochMin / periodMin) * 2.0 * kotlin.math.PI
            val incRad = inclinationDeg.toRadians
            lat = asin(sin(incRad) * sin(theta)).toDegrees
            var calcLng = (atan2(cos(incRad) * sin(theta), cos(theta)).toDegrees - (now / 60000.0 * 3.88)) % 360.0
            if (calcLng > 180.0) calcLng -= 360.0
            if (calcLng < -180.0) calcLng += 360.0
            lng = calcLng
            alt = defaultAlt
            vel = defaultVel
            isDaylight = true
        }

        val track = generateGroundTrack(lat, lng, periodMin, inclinationDeg)

        return SatelliteTelemetry(
            id = id,
            noradId = noradId,
            name = name,
            acronym = acronym,
            type = type,
            lat = lat,
            lng = lng,
            altitudeKm = alt,
            velocityKmH = vel,
            isDaylight = isDaylight,
            groundTrack = track,
            orbitalPeriodMin = periodMin,
            apogeeKm = apogeeKm,
            perigeeKm = perigeeKm,
            operator = operator,
            launchYear = launchYear,
        )
    }

    private fun calculateJwstTelemetry(now: Long): SatelliteTelemetry {
        val sun = AstronomyMath.calculateSunPosition(now)
        // Anti-solar vector at Earth-Sun L2 point
        val lat = -sun.lat
        var lng = sun.lng + 180.0
        if (lng > 180.0) lng -= 360.0

        val track = generateGroundTrack(lat, lng, 262800.0, 5.0)

        return SatelliteTelemetry(
            id = "JWST",
            noradId = 50463,
            name = "James Webb Space Telescope",
            acronym = "JWST",
            type = SatelliteType.DEEP_SPACE_OBSERVATORY,
            lat = lat,
            lng = lng,
            altitudeKm = 1500000.0,
            velocityKmH = 720.0,
            isDaylight = true,
            groundTrack = track,
            orbitalPeriodMin = 262800.0,
            apogeeKm = 1520000.0,
            perigeeKm = 1480000.0,
            operator = "NASA / ESA / CSA",
            launchYear = 2021,
        )
    }

    private fun generateGroundTrack(
        startLat: Double,
        startLng: Double,
        periodMin: Double,
        inclinationDeg: Double,
    ): List<LatLng> {
        val track = mutableListOf<LatLng>()
        val incRad = inclinationDeg.toRadians
        val startPhase = asin((sin(startLat.toRadians) / sin(incRad)).coerceIn(-1.0, 1.0))

        for (k in -36..36) {
            val deltaMin = (k.toDouble() / 36.0) * (periodMin / 2.0)
            val theta = startPhase + (deltaMin / periodMin) * 2.0 * kotlin.math.PI
            val lat = asin((sin(incRad) * sin(theta)).coerceIn(-1.0, 1.0)).toDegrees
            var lng = (startLng + (deltaMin / periodMin * 360.0) * cos(incRad)) % 360.0
            if (lng > 180.0) lng -= 360.0
            if (lng < -180.0) lng += 360.0
            track.add(LatLng(lat, lng))
        }
        return track
    }

    fun calculateNextPass(
        satellite: SatelliteTelemetry,
        target: LatLng,
    ): PassOverheadPrediction? {
        if (satellite.type == SatelliteType.DEEP_SPACE_OBSERVATORY) {
            return PassOverheadPrediction(0, "Deep Space L2", 90)
        }

        var minDistanceKm = Double.MAX_VALUE
        var bestMinutes = 15

        // Search over 24 orbits (~1440 mins)
        for (m in 15..1440 step 15) {
            val deltaMin = m.toDouble()
            val theta = (deltaMin / satellite.orbitalPeriodMin) * 2.0 * kotlin.math.PI
            val incRad = 51.6.toRadians
            val lat = asin((sin(incRad) * sin(theta)).coerceIn(-1.0, 1.0)).toDegrees
            var lng = (satellite.lng + atan2(cos(incRad) * sin(theta), cos(theta)).toDegrees - (deltaMin * 3.88)) % 360.0
            if (lng > 180.0) lng -= 360.0
            if (lng < -180.0) lng += 360.0

            val dist = AstronomyMath.calculateGreatCircleDistance(target, LatLng(lat, lng))
            if (dist < minDistanceKm) {
                minDistanceKm = dist
                bestMinutes = m
            }
        }

        val hours = bestMinutes / 60
        val mins = bestMinutes % 60
        val timeFormatted = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
        val maxElevation = (90.0 - (minDistanceKm / 30.0)).coerceIn(10.0, 88.0).roundToInt()

        return PassOverheadPrediction(
            minutesUntilPass = bestMinutes,
            passTimeFormatted = timeFormatted,
            maxElevationDeg = maxElevation,
        )
    }
}
