package com.dirzaaulia.countries.domain.solarsystem

object PlanetaryCatalog {
    val SUN =
        PlanetOrbitalData(
            id = PlanetId.SUN,
            name = "Sun",
            symbolTag = "[SUN]",
            colorHex = 0xFFFBBF24,
            diameterKm = 1392700.0,
            meanDistanceAu = 0.0,
            orbitalPeriodDays = 0.0,
            orbitalSpeedKms = 0.0,
            axialTiltDeg = 7.25,
            description = "Yellow dwarf G-type main-sequence star containing 99.86% of the Solar System's total mass.",
        )

    val MERCURY =
        PlanetOrbitalData(
            id = PlanetId.MERCURY,
            name = "Mercury",
            symbolTag = "[MERCURY]",
            colorHex = 0xFF94A3B8,
            diameterKm = 4879.0,
            meanDistanceAu = 0.387,
            orbitalPeriodDays = 87.97,
            orbitalSpeedKms = 47.36,
            axialTiltDeg = 0.034,
            description = "Smallest, most cratered terrestrial planet with an eccentric orbit and extreme temperature swings.",
        )

    val VENUS =
        PlanetOrbitalData(
            id = PlanetId.VENUS,
            name = "Venus",
            symbolTag = "[VENUS]",
            colorHex = 0xFFF59E0B,
            diameterKm = 12104.0,
            meanDistanceAu = 0.723,
            orbitalPeriodDays = 224.70,
            orbitalSpeedKms = 35.02,
            axialTiltDeg = 177.36,
            description = "Hottest planet, blanketed by a dense carbon dioxide greenhouse atmosphere and sulfuric acid clouds.",
        )

    val EARTH =
        PlanetOrbitalData(
            id = PlanetId.EARTH,
            name = "Earth",
            symbolTag = "[EARTH]",
            colorHex = 0xFF38BDF8,
            diameterKm = 12742.0,
            meanDistanceAu = 1.000,
            orbitalPeriodDays = 365.25,
            orbitalSpeedKms = 29.78,
            axialTiltDeg = 23.44,
            description = "Our home world, the only known celestial body harboring life, liquid oceans, and an active magnetosphere.",
        )

    val MOON =
        PlanetOrbitalData(
            id = PlanetId.MOON,
            name = "Moon",
            symbolTag = "[MOON]",
            colorHex = 0xFFFFD54F,
            diameterKm = 3474.8,
            meanDistanceAu = 0.00257,
            orbitalPeriodDays = 27.32,
            orbitalSpeedKms = 1.022,
            axialTiltDeg = 1.54,
            description = "Earth's only natural satellite, tidally locked with highlands, dark maria, and impact craters.",
        )

    val MARS =
        PlanetOrbitalData(
            id = PlanetId.MARS,
            name = "Mars",
            symbolTag = "[MARS]",
            colorHex = 0xFFF43F5E,
            diameterKm = 6779.0,
            meanDistanceAu = 1.524,
            orbitalPeriodDays = 686.98,
            orbitalSpeedKms = 24.07,
            axialTiltDeg = 25.19,
            description = "The Red Planet, home to Olympus Mons, Valles Marineris canyon, and past active aqueous systems.",
        )

    val JUPITER =
        PlanetOrbitalData(
            id = PlanetId.JUPITER,
            name = "Jupiter",
            symbolTag = "[JUPITER]",
            colorHex = 0xFFE2E8F0,
            diameterKm = 139820.0,
            meanDistanceAu = 5.204,
            orbitalPeriodDays = 4332.59,
            orbitalSpeedKms = 13.07,
            axialTiltDeg = 3.13,
            description = "Massive gas giant with alternating atmospheric jet streams, the Great Red Spot, and 95 known moons.",
        )

    val SATURN =
        PlanetOrbitalData(
            id = PlanetId.SATURN,
            name = "Saturn",
            symbolTag = "[SATURN]",
            colorHex = 0xFFFDE047,
            diameterKm = 116460.0,
            meanDistanceAu = 9.582,
            orbitalPeriodDays = 10759.22,
            orbitalSpeedKms = 9.68,
            axialTiltDeg = 26.73,
            description = "Gas giant renowned for its spectacular, highly reflective icy ring system spanning 282,000 km.",
        )

    val URANUS =
        PlanetOrbitalData(
            id = PlanetId.URANUS,
            name = "Uranus",
            symbolTag = "[URANUS]",
            colorHex = 0xFF2DD4BF,
            diameterKm = 50724.0,
            meanDistanceAu = 19.201,
            orbitalPeriodDays = 30685.4,
            orbitalSpeedKms = 6.80,
            axialTiltDeg = 97.77,
            description = "Ice giant with a cyan methane haze and an extreme 97.8° sideways axial tilt.",
        )

    val NEPTUNE =
        PlanetOrbitalData(
            id = PlanetId.NEPTUNE,
            name = "Neptune",
            symbolTag = "[NEPTUNE]",
            colorHex = 0xFF818CF8,
            diameterKm = 49244.0,
            meanDistanceAu = 30.047,
            orbitalPeriodDays = 60189.0,
            orbitalSpeedKms = 5.43,
            axialTiltDeg = 28.32,
            description = "Dynamic azure ice giant with high-velocity supersonic winds, methane cirrus clouds, and Great Dark Spot.",
        )

    val ALL_PLANETS = listOf(MERCURY, VENUS, EARTH, MOON, MARS, JUPITER, SATURN, URANUS, NEPTUNE)
}
