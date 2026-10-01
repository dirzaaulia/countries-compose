package com.dirzaaulia.countries.ui.satellite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.ui.theme.extendedColors

@Composable
fun SatelliteSelectorPill(
    fleet: List<SatelliteTelemetry>,
    selectedSatellite: SatelliteTelemetry?,
    onSelectSatellite: (SatelliteTelemetry) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (fleet.isEmpty()) return

    Surface(
        shape = RoundedCornerShape(19.dp),
        color = MaterialTheme.extendedColors.overlayBackground,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        modifier = modifier.height(38.dp),
    ) {
        Row(
            modifier = Modifier.padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            fleet.forEach { sat ->
                val isSelected = selectedSatellite?.id == sat.id
                val tabColor =
                    when (sat.id) {
                        "ISS" -> MaterialTheme.colorScheme.primary
                        "CSS" -> MaterialTheme.extendedColors.categoryRobotic
                        "HST" -> Color(0xFFA855F7)
                        else -> MaterialTheme.extendedColors.statusActive
                    }

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(17.dp))
                            .background(if (isSelected) tabColor.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { onSelectSatellite(sat) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = sat.acronym,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}
