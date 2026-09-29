package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetaryTimeMachineSheet(
    epochMillis: Long,
    isLive: Boolean,
    onScrubStarted: () -> Unit,
    onEpochSelected: (Long) -> Unit,
    onResetLive: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedEpochMillis by remember { mutableStateOf(epochMillis) }
    LaunchedEffect(epochMillis, isLive) {
        if (isLive) selectedEpochMillis = epochMillis
    }

    val calendar = remember(selectedEpochMillis) { calendarFor(selectedEpochMillis) }
    val hour = calendar.minuteOfDay / 60
    val minute = calendar.minuteOfDay % 60
    val accent = Color(0xFFF59E0B)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF209111E),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFF59E0B), width = 36.dp, height = 4.dp) },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                        Column {
                            Text("PLANETARY TIME", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                            Text(
                                text = if (isLive) "LIVE REAL-TIME SIMULATION" else "SIMULATED PLANETARY TIME",
                                color = if (isLive) Color(0xFF34D399) else accent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x33F59E0B),
                                border = BorderStroke(1.dp, Color(0x66F59E0B)),
                                modifier = Modifier.clickable { onResetLive() },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(Icons.Outlined.Restore, contentDescription = "Reset to live time", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                    Text("RESET LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            MinimalistCloseButton(onClick = onClose)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Time Slider (0 - 24 hours)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("TIME OF DAY", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} UTC", color = Color(0xFFE0F2FE), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
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

                    // Day / Year Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("DAY OF YEAR", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("DAY ${calendar.dayOfYear} / ${calendar.daysInYear}  ·  YEAR ${calendar.year}", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
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
