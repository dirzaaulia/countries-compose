package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

private const val DAY_MILLIS = 86_400_000L

private data class TimeMachineCalendar(
    val year: Int,
    val dayOfYear: Int,
    val minuteOfDay: Int,
    val daysInYear: Int,
    val yearStartMillis: Long,
)

private fun isLeapYear(year: Int) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

private fun calendarFor(epochMillis: Long): TimeMachineCalendar {
    var remainingDays = epochMillis.floorDiv(DAY_MILLIS)
    var year = 1970
    while (remainingDays >= if (isLeapYear(year)) 366 else 365) {
        remainingDays -= if (isLeapYear(year)) 366 else 365
        year++
    }
    val daysInYear = if (isLeapYear(year)) 366 else 365
    val minuteOfDay = ((epochMillis % DAY_MILLIS + DAY_MILLIS) % DAY_MILLIS / 60_000L).toInt()
    return TimeMachineCalendar(
        year = year,
        dayOfYear = remainingDays.toInt() + 1,
        minuteOfDay = minuteOfDay,
        daysInYear = daysInYear,
        yearStartMillis = epochMillis - remainingDays * DAY_MILLIS - minuteOfDay * 60_000L,
    )
}

@Composable
fun PlanetaryTimeMachineHud(
    epochMillis: Long,
    isLive: Boolean,
    onScrubStarted: () -> Unit,
    onEpochSelected: (Long) -> Unit,
    onResetLive: () -> Unit,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var selectedEpochMillis by remember { mutableStateOf(epochMillis) }
    LaunchedEffect(epochMillis, isLive) {
        if (isLive) selectedEpochMillis = epochMillis
    }

    val calendar = remember(selectedEpochMillis) { calendarFor(selectedEpochMillis) }
    val hour = calendar.minuteOfDay / 60
    val minute = calendar.minuteOfDay % 60
    val accent = Color(0xFFF59E0B)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xE80B1220),
        border = BorderStroke(1.dp, Color(0x33F59E0B)),
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Planetary Time", color = Color(0xFFF8FAFC), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isLive) "LIVE UTC" else "SIMULATED UTC",
                        color = if (isLive) Color(0xFF34D399) else accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                    )
                }
                TextButton(onClick = onResetLive) {
                    Icon(Icons.Outlined.Restore, contentDescription = "Reset to live time", tint = Color(0xFF94A3B8))
                    Text("LIVE", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                if (onClose != null) {
                    MinimalistCloseButton(onClick = onClose)
                }
            }

            Text("${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} UTC", color = Color(0xFFE0F2FE), fontSize = 13.sp)
            Slider(
                value = calendar.minuteOfDay.toFloat(),
                onValueChange = { minuteOfDay ->
                    onScrubStarted()
                    val selectedEpoch =
                        calendar.yearStartMillis +
                            (calendar.dayOfYear - 1) * DAY_MILLIS + minuteOfDay.toLong() * 60_000L
                    selectedEpochMillis = selectedEpoch
                    onEpochSelected(selectedEpoch)
                },
                valueRange = 0f..1439f,
                colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent),
            )

            Text("DAY ${calendar.dayOfYear} / ${calendar.daysInYear}  |  ${calendar.year}", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Slider(
                value = calendar.dayOfYear.toFloat(),
                onValueChange = { dayOfYear ->
                    onScrubStarted()
                    val selectedEpoch =
                        calendar.yearStartMillis +
                            (dayOfYear.toLong() - 1L) * DAY_MILLIS + calendar.minuteOfDay * 60_000L
                    selectedEpochMillis = selectedEpoch
                    onEpochSelected(selectedEpoch)
                },
                valueRange = 1f..calendar.daysInYear.toFloat(),
                steps = calendar.daysInYear - 2,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8)),
            )
        }
    }
}
