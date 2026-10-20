package com.dirzaaulia.countries.ui.mars.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var info by remember(epochMillis) { mutableStateOf(MarsAstronomyMath.calculateMarsSolarDate(epochMillis)) }

    LaunchedEffect(epochMillis) {
        var currentEpoch = epochMillis
        while (true) {
            delay(1000L)
            currentEpoch += 1000L
            info = MarsAstronomyMath.calculateMarsSolarDate(currentEpoch)
        }
    }

    Surface(
        shape = RoundedCornerShape(Spacing.md),
        color = MaterialTheme.extendedColors.overlayBackground,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier =
            modifier
                .navigationBarsPadding()
                .padding(Spacing.md)
                .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            MarsTimeRow(info.solNumber, info.mtcTimeFormatted, info.isDustStormSeason)

            Text(
                text = "${info.seasonName} (Ls ${(info.solarLongitudeDeg * 10).roundToInt() / 10.0}°)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(Spacing.xs))
            MarsTelemetryPill(info.earthDistanceMillionKm, info.radioDelayMinutes)
        }
    }
}

@Composable
private fun MarsTimeRow(
    sol: Int,
    mtc: String,
    isDustStorm: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "SOL $sol",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = "•  $mtc",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
            )
        }

        if (isDustStorm) {
            Box(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.extendedColors.statusWarning.copy(alpha = 0.2f))
                        .padding(horizontal = Spacing.sm, vertical = 2.dp),
            ) {
                Text(
                    text = "DUST STORM SEASON",
                    color = MaterialTheme.extendedColors.statusWarning,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                )
            }
        }
    }
}

@Composable
private fun MarsTelemetryPill(
    distanceMillionKm: Double,
    delayMinutes: Double,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Spacing.sm))
                .background(MaterialTheme.extendedColors.telemetryBackground)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "EARTH DISTANCE",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        Text(
            text = "${(distanceMillionKm * 10).roundToInt() / 10.0}M km (${(delayMinutes * 10).roundToInt() / 10.0}m signal delay)",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}
