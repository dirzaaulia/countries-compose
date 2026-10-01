package com.dirzaaulia.countries.domain.solarsystem

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object SolarKeplerianMath {
    private const val KM_PER_AU = 149597870.7

    private val J2000_ELEMENTS: Map<PlanetId, KeplerianOrbitElements> =
        mapOf(
            PlanetId.MERCURY to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 0.38709927,
                    eccentricity = 0.20563593,
                    inclinationDeg = 7.00497902,
                    meanLongitudeDeg = 252.25032350,
                    longitudeOfPerihelionDeg = 77.45779628,
                    longitudeOfAscendingNodeDeg = 48.33076593,
                    rateSemiMajorAxisAu = 0.00000037,
                    rateEccentricity = 0.00001906,
                    rateInclinationDeg = -0.00594749,
                    rateMeanLongitudeDeg = 149472.67411175,
                    rateLongitudeOfPerihelionDeg = 0.16047689,
                    rateLongitudeOfAscendingNodeDeg = -0.12534081,
                ),
            PlanetId.VENUS to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 0.72333566,
                    eccentricity = 0.00677672,
                    inclinationDeg = 3.39467605,
                    meanLongitudeDeg = 181.97909950,
                    longitudeOfPerihelionDeg = 131.60246718,
                    longitudeOfAscendingNodeDeg = 76.67984255,
                    rateSemiMajorAxisAu = 0.00000026,
                    rateEccentricity = -0.00004107,
                    rateInclinationDeg = -0.00078890,
                    rateMeanLongitudeDeg = 58517.81538729,
                    rateLongitudeOfPerihelionDeg = 0.00268329,
                    rateLongitudeOfAscendingNodeDeg = -0.27769418,
                ),
            PlanetId.EARTH to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 1.00000261,
                    eccentricity = 0.01671123,
                    inclinationDeg = 0.00001531,
                    meanLongitudeDeg = 100.46457166,
                    longitudeOfPerihelionDeg = 102.93768193,
                    longitudeOfAscendingNodeDeg = 0.0,
                    rateSemiMajorAxisAu = 0.00000562,
                    rateEccentricity = -0.00004392,
                    rateInclinationDeg = -0.01294668,
                    rateMeanLongitudeDeg = 35999.37244981,
                    rateLongitudeOfPerihelionDeg = 0.32327364,
                    rateLongitudeOfAscendingNodeDeg = 0.0,
                ),
            PlanetId.MARS to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 1.52371034,
                    eccentricity = 0.09339410,
                    inclinationDeg = 1.84969142,
                    meanLongitudeDeg = -4.55343205,
                    longitudeOfPerihelionDeg = -23.94362959,
                    longitudeOfAscendingNodeDeg = 49.55953891,
                    rateSemiMajorAxisAu = 0.00001847,
                    rateEccentricity = 0.00007882,
                    rateInclinationDeg = -0.00813131,
                    rateMeanLongitudeDeg = 19140.30268499,
                    rateLongitudeOfPerihelionDeg = 0.44441088,
                    rateLongitudeOfAscendingNodeDeg = -0.29257343,
                ),
            PlanetId.JUPITER to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 5.20288700,
                    eccentricity = 0.04838624,
                    inclinationDeg = 1.30439695,
                    meanLongitudeDeg = 34.39644051,
                    longitudeOfPerihelionDeg = 14.72847983,
                    longitudeOfAscendingNodeDeg = 100.47390909,
                    rateSemiMajorAxisAu = -0.00011607,
                    rateEccentricity = -0.00013257,
                    rateInclinationDeg = -0.00183714,
                    rateMeanLongitudeDeg = 3034.74612775,
                    rateLongitudeOfPerihelionDeg = 0.21252668,
                    rateLongitudeOfAscendingNodeDeg = 0.20469106,
                ),
            PlanetId.SATURN to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 9.53667594,
                    eccentricity = 0.05386179,
                    inclinationDeg = 2.48599187,
                    meanLongitudeDeg = 49.95424423,
                    longitudeOfPerihelionDeg = 92.59887831,
                    longitudeOfAscendingNodeDeg = 113.66242448,
                    rateSemiMajorAxisAu = -0.00125060,
                    rateEccentricity = -0.00050991,
                    rateInclinationDeg = 0.00193609,
                    rateMeanLongitudeDeg = 1222.49362201,
                    rateLongitudeOfPerihelionDeg = -0.41897216,
                    rateLongitudeOfAscendingNodeDeg = -0.28867794,
                ),
            PlanetId.URANUS to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 19.18916464,
                    eccentricity = 0.04725744,
                    inclinationDeg = 0.77263783,
                    meanLongitudeDeg = 313.23218806,
                    longitudeOfPerihelionDeg = 170.96424206,
                    longitudeOfAscendingNodeDeg = 74.01692503,
                    rateSemiMajorAxisAu = -0.00196176,
                    rateEccentricity = -0.00004397,
                    rateInclinationDeg = -0.00242939,
                    rateMeanLongitudeDeg = 428.48202785,
                    rateLongitudeOfPerihelionDeg = 0.40805281,
                    rateLongitudeOfAscendingNodeDeg = 0.04240589,
                ),
            PlanetId.NEPTUNE to
                KeplerianOrbitElements(
                    semiMajorAxisAu = 30.06992276,
                    eccentricity = 0.00859048,
                    inclinationDeg = 1.77004347,
                    meanLongitudeDeg = -55.12002969,
                    longitudeOfPerihelionDeg = 44.96476227,
                    longitudeOfAscendingNodeDeg = 131.78422574,
                    rateSemiMajorAxisAu = 0.00026291,
                    rateEccentricity = 0.00005105,
                    rateInclinationDeg = 0.00035372,
                    rateMeanLongitudeDeg = 218.45945325,
                    rateLongitudeOfPerihelionDeg = -0.32241464,
                    rateLongitudeOfAscendingNodeDeg = -0.00508664,
                ),
        )

    fun calculateLivePlanetPositions(epochMillis: Long): List<LivePlanetPosition> {
        val julianDate = 2440587.5 + (epochMillis / 86400000.0)
        val centuriesSinceJ2000 = (julianDate - 2451545.0) / 36525.0

        val earthElements = J2000_ELEMENTS[PlanetId.EARTH]!!
        val earthPos = computeLivePosition(PlanetaryCatalog.EARTH, earthElements, centuriesSinceJ2000)

        return PlanetaryCatalog.ALL_PLANETS.map { planet ->
            if (planet.id == PlanetId.MOON) {
                computeMoonLivePosition(planet, earthPos, centuriesSinceJ2000)
            } else {
                val elements = J2000_ELEMENTS[planet.id] ?: J2000_ELEMENTS[PlanetId.EARTH]!!
                computeLivePosition(planet, elements, centuriesSinceJ2000)
            }
        }
    }

    private fun computeMoonLivePosition(
        moon: PlanetOrbitalData,
        earthPos: LivePlanetPosition,
        t: Double,
    ): LivePlanetPosition {
        val daysSinceJ2000 = t * 36525.0
        val moonAngleRad = (daysSinceJ2000 * (2.0 * PI / 27.32166)) % (2.0 * PI)
        val visSeparationAu = 0.12
        val dx = cos(moonAngleRad) * visSeparationAu
        val dy = sin(moonAngleRad) * visSeparationAu
        val dz = sin(moonAngleRad * 0.5) * (visSeparationAu * 0.09)

        return LivePlanetPosition(
            planet = moon,
            xAu = earthPos.xAu + dx,
            yAu = earthPos.yAu + dy,
            zAu = earthPos.zAu + dz,
            distanceAu = 0.00257,
            distanceKm = 384400.0,
            trueAnomalyDeg = (moonAngleRad * 180.0 / PI) % 360.0,
            eclipticLongitudeDeg = (earthPos.eclipticLongitudeDeg + moonAngleRad * 180.0 / PI) % 360.0,
        )
    }

    private fun computeLivePosition(
        planet: PlanetOrbitalData,
        elements: KeplerianOrbitElements,
        t: Double,
    ): LivePlanetPosition {
        val a = elements.semiMajorAxisAu + elements.rateSemiMajorAxisAu * t
        val e = elements.eccentricity + elements.rateEccentricity * t
        val inc = (elements.inclinationDeg + elements.rateInclinationDeg * t) * (PI / 180.0)
        val l = (elements.meanLongitudeDeg + elements.rateMeanLongitudeDeg * t) % 360.0
        val p = (elements.longitudeOfPerihelionDeg + elements.rateLongitudeOfPerihelionDeg * t) % 360.0
        val node = (elements.longitudeOfAscendingNodeDeg + elements.rateLongitudeOfAscendingNodeDeg * t) % 360.0

        val nodeRad = node * (PI / 180.0)
        val omegaRad = (p - node) * (PI / 180.0)

        var mDeg = (l - p) % 360.0
        if (mDeg < 0) mDeg += 360.0
        val mRad = mDeg * (PI / 180.0)

        var eccentricAnomaly = mRad
        for (i in 0..4) {
            val delta = (eccentricAnomaly - e * sin(eccentricAnomaly) - mRad) / (1.0 - e * cos(eccentricAnomaly))
            eccentricAnomaly -= delta
        }

        val xOrb = a * (cos(eccentricAnomaly) - e)
        val yOrb = a * sqrt(1.0 - e * e) * sin(eccentricAnomaly)
        val r = sqrt(xOrb * xOrb + yOrb * yOrb)

        val cosO = cos(omegaRad)
        val sinO = sin(omegaRad)
        val cosN = cos(nodeRad)
        val sinN = sin(nodeRad)
        val cosI = cos(inc)
        val sinI = sin(inc)

        val xEcl = xOrb * (cosO * cosN - sinO * sinN * cosI) - yOrb * (sinO * cosN + cosO * sinN * cosI)
        val yEcl = xOrb * (cosO * sinN + sinO * cosN * cosI) - yOrb * (sinO * sinN - cosO * cosN * cosI)
        val zEcl = xOrb * (sinO * sinI) + yOrb * (cosO * sinI)

        val trueAnomaly = atan2(yOrb, xOrb) * (180.0 / PI)
        val eclipticLon = (atan2(yEcl, xEcl) * (180.0 / PI) + 360.0) % 360.0

        return LivePlanetPosition(
            planet = planet,
            xAu = xEcl,
            yAu = yEcl,
            zAu = zEcl,
            distanceAu = r,
            distanceKm = r * KM_PER_AU,
            trueAnomalyDeg = trueAnomaly,
            eclipticLongitudeDeg = eclipticLon,
        )
    }

    fun computeOrbitPoints(
        planetId: PlanetId,
        steps: Int = 64,
    ): List<Pair<Double, Double>> {
        if (planetId == PlanetId.MOON) {
            val visSeparationAu = 0.12
            return (0..steps).map { i ->
                val angle = (i.toDouble() / steps) * 2.0 * PI
                (cos(angle) * visSeparationAu) to (sin(angle) * visSeparationAu)
            }
        }
        val elements = J2000_ELEMENTS[planetId] ?: return emptyList()
        val a = elements.semiMajorAxisAu
        val e = elements.eccentricity
        val pRad = elements.longitudeOfPerihelionDeg * (PI / 180.0)
        val b = a * sqrt(1.0 - e * e)
        val c = a * e

        return (0..steps).map { i ->
            val angle = (i.toDouble() / steps) * 2.0 * PI
            val x0 = a * cos(angle) - c
            val y0 = b * sin(angle)
            val rotX = x0 * cos(pRad) - y0 * sin(pRad)
            val rotY = x0 * sin(pRad) + y0 * cos(pRad)
            rotX to rotY
        }
    }
}
