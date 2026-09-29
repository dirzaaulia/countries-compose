package com.dirzaaulia.countries.ui.satellite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import com.dirzaaulia.countries.util.formatNumber
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatelliteTelemetrySheet(
    satellite: SatelliteTelemetry,
    nextPassOverhead: String?,
    onClose: () -> Unit,
    onCenterView: () -> Unit,
) {
    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(topStart = Spacing.large, topEnd = Spacing.large),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large)
                .padding(bottom = Spacing.extraLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            HeaderSection(satellite, onClose)
            TelemetryGrid(satellite)
            if (nextPassOverhead != null) {
                NextPassCard(nextPassOverhead)
            }
            OperatorRow(satellite)
            Spacer(modifier = Modifier.height(Spacing.small))
        }
    }
}

@Composable
private fun HeaderSection(
    satellite: SatelliteTelemetry,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "${satellite.acronym} • NORAD #${satellite.noradId}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = satellite.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun TelemetryGrid(satellite: SatelliteTelemetry) {
    val machSpeed = (satellite.velocityKmH / 1234.8).roundToInt()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(Spacing.small))
                .background(MaterialTheme.extendedColors.telemetryBackground)
                .padding(Spacing.medium),
        ) {
            Text("ALTITUDE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${formatNumber(satellite.altitudeKm)} km", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(if (satellite.isDaylight) "Sunlit Orbit" else "In Earth Shadow", color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(Spacing.small))
                .background(MaterialTheme.extendedColors.telemetryBackground)
                .padding(Spacing.medium),
        ) {
            Text("VELOCITY", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${formatNumber(satellite.velocityKmH)} km/h", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Mach $machSpeed", color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
        }
    }
}

@Composable
private fun NextPassCard(nextPassFormatted: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.small))
            .background(MaterialTheme.extendedColors.telemetryBackground)
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall),
    ) {
        Text("NEXT PASS OVERHEAD (SELECTED COUNTRY)", color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(nextPassFormatted, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OperatorRow(satellite: SatelliteTelemetry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.small))
            .background(MaterialTheme.extendedColors.telemetryBackground)
            .padding(Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("OPERATOR & LAUNCH", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${satellite.operator} (${satellite.launchYear})", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Box(
                modifier = Modifier
                    .size(Spacing.small)
                    .background(MaterialTheme.extendedColors.statusActive, CircleShape),
            )
            Text("ACTIVE", color = MaterialTheme.extendedColors.statusActive, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
