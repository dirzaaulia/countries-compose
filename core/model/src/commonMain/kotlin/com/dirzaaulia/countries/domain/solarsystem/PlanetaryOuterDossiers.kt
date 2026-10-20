package com.dirzaaulia.countries.domain.solarsystem

internal object PlanetaryOuterDossiers {
    val JUPITER =
        PlanetaryDossier(
            planetId = PlanetId.JUPITER,
            classification = PlanetaryClassification.GAS_GIANT,
            orbitalData = PlanetaryCatalog.JUPITER,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "1.8982 x 10^27 kg",
                    massEarths = 317.8,
                    surfaceGravityMs2 = 24.79,
                    gravityEarths = 2.528,
                    escapeVelocityKms = 59.5,
                    meanDensityGcm3 = 1.326,
                    solarFluxWm2 = 50.3,
                    minTempC = -145,
                    maxTempC = -110,
                    meanTempC = -110,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Jovian Hydrogen-Helium Envelope",
                    surfacePressureBar = 1.0,
                    pressureDescription = ">1000 bar at depth (1 bar cloud deck level)",
                    gases =
                        listOf(
                            AtmosphericGas("Hydrogen", "H2", 89.8, 0xFF38BDF8),
                            AtmosphericGas("Helium", "He", 10.2, 0xFFF43F5E),
                            AtmosphericGas("Methane", "CH4", 0.3, 0xFF10B981),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Great Red Spot anticyclonic storm has persisted for over 350 years.",
                    "Mass exceeds that of all other planets in the solar system combined by 2.5x.",
                    "Immense magnetosphere extends up to 7 million km sunward.",
                ),
        )

    val SATURN =
        PlanetaryDossier(
            planetId = PlanetId.SATURN,
            classification = PlanetaryClassification.GAS_GIANT,
            orbitalData = PlanetaryCatalog.SATURN,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "5.6834 x 10^26 kg",
                    massEarths = 95.16,
                    surfaceGravityMs2 = 10.44,
                    gravityEarths = 1.065,
                    escapeVelocityKms = 35.5,
                    meanDensityGcm3 = 0.687,
                    solarFluxWm2 = 14.8,
                    minTempC = -178,
                    maxTempC = -139,
                    meanTempC = -140,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Saturnian Hydrogen-Helium Atmosphere",
                    surfacePressureBar = 1.0,
                    pressureDescription = ">1000 bar interior (0.687 g/cm3 mean density)",
                    gases =
                        listOf(
                            AtmosphericGas("Hydrogen", "H2", 96.3, 0xFF38BDF8),
                            AtmosphericGas("Helium", "He", 3.25, 0xFFF43F5E),
                            AtmosphericGas("Methane", "CH4", 0.45, 0xFF10B981),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Only planet in the solar system with a mean density lower than liquid water.",
                    "Iconic ring system is 99% pure water ice, extending up to 282,000 km across.",
                    "Persistent north polar hexagon wave discovered by Voyager and imaged by Cassini.",
                ),
        )

    val URANUS =
        PlanetaryDossier(
            planetId = PlanetId.URANUS,
            classification = PlanetaryClassification.ICE_GIANT,
            orbitalData = PlanetaryCatalog.URANUS,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "8.6810 x 10^25 kg",
                    massEarths = 14.54,
                    surfaceGravityMs2 = 8.69,
                    gravityEarths = 0.886,
                    escapeVelocityKms = 21.3,
                    meanDensityGcm3 = 1.270,
                    solarFluxWm2 = 3.71,
                    minTempC = -224,
                    maxTempC = -195,
                    meanTempC = -195,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Cyan Hydrogen-Helium-Methane Haze",
                    surfacePressureBar = 1.0,
                    pressureDescription = ">100 bar deep mantle (Coldest planetary atmosphere)",
                    gases =
                        listOf(
                            AtmosphericGas("Hydrogen", "H2", 82.5, 0xFF38BDF8),
                            AtmosphericGas("Helium", "He", 15.2, 0xFFF43F5E),
                            AtmosphericGas("Methane", "CH4", 2.3, 0xFF2DD4BF),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Extreme axial tilt of 97.77 degrees causes 42-year polar day/night cycles.",
                    "Coldest atmosphere in the solar system, reaching a minimum of 49 Kelvin (-224 C).",
                    "Composed primarily of water, ammonia, and methane ices over a small rocky core.",
                ),
        )

    val NEPTUNE =
        PlanetaryDossier(
            planetId = PlanetId.NEPTUNE,
            classification = PlanetaryClassification.ICE_GIANT,
            orbitalData = PlanetaryCatalog.NEPTUNE,
            specs =
                PlanetaryPhysicalSpecs(
                    massNotation = "1.0241 x 10^26 kg",
                    massEarths = 17.15,
                    surfaceGravityMs2 = 11.15,
                    gravityEarths = 1.137,
                    escapeVelocityKms = 23.5,
                    meanDensityGcm3 = 1.638,
                    solarFluxWm2 = 1.51,
                    minTempC = -218,
                    maxTempC = -201,
                    meanTempC = -200,
                ),
            atmosphere =
                PlanetaryAtmosphereProfile(
                    hasAtmosphere = true,
                    typeName = "Deep Azure Supersonic Wind Deck",
                    surfacePressureBar = 1.0,
                    pressureDescription = ">100 bar mantle boundary (Winds up to 2,100 km/h)",
                    gases =
                        listOf(
                            AtmosphericGas("Hydrogen", "H2", 80.0, 0xFF38BDF8),
                            AtmosphericGas("Helium", "He", 19.0, 0xFFF43F5E),
                            AtmosphericGas("Methane", "CH4", 1.0, 0xFF818CF8),
                        ),
                ),
            scientificHighlights =
                listOf(
                    "Fastest recorded planetary wind speeds in the solar system, exceeding Mach 1.7 (2,100 km/h).",
                    "Internal heat source radiates 2.61x more energy than it receives from the Sun.",
                    "Large retrograde moon Triton features active nitrogen cryogeysers.",
                ),
        )
}
