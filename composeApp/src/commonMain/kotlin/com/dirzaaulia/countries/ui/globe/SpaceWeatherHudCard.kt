package com.dirzaaulia.countries.ui.globe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.data.repository.AuroralCountry
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceWeatherHudCard(
    uiState: SpaceWeatherUiState,
    onClose: () -> Unit,
    onFlyToCountry: (lat: Double, lng: Double) -> Unit,
) {
    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(topStart = Spacing.large, topEnd = Spacing.large),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium)
                    .padding(bottom = Spacing.extraLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            HeaderSection(onClose)
            KpIndexGauge(uiState.kpIndex)
            TelemetryRow(uiState.solarWindSpeed, uiState.bzGsm)
            AffectedCountriesRow(uiState.affectedCountries, onFlyToCountry)
        }
    }
}

@Composable
private fun HeaderSection(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "SPACE WEATHER TELEMETRY",
                color = MaterialTheme.extendedColors.categoryCrewed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = "Auroral Oval & Geomagnetic Status",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun KpIndexGauge(kpIndex: Double) {
    val category =
        when {
            kpIndex < 3.0 -> "QUIET" to MaterialTheme.extendedColors.statusActive
            kpIndex < 4.0 -> "UNSETTLED" to MaterialTheme.extendedColors.categoryRobotic
            kpIndex < 5.0 -> "ACTIVE" to MaterialTheme.extendedColors.categoryRobotic
            kpIndex < 6.0 -> "MINOR STORM" to MaterialTheme.extendedColors.categoryGeological
            else -> "SEVERE STORM" to MaterialTheme.extendedColors.categoryGeological
        }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Spacing.small))
                .background(MaterialTheme.extendedColors.telemetryBackground)
                .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Kp-INDEX GAUGE",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${category.first} (Kp ${(kpIndex * 10).roundToInt() / 10.0})",
                color = category.second,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Spacing.small)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth((kpIndex / 9.0).toFloat().coerceIn(0f, 1f))
                        .height(Spacing.small)
                        .background(category.second),
            )
        }
    }
}

@Composable
private fun TelemetryRow(
    solarWindSpeed: Double,
    bzGsm: Double,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(Spacing.small))
                    .background(MaterialTheme.extendedColors.telemetryBackground)
                    .padding(Spacing.small),
        ) {
            Text("SOLAR WIND SPEED", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${solarWindSpeed.roundToInt()} km/s", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(Spacing.small))
                    .background(MaterialTheme.extendedColors.telemetryBackground)
                    .padding(Spacing.small),
        ) {
            Text("Bz GSM VECTOR", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("$bzGsm nT", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AffectedCountriesRow(
    countries: List<AuroralCountry>,
    onFlyToCountry: (lat: Double, lng: Double) -> Unit,
) {
    if (countries.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
        Text(
            text = "AURORAL ZONE COVERAGE (TAP TO FLY)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            items(countries) { country ->
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(Spacing.small))
                            .background(MaterialTheme.extendedColors.telemetryBackground)
                            .clickable { onFlyToCountry(country.lat, country.lng) }
                            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                ) {
                    Text(
                        text = country.name,
                        color = MaterialTheme.extendedColors.categoryCrewed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
