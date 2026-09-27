package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse
import com.dirzaaulia.countries.ui.components.UiSymbol

internal fun LazyListScope.DossierRestCountrySections(
    country: RestCountryResponse,
    onOpenLink: (String) -> Unit,
) {
    item(key = "restIdentity") {
        DossierRestSection("IDENTITY & LOCATION") {
            DossierRestNote("Official name", country.names?.official)
            DossierRestChips("Also known as", country.names?.alternates.orEmpty())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DossierRestStat("Region", country.region, UiSymbol.Earth, Modifier.weight(1f))
                DossierRestStat("Subregion", country.subregion, UiSymbol.Location, Modifier.weight(1f))
            }
            DossierRestChips("Continents", country.continents)
        }
    }
    item(key = "restCodes") {
        DossierRestSection("GLOBAL IDENTIFIERS") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DossierRestStat("ISO 2", country.codes?.alpha2, UiSymbol.Earth, Modifier.weight(1f))
                DossierRestStat("ISO 3", country.codes?.alpha3, UiSymbol.Earth, Modifier.weight(1f))
            }
            country.codes?.let { codes ->
                DossierRestChips(
                    "Other codes",
                    listOfNotNull(
                        codes.ccn3?.let { "NUM $it" },
                        codes.fips?.let { "FIPS $it" },
                        codes.gec?.let { "GEC $it" },
                        codes.cioc?.let { "IOC $it" },
                        codes.fifa?.let { "FIFA $it" },
                    ),
                )
            }
            DossierRestChips("Parent", listOfNotNull(country.parent?.alpha2, country.parent?.alpha3))
        }
    }
    item(key = "restFormats") { DossierRestConventions(country) }
    item(key = "restLinks") { DossierRestFlagAndLinks(country, onOpenLink) }
    DossierRestTranslations(country)
}
