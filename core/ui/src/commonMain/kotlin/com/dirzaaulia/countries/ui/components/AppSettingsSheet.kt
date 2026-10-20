package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallManager
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import kotlinx.coroutines.launch

data class AppModuleDescriptor(
    val id: String,
    val title: String,
    val description: String,
)

val DYNAMIC_MODULES_CATALOG =
    listOf(
        AppModuleDescriptor("mars", "Mars Expedition", "3D surface rendering, Jezero Crater landmarks, Perseverance/Curiosity photo gallery."),
        AppModuleDescriptor("deepspace", "Deep Space Telemetry", "Gas Giants 3D renderers, Saturn 3D Rings, X-Ray core cross-sections."),
        AppModuleDescriptor("moon", "Moon Explorer", "3D Moon surface, Apollo landing sites, Lunar geology, & live eclipse feed."),
        AppModuleDescriptor("weather", "Weather & Space Weather", "7-day Open-Meteo forecasts, atmospheric motion, & NOAA Auroral Oval."),
        AppModuleDescriptor("satellite", "Orbital Satellite Tracking", "ISS, Hubble, & CSS 3D hovering orbits and ground tethers."),
        AppModuleDescriptor("hazards", "Geology Hazards & NASA Crisis", "USGS live earthquakes, tectonic plates, & EONET crisis monitor."),
        AppModuleDescriptor("economy", "World Bank & Stock Exchanges", "GDP/Inflation trends & 30 global stock exchange timezone scrubber."),
        AppModuleDescriptor("quiz", "Geography Quiz Challenge", "Interactive silhouette quiz with neon aura glows."),
        AppModuleDescriptor("flight", "Supersonic Flight Simulator", "Concorde Mach 2.2 flight routes and supersonic physics."),
        AppModuleDescriptor("comparison", "Country Comparison & True Size", "Geodesic 3D Mercator distortion projection overlay."),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsSheet(
    installManager: FeatureInstallManager,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showInfoSheet by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "SYSTEM SETTINGS",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Text(
                            text = "Feature Module Manager",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    MinimalistCloseButton(onClick = onClose)
                }
            }

            item {
                Button(
                    onClick = { showInfoSheet = true },
                    shape = RoundedCornerShape(Spacing.sm),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("VIEW APP INFO & DATA SOURCES", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Text(
                    text = "Manage installed Play Feature Delivery modules to free up device storage.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }

            items(DYNAMIC_MODULES_CATALOG) { module ->
                val isInstalled = installManager.isFeatureInstalled(module.id)

                Surface(
                    shape = RoundedCornerShape(Spacing.sm),
                    color = MaterialTheme.extendedColors.telemetryBackground,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            ) {
                                Text(
                                    text = module.title,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Box(
                                    modifier =
                                        Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isInstalled) Color(0x2210B981) else Color(0x2264748B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = if (isInstalled) "INSTALLED" else "NOT INSTALLED",
                                        color = if (isInstalled) Color(0xFF34D399) else Color(0xFF94A3B8),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            Text(
                                text = module.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                            )
                        }

                        if (isInstalled) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        installManager.requestFeatureUninstall(module.id)
                                    }
                                },
                                shape = RoundedCornerShape(Spacing.sm),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
                            ) {
                                Text("DELETE", color = MaterialTheme.colorScheme.error, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        installManager.requestFeatureInstall(module.id)
                                    }
                                },
                                shape = RoundedCornerShape(Spacing.sm),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            ) {
                                Text("DOWNLOAD", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInfoSheet) {
        AppInfoSheet(installManager = installManager, onClose = { showInfoSheet = false })
    }
}
