package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun SolarTelemetryActionRow(
    planetId: PlanetId,
    onDiveToPlanet: (PlanetId) -> Unit,
    onOpenDossier: (PlanetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DossierButton(
            planetId = planetId,
            onOpenDossier = onOpenDossier,
            modifier = Modifier.weight(1f),
        )
        DiveButton(
            planetId = planetId,
            onDiveToPlanet = onDiveToPlanet,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun DossierButton(
    planetId: PlanetId,
    onOpenDossier: (PlanetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = { onOpenDossier(planetId) },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF38BDF8),
            ),
        modifier = modifier.height(44.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(16.dp),
            )
            Text(text = "Specs Dossier", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun DiveButton(
    planetId: PlanetId,
    onDiveToPlanet: (PlanetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canDive = planetId != PlanetId.SUN
    val label =
        when (planetId) {
            PlanetId.EARTH -> "Dive Earth"
            PlanetId.MOON -> "Explore Moon"
            PlanetId.MARS -> "Explore Mars"
            else -> "Dive Planet"
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
        modifier = modifier.height(44.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                modifier = Modifier.size(16.dp),
            )
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
