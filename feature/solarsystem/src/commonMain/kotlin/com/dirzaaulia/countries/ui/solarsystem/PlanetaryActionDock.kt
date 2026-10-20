package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlanetaryActionDock(
    showTimeMachine: Boolean,
    onToggleTimeMachine: () -> Unit,
    onOpenDossier: () -> Unit,
    modifier: Modifier = Modifier,
    hasInterior: Boolean = false,
    onOpenInterior: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        // Time Machine Button
        Surface(
            onClick = onToggleTimeMachine,
            shape = CircleShape,
            color = if (showTimeMachine) Color(0xFFF59E0B).copy(alpha = 0.3f) else Color(0xF209111E),
            border = BorderStroke(1.dp, if (showTimeMachine) Color(0xFFF59E0B) else Color(0x5538BDF8)),
            shadowElevation = 8.dp,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Planetary Time Machine",
                    tint = if (showTimeMachine) Color(0xFFF59E0B) else Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Dossier & Telemetry Button
        Surface(
            onClick = onOpenDossier,
            shape = CircleShape,
            color = Color(0xF209111E),
            border = BorderStroke(1.dp, Color(0x5538BDF8)),
            shadowElevation = 8.dp,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.Analytics,
                    contentDescription = "Planetary Dossier & Telemetry",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // X-Ray Core Cutaway / Interior Button
        if (hasInterior) {
            Surface(
                onClick = onOpenInterior,
                shape = CircleShape,
                color = Color(0xF209111E),
                border = BorderStroke(1.dp, Color(0x5538BDF8)),
                shadowElevation = 8.dp,
                modifier = Modifier.size(42.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.Layers,
                        contentDescription = "X-Ray Core Cutaway",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
