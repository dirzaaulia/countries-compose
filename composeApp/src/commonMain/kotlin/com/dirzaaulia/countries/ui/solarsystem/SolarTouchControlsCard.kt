package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun SolarTouchControlsCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF209111E),
        contentColor = Color.White,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ControlRow(symbol = UiSymbol.Clear, text = "1-Finger Drag: Orbit Camera")
            ControlRow(symbol = UiSymbol.Iss, text = "2-Finger Pinch: Zoom In/Out")
            ControlRow(symbol = UiSymbol.Earth, text = "2-Finger Drag: Pan View")
            ControlRow(symbol = UiSymbol.Info, text = "Tap Planet: Telemetry & Info")
            ControlRow(symbol = UiSymbol.Time, text = "Double Tap: Dive to Surface")
        }
    }
}

@Composable
private fun ControlRow(
    symbol: UiSymbol,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SemanticIcon(
            symbol = symbol,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFCBD5E1),
        )
    }
}
