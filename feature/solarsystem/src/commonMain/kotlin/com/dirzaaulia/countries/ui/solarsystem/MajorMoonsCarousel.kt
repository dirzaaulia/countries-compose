package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.MajorMoon
import com.dirzaaulia.countries.domain.solarsystem.MajorMoonsCatalog
import com.dirzaaulia.countries.domain.solarsystem.PlanetId

@Composable
fun MajorMoonsCarousel(
    planetId: PlanetId,
    onMoonSelected: (MajorMoon) -> Unit,
    modifier: Modifier = Modifier,
) {
    val moons = remember(planetId) { MajorMoonsCatalog.getMoonsForPlanet(planetId) }
    if (moons.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "MAJOR NATURAL SATELLITES",
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(moons, key = { it.id }) { moon ->
                MajorMoonCard(moon = moon, onClick = { onMoonSelected(moon) })
            }
        }
    }
}

@Composable
private fun MajorMoonCard(
    moon: MajorMoon,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .width(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .clickable(onClick = onClick)
                .padding(12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .background(Color(moon.colorHex), RoundedCornerShape(6.dp)),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = moon.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${moon.radiusKm} km radius",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Orbit: ${moon.orbitalDistanceKm} km",
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
            )
        }
    }
}
