package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.Country
import com.dirzaaulia.countries.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.InfoCard
import com.dirzaaulia.countries.util.formatArea
import com.dirzaaulia.countries.util.formatCoordinates
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber
import com.dirzaaulia.countries.util.formatPopulation

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountryDossierSheet(
    country: Country,
    liveDetails: LiveCountryDetails?,
    isFetchingLive: Boolean,
    allCountries: List<Country>,
    onClose: () -> Unit,
    onCenterView: () -> Unit,
    onNextCountry: () -> Unit,
    onSelectCountry: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xF20A111E),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        shadowElevation = 24.dp,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 560.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Header: Flag + Name + Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 40.dp), // Leaves room for pinned close button
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = country.flagEmoji,
                        fontSize = 38.sp
                    )
                    Column {
                        Text(
                            text = country.name,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ChipPill(text = country.id)
                            if (country.iso2.isNotEmpty() && country.iso2 != "-99") {
                                ChipPill(text = country.iso2)
                            }
                            if (country.unMember) {
                                ChipPill(
                                    text = "UN Member",
                                    icon = "🇺🇳",
                                    backgroundColor = Color(0x220284C7),
                                    borderColor = Color(0x440284C7)
                                )
                            }
                            if (country.landlocked) {
                                ChipPill(
                                    text = "Landlocked",
                                    icon = "🏔️",
                                    backgroundColor = Color(0x22F59E0B),
                                    borderColor = Color(0x44F59E0B),
                                    textColor = Color(0xFFFDE68A)
                                )
                            }
                        }
                    }
                }

                // Subtitle: Official and Native Name
                val displayOfficial = country.officialName.ifEmpty { country.formalName }
                if (displayOfficial.isNotEmpty() && displayOfficial != country.name) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (country.nativeName.isNotEmpty() && country.nativeName != displayOfficial) {
                            "$displayOfficial • ${country.nativeName}"
                        } else {
                            displayOfficial
                        },
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                val locationTag = listOfNotNull(
                    country.continent.takeIf { it.isNotEmpty() },
                    country.subregion.takeIf { it.isNotEmpty() && it != country.continent }
                ).joinToString(" • ")

                if (locationTag.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = locationTag,
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Live API Status Strip with Loading Spinner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x1F0F172A),
                    border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isFetchingLive) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "📡 Querying Live World Bank, Weather & NASA APIs...",
                                color = Color(0xFF7DD3FC),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Text(
                                text = "🟢 Live Planetary APIs Connected: World Bank • Open-Meteo • NASA",
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Real-Time Loading Indicator Card
                if (isFetchingLive && liveDetails?.weatherTempC == null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x300F172A),
                        border = BorderStroke(1.dp, Color(0x3338BDF8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF38BDF8)
                            )
                            Column {
                                Text(
                                    text = "Streaming Real-Time Capital Weather...",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Connecting to Open-Meteo and World Bank telemetry",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // 3. Open-Meteo Real-Time Weather Card
                liveDetails?.let { live ->
                    if (live.weatherTempC != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x350F172A),
                            border = BorderStroke(1.dp, Color(0x3338BDF8)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = live.weatherIcon ?: "☀️", fontSize = 24.sp)
                                        Column {
                                            Text(
                                                text = "${live.weatherTempC}°C",
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = live.weatherDescription ?: "Fair",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "CAPITAL WEATHER",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = country.capital.ifEmpty { country.name },
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    live.weatherHumidity?.let {
                                        Text("💧 $it% Humidity", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    live.weatherWindSpeed?.let {
                                        Text("💨 ${it.toInt()} km/h Wind", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    live.uvIndex?.let {
                                        Text("☀️ UV $it", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                }

                                if (live.sunrise != null && live.sunset != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("🌅 Sunrise: ${live.sunrise}", color = Color(0xFF7DD3FC), fontSize = 10.sp)
                                        Text("🌇 Sunset: ${live.sunset}", color = Color(0xFFFDBA74), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Primary Geography & Demographics
                Text(
                    text = "GEOGRAPHY & DEMOGRAPHICS",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard(
                        icon = "👥",
                        title = "POPULATION",
                        value = formatPopulation(country.population),
                        subValue = if (country.population > 0) "${formatNumber(country.population.toDouble())} people" else null,
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        icon = "📐",
                        title = "AREA",
                        value = formatArea(country.areaSqKm),
                        subValue = if (country.landlocked) "Landlocked" else "Coastal",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard(
                        icon = "🏛️",
                        title = "CAPITAL CITY",
                        value = country.capital.ifEmpty { "N/A" },
                        subValue = country.capitalLatLng?.let { formatCoordinates(it) },
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        icon = "🌐",
                        title = "CENTER COORDINATES",
                        value = formatCoordinates(country.center),
                        modifier = Modifier.weight(1f)
                    )
                }

                // 5. World Bank Open Data Indicators
                liveDetails?.let { live ->
                    if (live.isLiveWorldBankLoaded) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "WORLD BANK ECONOMIC & CLIMATE DATA",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            live.gdpPerCapita?.let {
                                InfoCard(
                                    icon = "💵",
                                    title = "GDP PER CAPITA",
                                    value = "$${formatNumber(it)} USD",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            live.lifeExpectancy?.let {
                                InfoCard(
                                    icon = "🩺",
                                    title = "LIFE EXPECTANCY",
                                    value = "${formatDecimal(it)} Years",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (live.inflationRate != null || live.unemploymentRate != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                live.inflationRate?.let {
                                    InfoCard(
                                        icon = "📈",
                                        title = "INFLATION RATE",
                                        value = "${formatDecimal(it)}%",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                live.unemploymentRate?.let {
                                    InfoCard(
                                        icon = "💼",
                                        title = "UNEMPLOYMENT",
                                        value = "${formatDecimal(it)}%",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        if (live.renewableEnergyShare != null || live.co2Emissions != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                live.renewableEnergyShare?.let {
                                    InfoCard(
                                        icon = "🌱",
                                        title = "RENEWABLE ENERGY",
                                        value = "${formatDecimal(it)}% of total",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                live.co2Emissions?.let {
                                    InfoCard(
                                        icon = "🏭",
                                        title = "CO₂ EMISSIONS",
                                        value = "${formatDecimal(it)} tons/capita",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Culture, Currencies & Administration
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "CULTURE, CURRENCY & ADMINISTRATION",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Currencies & Languages
                if (country.currencies.isNotEmpty()) {
                    Text(
                        text = "Official Currencies:",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        country.currencies.forEach { curr ->
                            ChipPill(
                                text = curr,
                                icon = "💵",
                                backgroundColor = Color(0x2210B981),
                                borderColor = Color(0x4410B981),
                                textColor = Color(0xFFA7F3D0)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (country.languages.isNotEmpty()) {
                    Text(
                        text = "Official Languages:",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        country.languages.forEach { lang ->
                            ChipPill(
                                text = lang,
                                icon = "🗣️",
                                backgroundColor = Color(0x228B5CF6),
                                borderColor = Color(0x448B5CF6),
                                textColor = Color(0xFFDDD6FE)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (country.callingCode.isNotEmpty()) {
                        InfoCard(
                            icon = "📞",
                            title = "CALLING CODE",
                            value = country.callingCode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (country.demonym.isNotEmpty()) {
                        InfoCard(
                            icon = "👤",
                            title = "DEMONYM",
                            value = country.demonym,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (country.drivingSide.isNotEmpty()) {
                        InfoCard(
                            icon = "🚗",
                            title = "DRIVING SIDE",
                            value = country.drivingSide,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (country.timezones.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Timezones:",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        country.timezones.forEach { tz ->
                            ChipPill(
                                text = tz,
                                icon = "⏰",
                                backgroundColor = Color(0x22334155),
                                borderColor = Color(0x44475569),
                                textColor = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                // 7. NASA Active Natural Events Nearby
                liveDetails?.nasaEvents?.takeIf { it.isNotEmpty() }?.let { events ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🛰️ NASA ACTIVE EARTH EVENTS NEARBY",
                        color = Color(0xFFF97316),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    events.forEach { event ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x24EA580C),
                            border = BorderStroke(1.dp, Color(0x44EA580C)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(event.categoryIcon, fontSize = 16.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        color = Color(0xFFFED7AA),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${event.category} • Date: ${event.date}",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 8. Interactive Bordering Nations
                if (country.borders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🗺️ BORDERING NATIONS (TAP TO FLY)",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        country.borders.forEach { borderCode ->
                            val neighbor = allCountries.find { it.id == borderCode || it.iso2 == borderCode }
                            val label = if (neighbor != null) {
                                "${neighbor.flagEmoji} ${neighbor.name}"
                            } else {
                                "📍 $borderCode"
                            }

                            ChipPill(
                                text = label,
                                backgroundColor = Color(0x330284C7),
                                borderColor = Color(0x6638BDF8),
                                textColor = Color(0xFFE0F2FE),
                                onClick = {
                                    if (neighbor != null) {
                                        onSelectCountry(neighbor)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 9. Action Buttons: Re-focus, Next Random Country, and Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0x44EF4444)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                        modifier = Modifier.weight(0.9f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text("Close ✕", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onCenterView,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0x6638BDF8)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        modifier = Modifier.weight(1.1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text("Center View", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onNextCountry,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text("Next Country 🎲", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            // Pinned Close Button (stays permanently visible at top-right regardless of scroll position)
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .size(34.dp)
                    .background(Color(0xE61E293B), CircleShape)
                    .border(BorderStroke(1.dp, Color(0x33FFFFFF)), CircleShape)
            ) {
                Text(
                    text = "✕",
                    color = Color(0xFFE2E8F0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
