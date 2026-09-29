package com.dirzaaulia.countries.ui.moon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LunarFarSideBadge(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!isVisible) return

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xCC0F172A),
        border = BorderStroke(1.dp, Color(0xFFC084FC)),
        modifier = modifier,
    ) {
        Text(
            text = "[ LUNAR FAR SIDE (TIDALLY LOCKED) ]",
            color = Color(0xFFC084FC),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}
