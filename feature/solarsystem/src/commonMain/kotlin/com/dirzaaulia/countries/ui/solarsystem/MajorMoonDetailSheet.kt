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
import com.dirzaaulia.countries.domain.solarsystem.MajorMoon
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MajorMoonDetailSheet(
    moon: MajorMoon,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            HeaderSection(moon, onClose)
            SpecsGrid(moon)
            AstrobiologySection(moon)
        }
    }
}

@Composable
private fun HeaderSection(
    moon: MajorMoon,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(moon.colorHex), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NATURAL SATELLITE",
                    color = Color(moon.colorHex),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = moon.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = moon.description, color = Color(0xFFCBD5E1), fontSize = 14.sp, lineHeight = 20.sp)
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun SpecsGrid(moon: MajorMoon) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            SpecItem(label = "RADIUS", value = "${moon.radiusKm} km", modifier = Modifier.weight(1f))
            SpecItem(label = "MASS", value = "${moon.massKg} kg", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            SpecItem(label = "ORBITAL DISTANCE", value = "${moon.orbitalDistanceKm} km", modifier = Modifier.weight(1f))
            SpecItem(label = "ORBITAL PERIOD", value = "${moon.orbitalPeriodDays} days", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            SpecItem(label = "SURFACE", value = moon.surfaceComposition, modifier = Modifier.weight(1f))
            SpecItem(label = "TIDAL HEATING", value = moon.tidalHeatingIndex, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AstrobiologySection(moon: MajorMoon) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "ASTROBIOLOGY POTENTIAL",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = "${moon.habitabilityScore}/5",
                color = if (moon.habitabilityScore >= 4) Color(0xFF10B981) else Color(0xFF38BDF8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) { index ->
                Box(
                    modifier =
                        Modifier
                            .height(4.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (index < moon.habitabilityScore) {
                                    if (moon.habitabilityScore >= 4) Color(0xFF10B981) else Color(0xFF38BDF8)
                                } else {
                                    Color(0xFF334155)
                                },
                            ),
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = moon.astrobiologyNotes,
            color = Color(0xFFF1F5F9),
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun SpecItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
