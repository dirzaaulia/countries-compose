package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.platform.isWasmTarget
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun SolarTouchControlsCard(
    modifier: Modifier = Modifier,
) {
    var isMouseMode by remember { mutableStateOf(isWasmTarget) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF209111E),
        contentColor = Color.White,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ControlsHeader(
                isMouseMode = isMouseMode,
                onToggleMode = { isMouseMode = it },
            )
            if (isMouseMode) {
                ControlRow(symbol = UiSymbol.Clear, text = "Left Drag: Orbit Camera")
                ControlRow(symbol = UiSymbol.Iss, text = "Middle Wheel: Zoom In/Out (NASA Eyes)")
                ControlRow(symbol = UiSymbol.Earth, text = "Right/Middle Drag: Pan View")
                ControlRow(symbol = UiSymbol.Info, text = "Left Click: Telemetry & Info")
                ControlRow(symbol = UiSymbol.Time, text = "Double Click: Dive to Planet")
            } else {
                ControlRow(symbol = UiSymbol.Clear, text = "1-Finger Drag: Orbit Camera")
                ControlRow(symbol = UiSymbol.Iss, text = "2-Finger Pinch: Zoom In/Out")
                ControlRow(symbol = UiSymbol.Earth, text = "2-Finger Drag: Pan View")
                ControlRow(symbol = UiSymbol.Info, text = "Tap Planet: Telemetry & Info")
                ControlRow(symbol = UiSymbol.Time, text = "Double Tap: Dive to Surface")
            }
        }
    }
}

@Composable
private fun ControlsHeader(
    isMouseMode: Boolean,
    onToggleMode: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = if (isMouseMode) "NASA EYES CONTROLS" else "TOUCH CONTROLS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8),
            letterSpacing = 0.08.sp,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Row(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            ModeChip(
                label = "Mouse",
                isSelected = isMouseMode,
                onClick = { onToggleMode(true) },
            )
            ModeChip(
                label = "Touch",
                isSelected = !isMouseMode,
                onClick = { onToggleMode(false) },
            )
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF0284C7) else Color.Transparent)
                .clickable { onClick() }
                .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF94A3B8),
        )
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
            modifier = Modifier.size(15.dp),
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
