package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.platform.PlatformCountryFlag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DossierBordersSection(
    borders: List<String>,
    allCountries: List<Country>,
    onSelectCountry: (Country) -> Unit,
) {
    if (borders.isEmpty()) return
    Spacer(Modifier.height(14.dp))
    Text(
        text = "BORDERING NATIONS (TAP TO FLY)",
        color = Color(0xFF64748B),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(6.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        borders.forEach { borderCode ->
            val neighbor =
                allCountries.find {
                    it.id.equals(borderCode, ignoreCase = true) || it.iso2.equals(borderCode, ignoreCase = true)
                }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x330284C7),
                border = BorderStroke(1.dp, Color(0x6638BDF8)),
                modifier = Modifier.then(if (neighbor != null) Modifier.clickable { onSelectCountry(neighbor) } else Modifier),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (neighbor != null) {
                        PlatformCountryFlag(
                            iso2 = neighbor.iso2,
                            modifier = Modifier.size(width = 24.dp, height = 16.dp),
                            contentDescription = "${neighbor.name} flag",
                        )
                    }
                    Text(neighbor?.name ?: borderCode, color = Color(0xFFE0F2FE), fontSize = 11.sp)
                }
            }
        }
    }
}
