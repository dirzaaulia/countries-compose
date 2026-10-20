package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.AtmosphericGas
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryAtmosphereProfile

@Composable
fun PlanetaryAtmosphereSection(
    atmosphere: PlanetaryAtmosphereProfile,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AtmosphereHeader(atmosphere)
        if (atmosphere.gases.isNotEmpty()) {
            AtmosphereSegmentedBar(atmosphere.gases)
            AtmosphereGasLegend(atmosphere.gases)
        }
        AtmospherePressureBadge(atmosphere)
    }
}

@Composable
private fun AtmosphereHeader(atmosphere: PlanetaryAtmosphereProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "ATMOSPHERIC COMPOSITION",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        Text(
            text = atmosphere.typeName,
            color = if (atmosphere.hasAtmosphere) Color(0xFF38BDF8) else Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AtmosphereSegmentedBar(gases: List<AtmosphericGas>) {
    val totalPct = gases.sumOf { it.percentage }.coerceAtLeast(1.0).toFloat()
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFF1E293B)),
    ) {
        gases.forEach { gas ->
            val weight = (gas.percentage.toFloat() / totalPct).coerceAtLeast(0.005f)
            Box(
                modifier =
                    Modifier
                        .weight(weight)
                        .height(14.dp)
                        .background(Color(gas.colorHex)),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AtmosphereGasLegend(gases: List<AtmosphericGas>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        gases.forEach { gas ->
            GasChip(gas)
        }
    }
}

@Composable
private fun GasChip(gas: AtmosphericGas) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(gas.colorHex)),
        )
        Text(
            text = "${gas.formula}: ${gas.percentage}%",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "(${gas.gasName})",
            color = Color(0xFF64748B),
            fontSize = 9.sp,
        )
    }
}

@Composable
private fun AtmospherePressureBadge(atmosphere: PlanetaryAtmosphereProfile) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x331E293B))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "SURFACE PRESSURE",
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = atmosphere.pressureDescription,
            color = Color(0xFFCBD5E1),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
