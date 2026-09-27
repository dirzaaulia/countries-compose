package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.data.restcountries.areaSqKm
import com.dirzaaulia.countries.data.restcountries.areaSqMiles
import com.dirzaaulia.countries.data.restcountries.capitalCoordinates
import com.dirzaaulia.countries.data.restcountries.officialName
import com.dirzaaulia.countries.data.restcountries.responses.RestCountryResponse
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.InfoCard
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatArea
import com.dirzaaulia.countries.util.formatCoordinates
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber
import com.dirzaaulia.countries.util.formatPopulation

@Composable
internal fun DossierNameSection(
    country: Country,
    rest: RestCountryResponse?,
) {
    val official = rest?.officialName?.ifEmpty { null } ?: country.officialName.ifEmpty { country.formalName }
    val native =
        rest
            ?.names
            ?.native
            ?.values
            ?.firstNotNullOfOrNull { it.common } ?: country.nativeName
    if (official.isEmpty() || official == country.name) return
    Spacer(Modifier.height(6.dp))
    Text(
        text = if (native.isNotEmpty() && native != official) "$official • $native" else official,
        color = Color(0xFF94A3B8),
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )
}

@Composable
fun DossierLoadingStatus(isVisible: Boolean) {
    if (!isVisible) return
    Spacer(Modifier.height(10.dp))
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0x1F0F172A), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = Color(0xFF38BDF8),
        )
        Text(
            text = "Loading telemetry data...",
            color = Color(0xFF7DD3FC),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
internal fun DossierGeographySection(
    country: Country,
    rest: RestCountryResponse?,
    displayCapital: String,
    localSolarTime: String,
) {
    val population = rest?.population?.takeIf { it > 0L } ?: country.population
    val area = rest?.areaSqKm?.takeIf { it > 0.0 } ?: country.areaSqKm
    val landlocked = rest?.landlocked ?: country.landlocked
    val restAreaSubvalue = rest?.areaSqMiles?.let { "${formatNumber(it)} mi²" }
    val capitalCoordinates =
        rest?.capitalCoordinates?.let { point ->
            if (point.lat != null && point.lng != null) LatLng(point.lat, point.lng) else null
        } ?: country.capitalLatLng

    Spacer(Modifier.height(12.dp))
    DossierSectionTitle("GEOGRAPHY & DEMOGRAPHICS")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoCard(
            symbol = UiSymbol.People,
            title = "POPULATION",
            value = formatPopulation(population),
            subValue = if (population > 0) "${formatNumber(population.toDouble())} people" else null,
            modifier = Modifier.weight(1f),
        )
        InfoCard(
            symbol = UiSymbol.Landscape,
            title = "AREA",
            value = formatArea(area),
            subValue =
                listOfNotNull(restAreaSubvalue, landlocked?.let { if (it) "Landlocked" else "Coastal" })
                    .joinToString(" · ")
                    .ifBlank { null },
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoCard(
            symbol = UiSymbol.Capital,
            title = "CAPITAL CITY",
            value = displayCapital,
            subValue = capitalCoordinates?.let(::formatCoordinates),
            modifier = Modifier.weight(1f),
        )
        InfoCard(
            symbol = UiSymbol.Location,
            title = "CENTER COORDINATES",
            value = formatCoordinates(country.center),
            subValue = "Solar: ~$localSolarTime",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun DossierWorldBankSection(
    liveDetails: LiveCountryDetails?,
    onOpenWorldBank: (() -> Unit)?,
) {
    val live = liveDetails?.takeIf { it.isLiveWorldBankLoaded } ?: return
    Spacer(Modifier.height(14.dp))
    DossierSectionTitle("WORLD BANK ECONOMIC DATA")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        live.gdpPerCapita?.let {
            InfoCard(
                symbol = UiSymbol.Economy,
                title = "GDP PER CAPITA",
                value = "$${formatNumber(it)} USD",
                modifier = Modifier.weight(1f),
            )
        }
        live.lifeExpectancy?.let {
            InfoCard(
                symbol = UiSymbol.LifeExpectancy,
                title = "LIFE EXPECTANCY",
                value = "${formatDecimal(it)} Years",
                modifier = Modifier.weight(1f),
            )
        }
    }
    if (onOpenWorldBank != null) {
        Spacer(Modifier.height(8.dp))
        DossierLinkButton(
            text = "View Macroeconomic Analysis (5-Year Trends) ↗",
            color = Color(0xFF10B981),
            textColor = Color(0xFFA7F3D0),
            onClick = onOpenWorldBank,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DossierCultureSection(
    currencies: List<String>,
    languages: List<String>,
    onOpenAdministrativeDivisions: (() -> Unit)?,
) {
    Spacer(Modifier.height(14.dp))
    DossierSectionTitle("CULTURE & ADMINISTRATION")
    if (currencies.isNotEmpty()) {
        DossierPillGroup(
            label = "Official Currencies:",
            values = currencies,
            symbol = UiSymbol.Currency,
            background = Color(0x2210B981),
            border = Color(0x4410B981),
            text = Color(0xFFA7F3D0),
        )
    }
    if (languages.isNotEmpty()) {
        DossierPillGroup(
            label = "Official Languages:",
            values = languages,
            symbol = UiSymbol.Language,
            background = Color(0x228B5CF6),
            border = Color(0x448B5CF6),
            text = Color(0xFFDDD6FE),
        )
    }
    if (onOpenAdministrativeDivisions != null) {
        Spacer(Modifier.height(4.dp))
        DossierLinkButton(
            text = "Explore Sub-National Divisions (ADM1 & ADM2) ↗",
            color = Color(0xFF8B5CF6),
            textColor = Color(0xFFDDD6FE),
            onClick = onOpenAdministrativeDivisions,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DossierPillGroup(
    label: String,
    values: List<String>,
    symbol: UiSymbol,
    background: Color,
    border: Color,
    text: Color,
) {
    Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp)
    Spacer(Modifier.height(4.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        values.forEach { value ->
            ChipPill(text = value, symbol = symbol, backgroundColor = background, borderColor = border, textColor = text)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun DossierActionButtons(
    onClose: () -> Unit,
    onNextCountry: () -> Unit,
) {
    Spacer(Modifier.height(18.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onClose,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0x44EF4444)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Text("Dismiss", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        Button(
            onClick = onNextCountry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7), contentColor = Color.White),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Text("Next Country", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun DossierSectionTitle(text: String) {
    Text(text, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun DossierLinkButton(
    text: String,
    color: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.33f)),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
