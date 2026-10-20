package com.dirzaaulia.countries.ui.economy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

enum class MarketFilter {
    ALL,
    OPEN,
    SOON,
    CLOSED,
}

@Composable
fun MarketFilterRow(
    selectedFilter: MarketFilter,
    allCount: Int,
    openCount: Int,
    onSelectFilter: (MarketFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MarketFilterChip(
            label = "ALL ($allCount)",
            isSelected = selectedFilter == MarketFilter.ALL,
            color = Color(0xFF38BDF8),
            onClick = { onSelectFilter(MarketFilter.ALL) },
            modifier = Modifier.weight(1f),
        )
        MarketFilterChip(
            label = "OPEN ($openCount)",
            isSelected = selectedFilter == MarketFilter.OPEN,
            color = Color(0xFF10B981),
            onClick = { onSelectFilter(MarketFilter.OPEN) },
            modifier = Modifier.weight(1f),
        )
        MarketFilterChip(
            label = "SOON",
            isSelected = selectedFilter == MarketFilter.SOON,
            color = Color(0xFFF59E0B),
            onClick = { onSelectFilter(MarketFilter.SOON) },
            modifier = Modifier.weight(1f),
        )
        MarketFilterChip(
            label = "CLOSED",
            isSelected = selectedFilter == MarketFilter.CLOSED,
            color = Color(0xFF64748B),
            onClick = { onSelectFilter(MarketFilter.CLOSED) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun MarketFilterChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.25f) else Color(0x221E293B),
        border = BorderStroke(1.dp, if (isSelected) color else Color(0x4464748B)),
        modifier = modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}
