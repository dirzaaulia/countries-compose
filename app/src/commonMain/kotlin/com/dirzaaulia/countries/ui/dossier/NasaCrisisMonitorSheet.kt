package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.dirzaaulia.countries.domain.country.NasaNaturalEvent
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.components.uiSymbolFor
import com.dirzaaulia.countries.util.formatNumber
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasaCrisisMonitorSheet(
    hazards: List<NasaNaturalEvent>,
    currentCountry: Country?,
    onClose: () -> Unit,
    onFlyToEpicenter: (NasaNaturalEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Volcanoes", "Wildfires", "Storms", "Ice", "Floods")

    val filteredHazards =
        remember(hazards, selectedCategory, currentCountry) {
            val catFiltered =
                when (selectedCategory) {
                    "Volcanoes" -> hazards.filter { it.category.contains("Volcano", ignoreCase = true) }
                    "Wildfires" -> hazards.filter { it.category.contains("Fire", ignoreCase = true) }
                    "Storms" -> hazards.filter { it.category.contains("Storm", ignoreCase = true) || it.category.contains("Cyclone", ignoreCase = true) }
                    "Ice" -> hazards.filter { it.category.contains("Ice", ignoreCase = true) }
                    "Floods" -> hazards.filter { it.category.contains("Flood", ignoreCase = true) || it.category.contains("Water", ignoreCase = true) }
                    else -> hazards
                }

            // Sort by distance to current country if available
            if (currentCountry != null) {
                catFiltered.sortedBy { h ->
                    haversineDistance(currentCountry.center.lat, currentCountry.center.lng, h.lat, h.lng)
                }
            } else {
                catFiltered
            }
        }

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color(0xF2070D18),
        tonalElevation = 16.dp,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                color = Color(0x44EF4444),
                shape = RoundedCornerShape(3.dp),
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(36.dp)
                                .background(Color(0x33EF4444), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("ALERT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(
                            text = "PLANETARY CRISIS MONITOR",
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Text(
                            text = "NASA EONET Real-Time Disasters",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                MinimalistCloseButton(onClick = onClose)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Row (Compact Horizontal LazyRow)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categories) { cat ->
                    CategoryPill(
                        label = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle status
            Text(
                text = "${filteredHazards.size} ACTIVE PLANETARY INCIDENTS DETECTED",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Crisis Feed List
            if (filteredHazards.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No active hazards reported in this category.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filteredHazards, key = { it.id }) { hazard ->
                        CrisisCard(
                            hazard = hazard,
                            currentCountry = currentCountry,
                            onFlyTo = {
                                onClose()
                                onFlyToEpicenter(hazard)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFEF4444) else Color(0x221E293B),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFF87171) else Color(0x22FFFFFF)),
        modifier = modifier.clickable { onClick() },
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun CrisisCard(
    hazard: NasaNaturalEvent,
    currentCountry: Country?,
    onFlyTo: () -> Unit,
) {
    val distKm =
        currentCountry?.let {
            haversineDistance(it.center.lat, it.center.lng, hazard.lat, hazard.lng).roundToInt()
        }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x300F172A),
        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SemanticIcon(uiSymbolFor(hazard.categoryIcon), hazard.category, Color(0xFFEF4444), Modifier.size(24.dp))
                    Column {
                        Text(
                            text = hazard.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                        )
                        Text(
                            text = "${hazard.category} • Detected ${hazard.date}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                        )
                    }
                }

                hazard.magnitude?.let { mag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33EF4444),
                        border = BorderStroke(1.dp, Color(0x66EF4444)),
                    ) {
                        Text(
                            text = mag,
                            color = Color(0xFFFCA5A5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (distKm != null) {
                    Text(
                        text = "${formatNumber(distKm.toDouble())} km from ${currentCountry.name}",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                    )
                } else {
                    Text(
                        text = "${hazard.lat.toInt()}°, ${hazard.lng.toInt()}°",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                    )
                }

                Button(
                    onClick = onFlyTo,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                    border = BorderStroke(1.dp, Color(0x66EF4444)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        SemanticIcon(UiSymbol.Location, "Fly to epicenter", Color(0xFFFCA5A5), Modifier.size(14.dp))
                        Text("Fly to Epicenter", color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun haversineDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
): Double {
    val r = 6371.0
    val dLat = (lat2 - lat1) * PI / 180.0
    val dLon = (lon2 - lon1) * PI / 180.0
    val a = sin(dLat / 2).pow(2) + cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
