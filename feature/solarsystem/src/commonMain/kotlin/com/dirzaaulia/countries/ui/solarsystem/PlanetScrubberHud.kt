package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.PlanetOrbitalData

@Composable
internal fun PlanetScrubberHud(
    planet: PlanetOrbitalData,
    isLive: Boolean,
    minuteOfDay: Int,
    onMinuteChanged: (Int) -> Unit,
    onResetLive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = Color(planet.colorHex)

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xF209111E),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        contentColor = Color.White,
        shadowElevation = 14.dp,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlanetScrubberHeader(planet, isLive, accent, onResetLive)
            PlanetScrubberTimeRow(minuteOfDay)
            PlanetScrubberSlider(minuteOfDay, accent, onMinuteChanged)
            PlanetTelemetryRow(planet)
        }
    }
}

@Composable
private fun PlanetScrubberHeader(
    planet: PlanetOrbitalData,
    isLive: Boolean,
    accent: Color,
    onResetLive: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "${planet.name.uppercase()} ${planet.symbolTag}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            Text(
                text = if (isLive) "LIVE REAL-TIME ILLUMINATION" else "SIMULATED SOLAR TERMINATOR",
                color = if (isLive) Color(0xFF34D399) else Color(0xFFFBBF24),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
            )
        }
        if (!isLive) {
            ResetLivePill(onResetLive, accent)
        }
    }
}

@Composable
private fun ResetLivePill(
    onResetLive: () -> Unit,
    accent: Color,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = accent.copy(alpha = 0.20f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.60f)),
        modifier = Modifier.clickable { onResetLive() },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Restore,
                contentDescription = "Reset live solar time",
                tint = accent,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = "RESET LIVE",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun PlanetScrubberTimeRow(minuteOfDay: Int) {
    val hour = (minuteOfDay / 60) % 24
    val minute = minuteOfDay % 60
    val hourStr = hour.toString().padStart(2, '0')
    val minStr = minute.toString().padStart(2, '0')
    val solarAngleDeg = ((minuteOfDay.toFloat() / 1440f) * 360f).toInt()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "SOLAR TERMINATOR",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Text(
            text = "$hourStr:$minStr UTC  ·  SOLAR $solarAngleDeg°",
            color = Color(0xFFE0F2FE),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PlanetScrubberSlider(
    minuteOfDay: Int,
    accent: Color,
    onMinuteChanged: (Int) -> Unit,
) {
    Slider(
        value = minuteOfDay.toFloat(),
        onValueChange = { onMinuteChanged(it.toInt().coerceIn(0, 1439)) },
        valueRange = 0f..1439f,
        colors =
            SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = accent.copy(alpha = 0.25f),
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(28.dp),
    )
}

@Composable
private fun PlanetTelemetryRow(planet: PlanetOrbitalData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Distance: ${planet.meanDistanceAu} AU",
            fontSize = 11.sp,
            color = Color(0xFF38BDF8),
        )
        Text(
            text = "Period: ${planet.orbitalPeriodDays} d",
            fontSize = 11.sp,
            color = Color(0xFF38BDF8),
        )
        Text(
            text = "Speed: ${planet.orbitalSpeedKms} km/s",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
        )
    }
}
