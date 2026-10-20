package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

@Composable
fun SolarZoomControlHud(
    zoomScale: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onResetCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xCC09111E),
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color(0x2238BDF8)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // Zoom In (+)
            Box(
                modifier =
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onZoomIn() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Zoom in",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(22.dp),
                )
            }

            // Current Zoom Indicator & Reset to 1.0x
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onResetZoom() }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center,
            ) {
                val zoomRounded = (round(zoomScale * 10.0) / 10.0)
                Text(
                    text = "${zoomRounded}x",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // Zoom Out (-)
            Box(
                modifier =
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onZoomOut() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = "Zoom out",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(22.dp),
                )
            }

            // Reset Camera Orbit & Position
            Box(
                modifier =
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onResetCamera() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Restore,
                    contentDescription = "Reset camera view",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
