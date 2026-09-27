package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

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
    showTimeMachine: Boolean = false,
    onToggleTimeMachine: () -> Unit = {},
    moonDistanceKm: Double = 384400.0,
    localTime: String = "",
    utcTime: String = "LIVE UTC",
    onOpenLegend: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenNasaCrisis: () -> Unit = {},
    earthMissionContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showMissionControl by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CelestialSwitcher(
                currentPage = currentPage,
                onSelectPage = onSelectPage,
                modifier = Modifier.weight(1f),
            )
            TopBarIconButton(
                contentDescription = "Mission control",
                onClick = { showMissionControl = true },
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }

    if (showMissionControl) {
        AdaptiveInfoSheet(
            onDismissRequest = { showMissionControl = false },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = Color(0xF209111E),
            contentColor = Color.White,
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
                        .widthIn(max = 760.dp)
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                        .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MISSION CONTROL",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = if (currentPage == 0) "Earth operations" else "Moon operations",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    MinimalistCloseButton(onClick = { showMissionControl = false })
                }

                MissionStatusCard(
                    currentPage = currentPage,
                    utcTime = utcTime,
                    localTime = localTime,
                    moonDistanceKm = moonDistanceKm,
                )

                MissionActionRow(
                    title = "Map legend and symbology",
                    symbol = UiSymbol.Info,
                    tint = Color(0xFF38BDF8),
                    onClick = {
                        showMissionControl = false
                        onOpenLegend()
                    },
                )

                if (currentPage == 0) {
                    MissionActionRow(
                        title = "NASA Planetary Crisis Monitor",
                        symbol = UiSymbol.Hazard,
                        tint = Color(0xFFEF4444),
                        onClick = {
                            showMissionControl = false
                            onOpenNasaCrisis()
                        },
                    )

                    Text(
                        text = "VISUALIZATION LAYERS",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LayerToggleCard("Borders", if (showBorders) "Adaptive" else "Off", UiSymbol.Earth, showBorders, Color(0xFF38BDF8), onToggleBorders, Modifier.weight(1f))
                        LayerToggleCard("Satellites", if (showSatellites) "Live ISS" else "Off", UiSymbol.Iss, showSatellites, Color(0xFF38BDF8), onToggleSatellites, Modifier.weight(1f))
                        LayerToggleCard("Hazards", if (showHazards) "NASA Events" else "Off", UiSymbol.Hazard, showHazards, Color(0xFFEF4444), onToggleHazards, Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LayerToggleCard("Flight Route", if (isFlightMode) "Active Arc" else "Start Flight", UiSymbol.Flight, isFlightMode, Color(0xFFF59E0B), onToggleFlightMode, Modifier.weight(1f))
                        LayerToggleCard("World Quiz", if (isQuizMode) "Playing" else "Start Quiz", UiSymbol.Quiz, isQuizMode, Color(0xFF10B981), onToggleQuizMode, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CelestialSwitcher(
    currentPage: Int,
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xEE0B1220),
        border = BorderStroke(1.dp, Color(0x4438BDF8)),
        modifier = modifier.heightIn(min = 50.dp),
    ) {
        Row(modifier = Modifier.padding(3.dp), verticalAlignment = Alignment.CenterVertically) {
            CelestialTabPill("Earth", UiSymbol.Earth, currentPage == 0, Color(0xFF38BDF8), { onSelectPage(0) }, Modifier.weight(1f))
            CelestialTabPill("Moon", UiSymbol.Moon, currentPage == 1, Color(0xFFFFD54F), { onSelectPage(1) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TopBarIconButton(
    symbol: UiSymbol? = null,
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null,
) {
    Surface(
        shape = CircleShape,
        color = Color(0xDD0B1220),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.size(44.dp),
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            if (content != null) content() else SemanticIcon(symbol!!, contentDescription, Color.White, Modifier.size(20.dp))
        }
    }
}

@Composable
private fun MissionStatusCard(
    currentPage: Int,
    utcTime: String,
    localTime: String,
    moonDistanceKm: Double,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x351E293B),
        border = BorderStroke(1.dp, Color(0x2238BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(if (currentPage == 0) Color(0xFF10B981) else Color(0xFFFFD54F), CircleShape))
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(if (currentPage == 0) utcTime else "LUNAR RANGE", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (currentPage == 0) localTime.takeIf { it.isNotEmpty() }?.plus(" LOCAL") ?: "LOCAL TIME UNAVAILABLE" else "${(moonDistanceKm / 1000.0).toInt()}K KM",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MissionActionRow(
    title: String,
    symbol: UiSymbol,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x351E293B),
        border = BorderStroke(1.dp, Color(0x2238BDF8)),
        modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).clickable { onClick() },
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SemanticIcon(symbol, title, tint, Modifier.size(20.dp))
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CelestialTabPill(
    label: String,
    icon: UiSymbol,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) activeColor.copy(alpha = 0.25f) else Color.Transparent)
                .clickable { onClick() }
                .heightIn(min = 44.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            SemanticIcon(icon, label, if (isSelected) Color.White else Color(0xFF94A3B8), Modifier.size(16.dp))
            Text(label, color = if (isSelected) Color.White else Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
private fun LayerToggleCard(
    label: String,
    subLabel: String,
    icon: UiSymbol,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) activeColor.copy(alpha = 0.20f) else Color(0x351E293B),
        border = BorderStroke(1.dp, if (isActive) activeColor.copy(alpha = 0.70f) else Color(0x33475569)),
        modifier = modifier.clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SemanticIcon(icon, label, if (isActive) activeColor else Color(0xFF94A3B8), Modifier.size(15.dp))
                Text(label, color = if (isActive) Color.White else Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(subLabel, color = if (isActive) activeColor else Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}
