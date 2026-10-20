package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryFeature
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryFeatureType
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericPlanetFeatureSheet(
    feature: PlanetaryFeature,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categoryColor =
        when (feature.type) {
            PlanetaryFeatureType.LANDER, PlanetaryFeatureType.ROVER, PlanetaryFeatureType.PROBE, PlanetaryFeatureType.ORBITER -> MaterialTheme.extendedColors.categoryRobotic
            PlanetaryFeatureType.BASIN, PlanetaryFeatureType.CRATER, PlanetaryFeatureType.MOUNTAIN, PlanetaryFeatureType.TERRA -> MaterialTheme.extendedColors.categoryGeological
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF209111E),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFF64748B), width = 36.dp, height = 4.dp)
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderSection(feature, categoryColor, onClose)
            DetailSection(feature, categoryColor)
            DiscoveriesSection(feature)
        }
    }
}

@Composable
private fun HeaderSection(
    feature: PlanetaryFeature,
    categoryColor: Color,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .background(categoryColor, RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = feature.type.name,
                    color = categoryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = feature.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = feature.significance,
                color = Color(0xFFCBD5E1),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun DetailSection(
    feature: PlanetaryFeature,
    categoryColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("COORDINATES", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(
                text = "LAT ${feature.lat}°, LON ${feature.lng}°",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (feature.missionAgency != null && feature.missionYear != null) {
            Column(modifier = Modifier.weight(1f)) {
                Text("MISSION", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SemanticIcon(
                        symbol = UiSymbol.Iss,
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${feature.missionAgency} (${feature.missionYear})",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoveriesSection(feature: PlanetaryFeature) {
    if (feature.discoveries.isNotEmpty()) {
        Column {
            Text(
                text = "KEY DISCOVERIES & FACTS",
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(8.dp))
            feature.discoveries.forEach { discovery ->
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text("•", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(text = discovery, color = Color(0xFFE2E8F0), fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}
