package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.InteriorLayer

@Composable
fun InteriorLayerCard(
    layer: InteriorLayer,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .background(Color(layer.colorHex), RoundedCornerShape(6.dp)),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = layer.name.uppercase(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = layer.description,
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            SpecColumn("DEPTH RANGE", layer.depthRangeKm, Modifier.weight(1f))
            SpecColumn("STATE OF MATTER", layer.stateOfMatter, Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            SpecColumn("PRESSURE", layer.pressureRange, Modifier.weight(1f))
            SpecColumn("TEMPERATURE", layer.temperatureKelvin, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SpecColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(text = value, color = Color(0xFFF1F5F9), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
