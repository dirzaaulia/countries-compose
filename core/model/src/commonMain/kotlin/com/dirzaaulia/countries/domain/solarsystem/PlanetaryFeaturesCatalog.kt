package com.dirzaaulia.countries.domain.solarsystem

object PlanetaryFeaturesCatalog {
    val MERCURY_FEATURES =
        listOf(
            PlanetaryFeature(
                id = "caloris_basin",
                planetId = PlanetId.MERCURY,
                name = "Caloris Basin",
                type = PlanetaryFeatureType.BASIN,
                lat = 30.5,
                lng = 189.8,
                significance = "One of the largest impact basins in the Solar System.",
                discoveries = listOf("1,550 km in diameter.", "Surrounded by a ring of mountains 2 km high.", "Discovered by Mariner 10 in 1974."),
            ),
            PlanetaryFeature(
                id = "rembrandt_basin",
                planetId = PlanetId.MERCURY,
                name = "Rembrandt Basin",
                type = PlanetaryFeatureType.BASIN,
                lat = -33.2,
                lng = 271.8,
                significance = "Large impact basin with unusual tectonic features.",
                discoveries = listOf("715 km in diameter.", "Discovered by MESSENGER in 2008.", "Well-preserved interior plains."),
            ),
            PlanetaryFeature(
                id = "messenger_impact",
                planetId = PlanetId.MERCURY,
                name = "MESSENGER Impact Site",
                type = PlanetaryFeatureType.PROBE,
                lat = 54.4,
                lng = 210.1,
                missionAgency = "NASA",
                missionYear = 2015,
                significance = "Final resting place of the MESSENGER spacecraft.",
                discoveries = listOf("First spacecraft to orbit Mercury.", "Mapped 100% of the surface.", "Found water ice in permanently shadowed polar craters."),
            ),
        )

    val VENUS_FEATURES =
        listOf(
            PlanetaryFeature(
                id = "maxwell_montes",
                planetId = PlanetId.VENUS,
                name = "Maxwell Montes",
                type = PlanetaryFeatureType.MOUNTAIN,
                lat = 65.2,
                lng = 3.3,
                significance = "The highest mountain peak on Venus.",
                discoveries = listOf("Stands 11 km (6.8 miles) high.", "Highly reflective to radar, indicating potential metallic snow (lead sulfide or bismuth sulfide)."),
            ),
            PlanetaryFeature(
                id = "ishtar_terra",
                planetId = PlanetId.VENUS,
                name = "Ishtar Terra",
                type = PlanetaryFeatureType.TERRA,
                lat = 70.4,
                lng = 27.5,
                significance = "One of the two main highland regions on Venus.",
                discoveries = listOf("Roughly the size of Australia.", "Contains the four main mountain ranges of Venus."),
            ),
            PlanetaryFeature(
                id = "venera_9",
                planetId = PlanetId.VENUS,
                name = "Venera 9 Lander",
                type = PlanetaryFeatureType.LANDER,
                lat = 31.01,
                lng = 291.64,
                missionAgency = "Soviet Union",
                missionYear = 1975,
                significance = "First spacecraft to return images from the surface of another planet.",
                discoveries = listOf("Operated for 53 minutes on the surface.", "Measured surface pressure of 90 atm and temperature of 485°C."),
            ),
        )

    val JUPITER_FEATURES =
        listOf(
            PlanetaryFeature(
                id = "great_red_spot",
                planetId = PlanetId.JUPITER,
                name = "Great Red Spot",
                type = PlanetaryFeatureType.STORM,
                lat = -22.0,
                lng = 0.0, // Longitude drifts, using 0 for relative visual mapping
                significance = "A persistent high-pressure region producing an anticyclonic storm.",
                discoveries = listOf("Observed for over 300 years.", "Large enough to swallow Earth.", "Winds peak at ~432 km/h."),
            ),
            PlanetaryFeature(
                id = "galileo_probe",
                planetId = PlanetId.JUPITER,
                name = "Galileo Probe Entry",
                type = PlanetaryFeatureType.PROBE,
                lat = 6.5,
                lng = -4.9,
                missionAgency = "NASA",
                missionYear = 1995,
                significance = "First probe to enter a gas giant's atmosphere.",
                discoveries = listOf("Survived entry speeds of 47 km/s.", "Transmitted data for 58 minutes before succumbing to extreme pressure (23 atm) and heat (153°C)."),
            ),
        )

    val SATURN_FEATURES =
        listOf(
            PlanetaryFeature(
                id = "north_polar_hexagon",
                planetId = PlanetId.SATURN,
                name = "North Polar Hexagon",
                type = PlanetaryFeatureType.STORM,
                lat = 78.0,
                lng = 0.0,
                significance = "A persistent hexagonal cloud pattern around the north pole.",
                discoveries = listOf("Sides are roughly 14,500 km long.", "Rotates with a period of 10h 39m 24s.", "First discovered by Voyager 1 in 1981."),
            ),
            PlanetaryFeature(
                id = "cassini_plunge",
                planetId = PlanetId.SATURN,
                name = "Cassini Grand Finale",
                type = PlanetaryFeatureType.PROBE,
                lat = 9.4,
                lng = 53.0,
                missionAgency = "NASA/ESA/ASI",
                missionYear = 2017,
                significance = "Intentional atmospheric entry to protect potentially habitable moons.",
                discoveries = listOf("Sampled Saturn's atmosphere directly.", "Revealed the rings are younger than previously thought (10-100 million years)."),
            ),
        )

    fun getFeaturesForPlanet(planetId: PlanetId): List<PlanetaryFeature> =
        when (planetId) {
            PlanetId.MERCURY -> MERCURY_FEATURES
            PlanetId.VENUS -> VENUS_FEATURES
            PlanetId.JUPITER -> JUPITER_FEATURES
            PlanetId.SATURN -> SATURN_FEATURES
            else -> emptyList() // Earth, Moon, Mars are handled by their own specific domain layers
        }
}
