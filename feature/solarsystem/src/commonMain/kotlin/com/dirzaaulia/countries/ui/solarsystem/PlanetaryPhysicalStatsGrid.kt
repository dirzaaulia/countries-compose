package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryDossier
import com.dirzaaulia.countries.util.formatDecimal

@Composable
fun PlanetaryPhysicalStatsGrid(
    dossier: PlanetaryDossier,
    modifier: Modifier = Modifier,
) {
    val specs = dossier.specs
    val orbital = dossier.orbitalData

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DossierStatCard(
                label = "MASS",
                primary = specs.massNotation,
                secondary = "${formatDecimal(specs.massEarths)} Earths",
                modifier = Modifier.weight(1f),
            )
            DossierStatCard(
                label = "SURFACE GRAVITY",
                primary = "${formatDecimal(specs.surfaceGravityMs2)} m/s^2",
                secondary = "${formatDecimal(specs.gravityEarths)} g",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DossierStatCard(
                label = "ESCAPE VELOCITY",
                primary = "${formatDecimal(specs.escapeVelocityKms)} km/s",
                secondary = "From surface datum",
                modifier = Modifier.weight(1f),
            )
            DossierStatCard(
                label = "MEAN DENSITY",
                primary = "${formatDecimal(specs.meanDensityGcm3)} g/cm^3",
                secondary = if (specs.meanDensityGcm3 < 1.0) "Floats in water" else "Bulk density",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DossierStatCard(
                label = "SOLAR FLUX",
                primary = "${formatDecimal(specs.solarFluxWm2)} W/m^2",
                secondary = "Solar irradiance",
                modifier = Modifier.weight(1f),
            )
            DossierStatCard(
                label = "AXIAL TILT",
                primary = "${formatDecimal(orbital.axialTiltDeg)} deg",
                secondary = if (orbital.axialTiltDeg > 90.0) "Retrograde spin" else "Obliquity",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DossierStatCard(
    label: String,
    primary: String,
    secondary: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = primary,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(1.dp))
        Text(
            text = secondary,
            color = Color(0xFF64748B),
            fontSize = 10.sp,
        )
    }
}
