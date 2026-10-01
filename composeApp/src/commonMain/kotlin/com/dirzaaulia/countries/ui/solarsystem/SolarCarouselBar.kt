package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.dirzaaulia.countries.domain.solarsystem.LivePlanetPosition

@Composable
internal fun SolarCarouselBar(
    planets: List<LivePlanetPosition>,
    selectedPlanet: LivePlanetPosition?,
    onSelectPlanet: (LivePlanetPosition) -> Unit,
    onResetView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            ResetViewChip(onResetView)
        }
        items(planets, key = { it.planet.id.name }) { livePos ->
            val isSelected = selectedPlanet?.planet?.id == livePos.planet.id
            PlanetChip(livePos, isSelected) { onSelectPlanet(livePos) }
        }
    }
}

@Composable
private fun ResetViewChip(onResetView: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xDD0F172A),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.clickable(onClick = onResetView),
    ) {
        Text(
            text = "Reset Orbit View",
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun PlanetChip(
    livePos: LivePlanetPosition,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val planet = livePos.planet
    val chipBorder = if (isSelected) Color(planet.colorHex) else Color(0x22FFFFFF)
    val chipBg = if (isSelected) Color(0xEE1E293B) else Color(0xCC09111E)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = chipBg,
        border = BorderStroke(1.dp, chipBorder),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(planet.colorHex)),
            )
            Text(
                text = planet.name,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}
