package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MissionControlTopBar(
    showBorders: Boolean,
    onToggleBorders: () -> Unit,
    showMoon: Boolean,
    onToggleMoon: () -> Unit,
    showSatellites: Boolean,
    onToggleSatellites: () -> Unit,
    showHazards: Boolean,
    onToggleHazards: () -> Unit,
    isFlightMode: Boolean,
    onToggleFlightMode: () -> Unit,
    isQuizMode: Boolean,
    onToggleQuizMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 10.dp)
    ) {
        // 1. Header Title & Astronomical Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PLANET EARTH",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "NASA Real-Time Space View",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Live Astronomical Solar Clock
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0x2238BDF8),
                border = BorderStroke(1.dp, Color(0x4438BDF8))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Text(
                        text = "LIVE UTC SOLAR",
                        color = Color(0xFFE0F2FE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Interactive Layer Controls Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Borders Toggle (Adaptive Day/Night)
            HudLayerChip(
                label = if (showBorders) "Borders: Adaptive" else "Borders: Off",
                isActive = showBorders,
                icon = "🗺️",
                activeColor = Color(0xFF38BDF8),
                onClick = onToggleBorders
            )

            // Moon Toggle
            HudLayerChip(
                label = if (showMoon) "Moon 3D: On" else "Moon 3D: Off",
                isActive = showMoon,
                icon = "🌔",
                activeColor = Color(0xFFE2E8F0),
                onClick = onToggleMoon
            )

            // ISS & Satellite Tracker Toggle
            HudLayerChip(
                label = if (showSatellites) "ISS Tracker: Live" else "ISS: Off",
                isActive = showSatellites,
                icon = "🛰️",
                activeColor = Color(0xFF38BDF8),
                onClick = onToggleSatellites
            )

            // NASA Natural Hazards Toggle
            HudLayerChip(
                label = if (showHazards) "NASA Hazards: On" else "Hazards: Off",
                isActive = showHazards,
                icon = "🌋",
                activeColor = Color(0xFFEF4444),
                onClick = onToggleHazards
            )

            // Flight Path Simulator Toggle
            HudLayerChip(
                label = if (isFlightMode) "Flights: Active" else "Flight Route",
                isActive = isFlightMode,
                icon = "✈️",
                activeColor = Color(0xFFF59E0B),
                onClick = onToggleFlightMode
            )

            // Geography Quiz Mode Toggle
            HudLayerChip(
                label = if (isQuizMode) "Quiz Mode 🎯" else "Play Quiz",
                isActive = isQuizMode,
                icon = "🎮",
                activeColor = Color(0xFF10B981),
                onClick = onToggleQuizMode
            )
        }
    }
}

@Composable
private fun HudLayerChip(
    label: String,
    isActive: Boolean,
    icon: String,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) activeColor.copy(alpha = 0.22f) else Color(0x351E293B),
        border = BorderStroke(
            1.dp,
            if (isActive) activeColor.copy(alpha = 0.65f) else Color(0x3394A3B8)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = icon, fontSize = 11.sp)
            Text(
                text = label,
                color = if (isActive) Color(0xFFF8FAFC) else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}
