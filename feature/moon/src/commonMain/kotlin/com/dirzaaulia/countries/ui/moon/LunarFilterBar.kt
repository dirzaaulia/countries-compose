package com.dirzaaulia.countries.ui.moon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.dirzaaulia.countries.domain.moon.LunarLandmarkType

@Composable
fun LunarFilterBar(
    selectedCategory: LunarLandmarkType?,
    onSelectCategory: (LunarLandmarkType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = listOf(null) + LunarLandmarkType.entries

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF209111E),
        border = BorderStroke(1.dp, Color(0x4438BDF8)),
        shadowElevation = 8.dp,
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                val (label, accentColor) =
                    when (cat) {
                        null -> "ALL" to Color(0xFF38BDF8)
                        LunarLandmarkType.CREWED_APOLLO -> "APOLLO" to Color(0xFFF59E0B)
                        LunarLandmarkType.HISTORIC_ROBOTIC -> "USSR" to Color(0xFFF43F5E)
                        LunarLandmarkType.MODERN_INTERNATIONAL -> "FLEET" to Color(0xFF06B6D4)
                        LunarLandmarkType.COMMERCIAL_CLPS -> "CLPS" to Color(0xFF10B981)
                        LunarLandmarkType.LUNAR_MARE -> "MARIA" to Color(0xFFC084FC)
                        LunarLandmarkType.IMPACT_CRATER -> "CRATERS" to Color(0xFF94A3B8)
                        LunarLandmarkType.ARTEMIS_SOUTH_POLE -> "ARTEMIS" to Color(0xFF8B5CF6)
                    }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) accentColor.copy(alpha = 0.3f) else Color(0x221E293B),
                    border = BorderStroke(1.dp, if (isSelected) accentColor else Color(0x3338BDF8)),
                    modifier = Modifier.clickable { onSelectCategory(cat) },
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
        }
    }
}
