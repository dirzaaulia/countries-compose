package com.dirzaaulia.countries.domain.solarsystem

object PlanetaryInteriorCatalog {
    val EARTH_INTERIOR =
        PlanetaryInterior(
            planetId = PlanetId.EARTH,
            totalRadiusKm = 6371.0,
            layers =
                listOf(
                    InteriorLayer(
                        "earth_inner_core",
                        "Inner Core",
                        0.19f,
                        0xFFFCD34D,
                        "5150 - 6371 km",
                        "330 - 360 GPa",
                        "5400 - 6000 K",
                        "Solid",
                        "Solid iron-nickel alloy.",
                    ),
                    InteriorLayer(
                        "earth_outer_core",
                        "Outer Core",
                        0.55f,
                        0xFFF59E0B,
                        "2890 - 5150 km",
                        "135 - 330 GPa",
                        "4000 - 5000 K",
                        "Liquid",
                        "Liquid iron and nickel; generates Earth's magnetic field.",
                    ),
                    InteriorLayer(
                        "earth_mantle",
                        "Mantle",
                        0.99f,
                        0xFFB45309,
                        "35 - 2890 km",
                        "1 - 135 GPa",
                        "500 - 4000 K",
                        "Solid / Plastic",
                        "Silicate rocks rich in magnesium and iron.",
                    ),
                    InteriorLayer(
                        "earth_crust",
                        "Crust",
                        1.0f,
                        0xFF475569,
                        "0 - 35 km",
                        "0.1 MPa - 1 GPa",
                        "250 - 500 K",
                        "Solid",
                        "Solid silicate rock crust.",
                    ),
                ),
        )

    val MARS_INTERIOR =
        PlanetaryInterior(
            planetId = PlanetId.MARS,
            totalRadiusKm = 3389.5,
            layers =
                listOf(
                    InteriorLayer(
                        "mars_core",
                        "Core",
                        0.54f,
                        0xFF9CA3AF,
                        "1830 - 3389 km",
                        "25 - 40 GPa",
                        "1500 - 2000 K",
                        "Liquid",
                        "Liquid iron-nickel-sulfur core.",
                    ),
                    InteriorLayer(
                        "mars_mantle",
                        "Mantle",
                        0.98f,
                        0xFF991B1B,
                        "50 - 1830 km",
                        "1 - 25 GPa",
                        "500 - 1500 K",
                        "Solid",
                        "Silicate rock mantle.",
                    ),
                    InteriorLayer(
                        "mars_crust",
                        "Crust",
                        1.0f,
                        0xFFDC2626,
                        "0 - 50 km",
                        "0.1 MPa - 1 GPa",
                        "150 - 500 K",
                        "Solid",
                        "Iron-rich basaltic crust.",
                    ),
                ),
        )

    val JUPITER_INTERIOR =
        PlanetaryInterior(
            planetId = PlanetId.JUPITER,
            totalRadiusKm = 69911.0,
            layers =
                listOf(
                    InteriorLayer(
                        "jupiter_core",
                        "Core",
                        0.15f,
                        0xFF334155,
                        "59000 - 69911 km",
                        "4000+ GPa",
                        "20000+ K",
                        "Solid / Dense Fluid",
                        "Dense rock and exotic ice core.",
                    ),
                    InteriorLayer(
                        "jupiter_metallic",
                        "Metallic Hydrogen",
                        0.80f,
                        0xFF94A3B8,
                        "14000 - 59000 km",
                        "200 - 4000 GPa",
                        "10000 - 20000 K",
                        "Liquid Metallic",
                        "Liquid metallic hydrogen driving the massive magnetosphere.",
                    ),
                    InteriorLayer(
                        "jupiter_molecular",
                        "Molecular Hydrogen",
                        1.0f,
                        0xFFFDE047,
                        "0 - 14000 km",
                        "1 bar - 200 GPa",
                        "165 - 10000 K",
                        "Gas / Supercritical Fluid",
                        "Hydrogen and helium envelope.",
                    ),
                ),
        )

    val SATURN_INTERIOR =
        PlanetaryInterior(
            planetId = PlanetId.SATURN,
            totalRadiusKm = 58232.0,
            layers =
                listOf(
                    InteriorLayer(
                        "saturn_core",
                        "Core",
                        0.25f,
                        0xFF475569,
                        "43000 - 58232 km",
                        "1000+ GPa",
                        "11700+ K",
                        "Dense Fluid / Solid",
                        "Heavy elements rock/ice core.",
                    ),
                    InteriorLayer(
                        "saturn_metallic",
                        "Metallic Hydrogen",
                        0.50f,
                        0xFF94A3B8,
                        "29000 - 43000 km",
                        "100 - 1000 GPa",
                        "6000 - 11700 K",
                        "Liquid Metallic",
                        "Liquid metallic hydrogen layer.",
                    ),
                    InteriorLayer(
                        "saturn_molecular",
                        "Molecular Hydrogen",
                        1.0f,
                        0xFFFEF08A,
                        "0 - 29000 km",
                        "1 bar - 100 GPa",
                        "134 - 6000 K",
                        "Gas / Supercritical Fluid",
                        "Molecular hydrogen and helium.",
                    ),
                ),
        )

    fun getInteriorForPlanet(planetId: PlanetId): PlanetaryInterior? =
        when (planetId) {
            PlanetId.EARTH -> EARTH_INTERIOR
            PlanetId.MARS -> MARS_INTERIOR
            PlanetId.JUPITER -> JUPITER_INTERIOR
            PlanetId.SATURN -> SATURN_INTERIOR
            // Provide fallbacks or other planets as needed
            PlanetId.MERCURY ->
                PlanetaryInterior(
                    PlanetId.MERCURY,
                    2439.7,
                    listOf(
                        InteriorLayer("merc_core", "Core", 0.85f, 0xFF94A3B8, "400-2439 km", "5-40 GPa", "2000K", "Solid/Liquid", "Massive iron core."),
                        InteriorLayer("merc_mantle", "Mantle/Crust", 1.0f, 0xFF475569, "0-400 km", "0-5 GPa", "400K", "Solid", "Thin silicate crust/mantle."),
                    ),
                )
            PlanetId.VENUS ->
                PlanetaryInterior(
                    PlanetId.VENUS,
                    6051.8,
                    listOf(
                        InteriorLayer("ven_core", "Core", 0.50f, 0xFFFCD34D, "3000-6051 km", "300 GPa", "5000K", "Liquid", "Liquid iron-nickel core."),
                        InteriorLayer("ven_mantle", "Mantle", 0.99f, 0xFFB45309, "50-3000 km", "1-300 GPa", "3000K", "Solid", "Silicate rock mantle."),
                        InteriorLayer("ven_crust", "Crust", 1.0f, 0xFFD97706, "0-50 km", "0.1-1 GPa", "737K", "Solid", "Basaltic crust."),
                    ),
                )
            PlanetId.URANUS ->
                PlanetaryInterior(
                    PlanetId.URANUS,
                    25362.0,
                    listOf(
                        InteriorLayer("ura_core", "Core", 0.20f, 0xFF475569, "20000-25362 km", "800 GPa", "5000K", "Solid", "Silicate/iron-nickel core."),
                        InteriorLayer("ura_mantle", "Mantle", 0.80f, 0xFF38BDF8, "5000-20000 km", "100-800 GPa", "2000-5000K", "Superionic Fluid", "Water, ammonia, methane ice/fluid."),
                        InteriorLayer("ura_atmo", "Atmosphere", 1.0f, 0xFFBAE6FD, "0-5000 km", "1-100 GPa", "76-2000K", "Gas", "Hydrogen, helium, methane."),
                    ),
                )
            PlanetId.NEPTUNE ->
                PlanetaryInterior(
                    PlanetId.NEPTUNE,
                    24622.0,
                    listOf(
                        InteriorLayer("nep_core", "Core", 0.20f, 0xFF475569, "19000-24622 km", "800 GPa", "5000K", "Solid", "Silicate/iron-nickel core."),
                        InteriorLayer("nep_mantle", "Mantle", 0.80f, 0xFF6366F1, "5000-19000 km", "100-800 GPa", "2000-5000K", "Superionic Fluid", "Water, ammonia, methane ice/fluid."),
                        InteriorLayer("nep_atmo", "Atmosphere", 1.0f, 0xFF818CF8, "0-5000 km", "1-100 GPa", "72-2000K", "Gas", "Hydrogen, helium, methane."),
                    ),
                )
            else -> null
        }
}
