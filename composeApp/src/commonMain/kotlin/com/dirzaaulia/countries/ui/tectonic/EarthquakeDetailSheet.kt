package com.dirzaaulia.countries.ui.tectonic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.tectonic.Earthquake
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatCoordinates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeDetailSheet(
    earthquake: Earthquake,
    onClose: () -> Unit,
    onFlyToEpicenter: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1626),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFFEF4444),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
        ) {
            EarthquakeHeader(earthquake = earthquake, onClose = onClose)
            Spacer(Modifier.height(14.dp))
            EarthquakeInfoCard(earthquake = earthquake)
            Spacer(Modifier.height(14.dp))
            FlyToButton(
                lat = earthquake.lat,
                lng = earthquake.lng,
                onFlyToEpicenter = onFlyToEpicenter,
            )
        }
    }
}

@Composable
private fun EarthquakeHeader(
    earthquake: Earthquake,
    onClose: () -> Unit,
) {
    val magColor = when {
        earthquake.magnitude >= 7.0 -> Color(0xFFEF4444)
        earthquake.magnitude >= 6.0 -> Color(0xFFF97316)
        else -> Color(0xFFF59E0B)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = magColor.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, magColor),
            ) {
                Text(
                    text = "[MAGNITUDE ${earthquake.magnitude}]",
                    color = magColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = earthquake.place,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}

@Composable
private fun EarthquakeInfoCard(earthquake: Earthquake) {
    val depthLabel = when {
        earthquake.depthKm < 70.0 -> "[SHALLOW] (${earthquake.depthKm} km)"
        earthquake.depthKm <= 300.0 -> "[INTERMEDIATE] (${earthquake.depthKm} km)"
        else -> "[DEEP] (${earthquake.depthKm} km)"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x33EF4444)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("SEISMIC EVENT PARAMETERS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Epicenter Coords", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(formatCoordinates(LatLng(earthquake.lat, earthquake.lng)), color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Focal Depth", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(depthLabel, color = Color(0xFFF97316), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            if (earthquake.tsunamiAlert) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x33EF4444),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                ) {
                    Text(
                        text = "[TSUNAMI WARNING ISSUED]",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FlyToButton(
    lat: Double,
    lng: Double,
    onFlyToEpicenter: (Double, Double) -> Unit,
) {
    Button(
        onClick = { onFlyToEpicenter(lat, lng) },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SemanticIcon(UiSymbol.Location, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Fly to Epicenter", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
