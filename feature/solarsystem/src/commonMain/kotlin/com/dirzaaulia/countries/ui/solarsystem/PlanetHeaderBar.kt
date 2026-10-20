package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlanetHeaderBar(
    title: String,
    onBackToSolarSystem: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    accentColor: Color = Color(0xFF38BDF8),
    showControlsCard: Boolean = false,
    onToggleControlsCard: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Top-Left: Back Button
        Surface(
            onClick = onBackToSolarSystem,
            shape = CircleShape,
            color = Color(0xF209111E),
            border = BorderStroke(1.dp, Color(0x5538BDF8)),
            shadowElevation = 10.dp,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back to Solar System",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Top-Center: Celestial Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xF209111E),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
            shadowElevation = 8.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                )
                Text(
                    text = if (subtitle != null) "$title • $subtitle".uppercase() else title.uppercase(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
        }

        // Top-Right: Un-cramped Glass Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Controls Guide Quick Toggle
            Surface(
                onClick = onToggleControlsCard,
                shape = CircleShape,
                color = if (showControlsCard) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color(0xF209111E),
                border = BorderStroke(1.dp, if (showControlsCard) Color(0xFF38BDF8) else Color(0x5538BDF8)),
                shadowElevation = 8.dp,
                modifier = Modifier.size(42.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = "Controls Guide",
                        tint = if (showControlsCard) Color(0xFF38BDF8) else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Settings Quick Button
            Surface(
                onClick = onOpenSettings,
                shape = CircleShape,
                color = Color(0xF209111E),
                border = BorderStroke(1.dp, Color(0x5538BDF8)),
                shadowElevation = 8.dp,
                modifier = Modifier.size(42.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
