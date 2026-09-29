package com.dirzaaulia.countries.data.tectonic

import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate

val PACIFIC_RING_OF_FIRE_BOUNDARIES: List<List<LatLng>> =
    listOf(
        listOf(
            LatLng(-45.0, 168.0), LatLng(-38.0, 176.0), LatLng(-20.0, 178.0),
            LatLng(-15.0, -173.0), LatLng(0.0, 126.0), LatLng(10.0, 126.0),
            LatLng(14.0, 144.0), LatLng(35.0, 140.0), LatLng(45.0, 150.0),
            LatLng(52.0, 174.0), LatLng(54.0, -165.0), LatLng(60.0, -145.0),
            LatLng(50.0, -128.0), LatLng(38.0, -123.0), LatLng(32.0, -115.0),
            LatLng(18.0, -105.0), LatLng(5.0, -85.0), LatLng(-15.0, -75.0),
            LatLng(-35.0, -73.0), LatLng(-55.0, -68.0),
        ),
    )

val ALL_TECTONIC_PLATES: List<TectonicPlate> =
    listOf(
        TectonicPlate(
            id = "pacific",
            name = "Pacific Plate",
            plateType = "Major",
            areaMillionSqKm = 103.3,
            driftVelocityCmYear = 8.1,
            driftDirection = "Northwest (305°)",
            boundaries = PACIFIC_RING_OF_FIRE_BOUNDARIES,
        ),
        TectonicPlate(
            id = "eurasian",
            name = "Eurasian Plate",
            plateType = "Major",
            areaMillionSqKm = 67.8,
            driftVelocityCmYear = 1.4,
            driftDirection = "East (95°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(65.0, -18.0), LatLng(50.0, -30.0), LatLng(36.0, -10.0),
                        LatLng(36.0, 25.0), LatLng(30.0, 60.0), LatLng(28.0, 85.0),
                        LatLng(10.0, 98.0), LatLng(0.0, 120.0), LatLng(35.0, 140.0),
                        LatLng(65.0, 170.0),
                    ),
                ),
        ),
        TectonicPlate(
            id = "north_american",
            name = "North American Plate",
            plateType = "Major",
            areaMillionSqKm = 75.9,
            driftVelocityCmYear = 2.3,
            driftDirection = "Southwest (230°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(80.0, 0.0), LatLng(65.0, -18.0), LatLng(30.0, -42.0),
                        LatLng(18.0, -65.0), LatLng(15.0, -90.0), LatLng(32.0, -115.0),
                        LatLng(50.0, -128.0), LatLng(60.0, -145.0), LatLng(65.0, 170.0),
                    ),
                ),
        ),
        TectonicPlate(
            id = "african",
            name = "African Plate",
            plateType = "Major",
            areaMillionSqKm = 61.3,
            driftVelocityCmYear = 2.15,
            driftDirection = "Northeast (45°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(36.0, -10.0), LatLng(0.0, -20.0), LatLng(-54.0, -5.0),
                        LatLng(-50.0, 30.0), LatLng(-12.0, 48.0), LatLng(12.0, 43.0),
                        LatLng(30.0, 32.0), LatLng(36.0, 25.0),
                    ),
                ),
        ),
        TectonicPlate(
            id = "indo_australian",
            name = "Indo-Australian Plate",
            plateType = "Major",
            areaMillionSqKm = 58.9,
            driftVelocityCmYear = 5.6,
            driftDirection = "Northeast (35°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(28.0, 85.0), LatLng(10.0, 98.0), LatLng(-10.0, 120.0),
                        LatLng(-45.0, 168.0), LatLng(-50.0, 110.0), LatLng(-35.0, 78.0),
                        LatLng(-10.0, 60.0), LatLng(25.0, 62.0),
                    ),
                ),
        ),
        TectonicPlate(
            id = "south_american",
            name = "South American Plate",
            plateType = "Major",
            areaMillionSqKm = 43.6,
            driftVelocityCmYear = 1.45,
            driftDirection = "West (270°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(12.0, -70.0), LatLng(0.0, -20.0), LatLng(-54.0, -5.0),
                        LatLng(-55.0, -68.0), LatLng(-35.0, -73.0), LatLng(-15.0, -75.0),
                        LatLng(5.0, -78.0),
                    ),
                ),
        ),
        TectonicPlate(
            id = "nazca",
            name = "Nazca Plate",
            plateType = "Minor",
            areaMillionSqKm = 15.6,
            driftVelocityCmYear = 7.5,
            driftDirection = "East (85°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(5.0, -85.0), LatLng(-15.0, -75.0), LatLng(-35.0, -73.0),
                        LatLng(-45.0, -75.0), LatLng(-40.0, -95.0), LatLng(-20.0, -115.0),
                        LatLng(0.0, -100.0),
                    ),
                ),
        ),
        TectonicPlate(
            id = "philippine_sea",
            name = "Philippine Sea Plate",
            plateType = "Minor",
            areaMillionSqKm = 5.5,
            driftVelocityCmYear = 6.3,
            driftDirection = "Northwest (310°)",
            boundaries =
                listOf(
                    listOf(
                        LatLng(35.0, 140.0), LatLng(25.0, 122.0), LatLng(10.0, 126.0),
                        LatLng(5.0, 135.0), LatLng(14.0, 144.0), LatLng(25.0, 143.0),
                    ),
                ),
        ),
    )

val FALLBACK_EARTHQUAKES: List<Earthquake> =
    listOf(
        Earthquake("eq1", "M 6.8 - 12km ENE of Hualien, Taiwan", 6.8, "Hualien, Taiwan", 1711200000000L, 23.97, 121.60, 15.2, true, "https://earthquake.usgs.gov"),
        Earthquake("eq2", "M 7.5 - Noto Peninsula, Japan", 7.5, "Noto Peninsula, Japan", 1704100000000L, 37.50, 137.20, 10.0, true, "https://earthquake.usgs.gov"),
        Earthquake("eq3", "M 5.8 - Southern Sumatra, Indonesia", 5.8, "Sumatra, Indonesia", 1711300000000L, -4.80, 102.50, 48.0, false, "https://earthquake.usgs.gov"),
        Earthquake("eq4", "M 6.2 - Near Coast of Central Chile", 6.2, "Central Chile", 1711400000000L, -30.50, -71.40, 35.0, false, "https://earthquake.usgs.gov"),
        Earthquake("eq5", "M 5.1 - Southern California, USA", 5.1, "Ojai, California, USA", 1711500000000L, 34.40, -119.10, 14.5, false, "https://earthquake.usgs.gov"),
        Earthquake("eq6", "M 7.1 - Banda Sea, Deep Focus", 7.1, "Banda Sea", 1711600000000L, -6.50, 129.80, 520.0, false, "https://earthquake.usgs.gov"),
    )
