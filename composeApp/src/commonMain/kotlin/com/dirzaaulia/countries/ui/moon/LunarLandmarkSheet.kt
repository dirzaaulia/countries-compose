package com.dirzaaulia.countries.ui.moon

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.LatLng
import com.dirzaaulia.countries.domain.moon.LunarLandmark
import com.dirzaaulia.countries.domain.moon.LunarLandmarkType
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatCoordinates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunarLandmarkSheet(
    landmark: LunarLandmark,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onFlyToSite: ((Double, Double) -> Unit)? = null,
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
                color = Color(0xFFC084FC),
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
            LandmarkHeader(landmark = landmark, onClose = onClose)
            Spacer(Modifier.height(14.dp))
            LandmarkDetailsList(landmark = landmark)
            if (onFlyToSite != null) {
                Spacer(Modifier.height(14.dp))
                FlyToSiteButton(lat = landmark.lat, lng = landmark.lng, onFlyToSite = onFlyToSite)
            }
        }
    }
}

@Composable
private fun LandmarkHeader(
    landmark: LunarLandmark,
    onClose: () -> Unit,
) {
    val (typeLabel, typeColor) =
        when (landmark.type) {
            LunarLandmarkType.CREWED_APOLLO -> "APOLLO CREWED" to Color(0xFFF59E0B)
            LunarLandmarkType.HISTORIC_ROBOTIC -> "HISTORIC USSR" to Color(0xFFF43F5E)
            LunarLandmarkType.MODERN_INTERNATIONAL -> "MODERN FLEET" to Color(0xFF06B6D4)
            LunarLandmarkType.COMMERCIAL_CLPS -> "CLPS LANDER" to Color(0xFF10B981)
            LunarLandmarkType.LUNAR_MARE -> "MARE BASALT" to Color(0xFFC084FC)
            LunarLandmarkType.IMPACT_CRATER -> "IMPACT CRATER" to Color(0xFF94A3B8)
            LunarLandmarkType.ARTEMIS_SOUTH_POLE -> "ARTEMIS SITE" to Color(0xFF8B5CF6)
        }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = typeColor.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, typeColor),
            ) {
                Text(
                    text = typeLabel,
                    color = typeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = landmark.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            landmark.latinName?.let { latin ->
                Text(
                    text = latin,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
            }
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}

@Composable
private fun LandmarkDetailsList(landmark: LunarLandmark) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        landmark.mission?.let { mission ->
            item(key = "mission") { MissionSpecsCard(mission = mission) }
        }
        item(key = "coords") { LocationSpecsCard(landmark = landmark) }
        item(key = "significance") { NarrativeCard(significance = landmark.significance) }
    }
}

@Composable
private fun MissionSpecsCard(mission: com.dirzaaulia.countries.domain.moon.LunarMissionMetadata) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x33C084FC)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformCountryFlag(
                        iso2 = mission.countryIso2,
                        modifier = Modifier.height(16.dp).width(24.dp).clip(RoundedCornerShape(3.dp)),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(mission.agency, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text(mission.landingDate, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            mission.sampleMassKg?.let { mass ->
                Spacer(Modifier.height(6.dp))
                Text("Sample Return: $mass kg", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            mission.roverName?.let { rover ->
                Spacer(Modifier.height(4.dp))
                Text("Rover Vehicle: $rover", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun LocationSpecsCard(landmark: LunarLandmark) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x33C084FC)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Selenographic Coords", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(formatCoordinates(LatLng(landmark.lat, landmark.lng)), color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                landmark.diameterKm?.let { diam ->
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Diameter", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("$diam km", color = Color(0xFFC084FC), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun NarrativeCard(significance: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x33C084FC)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("HISTORICAL & SCIENTIFIC SIGNIFICANCE", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                text = significance,
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun FlyToSiteButton(
    lat: Double,
    lng: Double,
    onFlyToSite: (Double, Double) -> Unit,
) {
    Button(
        onClick = { onFlyToSite(lat, lng) },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC084FC), contentColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SemanticIcon(UiSymbol.Location, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Fly to Landing Site", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
