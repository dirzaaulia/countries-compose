package com.dirzaaulia.countries.ui.hud

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionControlTopBar(
    currentPage: Int,
    onSelectPage: (Int) -> Unit,
    showBorders: Boolean,
    onToggleBorders: () -> Unit,
    showSatellites: Boolean,
    onToggleSatellites: () -> Unit,
    showHazards: Boolean,
    onToggleHazards: () -> Unit,
    isFlightMode: Boolean,
    onToggleFlightMode: () -> Unit,
    isQuizMode: Boolean,
    onToggleQuizMode: () -> Unit,
    moonDistanceKm: Double = 384400.0,
    localTime: String = "",
    utcTime: String = "LIVE UTC",
    onOpenLegend: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLayersMenu by remember { mutableStateOf(false) }
    val activeLayersCount = listOf(showBorders, showSatellites, showHazards, isFlightMode, isQuizMode).count { it }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // 1. Sleek Compact Glass Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // A. Left: Live Status Badge with UTC (Top) and Local Time (Bottom)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xDD0B1220),
                border = BorderStroke(1.dp, Color(0x3338BDF8))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    if (currentPage == 0) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = utcTime,
                                color = Color(0xFFE0F2FE),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (localTime.isNotEmpty()) {
                                Text(
                                    text = "$localTime LOCAL",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    } else {
                        val distStr = if (moonDistanceKm >= 1000.0) {
                            "${(moonDistanceKm / 1000.0).toInt()}K KM"
                        } else {
                            "${moonDistanceKm.toInt()} KM"
                        }
                        Text(
                            text = distStr,
                            color = Color(0xFFE0F2FE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // B. Center: Segmented Celestial Switcher Capsule [ 🌍 Earth | 🌕 Moon ]
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xEE0B1220),
                border = BorderStroke(1.dp, Color(0x4438BDF8)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Earth Tab
                    CelestialTabPill(
                        label = "Earth",
                        icon = "🌍",
                        isSelected = currentPage == 0,
                        activeColor = Color(0xFF38BDF8),
                        onClick = { onSelectPage(0) }
                    )

                    // Moon Tab
                    CelestialTabPill(
                        label = "Moon",
                        icon = "🌕",
                        isSelected = currentPage == 1,
                        activeColor = Color(0xFFFFD54F),
                        onClick = { onSelectPage(1) }
                    )
                }
            }

            // C. Right: Floating Quick Layers & Legend Buttons
            if (currentPage == 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sleek Circular Legend Action Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xDD0B1220),
                        border = BorderStroke(1.dp, Color(0x3338BDF8)),
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { onOpenLegend() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("ℹ️", fontSize = 12.sp)
                        }
                    }

                    // Compact Layers Capsule with Active Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (showLayersMenu) Color(0xFF0284C7) else Color(0xDD0B1220),
                        border = BorderStroke(
                            1.dp,
                            if (showLayersMenu) Color(0xFF38BDF8) else Color(0x3338BDF8)
                        ),
                        modifier = Modifier.clickable { showLayersMenu = !showLayersMenu }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text("⛯", fontSize = 12.sp, color = Color.White)
                            Text(
                                text = "Layers",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (activeLayersCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color(0xFF38BDF8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$activeLayersCount",
                                        color = Color(0xFF0B1220),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Moon Page info indicator
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xDD0B1220),
                    border = BorderStroke(1.dp, Color(0x33FFD54F))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🚀", fontSize = 11.sp)
                        Text(
                            text = "Apollo",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 2. Thumb-Friendly Native Layers Modal Bottom Sheet
        if (showLayersMenu && currentPage == 0) {
            ModalBottomSheet(
                onDismissRequest = { showLayersMenu = false },
                containerColor = Color(0xF20B1324),
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 24.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⛯", fontSize = 16.sp, color = Color(0xFF38BDF8))
                            Text(
                                text = "STRATEGIC HORIZON LAYERS",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        MinimalistCloseButton(onClick = { showLayersMenu = false })
                    }

                    // Grid of Layer Toggle Items
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LayerToggleCard(
                            label = "Borders",
                            subLabel = if (showBorders) "Adaptive" else "Off",
                            icon = "🗺️",
                            isActive = showBorders,
                            activeColor = Color(0xFF38BDF8),
                            onClick = onToggleBorders,
                            modifier = Modifier.weight(1f)
                        )
                        LayerToggleCard(
                            label = "Satellites",
                            subLabel = if (showSatellites) "Live ISS" else "Off",
                            icon = "🛰️",
                            isActive = showSatellites,
                            activeColor = Color(0xFF38BDF8),
                            onClick = onToggleSatellites,
                            modifier = Modifier.weight(1f)
                        )
                        LayerToggleCard(
                            label = "Hazards",
                            subLabel = if (showHazards) "NASA Events" else "Off",
                            icon = "🌋",
                            isActive = showHazards,
                            activeColor = Color(0xFFEF4444),
                            onClick = onToggleHazards,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LayerToggleCard(
                            label = "Flight Route",
                            subLabel = if (isFlightMode) "Active Arc" else "Start Flight",
                            icon = "✈️",
                            isActive = isFlightMode,
                            activeColor = Color(0xFFF59E0B),
                            onClick = onToggleFlightMode,
                            modifier = Modifier.weight(1f)
                        )
                        LayerToggleCard(
                            label = "World Quiz",
                            subLabel = if (isQuizMode) "Playing Challenge" else "Start Quiz",
                            icon = "🎮",
                            isActive = isQuizMode,
                            activeColor = Color(0xFF10B981),
                            onClick = onToggleQuizMode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CelestialTabPill(
    label: String,
    icon: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) activeColor.copy(alpha = 0.25f) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(icon, fontSize = 13.sp)
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LayerToggleCard(
    label: String,
    subLabel: String,
    icon: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) activeColor.copy(alpha = 0.20f) else Color(0x351E293B),
        border = BorderStroke(
            1.dp,
            if (isActive) activeColor.copy(alpha = 0.70f) else Color(0x33475569)
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(icon, fontSize = 12.sp)
                Text(
                    text = label,
                    color = if (isActive) Color.White else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = subLabel,
                color = if (isActive) activeColor else Color(0xFF64748B),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
