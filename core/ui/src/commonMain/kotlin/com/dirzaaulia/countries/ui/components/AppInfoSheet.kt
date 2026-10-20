package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallManager
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors

data class DataSourceAttribution(
    val name: String,
    val domain: String,
    val description: String,
    val url: String,
)

val APP_DATA_SOURCES =
    listOf(
        DataSourceAttribution("REST Countries v5", "restcountries.com", "Country demographics, flags, currencies, languages, & borders.", "https://restcountries.com"),
        DataSourceAttribution("Open-Meteo Meteorology", "open-meteo.com", "7-day weather forecasts, wind compass, and UV index.", "https://open-meteo.com"),
        DataSourceAttribution("NOAA Space Weather", "swpc.noaa.gov", "Kp-index, solar wind speed, & 1:1 OVATION auroral model.", "https://www.swpc.noaa.gov"),
        DataSourceAttribution("USGS Earthquake Hazards", "earthquake.usgs.gov", "Real-time seismic activity, epicenter coordinates, & fault lines.", "https://earthquake.usgs.gov"),
        DataSourceAttribution("NASA EONET Crisis Tracker", "eonet.gsfc.nasa.gov", "Global natural hazards (wildfires, storms, volcanoes).", "https://eonet.gsfc.nasa.gov"),
        DataSourceAttribution("World Bank Open Data", "data.worldbank.org", "Macroeconomic GDP, CPI inflation %, & unemployment gauges.", "https://data.worldbank.org"),
        DataSourceAttribution("CelesTrak / N2YO", "celestrak.org", "ISS, Hubble, & CSS 3D hovering orbit telemetry.", "https://celestrak.org"),
        DataSourceAttribution("NASA Solar System", "solarsystem.nasa.gov", "Planetary dimensions, atmospheres, and historic probes.", "https://solarsystem.nasa.gov"),
        DataSourceAttribution("FlagCDN", "flagcdn.com", "High-resolution SVG vector flag graphics.", "https://flagcdn.com"),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoSheet(
    onClose: () -> Unit,
    installManager: FeatureInstallManager = remember { FeatureInstallManager() },
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uriHandler = LocalUriHandler.current
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            Text(
                                text = "TERRA",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                            )
                            Box(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "v1.0.0-internal.20",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Text(
                            text = "App & Developer Info",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    MinimalistCloseButton(onClick = onClose)
                }
            }

            item {
                Text(
                    text = "SUPPORT THE DEVELOPER",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
            }

            item {
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
                        Text(
                            text = "TERRA is an open-source multiplatform application built by Dirza Aulia. If you enjoy the app, consider supporting development!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            Button(
                                onClick = { uriHandler.openUri("https://dirzaaulia.com") },
                                shape = RoundedCornerShape(Spacing.sm),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("WEBSITE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { uriHandler.openUri("https://ko-fi.com/dirzaaulia") },
                                shape = RoundedCornerShape(Spacing.sm),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29ABE2)),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("KO-FI", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { uriHandler.openUri("https://saweria.co/dirzaaulia") },
                                shape = RoundedCornerShape(Spacing.sm),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5890A)),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("SAWERIA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "SETTINGS & LEGAL",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Button(
                        onClick = { showSettings = true },
                        shape = RoundedCornerShape(Spacing.sm),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1.2f),
                    ) {
                        Text("MANAGE MODULES", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showPrivacyPolicy = !showPrivacyPolicy
                            if (showPrivacyPolicy) showTerms = false
                        },
                        shape = RoundedCornerShape(Spacing.sm),
                        border = BorderStroke(1.dp, if (showPrivacyPolicy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("PRIVACY", color = if (showPrivacyPolicy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showTerms = !showTerms
                            if (showTerms) showPrivacyPolicy = false
                        },
                        shape = RoundedCornerShape(Spacing.sm),
                        border = BorderStroke(1.dp, if (showTerms) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("TERMS", color = if (showTerms) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (showPrivacyPolicy) {
                item {
                    Surface(
                        shape = RoundedCornerShape(Spacing.md),
                        color = MaterialTheme.extendedColors.telemetryBackground,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Text(
                                text = "PRIVACY POLICY",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            )
                            Text(
                                text = "1. Zero Personal Data Collection\nTERRA does NOT collect, store, or share any personally identifiable information (PII). No user accounts or advertising identifiers are used.\n\n2. Network Scientific APIs\nDirect requests are made to open scientific APIs (NASA, NOAA, USGS, Open-Meteo, World Bank, REST Countries) to fulfill telemetry data. No personal data is transmitted.\n\n3. Local In-Memory Cache\nSession data is stored temporarily in local memory to reduce bandwidth and is cleared when the app closes.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                            )
                            Button(
                                onClick = { uriHandler.openUri("https://countries-509405.web.app/privacy") },
                                shape = RoundedCornerShape(Spacing.sm),
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                Text("OPEN WEB PRIVACY POLICY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (showTerms) {
                item {
                    Surface(
                        shape = RoundedCornerShape(Spacing.md),
                        color = MaterialTheme.extendedColors.telemetryBackground,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Text(
                                text = "TERMS & CONDITIONS",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            )
                            Text(
                                text = "1. Acceptance of Terms\nBy accessing TERRA, you agree to these terms. The app is designed for educational and informational visualization.\n\n2. Scientific Telemetry Disclaimer\nData is provided 'AS IS' from open scientific sources. Telemetry should not be used for emergency navigation or life-safety decisions.\n\n3. Developer Support\nVoluntary donations via Ko-fi or Saweria support ongoing independent development.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                            )
                            Button(
                                onClick = { uriHandler.openUri("https://countries-509405.web.app/tnc") },
                                shape = RoundedCornerShape(Spacing.sm),
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                Text("OPEN WEB TERMS & CONDITIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "DATA SOURCES & ATTRIBUTION",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
            }

            items(APP_DATA_SOURCES) { source ->
                Surface(
                    shape = RoundedCornerShape(Spacing.sm),
                    color = MaterialTheme.extendedColors.telemetryBackground,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { uriHandler.openUri(source.url) },
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
                                    text = source.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = source.domain,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                )
                            }
                            Text(
                                text = source.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {
        AppSettingsSheet(installManager = installManager, onClose = { showSettings = false })
    }
}
