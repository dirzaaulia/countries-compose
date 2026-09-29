package com.dirzaaulia.countries.ui.comparison

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.comparison.buildComparisonData
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.components.uiSymbolFor
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber
import kotlin.math.abs

@Composable
fun ComparisonMacroeconomicsCard(
    countryA: Country,
    countryB: Country,
    detailsA: LiveCountryDetails?,
    detailsB: LiveCountryDetails?,
) {
    val gdpA = detailsA?.gdpPerCapita ?: ((countryA.gdpMillions * 1_000_000.0) / countryA.population.coerceAtLeast(1L))
    val gdpB = detailsB?.gdpPerCapita ?: ((countryB.gdpMillions * 1_000_000.0) / countryB.population.coerceAtLeast(1L))
    val infA = detailsA?.inflationRate
    val infB = detailsB?.inflationRate
    val unempA = detailsA?.unemploymentRate
    val unempB = detailsB?.unemploymentRate

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SemanticIcon(UiSymbol.Economy, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(Modifier.width(8.dp))
                Text("MACROECONOMICS (WORLD BANK)", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            MacroRow(
                label = "GDP per Capita",
                valA = if (gdpA > 0) "$${formatNumber(gdpA)}" else "N/A",
                valB = if (gdpB > 0) "$${formatNumber(gdpB)}" else "N/A",
            )
            Spacer(Modifier.height(6.dp))
            MacroRow(
                label = "Inflation Rate",
                valA = infA?.let { "${formatDecimal(it)}%" } ?: "N/A",
                valB = infB?.let { "${formatDecimal(it)}%" } ?: "N/A",
            )
            Spacer(Modifier.height(6.dp))
            MacroRow(
                label = "Unemployment",
                valA = unempA?.let { "${formatDecimal(it)}%" } ?: "N/A",
                valB = unempB?.let { "${formatDecimal(it)}%" } ?: "N/A",
            )
        }
    }
}

@Composable
private fun MacroRow(
    label: String,
    valA: String,
    valB: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(valA, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(valB, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
fun ComparisonWeatherTimeCard(
    countryA: Country,
    countryB: Country,
    detailsA: LiveCountryDetails?,
    detailsB: LiveCountryDetails?,
) {
    val comp = buildComparisonData(countryA, countryB, detailsA, detailsB)
    val diff = comp.timeDifferenceHours
    val diffText =
        when {
            abs(diff) < 0.1 -> "Same timezone"
            diff > 0 -> "+${formatDecimal(diff)} hrs apart"
            else -> "${formatDecimal(diff)} hrs apart"
        }

    val tempA = detailsA?.weatherTempC?.let { "${formatDecimal(it)}°C" } ?: "N/A"
    val tempB = detailsB?.weatherTempC?.let { "${formatDecimal(it)}°C" } ?: "N/A"
    val iconA = detailsA?.weatherIcon?.let { uiSymbolFor(it) } ?: UiSymbol.Clear
    val iconB = detailsB?.weatherIcon?.let { uiSymbolFor(it) } ?: UiSymbol.Clear

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SemanticIcon(UiSymbol.Time, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(Modifier.width(8.dp))
                Text("CAPITAL WEATHER & TIME", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    SemanticIcon(iconA, contentDescription = null, tint = Color(0xFF38BDF8))
                    Spacer(Modifier.width(4.dp))
                    Text(tempA, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text(
                    diffText,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(tempB, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    SemanticIcon(iconB, contentDescription = null, tint = Color(0xFFF59E0B))
                }
            }
        }
    }
}
