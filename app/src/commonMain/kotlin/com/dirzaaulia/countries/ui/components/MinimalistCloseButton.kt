package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern borderless close button: a clean vector X icon in muted slate that brightens
 * with a subtle circular ripple on press. No frosted circle border — pure minimalism.
 *
 * Task 51: Close Button (X) Design Revamp — Option 1: Borderless Flush Icon.
 */
@Composable
fun MinimalistCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 36.dp,
    iconSize: Dp = 16.dp,
    iconColor: Color = Color(0xFF94A3B8)
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
        interactionSource = remember { MutableInteractionSource() },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor,
            containerColor = Color.Transparent
        )
    ) {
        Canvas(modifier = Modifier.size(iconSize)) {
            val stroke = 1.75.dp.toPx()
            val inset = size.width * 0.18f
            drawLine(
                color = iconColor,
                start = Offset(inset, inset),
                end = Offset(size.width - inset, size.height - inset),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = iconColor,
                start = Offset(size.width - inset, inset),
                end = Offset(inset, size.height - inset),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
