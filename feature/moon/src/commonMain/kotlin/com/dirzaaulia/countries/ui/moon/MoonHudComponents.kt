package com.dirzaaulia.countries.ui.moon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.EclipseEvent

@Composable
internal fun MoonEclipseBanner(
    selectedEclipse: EclipseEvent?,
    nextEclipse: EclipseEvent?,
    modifier: Modifier = Modifier,
) {
    val displayEclipse = selectedEclipse ?: nextEclipse
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x1822D3EE),
        border = BorderStroke(1.dp, Color(0x5538BDF8)),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (selectedEclipse != null) {
                EclipseAppearancePreview(selectedEclipse, modifier = Modifier.size(34.dp))
            }
            Column {
                val headerText =
                    when {
                        displayEclipse == null -> "[ECLIPSE] LIVE FEED UNAVAILABLE"
                        selectedEclipse != null -> "[ECLIPSE DAY] ${displayEclipse.type} ${displayEclipse.kind}"
                        else -> "[NEXT ECLIPSE] ${displayEclipse.type} ${displayEclipse.kind}"
                    }
                Text(
                    text = headerText,
                    color = Color(0xFF67E8F9),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (displayEclipse != null) {
                    Text(
                        text = "${displayEclipse.date} | ${displayEclipse.visibility}",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun MoonYearlyEclipseMarkers(
    yearlyEclipses: List<EclipseEvent>,
    currentYear: Int,
    totalDaysInYear: Int,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(5.dp),
    ) {
        yearlyEclipses.forEach { event ->
            val eventDay = event.date.substringAfterLast('-').toIntOrNull()
            val eventMonth = event.date.substring(5, 7).toIntOrNull()
            if (eventDay != null && eventMonth != null) {
                val day = getDaysInMonths(currentYear).take(eventMonth - 1).sum() + eventDay
                val x = size.width * (day - 1) / (totalDaysInYear - 1)
                drawCircle(
                    color = if (event.kind == "solar") Color(0xFFFFD54F) else Color(0xFFF97316),
                    radius = 2.4f,
                    center = Offset(x, size.height / 2f),
                )
            }
        }
    }
}

@Composable
internal fun MoonMonthChips(
    currentYear: Int,
    selectedDayOfYear: Int,
    yearlyEclipses: List<EclipseEvent>,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthNames =
        listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec",
        )
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        monthNames.forEachIndexed { mIdx, mName ->
            val isCurrentMonth = getMonthFromDayOfYear(currentYear, selectedDayOfYear) == mIdx
            val monthEclipse =
                yearlyEclipses.firstOrNull {
                    it.date.substring(5, 7) == (mIdx + 1).toString().padStart(2, '0')
                }
            MoonMonthChipItem(
                monthName = mName,
                isCurrentMonth = isCurrentMonth,
                monthEclipse = monthEclipse,
                onClick = { onDaySelected(getFirstDayOfMonth(currentYear, mIdx)) },
            )
        }
    }
}

@Composable
private fun MoonMonthChipItem(
    monthName: String,
    isCurrentMonth: Boolean,
    monthEclipse: EclipseEvent?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isCurrentMonth) Color(0xFFFFD54F).copy(alpha = 0.25f) else Color(0x15FFFFFF),
        border = if (isCurrentMonth) BorderStroke(1.dp, Color(0xFFFFD54F)) else null,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
        ) {
            Text(
                text = monthName,
                color = if (isCurrentMonth) Color(0xFFFFD54F) else Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
            )
            if (monthEclipse != null) {
                Canvas(modifier = Modifier.size(4.dp)) {
                    val color = if (monthEclipse.kind == "solar") Color(0xFFFFD54F) else Color(0xFFF97316)
                    drawCircle(color = color)
                }
            }
        }
    }
}
