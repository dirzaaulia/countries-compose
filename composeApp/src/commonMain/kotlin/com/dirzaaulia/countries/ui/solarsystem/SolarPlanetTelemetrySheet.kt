package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SolarPlanetTelemetrySheet(
    livePlanet: LivePlanetPosition?,
    onClose: () -> Unit,
    onDiveToPlanet: (PlanetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (livePlanet == null) return
    val planet = livePlanet.planet

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF209111E),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PlanetSheetHeader(livePlanet, onClose)
            PlanetMetricGrid(livePlanet)
            Text(
                text = planet.description,
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            DiveButton(planet.id, onDiveToPlanet)
        }
    }
}

@Composable
private fun PlanetSheetHeader(
    livePlanet: LivePlanetPosition,
    onClose: () -> Unit,
) {
    val planet = livePlanet.planet
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(planet.colorHex)),
            )
            Column {
                Text(
                    text = planet.symbolTag,
                    color = Color(planet.colorHex),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = planet.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun PlanetMetricGrid(livePlanet: LivePlanetPosition) {
    val planet = livePlanet.planet
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TelemetryStatTile(
            label = "SUN DISTANCE",
            value = "${formatDecimal(livePlanet.distanceAu)} AU",
            subtext = "${formatNumber(livePlanet.distanceKm / 1_000_000.0)}M km",
            modifier = Modifier.weight(1f),
        )
        TelemetryStatTile(
            label = "VELOCITY",
            value = "${formatDecimal(planet.orbitalSpeedKms)} km/s",
            subtext = "Orbital speed",
            modifier = Modifier.weight(1f),
        )
        TelemetryStatTile(
            label = "PERIOD",
            value = "${formatNumber(planet.orbitalPeriodDays)} d",
            subtext = "${formatDecimal(planet.orbitalPeriodDays / 365.25)} yrs",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TelemetryStatTile(
    label: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .padding(10.dp),
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(1.dp))
        Text(text = subtext, color = Color(0xFF64748B), fontSize = 10.sp)
    }
}

@Composable
private fun DiveButton(
    planetId: PlanetId,
    onDiveToPlanet: (PlanetId) -> Unit,
) {
    val canDive = planetId != PlanetId.SUN
    val label =
        when (planetId) {
            PlanetId.EARTH -> "Dive to Earth Surface"
            PlanetId.MOON -> "Explore Lunar Surface"
            PlanetId.MARS -> "Explore Mars Terrain"
            else -> "Dive into ${planetId.name.lowercase().replaceFirstChar { it.uppercase() }} Surface"
        }

    Button(
        onClick = { if (canDive) onDiveToPlanet(planetId) },
        enabled = canDive,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0284C7),
                disabledContainerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                disabledContentColor = Color(0xFF64748B),
            ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(44.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SemanticIcon(
                symbol =
                    when (planetId) {
                        PlanetId.MARS -> UiSymbol.Mars
                        PlanetId.MOON -> UiSymbol.Moon
                        else -> UiSymbol.Earth
                    },
                contentDescription = null,
                tint = if (canDive) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(18.dp),
            )
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
