package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ISSTelemetry

@Composable
fun ISSTelemetryCard(
    telemetry: ISSTelemetry,
    onClose: () -> Unit,
    onCenterView: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xF00B1220),
        border = BorderStroke(1.dp, Color(0x6638BDF8)),
        shadowElevation = 18.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛰️ ISS LIVE ORBITAL TELEMETRY",
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Text("✕", color = Color(0xFF94A3B8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("🛰️", fontSize = 36.sp)
                Column {
                    Text(
                        text = "International Space Station",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Low Earth Orbit (LEO) • 92.9 min orbital period",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metric Tiles Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Velocity
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x221E293B),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("VELOCITY", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val velKmh = ((telemetry.velocityKmh * 10).toLong() / 10.0).toString()
                        val velKms = ((telemetry.velocityKmh / 3600.0 * 10).toLong() / 10.0).toString()
                        Text("$velKmh km/h", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("$velKms km/s", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }

                // Altitude
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x221E293B),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("ALTITUDE", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val altKm = ((telemetry.altitudeKm * 10).toLong() / 10.0).toString()
                        Text("$altKm km", color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Low Earth Orbit", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Coordinates & Solar Illumination
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x221E293B),
                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("COORDINATES", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val latStr = "${((telemetry.latitude * 10).toLong() / 10.0)}°" + if (telemetry.latitude >= 0) "N" else "S"
                        val lngStr = "${((telemetry.longitude * 10).toLong() / 10.0)}°" + if (telemetry.longitude >= 0) "E" else "W"
                        Text("$latStr, $lngStr", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("SOLAR ILLUMINATION", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val isDaylight = telemetry.visibility.equals("daylight", ignoreCase = true)
                        val visText = if (isDaylight) "☀️ Sunlight (Arrays Active)" else "🌑 In Earth's Shadow (Eclipse)"
                        val visColor = if (isDaylight) Color(0xFFFBBF24) else Color(0xFF818CF8)
                        Text(visText, color = visColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Center Camera Button
            Button(
                onClick = onCenterView,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text("🔭 Track & Center Camera on ISS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
