package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.platform.isWasmTarget
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

enum class AppInputMode {
    TOUCH,
    MOUSE_KEYBOARD,
    CONTROLLER,
}

fun detectInputModeFromPointer(event: PointerEvent): AppInputMode? {
    val pointerType = event.changes.firstOrNull()?.type
    return when (pointerType) {
        PointerType.Mouse -> AppInputMode.MOUSE_KEYBOARD
        PointerType.Touch, PointerType.Stylus -> AppInputMode.TOUCH
        else -> null
    }
}

fun detectInputModeFromKey(keyEvent: KeyEvent): AppInputMode? =
    when (keyEvent.key) {
        Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight,
        Key.ButtonA, Key.ButtonB, Key.ButtonX, Key.ButtonY,
        Key.ButtonL1, Key.ButtonR1, Key.ButtonL2, Key.ButtonR2,
        Key.ButtonSelect, Key.ButtonStart,
        -> AppInputMode.CONTROLLER

        Key.W, Key.A, Key.S, Key.D, Key.R,
        Key.Spacebar, Key.Tab, Key.Escape, Key.Enter,
        -> AppInputMode.MOUSE_KEYBOARD

        else -> null
    }

@Composable
fun PlanetControlsCard(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    initialMode: AppInputMode = if (isWasmTarget) AppInputMode.MOUSE_KEYBOARD else AppInputMode.TOUCH,
    currentMode: AppInputMode = initialMode,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF209111E),
        border = BorderStroke(1.dp, Color(0x5538BDF8)),
        contentColor = Color.White,
        shadowElevation = 12.dp,
        modifier = modifier.widthIn(max = 380.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ControlsHeader(
                currentMode = currentMode,
                onClose = onClose,
            )

            when (currentMode) {
                AppInputMode.MOUSE_KEYBOARD -> {
                    ControlRow(symbol = UiSymbol.Clear, text = "W/A/S/D / Drag: Orbit Camera")
                    ControlRow(symbol = UiSymbol.Iss, text = "Scroll Wheel: Zoom In/Out")
                    ControlRow(symbol = UiSymbol.Earth, text = "Right/Middle Drag: Pan View")
                    ControlRow(symbol = UiSymbol.Info, text = "Left Click: Telemetry & Info")
                    ControlRow(symbol = UiSymbol.Time, text = "Double Click: Surface Dive")
                }
                AppInputMode.CONTROLLER -> {
                    ControlRow(symbol = UiSymbol.Clear, text = "Left Stick / D-Pad: Orbit Camera")
                    ControlRow(symbol = UiSymbol.Iss, text = "R2 / L2 Triggers: Zoom In/Out")
                    ControlRow(symbol = UiSymbol.Earth, text = "Right Stick: Pan View")
                    ControlRow(symbol = UiSymbol.Info, text = "Button A / Cross: Select & Inspect")
                    ControlRow(symbol = UiSymbol.Time, text = "Button Y / Triangle: Reset View")
                }
                AppInputMode.TOUCH -> {
                    ControlRow(symbol = UiSymbol.Clear, text = "1-Finger Drag: Orbit Camera")
                    ControlRow(symbol = UiSymbol.Iss, text = "2-Finger Pinch: Zoom In/Out")
                    ControlRow(symbol = UiSymbol.Earth, text = "2-Finger Drag: Pan View")
                    ControlRow(symbol = UiSymbol.Info, text = "Tap Planet: Telemetry & Info")
                    ControlRow(symbol = UiSymbol.Time, text = "Double Tap: Surface Dive")
                }
            }
        }
    }
}

@Composable
private fun ControlsHeader(
    currentMode: AppInputMode,
    onClose: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val titleText =
            when (currentMode) {
                AppInputMode.MOUSE_KEYBOARD -> "KEYBOARD & MOUSE"
                AppInputMode.CONTROLLER -> "GAMEPAD CONTROLLER"
                AppInputMode.TOUCH -> "TOUCH CONTROLS"
            }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = titleText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                letterSpacing = 0.08.sp,
            )
            Box(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "AUTO",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                )
            }
        }
        MinimalistCloseButton(onClick = onClose)
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
