package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.CountryFlagIcon
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun DossierHeaderSection(
    country: Country,
    displayCapital: String,
    dayNightStatus: String,
    civilTimeInfo: Pair<String, String>,
    headerGlowBrush: Brush,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(headerGlowBrush)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CountryFlagIcon(country = country)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitleText =
                    listOfNotNull(
                        displayCapital.takeIf { it.isNotEmpty() && it != "N/A" }?.let { "Capital: $it" },
                        country.continent.takeIf { it.isNotEmpty() },
                    ).joinToString(" • ")

                if (subtitleText.isNotEmpty()) {
                    Text(
                        text = subtitleText,
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            MinimalistCloseButton(onClick = onClose)
        }

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ChipPill(text = country.id)
            if (country.iso2.isNotEmpty() && country.iso2 != "-99") {
                ChipPill(text = country.iso2)
            }
            val (civilTime, civilTz) = civilTimeInfo
            when {
                dayNightStatus.startsWith("SPLIT") -> {
                    ChipPill(
                        text = "Day/Night Split • $civilTime ($civilTz)",
                        symbol = UiSymbol.Clear,
                        backgroundColor = Color(0x35F59E0B),
                        borderColor = Color(0x66F59E0B),
                        textColor = Color(0xFFFDE68A),
                    )
                }
                dayNightStatus == "DAY" -> {
                    ChipPill(
                        text = "Daylight • $civilTime ($civilTz)",
                        symbol = UiSymbol.Clear,
                        backgroundColor = Color(0x28F59E0B),
                        borderColor = Color(0x55F59E0B),
                        textColor = Color(0xFFFDE68A),
                    )
                }
                else -> {
                    ChipPill(
                        text = "Nighttime • $civilTime ($civilTz)",
                        symbol = UiSymbol.Moon,
                        backgroundColor = Color(0x286366F1),
                        borderColor = Color(0x556366F1),
                        textColor = Color(0xFFC7D2FE),
                    )
                }
            }
            if (country.timezones.size > 1) {
                ChipPill(
                    text = "${country.timezones.size} Timezones",
                    symbol = UiSymbol.Earth,
                    backgroundColor = Color(0x2238BDF8),
                    borderColor = Color(0x4438BDF8),
                )
            }
            if (country.unMember) {
                ChipPill(
                    text = "UN Member",
                    symbol = UiSymbol.Info,
                    backgroundColor = Color(0x220284C7),
                    borderColor = Color(0x440284C7),
                )
            }
            if (country.landlocked) {
                ChipPill(
                    text = "Landlocked",
                    symbol = UiSymbol.Landscape,
                    backgroundColor = Color(0x22F59E0B),
                    borderColor = Color(0x44F59E0B),
                    textColor = Color(0xFFFDE68A),
                )
            }
        }
    }
}
