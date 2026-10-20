package com.dirzaaulia.countries.domain.solarsystem

enum class PlanetaryClassification(
    val label: String,
) {
    TERRESTRIAL("TERRESTRIAL PLANET"),
    GAS_GIANT("GAS GIANT"),
    ICE_GIANT("ICE GIANT"),
    NATURAL_SATELLITE("NATURAL SATELLITE"),
}

data class AtmosphericGas(
    val gasName: String,
    val formula: String,
    val percentage: Double,
    val colorHex: Long,
)

data class PlanetaryAtmosphereProfile(
    val hasAtmosphere: Boolean,
    val typeName: String,
    val surfacePressureBar: Double,
    val pressureDescription: String,
    val gases: List<AtmosphericGas>,
)

data class PlanetaryPhysicalSpecs(
    val massNotation: String,
    val massEarths: Double,
    val surfaceGravityMs2: Double,
    val gravityEarths: Double,
    val escapeVelocityKms: Double,
    val meanDensityGcm3: Double,
    val solarFluxWm2: Double,
    val minTempC: Int,
    val maxTempC: Int,
    val meanTempC: Int,
)

data class PlanetaryDossier(
    val planetId: PlanetId,
    val classification: PlanetaryClassification,
    val orbitalData: PlanetOrbitalData,
    val specs: PlanetaryPhysicalSpecs,
    val atmosphere: PlanetaryAtmosphereProfile,
    val scientificHighlights: List<String>,
)
