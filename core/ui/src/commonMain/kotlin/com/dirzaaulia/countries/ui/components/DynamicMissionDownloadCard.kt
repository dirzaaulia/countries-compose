package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallState
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallStatus
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicMissionDownloadCard(
    missionTitle: String,
    installState: FeatureInstallState,
    onRetry: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

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
                color = Color(0xFF64748B),
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
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "ON-DEMAND MISSION TELEMETRY",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    )
                    Text(
                        text = missionTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                MinimalistCloseButton(onClick = onClose)
            }

            Surface(
                shape = RoundedCornerShape(Spacing.md),
                color = MaterialTheme.extendedColors.telemetryBackground,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    val downloadedMb = installState.bytesDownloaded / (1024f * 1024f)
                    val totalMb = installState.totalBytesToDownload / (1024f * 1024f)
                    val progressPercent = (installState.progressFraction * 100f).toInt()
                    val sizeText =
                        if (totalMb > 0f) {
                            "${(downloadedMb * 10).toInt() / 10.0} MB / ${(totalMb * 10).toInt() / 10.0} MB ($progressPercent%)"
                        } else {
                            "$progressPercent%"
                        }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (installState.status == FeatureInstallStatus.FAILED) "STATUS: DOWNLOAD FAILED" else "DOWNLOADING TELEMETRY PACK...",
                            color = if (installState.status == FeatureInstallStatus.FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = sizeText,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                        )
                    }

                    LinearProgressIndicator(
                        progress = { installState.progressFraction },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                    if (installState.status == FeatureInstallStatus.FAILED) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Button(
                            onClick = onRetry,
                            shape = RoundedCornerShape(Spacing.sm),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text("RETRY DOWNLOAD", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
