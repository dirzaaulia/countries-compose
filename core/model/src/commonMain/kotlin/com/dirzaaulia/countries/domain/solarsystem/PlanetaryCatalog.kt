package com.dirzaaulia.countries.domain.solarsystem

object PlanetaryCatalog {
    val SUN =
        PlanetOrbitalData(
            id = PlanetId.SUN,
            name = "Sun",
            symbolTag = "[SUN]",
            colorHex = 0xFFFFD700,
            diameterKm = 1392700.0,
            meanDistanceAu = 0.0,
            orbitalPeriodDays = 0.0,
            orbitalSpeedKms = 0.0,
            axialTiltDeg = 7.25,
            description = "Yellow dwarf star at the center of the Solar System.",
        )

    val MERCURY =
        PlanetOrbitalData(
            id = PlanetId.MERCURY,
            name = "Mercury",
            symbolTag = "[MERCURY]",
            colorHex = 0xFFA0A0A0,
            diameterKm = 4879.0,
            meanDistanceAu = 0.387,
            orbitalPeriodDays = 87.97,
            orbitalSpeedKms = 47.36,
            axialTiltDeg = 0.03,
            description = "Smallest planet, heavily cratered with extreme temperature fluctuations.",
        )

    val VENUS =
        PlanetOrbitalData(
            id = PlanetId.VENUS,
            name = "Venus",
            symbolTag = "[VENUS]",
            colorHex = 0xFFE3BB76,
            diameterKm = 12104.0,
            meanDistanceAu = 0.723,
            orbitalPeriodDays = 224.70,
            orbitalSpeedKms = 35.02,
            axialTiltDeg = 177.36,
            description = "Hottest planet with a dense toxic atmosphere and runaway greenhouse effect.",
        )

    val EARTH =
        PlanetOrbitalData(
            id = PlanetId.EARTH,
            name = "Earth",
            symbolTag = "[EARTH]",
            colorHex = 0xFF3892FF,
            diameterKm = 12756.0,
            meanDistanceAu = 1.000,
            orbitalPeriodDays = 365.25,
            orbitalSpeedKms = 29.78,
            axialTiltDeg = 23.44,
            description = "Our home planet, the only known world harboring abundant liquid water and life.",
        )

    val MOON =
        PlanetOrbitalData(
            id = PlanetId.MOON,
            name = "Moon",
            symbolTag = "[MOON]",
            colorHex = 0xFFE2E8F0,
            diameterKm = 3474.8,
            meanDistanceAu = 0.00257,
            orbitalPeriodDays = 27.32,
            orbitalSpeedKms = 1.022,
            axialTiltDeg = 1.54,
            description = "Earth's sole natural satellite, tidally locked with a cratered basaltic surface.",
        )

    val MARS =
        PlanetOrbitalData(
            id = PlanetId.MARS,
            name = "Mars",
            symbolTag = "[MARS]",
            colorHex = 0xFFE67E22,
            diameterKm = 6792.0,
            meanDistanceAu = 1.524,
            orbitalPeriodDays = 686.98,
            orbitalSpeedKms = 24.07,
            axialTiltDeg = 25.19,
            description = "The Red Planet, featuring Olympus Mons, Valles Marineris, and ancient dry riverbeds.",
        )

    val JUPITER =
        PlanetOrbitalData(
            id = PlanetId.JUPITER,
            name = "Jupiter",
            symbolTag = "[JUPITER]",
            colorHex = 0xFFD4A373,
            diameterKm = 142984.0,
            meanDistanceAu = 5.204,
            orbitalPeriodDays = 4332.59,
            orbitalSpeedKms = 13.07,
            axialTiltDeg = 3.13,
            description = "Largest gas giant in the Solar System, featuring the Great Red Spot and dozens of moons.",
        )

    val SATURN =
        PlanetOrbitalData(
            id = PlanetId.SATURN,
            name = "Saturn",
            symbolTag = "[SATURN]",
            colorHex = 0xFFE0C068,
            diameterKm = 120536.0,
            meanDistanceAu = 9.582,
            orbitalPeriodDays = 10759.22,
            orbitalSpeedKms = 9.68,
            axialTiltDeg = 26.73,
            description = "Ringed gas giant with an extensive system of ice particles, Titan, and Enceladus.",
        )

    val URANUS =
        PlanetOrbitalData(
            id = PlanetId.URANUS,
            name = "Uranus",
            symbolTag = "[URANUS]",
            colorHex = 0xFF72DDF7,
            diameterKm = 51118.0,
            meanDistanceAu = 19.201,
            orbitalPeriodDays = 30685.40,
            orbitalSpeedKms = 6.80,
            axialTiltDeg = 97.77,
            description = "An ice giant tilted nearly on its side, with a faint ring system and methane atmosphere.",
        )

    val NEPTUNE =
        PlanetOrbitalData(
            id = PlanetId.NEPTUNE,
            name = "Neptune",
            symbolTag = "[NEPTUNE]",
            colorHex = 0xFF4361EE,
            diameterKm = 49528.0,
            meanDistanceAu = 30.047,
            orbitalPeriodDays = 60189.00,
            orbitalSpeedKms = 5.43,
            axialTiltDeg = 28.32,
            description = "Distant deep-blue ice giant known for supersonic winds and Triton's retrograde orbit.",
        )

    val ALL_PLANETS =
        listOf(
            MERCURY,
            VENUS,
            EARTH,
            MOON,
            MARS,
            JUPITER,
            SATURN,
            URANUS,
            NEPTUNE,
        )
}
