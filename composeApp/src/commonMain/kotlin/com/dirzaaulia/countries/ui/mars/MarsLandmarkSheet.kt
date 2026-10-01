package com.dirzaaulia.countries.ui.mars

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
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.mars.MarsLandmark
import com.dirzaaulia.countries.generated.resources.Res
import com.dirzaaulia.countries.generated.resources.mars_agency
import com.dirzaaulia.countries.generated.resources.mars_elevation
import com.dirzaaulia.countries.generated.resources.mars_elevation_format
import com.dirzaaulia.countries.generated.resources.mars_historical
import com.dirzaaulia.countries.generated.resources.mars_lat
import com.dirzaaulia.countries.generated.resources.mars_lat_format
import com.dirzaaulia.countries.generated.resources.mars_lng
import com.dirzaaulia.countries.generated.resources.mars_year
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarsLandmarkSheet(
    landmark: MarsLandmark,
    onClose: () -> Unit,
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
                    .padding(horizontal = Spacing.large)
                    .padding(bottom = Spacing.extraLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    val categoryColor =
                        when (landmark.category) {
                            "ROVER", "LANDER" -> MaterialTheme.extendedColors.categoryRobotic
                            "MOUNTAIN", "CANYON", "BASIN" -> MaterialTheme.extendedColors.categoryGeological
                            "POLAR_CAP" -> MaterialTheme.extendedColors.categoryPolar
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                    Text(
                        text = landmark.subtitle.uppercase(),
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = landmark.name.uppercase(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                MinimalistCloseButton(onClick = onClose)
            }

            Text(
                text = landmark.significance,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                InfoBadge(
                    label = stringResource(Res.string.mars_lat),
                    value = stringResource(Res.string.mars_lat_format, landmark.lat),
                    modifier = Modifier.weight(1f),
                )
                InfoBadge(
                    label = stringResource(Res.string.mars_lng),
                    value = stringResource(Res.string.mars_lat_format, landmark.lng),
                    modifier = Modifier.weight(1f),
                )
                if (landmark.year != null) {
                    InfoBadge(
                        label = stringResource(Res.string.mars_year),
                        value = "${landmark.year}",
                        modifier = Modifier.weight(1f),
                    )
                } else if (landmark.elevationKm != null) {
                    InfoBadge(
                        label = stringResource(Res.string.mars_elevation),
                        value = stringResource(Res.string.mars_elevation_format, landmark.elevationKm),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (landmark.agency != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Spacing.medium))
                            .background(MaterialTheme.extendedColors.telemetryBackground)
                            .padding(Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.mars_agency),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = landmark.agency.uppercase(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(Spacing.small)
                                    .background(MaterialTheme.extendedColors.statusActive, CircleShape),
                        )
                        Text(
                            text = stringResource(Res.string.mars_historical),
                            color = MaterialTheme.extendedColors.statusActive,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
    }
}

@Composable
private fun InfoBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(Spacing.small))
                .background(MaterialTheme.extendedColors.telemetryBackground)
                .padding(Spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
