package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
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
import com.dirzaaulia.countries.domain.solarsystem.PlanetId
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryDossier
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryDossierCatalog
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetaryDossierSheet(
    planetId: PlanetId?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (planetId == null) return
    val dossier = PlanetaryDossierCatalog.getDossier(planetId)
    val scrollState = rememberScrollState()

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
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DossierSheetHeader(dossier, onClose)
            Text(
                text = dossier.orbitalData.description,
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            PlanetaryPhysicalStatsGrid(dossier)
            PlanetaryTemperatureSection(dossier.specs)
            PlanetaryAtmosphereSection(dossier.atmosphere)
            ScientificHighlightsSection(dossier.scientificHighlights)
        }
    }
}

@Composable
private fun DossierSheetHeader(
    dossier: PlanetaryDossier,
    onClose: () -> Unit,
) {
    val planet = dossier.orbitalData
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = planet.symbolTag,
                        color = Color(planet.colorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "[${dossier.classification.label}]",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = "${planet.name} Planetary Dossier",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun ScientificHighlightsSection(highlights: List<String>) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "SCIENTIFIC HIGHLIGHTS & OBSERVATIONS",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        highlights.forEach { note ->
            HighlightBulletRow(note)
        }
    }
}

@Composable
private fun HighlightBulletRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = ">",
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = text,
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
    }
}
