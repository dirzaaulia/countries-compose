package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.data.restcountries.capitalName
import com.dirzaaulia.countries.domain.astronomy.AstronomyMath
import com.dirzaaulia.countries.domain.astronomy.SunPosition
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.domain.globe.latLngToCartesian
import com.dirzaaulia.countries.platform.PlatformDossierBrowser
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.dossier.components.DossierActionButtons
import com.dirzaaulia.countries.ui.dossier.components.DossierBordersSection
import com.dirzaaulia.countries.ui.dossier.components.DossierCultureSection
import com.dirzaaulia.countries.ui.dossier.components.DossierGeographySection
import com.dirzaaulia.countries.ui.dossier.components.DossierHeaderSection
import com.dirzaaulia.countries.ui.dossier.components.DossierLoadingStatus
import com.dirzaaulia.countries.ui.dossier.components.DossierNameSection
import com.dirzaaulia.countries.ui.dossier.components.DossierRestCountrySections
import com.dirzaaulia.countries.ui.dossier.components.DossierRestGovernment
import com.dirzaaulia.countries.ui.dossier.components.DossierRestTerritory
import com.dirzaaulia.countries.ui.dossier.components.DossierWeatherSection
import com.dirzaaulia.countries.ui.dossier.components.DossierWorldBankSection
import com.dirzaaulia.countries.ui.dossier.components.dossierHeaderBrush
import com.dirzaaulia.countries.ui.dossier.components.dossierTimeInfo
import com.dirzaaulia.countries.ui.dossier.components.localSolarTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDossierSheet(
    modifier: Modifier = Modifier,
    country: Country,
    liveDetails: LiveCountryDetails?,
    isFetchingLive: Boolean,
    allCountries: List<Country>,
    onClose: () -> Unit,
    onCenterView: (() -> Unit)? = null,
    onNextCountry: () -> Unit,
    onSelectCountry: (Country) -> Unit,
    onOpenMeteorology: (() -> Unit)? = null,
    onOpenWorldBank: (() -> Unit)? = null,
    onOpenNasaCrisis: (() -> Unit)? = null,
    onCompareCountry: (() -> Unit)? = null,
    sunPos: SunPosition? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val showFloatingClose by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    var browserUrl by remember(country.id) { mutableStateOf<String?>(null) }
    val rest = liveDetails?.restCountry
    val displayCapital =
        rest?.capitalName?.takeIf { it.isNotEmpty() && it != "N/A" }
            ?: country.capital.ifEmpty { "N/A" }
    val displayBorders = rest?.borders?.ifEmpty { null } ?: country.borders
    val isDaylight =
        remember(country.id, sunPos) {
            sunPos?.let { AstronomyMath.isDaylight(country.center, it.vector) } ?: true
        }
    val dayNightStatus =
        remember(country.id, sunPos) {
            calculateDayNightStatus(country, sunPos)
        }

    if (browserUrl != null) {
        PlatformDossierBrowser(url = browserUrl!!, onClose = { browserUrl = null })
        return
    }

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = if (dayNightStatus != "NIGHT") Color(0xF20B1626) else Color(0xF2070D18),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = if (isDaylight) Color(0xFF64748B) else Color(0xFF475569),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier.fillMaxHeight(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
            ) {
                item(key = "header") {
                    DossierHeaderSection(
                        country = country,
                        displayCapital = displayCapital,
                        dayNightStatus = dayNightStatus,
                        civilTimeInfo =
                            remember(country.timezones, country.center.lng, rest?.timezones) {
                                dossierTimeInfo(country, rest?.timezones)
                            },
                        headerGlowBrush = remember(dayNightStatus) { dossierHeaderBrush(dayNightStatus) },
                        onClose = onClose,
                    )
                }
                item(key = "name") { DossierNameSection(country = country, rest = rest) }
                item(key = "loading") { DossierLoadingStatus(isVisible = isFetchingLive && liveDetails == null) }
                item(key = "weather") {
                    DossierWeatherSection(
                        liveDetails = liveDetails,
                        capital = displayCapital,
                        onOpenMeteorology = onOpenMeteorology,
                    )
                }
                item(key = "geography") {
                    DossierGeographySection(
                        country = country,
                        rest = rest,
                        displayCapital = displayCapital,
                        localSolarTime = remember(country.center.lng) { localSolarTime(country.center.lng) },
                    )
                }
                rest?.let { item(key = "territory") { DossierRestTerritory(it) } }
                rest?.let { item(key = "government") { DossierRestGovernment(it, onOpenLink = { url -> browserUrl = url }) } }
                item(key = "worldBank") { DossierWorldBankSection(liveDetails = liveDetails, onOpenWorldBank = onOpenWorldBank) }
                item(key = "culture") {
                    DossierCultureSection(
                        currencies = rest?.currencies?.mapNotNull { it.name }?.ifEmpty { null } ?: country.currencies,
                        languages = rest?.languages?.mapNotNull { it.name }?.ifEmpty { null } ?: country.languages,
                    )
                }
                item(key = "borders") {
                    DossierBordersSection(
                        borders = displayBorders,
                        allCountries = allCountries,
                        onSelectCountry = onSelectCountry,
                    )
                }
                rest?.let { response -> DossierRestCountrySections(response, onOpenLink = { url -> browserUrl = url }) }
                item(key = "actions") {
                    DossierActionButtons(
                        onClose = onClose,
                        onNextCountry = onNextCountry,
                        onCompare = onCompareCountry,
                    )
                }
            }
            if (showFloatingClose) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 20.dp),
                    shape = CircleShape,
                    color = Color(0xF20B1626),
                    shadowElevation = 8.dp,
                ) {
                    MinimalistCloseButton(onClick = onClose)
                }
            }
        }
    }
}

private fun calculateDayNightStatus(
    country: Country,
    sunPos: SunPosition?,
): String {
    if (sunPos == null) return "DAY"
    val lat = country.center.lat
    val west = latLngToCartesian(lat, country.boundingBox.minLng, 1.0)
    val east = latLngToCartesian(lat, country.boundingBox.maxLng, 1.0)
    val center = latLngToCartesian(lat, country.center.lng, 1.0)
    val westDot = west.x * sunPos.vector.x + west.y * sunPos.vector.y + west.z * sunPos.vector.z
    val eastDot = east.x * sunPos.vector.x + east.y * sunPos.vector.y + east.z * sunPos.vector.z
    val centerDot = center.x * sunPos.vector.x + center.y * sunPos.vector.y + center.z * sunPos.vector.z
    val isWestDay = westDot > -0.02
    val isEastDay = eastDot > -0.02

    return when {
        isWestDay && !isEastDay -> "SPLIT_WEST_DAY"
        !isWestDay && isEastDay -> "SPLIT_EAST_DAY"
        centerDot > -0.02 -> "DAY"
        else -> "NIGHT"
    }
}
