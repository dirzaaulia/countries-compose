package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatNumber
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightRouteHudCard(
    origin: Country,
    destination: Country,
    distanceKm: Double,
    onRandomRoute: () -> Unit,
    onClose: () -> Unit,
    allCountries: List<Country> = emptyList(),
    onSelectOrigin: ((Country) -> Unit)? = null,
    onSelectDestination: ((Country) -> Unit)? = null,
    isSupersonic: Boolean = false,
    onToggleSupersonic: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var pickingFor by remember { mutableStateOf<String?>(null) } // "origin" or "destination"
    var searchQuery by remember { mutableStateOf("") }

    val speedKmh = if (isSupersonic) 2335.0 else 850.0
    val machNumber = if (isSupersonic) "Mach 2.2 (SST Concorde)" else "Mach 0.78 (Commercial Airliner)"
    val estHours = (distanceKm / speedKmh * 10).roundToInt() / 10.0
    val estMiles = (distanceKm * 0.621371).roundToInt()

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1220),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = if (isSupersonic) Color(0xFFEF4444) else Color(0xFFF59E0B),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (isSupersonic) "SUPERSONIC INTERCONTINENTAL CORRIDOR" else "GEODESIC FLIGHT ROUTE (GREAT CIRCLE)",
                            color = if (isSupersonic) Color(0xFFEF4444) else Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )

                        MinimalistCloseButton(onClick = onClose)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Origin -> Destination (Tap to change)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable(enabled = onSelectOrigin != null) {
                                        pickingFor = "origin"
                                        searchQuery = ""
                                    },
                        ) {
                            Text(
                                text = "DEPARTURE (TAP TO CHANGE)",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                PlatformCountryFlag(origin.iso2, Modifier.size(20.dp), "${origin.name} flag")
                                Text(origin.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(origin.capital.ifEmpty { "Mainland" }, color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }

                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            SemanticIcon(UiSymbol.Flight, "Flight route", if (isSupersonic) Color(0xFFEF4444) else Color(0xFFF59E0B), Modifier.size(16.dp))
                            Text(if (isSupersonic) "[SST]" else "[SUB]", color = if (isSupersonic) Color(0xFFEF4444) else Color(0xFFF59E0B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable(enabled = onSelectDestination != null) {
                                        pickingFor = "destination"
                                        searchQuery = ""
                                    },
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                text = "ARRIVAL (TAP TO CHANGE)",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(destination.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                PlatformCountryFlag(destination.iso2, Modifier.size(20.dp), "${destination.name} flag")
                            }
                            Text(destination.capital.ifEmpty { "Mainland" }, color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }
                    }

                    // Country Selector Expansion
                    if (pickingFor != null && allCountries.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x350F172A),
                            border = BorderStroke(1.dp, Color(0x4438BDF8)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Select ${if (pickingFor == "origin") "Departure" else "Arrival"} Country:",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Cancel [X]",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        modifier = Modifier.clickable { pickingFor = null },
                                    )
                                }

                                val candidates =
                                    allCountries
                                        .filter { it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true) }
                                        .take(6)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    candidates.forEach { c ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0x221E293B),
                                            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                                            modifier =
                                                Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        if (pickingFor == "origin") {
                                                            onSelectOrigin?.invoke(c)
                                                        } else {
                                                            onSelectDestination?.invoke(c)
                                                        }
                                                        pickingFor = null
                                                    },
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            ) {
                                                PlatformCountryFlag(c.iso2, Modifier.size(14.dp), "${c.name} flag")
                                                Text(c.id, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Flight Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x351E293B),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("DISTANCE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${formatNumber(distanceKm)} km",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text("$estMiles miles", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x351E293B),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ESTIMATED TRANSIT", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "~$estHours hrs",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(machNumber, color = if (isSupersonic) Color(0xFFFCA5A5) else Color(0xFF64748B), fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Supersonic Mode & Random Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = onToggleSupersonic,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isSupersonic) Color(0xFFEF4444) else Color(0x5538BDF8)),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isSupersonic) Color(0xFFFCA5A5) else Color(0xFF38BDF8),
                                ),
                            modifier = Modifier.weight(1.2f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isSupersonic) "[SST] Mach 2.2 Concorde" else "[SUB] Subsonic Airliner",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = if (isSupersonic) "2,335 km/h · Fast SST" else "850 km/h · Standard",
                                    fontSize = 9.sp,
                                    color = if (isSupersonic) Color(0xFFF87171) else Color(0xFF94A3B8),
                                )
                            }
                        }

                        Button(
                            onClick = onRandomRoute,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            modifier = Modifier.weight(0.8f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        ) {
                            Text("Shuffle Route", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
