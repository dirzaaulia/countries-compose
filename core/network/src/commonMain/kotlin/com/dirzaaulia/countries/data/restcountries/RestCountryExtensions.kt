package com.dirzaaulia.countries.data.restcountries

import com.dirzaaulia.countries.data.restcountries.responses.RestCountryCoordinates
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse

val RestCountryResponse.officialName: String
    get() = names?.official.orEmpty()

val RestCountryResponse.commonName: String
    get() = names?.common.orEmpty()

val RestCountryResponse.capitalName: String
    get() =
        capitals.firstOrNull { it.attributes?.primary == true }?.name
            ?: capitals.firstOrNull()?.name.orEmpty()

val RestCountryResponse.capitalCoordinates: RestCountryCoordinates?
    get() =
        capitals.firstOrNull { it.attributes?.primary == true }?.coordinates
            ?: capitals.firstOrNull()?.coordinates

val RestCountryResponse.areaSqKm: Double?
    get() = area?.kilometers

val RestCountryResponse.areaSqMiles: Double?
    get() = area?.miles
