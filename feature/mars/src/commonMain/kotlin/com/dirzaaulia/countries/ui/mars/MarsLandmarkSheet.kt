package com.dirzaaulia.countries.ui.mars

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.mars.MarsLandmark
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarsLandmarkSheet(
    landmark: MarsLandmark,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outlineVariant,
                width = 36.dp,
                height = 4.dp,
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "MARS SURFACE LANDMARK",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    )
                    Text(
                        text = landmark.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                MinimalistCloseButton(onClick = onClose)
            }

            Text(
                text = landmark.significance,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                InfoBadge(
                    label = "LATITUDE",
                    value = "${landmark.lat}°",
                    modifier = Modifier.weight(1f),
                )
                InfoBadge(
                    label = "LONGITUDE",
                    value = "${landmark.lng}°",
                    modifier = Modifier.weight(1f),
                )
                if (landmark.year != null) {
                    InfoBadge(
                        label = "YEAR",
                        value = "${landmark.year}",
                        modifier = Modifier.weight(1f),
                    )
                } else if (landmark.elevationKm != null) {
                    InfoBadge(
                        label = "ELEVATION",
                        value = "${landmark.elevationKm} km",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (landmark.agency != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Spacing.md))
                            .background(MaterialTheme.extendedColors.telemetryBackground)
                            .padding(Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "OPERATING AGENCY",
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
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(Spacing.sm)
                                    .background(MaterialTheme.extendedColors.statusActive, CircleShape),
                        )
                        Text(
                            text = "HISTORICAL MISSION",
                            color = MaterialTheme.extendedColors.statusActive,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun InfoBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(Spacing.md),
        color = MaterialTheme.extendedColors.telemetryBackground,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}
