package com.dirzaaulia.countries.data.restcountries.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RestCountriesEnvelope(
    val data: RestCountriesData? = null,
    val errors: List<RestCountriesApiError> = emptyList(),
)

@Serializable
internal data class RestCountriesData(
    val objects: List<RestCountryResponse> = emptyList(),
    val meta: RestCountriesPageMeta? = null,
    @SerialName("_demo") val demo: RestCountriesDemoNotice? = null,
)

@Serializable
internal data class RestCountriesApiError(
    val message: String? = null,
    val code: String? = null,
)

@Serializable
internal data class RestCountriesDemoNotice(
    val message: String? = null,
)

@Serializable
internal data class RestCountriesPageMeta(
    val total: Int? = null,
    val count: Int? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val more: Boolean? = null,
)

@Serializable
data class RestCountryResponse(
    val names: RestCountryNames? = null,
    val codes: RestCountryCodes? = null,
    val flag: RestCountryFlag? = null,
    val capitals: List<RestCountryCapital> = emptyList(),
    val region: String? = null,
    val subregion: String? = null,
    val area: RestCountryArea? = null,
    val borders: List<String> = emptyList(),
    @SerialName("calling_codes") val callingCodes: List<String> = emptyList(),
    val currencies: List<RestCountryCurrencyResponse> = emptyList(),
    val languages: List<RestCountryLanguageResponse> = emptyList(),
    val leaders: List<RestCountryLeader> = emptyList(),
    val memberships: RestCountryMemberships? = null,
    val population: Long? = null,
    val timezones: List<String> = emptyList(),
    val tlds: List<String> = emptyList(),
    val cars: RestCountryCars? = null,
    val classification: RestCountryClassification? = null,
    val continents: List<String> = emptyList(),
    val coordinates: RestCountryCoordinates? = null,
    val date: RestCountryDate? = null,
    val demonyms: Map<String, RestCountryDemonym>? = null,
    val economy: RestCountryEconomy? = null,
    val governmentType: String? = null,
    val links: RestCountryLinks? = null,
    val landlocked: Boolean? = null,
    val numberFormat: RestCountryNumberFormat? = null,
    val parent: RestCountryParent? = null,
    val postalCode: RestCountryPostalCode? = null,
    val units: RestCountryUnits? = null,
    val uuid: String? = null,
)

@Serializable
data class RestCountryNames(
    val common: String? = null,
    val official: String? = null,
    val alternates: List<String> = emptyList(),
    val native: Map<String, RestCountryLocalizedName> = emptyMap(),
    val translations: Map<String, RestCountryLocalizedName> = emptyMap(),
)

@Serializable
data class RestCountryLocalizedName(
    val common: String? = null,
    val official: String? = null,
)

@Serializable
data class RestCountryCodes(
    @SerialName("alpha_2") val alpha2: String? = null,
    @SerialName("alpha_3") val alpha3: String? = null,
    val ccn3: String? = null,
    val fips: String? = null,
    val gec: String? = null,
    val cioc: String? = null,
    val fifa: String? = null,
)

@Serializable
data class RestCountryFlag(
    val emoji: String? = null,
    val unicode: String? = null,
    @SerialName("html_entity") val htmlEntity: String? = null,
    @SerialName("url_png") val pngUrl: String? = null,
    @SerialName("url_svg") val svgUrl: String? = null,
    val description: String? = null,
    val colors: RestCountryFlagColors? = null,
)

@Serializable
data class RestCountryFlagColors(
    val dominant: String? = null,
    val prominent: String? = null,
    val palette: List<RestCountryFlagColor> = emptyList(),
    val swatches: Map<String, String?> = emptyMap(),
)

@Serializable
data class RestCountryFlagColor(
    val hex: String? = null,
    val proportion: Double? = null,
)

@Serializable
data class RestCountryCapital(
    val name: String? = null,
    val coordinates: RestCountryCoordinates? = null,
    val attributes: RestCountryCapitalAttributes? = null,
)

@Serializable
data class RestCountryCapitalAttributes(
    val primary: Boolean? = null,
    val constitutional: Boolean? = null,
    val administrative: Boolean? = null,
    val executive: Boolean? = null,
    val legislative: Boolean? = null,
    val judicial: Boolean? = null,
)

@Serializable
data class RestCountryArea(
    val kilometers: Double? = null,
    val miles: Double? = null,
)

@Serializable
data class RestCountryCoordinates(
    val lat: Double? = null,
    val lng: Double? = null,
)

@Serializable
data class RestCountryCurrencyResponse(
    val code: String? = null,
    val name: String? = null,
    val symbol: String? = null,
)

@Serializable
data class RestCountryLanguageResponse(
    val iso639_1: String? = null,
    val iso639_2: String? = null,
    val iso639_3: String? = null,
    val bcp47: String? = null,
    val name: String? = null,
    val nativeName: String? = null,
)

@Serializable
data class RestCountryLeader(
    val assets: List<RestCountryLeaderAsset> = emptyList(),
    val attributes: RestCountryLeaderAttributes? = null,
    val links: RestCountryLeaderLinks? = null,
    val name: String? = null,
    val title: String? = null,
)

@Serializable
data class RestCountryLeaderAsset(
    val type: String? = null,
    val url: String? = null,
)

@Serializable
data class RestCountryLeaderAttributes(
    @SerialName("head_of_government") val headOfGovernment: Boolean? = null,
    @SerialName("head_of_state") val headOfState: Boolean? = null,
)

@Serializable
data class RestCountryLeaderLinks(
    val wikipedia: String? = null,
)

@Serializable
data class RestCountryMemberships(
    val un: Boolean? = null,
    val eu: Boolean? = null,
    val eurozone: Boolean? = null,
    val schengen: Boolean? = null,
    val nato: Boolean? = null,
    val commonwealth: Boolean? = null,
    val oecd: Boolean? = null,
    val g7: Boolean? = null,
    val g20: Boolean? = null,
    val brics: Boolean? = null,
    val opec: Boolean? = null,
    @SerialName("african_union") val africanUnion: Boolean? = null,
    val asean: Boolean? = null,
    @SerialName("arab_league") val arabLeague: Boolean? = null,
)

@Serializable
data class RestCountryCars(
    @SerialName("driving_side") val drivingSide: String? = null,
    val signs: List<String> = emptyList(),
)

@Serializable
data class RestCountryClassification(
    val sovereign: Boolean? = null,
    @SerialName("un_member") val unMember: Boolean? = null,
    @SerialName("un_observer") val unObserver: Boolean? = null,
    val disputed: Boolean? = null,
    val dependency: Boolean? = null,
    @SerialName("dependency_type") val dependencyType: String? = null,
    @SerialName("iso_status") val isoStatus: String? = null,
)

@Serializable
data class RestCountryDate(
    @SerialName("start_of_week") val startOfWeek: String? = null,
    @SerialName("academic_year_start") val academicYearStart: RestCountryDateParts? = null,
    @SerialName("fiscal_year_start") val fiscalYearStart: RestCountryFiscalYearStart? = null,
)

@Serializable
data class RestCountryDateParts(
    val month: Int? = null,
    val day: Int? = null,
)

@Serializable
data class RestCountryFiscalYearStart(
    val government: RestCountryDateParts? = null,
    val corporate: RestCountryCorporateYearStart? = null,
    val personal: RestCountryDateParts? = null,
)

@Serializable
data class RestCountryCorporateYearStart(
    val month: Int? = null,
    val day: Int? = null,
    val basis: String? = null,
)

@Serializable
data class RestCountryDemonym(
    val m: String? = null,
    val f: String? = null,
)

@Serializable
data class RestCountryEconomy(
    @SerialName("gini_coefficient") val giniCoefficient: Map<String, Double> = emptyMap(),
)

@Serializable
data class RestCountryLinks(
    val official: String? = null,
    val wikipedia: String? = null,
    @SerialName("open_street_maps") val openStreetMaps: String? = null,
    @SerialName("google_maps") val googleMaps: String? = null,
)

@Serializable
data class RestCountryNumberFormat(
    @SerialName("decimal_separator") val decimalSeparator: String? = null,
    @SerialName("thousands_separator") val thousandsSeparator: String? = null,
)

@Serializable
data class RestCountryParent(
    @SerialName("alpha_2") val alpha2: String? = null,
    @SerialName("alpha_3") val alpha3: String? = null,
)

@Serializable
data class RestCountryPostalCode(
    val format: String? = null,
    val regex: String? = null,
)

@Serializable
data class RestCountryUnits(
    @SerialName("measurement_system") val measurementSystem: String? = null,
    @SerialName("temperature_scale") val temperatureScale: String? = null,
)
