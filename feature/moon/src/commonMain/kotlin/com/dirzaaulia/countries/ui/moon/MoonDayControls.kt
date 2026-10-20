package com.dirzaaulia.countries.ui.moon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun MoonDayControls(
    selectedDayOfYear: Int,
    todayDayOfYear: Int,
    totalDaysInYear: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier,
    ) {
        MoonDayButton(
            icon = Icons.Outlined.ChevronLeft,
            description = "Previous day",
            onClick = { if (selectedDayOfYear > 1) onDaySelected(selectedDayOfYear - 1) },
        )
        MoonDayButton(
            icon = Icons.Outlined.ChevronRight,
            description = "Next day",
            onClick = { if (selectedDayOfYear < totalDaysInYear) onDaySelected(selectedDayOfYear + 1) },
        )
        if (selectedDayOfYear != todayDayOfYear) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x3338BDF8),
                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                modifier = Modifier.clickable { onDaySelected(todayDayOfYear) },
            ) {
                Text(
                    text = "TODAY",
                    color = Color(0xFF38BDF8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                )
            }
        }
    }
}

@Composable
private fun MoonDayButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0x2238BDF8))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(20.dp),
        )
    }
}
