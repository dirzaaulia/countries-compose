package com.dirzaaulia.countries.domain.solarsystem

object PlanetaryDossierCatalog {
    val MERCURY get() = PlanetaryInnerDossiers.MERCURY
    val VENUS get() = PlanetaryInnerDossiers.VENUS
    val EARTH get() = PlanetaryInnerDossiers.EARTH
    val MOON get() = PlanetaryInnerDossiers.MOON
    val MARS get() = PlanetaryInnerDossiers.MARS

    val JUPITER get() = PlanetaryOuterDossiers.JUPITER
    val SATURN get() = PlanetaryOuterDossiers.SATURN
    val URANUS get() = PlanetaryOuterDossiers.URANUS
    val NEPTUNE get() = PlanetaryOuterDossiers.NEPTUNE

    val ALL_DOSSIERS =
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

    fun getDossier(planetId: PlanetId): PlanetaryDossier =
        when (planetId) {
            PlanetId.MERCURY -> MERCURY
            PlanetId.VENUS -> VENUS
            PlanetId.EARTH -> EARTH
            PlanetId.MOON -> MOON
            PlanetId.MARS -> MARS
            PlanetId.JUPITER -> JUPITER
            PlanetId.SATURN -> SATURN
            PlanetId.URANUS -> URANUS
            PlanetId.NEPTUNE -> NEPTUNE
            PlanetId.SUN -> EARTH
        }
}
