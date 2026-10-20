package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun TimeMachinePlaybackBar(
    isPlaying: Boolean,
    isReverse: Boolean,
    timeMultiplier: Float,
    onTogglePlayPause: () -> Unit,
    onToggleDirection: () -> Unit,
    onSelectSpeedMultiplier: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val multipliers = listOf(1f, 60f, 300f, 3600f, 86400f)
    val labels = listOf("1x", "60x", "300x", "1h/s", "1d/s")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isReverse) Color(0x33EF4444) else Color(0x22FFFFFF),
            border = BorderStroke(1.dp, if (isReverse) Color(0x88EF4444) else Color(0x44FFFFFF)),
            modifier = Modifier.clickable { onToggleDirection() },
        ) {
            Text(
                text = if (isReverse) "◄ REV" else "FWD ►",
                color = if (isReverse) Color(0xFFF87171) else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isPlaying) Color(0xFFF59E0B) else Color(0x33F59E0B),
            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
            modifier = Modifier.clickable { onTogglePlayPause() },
        ) {
            Text(
                text = if (isPlaying) "❚❚ PAUSE" else "▶ PLAY",
                color = if (isPlaying) Color.Black else Color(0xFFF59E0B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            multipliers.forEachIndexed { index, mult ->
                val isSelected = timeMultiplier == mult
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) Color(0xFF38BDF8) else Color(0x2238BDF8),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0x4438BDF8)),
                    modifier = Modifier.clickable { onSelectSpeedMultiplier(mult) },
                ) {
                    Text(
                        text = labels[index],
                        color = if (isSelected) Color.Black else Color(0xFFE0F2FE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}
