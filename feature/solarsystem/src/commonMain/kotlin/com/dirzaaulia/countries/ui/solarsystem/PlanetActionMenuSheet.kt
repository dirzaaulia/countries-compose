package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetActionMenuSheet(
    planetName: String,
    showControlsCard: Boolean,
    onToggleControlsCard: () -> Unit,
    showTimeMachine: Boolean,
    onToggleTimeMachine: () -> Unit,
    hasInterior: Boolean,
    onOpenInterior: () -> Unit,
    onOpenDossier: () -> Unit,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenRoverGallery: (() -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = Color(0x99000000),
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
                    .widthIn(max = 640.dp)
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "PLANETARY ACTION MENU",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    )
                    Text(
                        text = planetName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                MinimalistCloseButton(onClick = onClose)
            }

            ActionMenuItem(
                title = "Controls Guide",
                description = "Toggle gesture and keyboard/mouse guidance overlay",
                iconVector = Icons.Outlined.TouchApp,
                isActive = showControlsCard,
                onClick = {
                    onToggleControlsCard()
                    onClose()
                },
            )

            ActionMenuItem(
                title = "Planetary Time Machine",
                description = "Scrub real-time solar illumination and epoch simulation",
                symbol = UiSymbol.Time,
                isActive = showTimeMachine,
                onClick = {
                    onToggleTimeMachine()
                    onClose()
                },
            )

            ActionMenuItem(
                title = "Planetary Dossier & Telemetry",
                description = "Physical dimensions, atmosphere composition, and solar facts",
                iconVector = Icons.Outlined.Info,
                onClick = {
                    onOpenDossier()
                    onClose()
                },
            )

            if (hasInterior) {
                ActionMenuItem(
                    title = "X-Ray Core Cutaway",
                    description = "Interactive interior cross-section HUD (crust, mantle, core)",
                    iconVector = Icons.Outlined.Adjust,
                    onClick = {
                        onOpenInterior()
                        onClose()
                    },
                )
            }

            if (onOpenRoverGallery != null) {
                ActionMenuItem(
                    title = "NASA Rover Photo Gallery",
                    description = "Perseverance & Curiosity Mars surface photos",
                    iconVector = Icons.Outlined.PhotoCamera,
                    onClick = {
                        onOpenRoverGallery()
                        onClose()
                    },
                )
            }

            ActionMenuItem(
                title = "System Module Manager Settings",
                description = "View, download, or delete dynamic feature packs",
                iconVector = Icons.Outlined.Settings,
                onClick = {
                    onOpenSettings()
                    onClose()
                },
            )
        }
    }
}

@Composable
private fun ActionMenuItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconVector: ImageVector? = null,
    symbol: UiSymbol? = null,
    isActive: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(Spacing.sm),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.extendedColors.telemetryBackground,
        border = BorderStroke(1.dp, if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(Spacing.xs),
                color = if (isActive) MaterialTheme.colorScheme.primary else Color(0x2238BDF8),
                modifier = Modifier.padding(2.dp),
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (iconVector != null) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = title,
                            tint = if (isActive) MaterialTheme.colorScheme.onPrimary else Color(0xFF38BDF8),
                        )
                    } else if (symbol != null) {
                        SemanticIcon(
                            symbol = symbol,
                            contentDescription = title,
                            tint = if (isActive) MaterialTheme.colorScheme.onPrimary else Color(0xFF38BDF8),
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                )
            }
        }
    }
}
