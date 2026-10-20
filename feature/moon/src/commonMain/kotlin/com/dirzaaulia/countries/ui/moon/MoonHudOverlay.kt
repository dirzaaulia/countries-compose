package com.dirzaaulia.countries.ui.moon

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.EclipseFeed
import com.dirzaaulia.countries.domain.astronomy.MoonInfo
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun MoonScrubberHud(
    displayedMoonInfo: MoonInfo,
    currentYear: Int,
    selectedDayOfYear: Int,
    todayDayOfYear: Int,
    totalDaysInYear: Int,
    eclipseFeed: EclipseFeed?,
    isEclipseFeedLoading: Boolean,
    onDaySelected: (Int) -> Unit,
    onShowDetailSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val todayDate = remember(currentYear, todayDayOfYear) { formatIsoDate(currentYear, todayDayOfYear) }
    val nextEclipse = remember(eclipseFeed, todayDate) { eclipseFeed?.events?.firstOrNull { it.date >= todayDate } }
    val selectedDate = remember(currentYear, selectedDayOfYear) { formatIsoDate(currentYear, selectedDayOfYear) }
    val selectedEclipse =
        remember(eclipseFeed, selectedDate) {
            eclipseFeed?.events?.firstOrNull { it.date == selectedDate }
        }
    val yearlyEclipses =
        remember(eclipseFeed, currentYear) {
            eclipseFeed?.events?.filter { it.date.startsWith("$currentYear-") }.orEmpty()
        }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xEE0B1220),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        shadowElevation = 16.dp,
        modifier =
            modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .navigationBarsPadding()
                .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            MoonHudHeader(
                displayedMoonInfo = displayedMoonInfo,
                currentYear = currentYear,
                selectedDayOfYear = selectedDayOfYear,
                todayDayOfYear = todayDayOfYear,
                totalDaysInYear = totalDaysInYear,
                onDaySelected = onDaySelected,
                onShowDetailSheet = onShowDetailSheet,
            )

            if (!isEclipseFeedLoading) {
                Spacer(Modifier.height(8.dp))
                MoonEclipseBanner(selectedEclipse = selectedEclipse, nextEclipse = nextEclipse)
            }

            Slider(
                value = selectedDayOfYear.toFloat(),
                onValueChange = { onDaySelected(it.toInt().coerceIn(1, totalDaysInYear)) },
                valueRange = 1f..totalDaysInYear.toFloat(),
                colors =
                    SliderDefaults.colors(
                        thumbColor = Color(0xFFFFD54F),
                        activeTrackColor = Color(0xFFFFD54F),
                        inactiveTrackColor = Color(0x3338BDF8),
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(30.dp),
            )

            if (yearlyEclipses.isNotEmpty()) {
                MoonYearlyEclipseMarkers(
                    yearlyEclipses = yearlyEclipses,
                    currentYear = currentYear,
                    totalDaysInYear = totalDaysInYear,
                )
            }

            MoonMonthChips(
                currentYear = currentYear,
                selectedDayOfYear = selectedDayOfYear,
                yearlyEclipses = yearlyEclipses,
                onDaySelected = onDaySelected,
            )
        }
    }
}

@Composable
private fun MoonHudHeader(
    displayedMoonInfo: MoonInfo,
    currentYear: Int,
    selectedDayOfYear: Int,
    todayDayOfYear: Int,
    totalDaysInYear: Int,
    onDaySelected: (Int) -> Unit,
    onShowDetailSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.clickable(onClick = onShowDetailSheet),
        ) {
            LunarPhaseIcon(
                phaseAngle = displayedMoonInfo.phaseAngle,
                contentDescription = displayedMoonInfo.phaseName,
                modifier = Modifier.size(28.dp),
            )
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val illuminatedPct = (displayedMoonInfo.illuminatedFraction * 100).toInt()
                    Text(
                        text = "${displayedMoonInfo.phaseName} • $illuminatedPct%",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    SemanticIcon(
                        symbol = UiSymbol.Info,
                        contentDescription = "Phase information",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp),
                    )
                }
                Text(
                    text = formatDayAndMonth(currentYear, selectedDayOfYear),
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        MoonDayControls(
            selectedDayOfYear = selectedDayOfYear,
            todayDayOfYear = todayDayOfYear,
            totalDaysInYear = totalDaysInYear,
            onDaySelected = onDaySelected,
        )
    }
}
