package com.dirzaaulia.countries.ui.comparison

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.comparison.buildComparisonData
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.components.CountryFlagIcon
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatArea
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber
import com.dirzaaulia.countries.util.formatPopulation

@Composable
fun ComparisonHeaderSection(
    countryA: Country?,
    countryB: Country?,
    onSwap: () -> Unit,
    onSelectSlot: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ComparisonSlotTile(
            country = countryA,
            slotNumber = 1,
            label = "Country A",
            accentColor = Color(0xFF38BDF8),
            onClick = { onSelectSlot(1) },
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onSwap,
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            SemanticIcon(
                symbol = UiSymbol.Swap,
                contentDescription = "Swap countries",
                tint = Color(0xFF38BDF8),
            )
        }
        ComparisonSlotTile(
            country = countryB,
            slotNumber = 2,
            label = "Country B",
            accentColor = Color(0xFFF59E0B),
            onClick = { onSelectSlot(2) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ComparisonSlotTile(
    country: Country?,
    slotNumber: Int,
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x221E293B),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier.clickable { onClick() },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            if (country != null) {
                CountryFlagIcon(country = country)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = country.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = country.capital.ifEmpty { "N/A" },
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = "+ Select Country $slotNumber",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun ComparisonAreaCard(
    countryA: Country,
    countryB: Country,
) {
    val comp = buildComparisonData(countryA, countryB, null, null)
    val ratioText =
        when {
            comp.areaRatio >= 1.0 -> "${countryA.name} is ${formatDecimal(comp.areaRatio)}× larger"
            comp.areaRatio > 0.0 -> "${countryA.name} is ${formatDecimal(comp.areaRatio * 100.0)}% of ${countryB.name}"
            else -> "N/A"
        }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SemanticIcon(UiSymbol.Landscape, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(Modifier.width(8.dp))
                Text("TRUE LAND AREA", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatArea(countryA.areaSqKm), color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(formatArea(countryB.areaSqKm), color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            ComparisonBarIndicator(valA = countryA.areaSqKm, valB = countryB.areaSqKm)
            Spacer(Modifier.height(6.dp))
            Text(ratioText, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ComparisonPopulationCard(
    countryA: Country,
    countryB: Country,
) {
    val densityA = if (countryA.areaSqKm > 0) countryA.population / countryA.areaSqKm else 0.0
    val densityB = if (countryB.areaSqKm > 0) countryB.population / countryB.areaSqKm else 0.0

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SemanticIcon(UiSymbol.People, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(Modifier.width(8.dp))
                Text("POPULATION & DENSITY", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(formatPopulation(countryA.population), color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${formatDecimal(densityA)} /km²", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatPopulation(countryB.population), color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${formatDecimal(densityB)} /km²", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(6.dp))
            ComparisonBarIndicator(valA = countryA.population.toDouble(), valB = countryB.population.toDouble())
        }
    }
}

@Composable
fun ComparisonBarIndicator(
    valA: Double,
    valB: Double,
) {
    val total = (valA + valB).coerceAtLeast(1.0)
    val weightA = ((valA / total) * 100.0).coerceIn(5.0, 95.0).toFloat() / 100f
    val weightB = 1f - weightA

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1E293B)),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(weightA)
                    .height(8.dp)
                    .background(Color(0xFF38BDF8)),
        )
        Box(
            modifier =
                Modifier
                    .weight(weightB)
                    .height(8.dp)
                    .background(Color(0xFFF59E0B)),
        )
    }
}
