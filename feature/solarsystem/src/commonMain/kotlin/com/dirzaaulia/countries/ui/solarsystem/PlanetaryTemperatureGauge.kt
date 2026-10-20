package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryPhysicalSpecs

@Composable
fun PlanetaryTemperatureSection(
    specs: PlanetaryPhysicalSpecs,
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
        TemperatureHeader(specs.meanTempC)
        TemperatureGradientBar()
        TemperatureMetricsRow(specs.minTempC, specs.meanTempC, specs.maxTempC)
    }
}

@Composable
private fun TemperatureHeader(meanTempC: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "SURFACE TEMPERATURE EXTREMES",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        Text(
            text = "Mean: ${formatTemp(meanTempC)}",
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TemperatureGradientBar() {
    val tempBrush =
        Brush.horizontalGradient(
            colors =
                listOf(
                    Color(0xFF0284C7), // Cryo
                    Color(0xFF38BDF8), // Cool
                    Color(0xFF10B981), // Temperate
                    Color(0xFFF59E0B), // Warm
                    Color(0xFFEF4444), // Extreme Hot
                ),
        )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(tempBrush),
    )
}

@Composable
private fun TemperatureMetricsRow(
    minC: Int,
    meanC: Int,
    maxC: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TempReadingColumn(label = "MINIMUM", tempC = minC, color = Color(0xFF38BDF8), align = Alignment.Start)
        TempReadingColumn(label = "MEAN AVERAGE", tempC = meanC, color = Color(0xFFF1F5F9), align = Alignment.CenterHorizontally)
        TempReadingColumn(label = "MAXIMUM", tempC = maxC, color = Color(0xFFEF4444), align = Alignment.End)
    }
}

@Composable
private fun TempReadingColumn(
    label: String,
    tempC: Int,
    color: Color,
    align: Alignment.Horizontal,
) {
    Column(horizontalAlignment = align) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(1.dp))
        Text(
            text = formatTemp(tempC),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${(tempC * 9 / 5) + 32} F",
            color = Color(0xFF64748B),
            fontSize = 9.sp,
        )
    }
}

private fun formatTemp(tempC: Int): String {
    val sign = if (tempC > 0) "+" else ""
    return "$sign$tempC C"
}
