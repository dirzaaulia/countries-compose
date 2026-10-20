package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatDecimal

@Composable
internal fun DossierRestConventions(country: RestCountryResponse) {
    DossierRestSection("LOCAL CONVENTIONS") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DossierRestStat("Start of week", country.date?.startOfWeek, UiSymbol.Time, Modifier.weight(1f))
            DossierRestStat("Measurement", country.units?.measurementSystem, UiSymbol.Landscape, Modifier.weight(1f))
        }
        DossierRestChips(
            "Number format",
            listOfNotNull(
                country.numberFormat?.decimalSeparator?.let { "Decimal: $it" },
                country.numberFormat?.thousandsSeparator?.let { "Thousands: $it" },
                country.units?.temperatureScale?.let { "Temperature: $it" },
            ),
        )
        DossierRestNote("Postal format", country.postalCode?.format)
        DossierRestNote("Postal pattern", country.postalCode?.regex)
        DossierRestChips("Academic year begins", listOfNotNull(country.date?.academicYearStart?.let { "${it.month}/${it.day}" }))
        country.date?.fiscalYearStart?.let { year ->
            DossierRestChips(
                "Fiscal year begins",
                listOfNotNull(
                    year.government?.let { "Government ${it.month}/${it.day}" },
                    year.corporate?.let { "Corporate ${it.month}/${it.day} ${it.basis.orEmpty()}" },
                    year.personal?.let { "Personal ${it.month}/${it.day}" },
                ),
            )
        }
    }
}

@Composable
internal fun DossierRestFlagAndLinks(
    country: RestCountryResponse,
    onOpenLink: (String) -> Unit,
) {
    DossierRestSection("FLAG & WORLD LINKS") {
        DossierRestNote("Flag description", country.flag?.description)
        DossierRestLinkTile("View flag image", country.flag?.pngUrl, onOpenLink)
        DossierRestLinkTile("Official website", country.links?.official, onOpenLink)
        DossierRestLinkTile("Wikipedia", country.links?.wikipedia, onOpenLink)
        DossierRestLinkTile("OpenStreetMap", country.links?.openStreetMaps, onOpenLink)
        DossierRestLinkTile("Google Maps", country.links?.googleMaps, onOpenLink)
    }
}

@Composable
internal fun DossierRestGovernment(
    country: RestCountryResponse,
    onOpenLink: (String) -> Unit,
) {
    DossierRestSection("GOVERNMENT & ECONOMY") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DossierRestStat("Government", country.governmentType, UiSymbol.Economy, Modifier.weight(1f))
            val latestGini =
                country.economy
                    ?.giniCoefficient
                    ?.entries
                    ?.maxByOrNull { it.key }
            DossierRestStat("Gini · ${latestGini?.key.orEmpty()}", latestGini?.value?.let(::formatDecimal), UiSymbol.People, Modifier.weight(1f))
        }
        country.economy?.giniCoefficient?.takeIf { it.isNotEmpty() }?.let { gini ->
            DossierRestChips("Gini history", gini.entries.sortedByDescending { it.key }.map { "${it.key}: ${formatDecimal(it.value)}" })
        }
        country.memberships?.let { member ->
            DossierRestChips(
                "Memberships",
                listOfNotNull(
                    "UN".takeIf { member.un == true },
                    "EU".takeIf { member.eu == true },
                    "Eurozone".takeIf { member.eurozone == true },
                    "Schengen".takeIf { member.schengen == true },
                    "NATO".takeIf { member.nato == true },
                    "Commonwealth".takeIf { member.commonwealth == true },
                    "OECD".takeIf { member.oecd == true },
                    "G7".takeIf { member.g7 == true },
                    "G20".takeIf { member.g20 == true },
                    "BRICS".takeIf { member.brics == true },
                    "OPEC".takeIf { member.opec == true },
                    "African Union".takeIf { member.africanUnion == true },
                    "ASEAN".takeIf { member.asean == true },
                    "Arab League".takeIf { member.arabLeague == true },
                ),
            )
        }
        country.leaders.forEach { leader ->
            val role =
                listOfNotNull(
                    leader.title,
                    "Head of state".takeIf { leader.attributes?.headOfState == true },
                    "Head of government".takeIf { leader.attributes?.headOfGovernment == true },
                ).joinToString(" · ")
            DossierRestNote(role.ifBlank { "Leader" }, leader.name)
            DossierRestLinkTile("Leader biography", leader.links?.wikipedia, onOpenLink)
            leader.assets.forEach { asset -> DossierRestLinkTile(asset.type ?: "Asset", asset.url, onOpenLink) }
        }
    }
}

internal fun isSafeDossierUrl(url: String): Boolean {
    val host =
        url
            .substringAfter("https://", "")
            .substringBefore('/')
            .substringBefore('?')
            .substringBefore('#')
    return url.startsWith("https://", ignoreCase = true) &&
        host.isNotBlank() &&
        host.contains('.') &&
        host.none { it.isWhitespace() || it == '@' || it == ':' || it == '\\' } &&
        url.none { it == '\n' || it == '\r' }
}

internal fun LazyListScope.DossierRestTranslations(country: RestCountryResponse) {
    country.names?.native?.takeIf { it.isNotEmpty() }?.let { native ->
        item(key = "restNativeNames") {
            DossierRestSection("NATIVE NAMES") {
                native.entries.sortedBy { it.key }.forEach { (locale, name) ->
                    DossierRestNote(dossierLanguageName(locale), listOfNotNull(name.common, name.official).distinct().joinToString(" · "))
                }
            }
        }
    }
    country.names?.translations?.takeIf { it.isNotEmpty() }?.let { translations ->
        item(key = "restTranslations") {
            DossierRestSection("TRANSLATIONS") {
                translations.entries.sortedBy { it.key }.forEach { (locale, name) ->
                    DossierRestNote(dossierLanguageName(locale), listOfNotNull(name.common, name.official).distinct().joinToString(" · "))
                }
            }
        }
    }
    country.demonyms?.takeIf { it.isNotEmpty() }?.let { demonyms ->
        item(key = "restDemonyms") {
            DossierRestSection("DEMONYMS") {
                demonyms.entries.sortedBy { it.key }.forEach { (locale, demonym) ->
                    DossierRestChips(dossierLanguageName(locale), listOfNotNull(demonym.m, demonym.f))
                }
            }
        }
    }
}
