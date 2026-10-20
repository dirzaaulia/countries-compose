package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
internal fun DossierRestTerritory(country: RestCountryResponse) {
    DossierRestSection("TERRITORY & EVERYDAY LIFE") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DossierRestStat("Driving side", country.cars?.drivingSide, UiSymbol.Location, Modifier.weight(1f))
            DossierRestStat("ISO status", country.classification?.isoStatus, UiSymbol.Info, Modifier.weight(1f))
        }
        DossierRestChips("Timezones", country.timezones)
        DossierRestChips("Calling codes", country.callingCodes)
        DossierRestChips("Web domains", country.tlds)
        DossierRestChips("Vehicle signs", country.cars?.signs.orEmpty())
        country.classification?.let { classification ->
            DossierRestChips(
                "Status",
                listOfNotNull(
                    "Sovereign".takeIf { classification.sovereign == true },
                    "UN member".takeIf { classification.unMember == true },
                    "UN observer".takeIf { classification.unObserver == true },
                    "Disputed".takeIf { classification.disputed == true },
                    "Dependency".takeIf { classification.dependency == true },
                    classification.dependencyType,
                ),
            )
        }
    }
}
