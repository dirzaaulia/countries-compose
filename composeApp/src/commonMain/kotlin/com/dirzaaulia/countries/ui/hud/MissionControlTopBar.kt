package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.domain.satellite.SatelliteTelemetry
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.satellite.SatelliteSelectorPill

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
    modifier: Modifier = Modifier,
    showAurora: Boolean = true,
    onToggleAurora: () -> Unit = {},
    showTimezones: Boolean = false,
    onToggleTimezones: () -> Unit = {},
    showMarkets: Boolean = false,
    onToggleMarkets: () -> Unit = {},
    showTectonic: Boolean = false,
    onToggleTectonic: () -> Unit = {},
    showTimeMachine: Boolean = false,
    onToggleTimeMachine: () -> Unit = {},
    moonDistanceKm: Double = 384400.0,
    localTime: String = "",
    utcTime: String = "LIVE UTC",
    satelliteFleet: List<SatelliteTelemetry> = emptyList(),
    selectedSatellite: SatelliteTelemetry? = null,
    onSelectSatellite: ((SatelliteTelemetry) -> Unit)? = null,
    onOpenLegend: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenSpaceWeather: () -> Unit = {},
    earthMissionContent: @Composable (() -> Unit)? = null,
) {
    var showMissionControl by remember { mutableStateOf(false) }
    var showSatelliteSelector by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TimeDisplayCard(
                utcTime = utcTime,
                localTime = localTime,
                modifier = Modifier.fillMaxHeight(),
            )
            CelestialSwitcher(
                currentPage = currentPage,
                onSelectPage = onSelectPage,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            if (showSatellites && satelliteFleet.isNotEmpty()) {
                TopBarIconButton(
                    contentDescription = "Satellite Fleet",
                    onClick = { showSatelliteSelector = !showSatelliteSelector },
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    SemanticIcon(
                        symbol = UiSymbol.Iss,
                        contentDescription = "Satellites",
                        tint = if (selectedSatellite != null) Color(0xFF38BDF8) else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            TopBarIconButton(
                contentDescription = "Mission control",
                onClick = { showMissionControl = true },
                modifier = Modifier.fillMaxHeight(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        if (showSatelliteSelector && showSatellites && satelliteFleet.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            SatelliteSelectorPill(
                fleet = satelliteFleet,
                selectedSatellite = selectedSatellite,
                onSelectSatellite = { sat ->
                    onSelectSatellite?.invoke(sat)
                    showSatelliteSelector = false
                },
                modifier = Modifier.fillMaxWidth(),
            )
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
                            text = when (currentPage) {
                                0 -> "Earth operations"
                                1 -> "Moon operations"
                                else -> "Mars operations"
                            },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    MinimalistCloseButton(onClick = { showMissionControl = false })
                }

                MissionStatusCard(
                    currentPage = currentPage,
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
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LayerToggleCard("Aurora", if (showAurora) "Oval" else "Off", UiSymbol.Clear, showAurora, Color(0xFF10B981), onToggleAurora, Modifier.weight(1f))
                        LayerToggleCard("Timezones", if (showTimezones) "24 Grid" else "Off", UiSymbol.Time, showTimezones, Color(0xFF38BDF8), onToggleTimezones, Modifier.weight(1f))
                        LayerToggleCard("Markets", if (showMarkets) "Exchanges" else "Off", UiSymbol.Economy, showMarkets, Color(0xFFF59E0B), onToggleMarkets, Modifier.weight(1f))
                        LayerToggleCard("Tectonic", if (showTectonic) "Plates/Quakes" else "Off", UiSymbol.Landscape, showTectonic, Color(0xFFEF4444), onToggleTectonic, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
