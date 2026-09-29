package com.dirzaaulia.countries.ui.mars.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.MarsAstronomyMath
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun MarsEnvironmentHudCard(
    epochMillis: Long,
    modifier: Modifier = Modifier,
) {
    var info by remember(epochMillis) { mutableStateOf(MarsAstronomyMath.calculateMarsEnvironmentInfo(epochMillis)) }

    LaunchedEffect(epochMillis) {
        var currentEpoch = epochMillis
        while (true) {
            delay(1000L)
            currentEpoch += 1000L
            info = MarsAstronomyMath.calculateMarsEnvironmentInfo(currentEpoch)
        }
    }

    Surface(
        shape = RoundedCornerShape(Spacing.medium),
        color = MaterialTheme.extendedColors.overlayBackground,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
            .navigationBarsPadding()
            .padding(Spacing.medium)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            MarsTimeRow(info.solNumber, info.mtcTimeFormatted, info.isDustStormSeason)

            Text(
                text = "${info.seasonName} (Ls ${(info.solarLongitudeDeg * 10).roundToInt() / 10.0}°)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            MarsTelemetryPill(info.earthDistanceMillionKm, info.radioDelayMinutes)
        }
    }
}

@Composable
private fun MarsTimeRow(sol: Long, mtc: String, isDustStorm: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOL $sol",
                color = MaterialTheme.extendedColors.categoryRobotic,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = mtc,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        if (isDustStorm) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Spacing.small))
                    .background(MaterialTheme.extendedColors.categoryGeological.copy(alpha = 0.2f))
                    .padding(horizontal = Spacing.small, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = "Dust Storm Warning",
                    tint = MaterialTheme.extendedColors.categoryGeological,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "DUST STORM SEASON",
                    color = MaterialTheme.extendedColors.categoryGeological,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MarsTelemetryPill(distance: Double, delayMin: Double) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Spacing.small))
            .background(MaterialTheme.extendedColors.telemetryBackground)
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        Icon(
            imageVector = Icons.Outlined.CellTower,
            contentDescription = "Radio Link",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "${(distance * 10).roundToInt() / 10.0}M km",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.outline,
            fontSize = 11.sp
        )
        Text(
            text = "${(delayMin * 10).roundToInt() / 10.0} min delay",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
