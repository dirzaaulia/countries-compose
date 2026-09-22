package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.Country
import com.dirzaaulia.countries.util.formatNumber
import kotlin.math.roundToInt

@Composable
fun FlightRouteHudCard(
    origin: Country,
    destination: Country,
    distanceKm: Double,
    onRandomRoute: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estHours = (distanceKm / 850.0).roundToInt()
    val estMiles = (distanceKm * 0.621371).roundToInt()

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xF00B1220),
        border = BorderStroke(1.dp, Color(0x66F59E0B)),
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✈️ GEODESIC FLIGHT ROUTE (GREAT CIRCLE)",
                    color = Color(0xFFF59E0B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("✕", color = Color(0xFF94A3B8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Origin ➔ Destination
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("DEPARTURE", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${origin.flagEmoji} ${origin.name}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(origin.capital, color = Color(0xFF38BDF8), fontSize = 11.sp)
                }

                Text("✈️ ➔", color = Color(0xFFF59E0B), fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("ARRIVAL", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${destination.name} ${destination.flagEmoji}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(destination.capital, color = Color(0xFF38BDF8), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Flight Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x351E293B),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("DISTANCE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${formatNumber(distanceKm)} km",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("$estMiles miles", color = Color(0xFF64748B), fontSize = 9.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x351E293B),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("FLIGHT TIME", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "~$estHours hours",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Cruise at 850 km/h", color = Color(0xFF64748B), fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRandomRoute,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New Random Flight Route 🎲", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
