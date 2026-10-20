package com.dirzaaulia.countries.domain.solarsystem

internal object PlanetaryInnerDossiers {
    val MERCURY =
        PlanetaryDossier(
            planetId = PlanetId.MERCURY,
            classification = PlanetaryClassification.TERRESTRIAL,
            orbitalData = PlanetaryCatalog.MERCURY,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "3.3011 x 10^23 kg",
                    massEarths = 0.0553,
                    surfaceGravityMs2 = 3.70,
                    gravityEarths = 0.38,
                    escapeVelocityKms = 4.25,
                    meanDensityGcm3 = 5.427,
                    solarFluxWm2 = 9080.0,
                    minTempC = -180,
                    maxTempC = 430,
                    meanTempC = 167,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = false,
                    typeName = "Surface Boundary Exosphere (Trace)",
                    surfacePressureBar = 1e-14,
                    pressureDescription = "Ultra-high vacuum exosphere",
                    gases =
                        listOf(
                            AtmosphericGas("Oxygen", "O2", 42.0, 0xFF38BDF8),
                            AtmosphericGas("Sodium", "Na", 29.0, 0xFFF59E0B),
                            AtmosphericGas("Hydrogen", "H2", 22.0, 0xFFA78BFA),
                            AtmosphericGas("Helium", "He", 6.0, 0xFFF43F5E),
                            AtmosphericGas("Potassium", "K", 0.5, 0xFF10B981),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Highest orbital eccentricity (0.2056) of any major planet.",
                    "Enormous metallic iron core making up ~85% of planetary radius.",
                    "Permanently shadowed polar crater floors hold deposits of water ice.",
                ),
        )

    val VENUS =
        PlanetaryDossier(
            planetId = PlanetId.VENUS,
            classification = PlanetaryClassification.TERRESTRIAL,
            orbitalData = PlanetaryCatalog.VENUS,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "4.8675 x 10^24 kg",
                    massEarths = 0.815,
                    surfaceGravityMs2 = 8.87,
                    gravityEarths = 0.904,
                    escapeVelocityKms = 10.36,
                    meanDensityGcm3 = 5.243,
                    solarFluxWm2 = 2601.0,
                    minTempC = 462,
                    maxTempC = 467,
                    meanTempC = 464,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Supercritical CO2 Runaway Greenhouse",
                    surfacePressureBar = 92.0,
                    pressureDescription = "92 bar (Equivalent to 900m ocean depth)",
                    gases =
                        listOf(
                            AtmosphericGas("Carbon Dioxide", "CO2", 96.5, 0xFFF59E0B),
                            AtmosphericGas("Nitrogen", "N2", 3.5, 0xFF38BDF8),
                            AtmosphericGas("Sulfur Dioxide", "SO2", 0.015, 0xFFFCD34D),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Retrograde rotation with a solar day longer than its orbital year (243 Earth days).",
                    "Runaway greenhouse effect creates the hottest planetary surface in the solar system.",
                    "Atmospheric super-rotation circles the entire globe in just 4 Earth days.",
                ),
        )

    val EARTH =
        PlanetaryDossier(
            planetId = PlanetId.EARTH,
            classification = PlanetaryClassification.TERRESTRIAL,
            orbitalData = PlanetaryCatalog.EARTH,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "5.9722 x 10^24 kg",
                    massEarths = 1.000,
                    surfaceGravityMs2 = 9.807,
                    gravityEarths = 1.000,
                    escapeVelocityKms = 11.186,
                    meanDensityGcm3 = 5.514,
                    solarFluxWm2 = 1361.0,
                    minTempC = -89,
                    maxTempC = 57,
                    meanTempC = 15,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Nitrogen-Oxygen Life-Sustaining Biosphere",
                    surfacePressureBar = 1.013,
                    pressureDescription = "1.013 bar (101.3 kPa standard)",
                    gases =
                        listOf(
                            AtmosphericGas("Nitrogen", "N2", 78.08, 0xFF38BDF8),
                            AtmosphericGas("Oxygen", "O2", 20.95, 0xFF10B981),
                            AtmosphericGas("Argon", "Ar", 0.93, 0xFFA78BFA),
                            AtmosphericGas("Carbon Dioxide", "CO2", 0.04, 0xFFF59E0B),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Only known astronomical object known to harbor biological life.",
                    "Active plate tectonics continuously recycle continental crust and minerals.",
                    "Dynamic geodynamo generates a protective dipole magnetosphere.",
                ),
        )

    val MOON =
        PlanetaryDossier(
            planetId = PlanetId.MOON,
            classification = PlanetaryClassification.NATURAL_SATELLITE,
            orbitalData = PlanetaryCatalog.MOON,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "7.342 x 10^22 kg",
                    massEarths = 0.0123,
                    surfaceGravityMs2 = 1.62,
                    gravityEarths = 0.165,
                    escapeVelocityKms = 2.38,
                    meanDensityGcm3 = 3.344,
                    solarFluxWm2 = 1361.0,
                    minTempC = -246,
                    maxTempC = 121,
                    meanTempC = -20,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = false,
                    typeName = "Lunar Surface Boundary Exosphere",
                    surfacePressureBar = 3e-15,
                    pressureDescription = "Collisionless vacuum exosphere",
                    gases =
                        listOf(
                            AtmosphericGas("Argon", "Ar", 40.0, 0xFFA78BFA),
                            AtmosphericGas("Helium", "He", 40.0, 0xFFF43F5E),
                            AtmosphericGas("Neon", "Ne", 20.0, 0xFF38BDF8),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Tidally locked in synchronous 1:1 rotation with Earth.",
                    "Formed via giant impact hypothesis (Theia collision ~4.5 Ga).",
                    "Deep impact craters at south pole contain primordial volatile water ice.",
                ),
        )

    val MARS =
        PlanetaryDossier(
            planetId = PlanetId.MARS,
            classification = PlanetaryClassification.TERRESTRIAL,
            orbitalData = PlanetaryCatalog.MARS,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "6.4171 x 10^23 kg",
                    massEarths = 0.107,
                    surfaceGravityMs2 = 3.72,
                    gravityEarths = 0.379,
                    escapeVelocityKms = 5.03,
                    meanDensityGcm3 = 3.933,
                    solarFluxWm2 = 586.0,
                    minTempC = -140,
                    maxTempC = 20,
                    meanTempC = -63,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Rarefied Carbon Dioxide Atmosphere",
                    surfacePressureBar = 0.00636,
                    pressureDescription = "6.36 hPa (~0.6% of Earth sea level)",
                    gases =
                        listOf(
                            AtmosphericGas("Carbon Dioxide", "CO2", 95.32, 0xFFF59E0B),
                            AtmosphericGas("Nitrogen", "N2", 2.60, 0xFF38BDF8),
                            AtmosphericGas("Argon", "Ar", 1.90, 0xFFA78BFA),
                            AtmosphericGas("Oxygen", "O2", 0.13, 0xFF10B981),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Home to Olympus Mons, the largest shield volcano in the solar system (21.9 km high).",
                    "Valles Marineris rift canyon spans 4,000 km, over 4x deeper than the Grand Canyon.",
                    "Extensive dry river channels and lake beds record ancient wet habitability.",
                ),
        )
}
