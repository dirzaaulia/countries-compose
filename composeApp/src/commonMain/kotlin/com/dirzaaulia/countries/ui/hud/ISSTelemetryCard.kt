package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.ISSTelemetry
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ISSTelemetryCard(
    telemetry: ISSTelemetry,
    onClose: () -> Unit,
    onCenterView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1220),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF38BDF8).copy(alpha = 0.6f),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Header bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "ISS LIVE ORBITAL TELEMETRY",
                                color = Color(0xFF38BDF8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x3310B981),
                                border = BorderStroke(1.dp, Color(0x6610B981)),
                            ) {
                                Text(
                                    text = "LIVE LEO",
                                    color = Color(0xFF34D399),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                        MinimalistCloseButton(onClick = onClose)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        SemanticIcon(UiSymbol.Iss, "International Space Station", Color(0xFF38BDF8), Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "International Space Station",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Low Earth Orbit (LEO) • 92.9 min orbital period",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metric Tiles Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Velocity
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x221E293B),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("VELOCITY", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                val velKmh = ((telemetry.velocityKmh * 10).toLong() / 10.0).toString()
                                val velKms = ((telemetry.velocityKmh / 3600.0 * 10).toLong() / 10.0).toString()
                                Text("$velKmh km/h", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("$velKms km/s • Mach 22.5", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }

                        // Altitude
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x221E293B),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ALTITUDE", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                val altKm = ((telemetry.altitudeKm * 10).toLong() / 10.0).toString()
                                Text("$altKm km", color = Color(0xFF34D399), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("418 km mean perigee", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Coordinates & Solar Illumination
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x221E293B),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("GROUND TRACK POSITION", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                val latStr = "${((telemetry.latitude * 10).toLong() / 10.0)}°" + if (telemetry.latitude >= 0) "N" else "S"
                                val lngStr = "${((telemetry.longitude * 10).toLong() / 10.0)}°" + if (telemetry.longitude >= 0) "E" else "W"
                                Text("$latStr, $lngStr", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("SOLAR ILLUMINATION", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                val isDaylight = telemetry.visibility.equals("daylight", ignoreCase = true)
                                val visText = if (isDaylight) "Sunlight (Arrays Active)" else "Earth Shadow (Eclipse)"
                                val visColor = if (isDaylight) Color(0xFFFBBF24) else Color(0xFF818CF8)
                                Text(visText, color = visColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Center Camera Button
                    Button(
                        onClick = onCenterView,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0284C7),
                                contentColor = Color.White,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                    ) {
                        Text("Track & Center Camera on ISS", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
