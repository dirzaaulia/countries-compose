package com.dirzaaulia.countries.ui.dossier.administration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@Composable
internal fun AdminHeaderCard(
    country: Country,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x330284C7))
                .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlatformCountryFlag(country.iso2, Modifier.size(32.dp), "${country.name} flag")
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ADMINISTRATIVE EXPLORER",
                color = Color(0xFF38BDF8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = country.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
internal fun AdminLevelSelector(
    administrativeLevel: AdminLevel,
    selectedAdm1Division: AdministrativeDivision?,
    onShowAdm1: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (administrativeLevel == AdminLevel.ADM1) Color(0x3338BDF8) else Color(0x1F0F172A),
                border = BorderStroke(1.dp, if (administrativeLevel == AdminLevel.ADM1) Color(0x6638BDF8) else Color(0x33FFFFFF)),
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onShowAdm1() },
            ) {
                Text(
                    text = "ADM1 (Provinces)",
                    color = if (administrativeLevel == AdminLevel.ADM1) Color(0xFFE0F2FE) else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }

            if (selectedAdm1Division != null || administrativeLevel == AdminLevel.ADM2) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (administrativeLevel == AdminLevel.ADM2) Color(0x3338BDF8) else Color(0x1F0F172A),
                    border = BorderStroke(1.dp, if (administrativeLevel == AdminLevel.ADM2) Color(0x6638BDF8) else Color(0x33FFFFFF)),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                ) {
                    Text(
                        text = "ADM2 (Local Divisions)",
                        color = if (administrativeLevel == AdminLevel.ADM2) Color(0xFFE0F2FE) else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }
        }

        if (administrativeLevel == AdminLevel.ADM2 && selectedAdm1Division != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Province: ${selectedAdm1Division.name}",
                    color = Color(0xFF67E8F9),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
internal fun AdminDivisionRow(
    division: AdministrativeDivision,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0x3338BDF8) else Color(0x180F172A),
        border = BorderStroke(1.dp, if (isSelected) Color(0x8838BDF8) else Color(0x1FFFFFFF)),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = division.name,
                    color = if (isSelected) Color(0xFF67E8F9) else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text =
                        if (division.shapeType.isNotEmpty()) {
                            "${division.shapeType} • Code: ${division.code}"
                        } else {
                            "Code: ${division.code}"
                        },
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF0284C7) else Color(0x330284C7),
                border = BorderStroke(1.dp, Color(0x6638BDF8)),
            ) {
                Text(
                    text = "FLY",
                    color = Color(0xFFE0F2FE),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}
