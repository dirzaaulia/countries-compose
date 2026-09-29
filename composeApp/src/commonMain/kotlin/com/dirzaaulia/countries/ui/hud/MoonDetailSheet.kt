package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.util.formatNumber
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.MoonInfo
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import androidx.compose.material3.MaterialTheme
import com.dirzaaulia.countries.generated.resources.Res
import com.dirzaaulia.countries.generated.resources.moon_apogee_format
import com.dirzaaulia.countries.generated.resources.moon_atmosphere
import com.dirzaaulia.countries.generated.resources.moon_atmosphere_subtitle
import com.dirzaaulia.countries.generated.resources.moon_atmosphere_value
import com.dirzaaulia.countries.generated.resources.moon_celestial_body
import com.dirzaaulia.countries.generated.resources.moon_gravity
import com.dirzaaulia.countries.generated.resources.moon_gravity_subtitle
import com.dirzaaulia.countries.generated.resources.moon_gravity_value
import com.dirzaaulia.countries.generated.resources.moon_illumination_format
import com.dirzaaulia.countries.generated.resources.moon_light_seconds_format
import com.dirzaaulia.countries.generated.resources.moon_luna_title
import com.dirzaaulia.countries.generated.resources.moon_lunation_cycle
import com.dirzaaulia.countries.generated.resources.moon_lunation_format
import com.dirzaaulia.countries.generated.resources.moon_orbital_distance
import com.dirzaaulia.countries.generated.resources.moon_perigee_apogee
import com.dirzaaulia.countries.generated.resources.moon_physical_overview
import com.dirzaaulia.countries.generated.resources.moon_polar_temp
import com.dirzaaulia.countries.generated.resources.moon_radius
import com.dirzaaulia.countries.generated.resources.moon_radius_subtitle
import com.dirzaaulia.countries.generated.resources.moon_radius_value
import com.dirzaaulia.countries.generated.resources.moon_supermoon
import com.dirzaaulia.countries.generated.resources.moon_surface_temp
import com.dirzaaulia.countries.generated.resources.moon_temp_value
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonDetailSheet(
    moonInfo: MoonInfo,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = Spacing.large, topEnd = Spacing.large),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                width = 36.dp,
                height = Spacing.extraSmall,
            )
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.moon_celestial_body),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    MinimalistCloseButton(onClick = onClose)
                }
            }
            item { MoonHeader(moonInfo) }
            item { MoonTelemetryGrid(moonInfo) }
            item { MoonPhysicalOverview() }
        }
    }
}

@Composable
private fun MoonHeader(moonInfo: MoonInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SemanticIcon(UiSymbol.Moon, moonInfo.phaseName, MaterialTheme.extendedColors.categoryRobotic, Modifier.size(36.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Text(
                    text = stringResource(Res.string.moon_luna_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (moonInfo.isSupermoon) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Spacing.extraSmall))
                            .background(MaterialTheme.extendedColors.statusWarning.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.moon_supermoon),
                            color = MaterialTheme.extendedColors.statusWarning,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Text(
                text = stringResource(Res.string.moon_illumination_format, moonInfo.phaseName, (moonInfo.illuminatedFraction * 100).toInt()),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun MoonTelemetryGrid(moonInfo: MoonInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TelemetryCard(
                title = stringResource(Res.string.moon_orbital_distance),
                value = "${formatNumber(moonInfo.distanceKm)} km",
                subtitle = stringResource(Res.string.moon_light_seconds_format, ((moonInfo.lightTravelSec * 100).roundToInt() / 100.0)),
                modifier = Modifier.weight(1f)
            )
            val ratio = (moonInfo.distancePercent * 100).toInt()
            TelemetryCardWithProgress(
                title = stringResource(Res.string.moon_perigee_apogee),
                value = stringResource(Res.string.moon_apogee_format, ratio),
                progress = moonInfo.distancePercent,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TelemetryCardWithProgress(
                title = stringResource(Res.string.moon_lunation_cycle),
                value = stringResource(Res.string.moon_lunation_format, ((moonInfo.moonAgeDays * 10).roundToInt() / 10.0)),
                progress = (moonInfo.moonAgeDays / 29.53).toFloat(),
                modifier = Modifier.weight(1f)
            )
            TelemetryCard(
                title = stringResource(Res.string.moon_surface_temp),
                value = stringResource(Res.string.moon_temp_value),
                subtitle = stringResource(Res.string.moon_polar_temp),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.extendedColors.telemetryBackground,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TelemetryCardWithProgress(
    title: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.extendedColors.telemetryBackground,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.extraSmall)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(Spacing.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
private fun MoonPhysicalOverview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.extendedColors.telemetryBackground)
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(Res.string.moon_physical_overview), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            OverviewItem(stringResource(Res.string.moon_gravity), stringResource(Res.string.moon_gravity_value), stringResource(Res.string.moon_gravity_subtitle), Modifier.weight(1f))
            OverviewItem(stringResource(Res.string.moon_radius), stringResource(Res.string.moon_radius_value), stringResource(Res.string.moon_radius_subtitle), Modifier.weight(1f))
        }
        Column {
            Text(stringResource(Res.string.moon_atmosphere), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(Res.string.moon_atmosphere_value), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(Res.string.moon_atmosphere_subtitle), color = MaterialTheme.colorScheme.outline, fontSize = 11.sp)
        }
    }
}

@Composable
private fun OverviewItem(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = MaterialTheme.colorScheme.outline, fontSize = 11.sp)
    }
}
