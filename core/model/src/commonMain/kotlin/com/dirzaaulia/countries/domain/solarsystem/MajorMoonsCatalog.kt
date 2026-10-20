package com.dirzaaulia.countries.domain.solarsystem

object MajorMoonsCatalog {
    val JUPITER_MOONS =
        listOf(
            MajorMoon(
                id = "io",
                planetId = PlanetId.JUPITER,
                name = "Io",
                description = "The most volcanically active body in the Solar System.",
                colorHex = 0xFFFDE047,
                radiusKm = 1821.6,
                massKg = "8.93 x 10^22",
                orbitalDistanceKm = 421700.0,
                orbitalPeriodDays = 1.77,
                surfaceComposition = "Silicate rock and sulfur.",
                tidalHeatingIndex = "Extreme",
                habitabilityScore = 1,
                astrobiologyNotes = "Constant volcanic resurfacing and intense radiation make life highly unlikely.",
            ),
            MajorMoon(
                id = "europa",
                planetId = PlanetId.JUPITER,
                name = "Europa",
                description = "An ice-crusted moon harboring a vast subsurface water ocean.",
                colorHex = 0xFFE2E8F0,
                radiusKm = 1560.8,
                massKg = "4.80 x 10^22",
                orbitalDistanceKm = 670900.0,
                orbitalPeriodDays = 3.55,
                surfaceComposition = "Water ice.",
                tidalHeatingIndex = "High",
                habitabilityScore = 5,
                astrobiologyNotes = "Subsurface liquid ocean in contact with rocky mantle provides prime astrobiology potential.",
            ),
            MajorMoon(
                id = "ganymede",
                planetId = PlanetId.JUPITER,
                name = "Ganymede",
                description = "The largest moon in the Solar System, possessing its own magnetic field.",
                colorHex = 0xFF94A3B8,
                radiusKm = 2634.1,
                massKg = "1.48 x 10^23",
                orbitalDistanceKm = 1070400.0,
                orbitalPeriodDays = 7.15,
                surfaceComposition = "Silicate rock and water ice.",
                tidalHeatingIndex = "Moderate",
                habitabilityScore = 3,
                astrobiologyNotes = "Contains a deep subsurface ocean, but sandwiched between layers of ice, limiting chemical exchange.",
            ),
            MajorMoon(
                id = "callisto",
                planetId = PlanetId.JUPITER,
                name = "Callisto",
                description = "A heavily cratered, ancient ice ball with little to no geological activity.",
                colorHex = 0xFF475569,
                radiusKm = 2410.3,
                massKg = "1.08 x 10^23",
                orbitalDistanceKm = 1882700.0,
                orbitalPeriodDays = 16.69,
                surfaceComposition = "Equal parts rock and ice.",
                tidalHeatingIndex = "Low",
                habitabilityScore = 2,
                astrobiologyNotes = "May have a deep ocean, but lack of tidal heating means limited energy sources for life.",
            ),
        )

    val SATURN_MOONS =
        listOf(
            MajorMoon(
                id = "titan",
                planetId = PlanetId.SATURN,
                name = "Titan",
                description = "The only moon with a dense atmosphere and stable surface lakes of liquid methane.",
                colorHex = 0xFFF59E0B,
                radiusKm = 2574.7,
                massKg = "1.34 x 10^23",
                orbitalDistanceKm = 1221800.0,
                orbitalPeriodDays = 15.94,
                surfaceComposition = "Water ice and hydrocarbon organics.",
                tidalHeatingIndex = "Low",
                habitabilityScore = 4,
                astrobiologyNotes = "Rich in complex organics; potential for exotic methane-based life forms.",
            ),
            MajorMoon(
                id = "enceladus",
                planetId = PlanetId.SATURN,
                name = "Enceladus",
                description = "An icy moon ejecting plumes of water and organics from its south pole.",
                colorHex = 0xFFF8FAFC,
                radiusKm = 252.1,
                massKg = "1.08 x 10^20",
                orbitalDistanceKm = 237948.0,
                orbitalPeriodDays = 1.37,
                surfaceComposition = "Pure water ice.",
                tidalHeatingIndex = "High",
                habitabilityScore = 5,
                astrobiologyNotes = "Hydrothermal vents at the ocean floor provide heat and nutrients, making it a top target for life.",
            ),
            MajorMoon(
                id = "mimas",
                planetId = PlanetId.SATURN,
                name = "Mimas",
                description = "Known as the 'Death Star' moon due to the massive Herschel crater.",
                colorHex = 0xFFCBD5E1,
                radiusKm = 198.2,
                massKg = "3.75 x 10^19",
                orbitalDistanceKm = 185520.0,
                orbitalPeriodDays = 0.94,
                surfaceComposition = "Water ice.",
                tidalHeatingIndex = "Moderate",
                habitabilityScore = 2,
                astrobiologyNotes = "Recent orbital anomalies suggest a potential young subsurface ocean.",
            ),
        )

    val NEPTUNE_MOONS =
        listOf(
            MajorMoon(
                id = "triton",
                planetId = PlanetId.NEPTUNE,
                name = "Triton",
                description = "A captured Kuiper Belt object with a retrograde orbit and active nitrogen geysers.",
                colorHex = 0xFFBAE6FD,
                radiusKm = 1353.4,
                massKg = "2.14 x 10^22",
                orbitalDistanceKm = 354759.0,
                orbitalPeriodDays = 5.88,
                surfaceComposition = "Frozen nitrogen, water, and carbon dioxide.",
                tidalHeatingIndex = "Moderate",
                habitabilityScore = 2,
                astrobiologyNotes = "Tidal heating from capture may have maintained a subsurface ocean.",
            ),
        )

    fun getMoonsForPlanet(planetId: PlanetId): List<MajorMoon> =
        when (planetId) {
            PlanetId.JUPITER -> JUPITER_MOONS
            PlanetId.SATURN -> SATURN_MOONS
            PlanetId.NEPTUNE -> NEPTUNE_MOONS
            else -> emptyList()
        }
}
