package com.dirzaaulia.countries.ui.dossier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    var isExpanded by remember(country.id) { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xF20A111E),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        shadowElevation = 18.dp,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = if (isExpanded) 560.dp else 68.dp)
            .animateContentSize(animationSpec = tween(350, easing = FastOutSlowInEasing))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(country.id) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -10f) {
                            isExpanded = true
                        } else if (dragAmount > 10f) {
                            if (isExpanded) {
                                isExpanded = false
                            } else {
                                onClose()
                            }
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .then(if (isExpanded) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            ) {
                // 0. Drag Handle Pill Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(Color(0xFF64748B), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 1. Initial Compact Peek Header: Flag + Country Name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 36.dp)
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = country.flagEmoji,
                        fontSize = 28.sp
                    )
                    Text(
                        text = country.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isExpanded) "▼" else "▲",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 2. Expanded Full Dossier Details
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        val subtitleText = listOfNotNull(
                            country.capital.takeIf { it.isNotEmpty() }?.let { "Capital: $it" },
                            country.continent.takeIf { it.isNotEmpty() }
                        ).joinToString(" • ")

                        if (subtitleText.isNotEmpty()) {
                            Text(
                                text = subtitleText,
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        // Badges Row
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

                        // Subtitle: Official and Native Name
                        val displayOfficial = country.officialName.ifEmpty { country.formalName }
                        if (displayOfficial.isNotEmpty() && displayOfficial != country.name) {
                            Spacer(modifier = Modifier.height(6.dp))
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live API Status Strip
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

                        // Real-Time Weather Card
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
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Primary Geography & Demographics
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

                        // World Bank Economic Data
                        liveDetails?.let { live ->
                            if (live.isLiveWorldBankLoaded) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "WORLD BANK ECONOMIC DATA",
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
                            }
                        }

                        // Culture, Currencies & Administration
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "CULTURE & ADMINISTRATION",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (country.currencies.isNotEmpty()) {
                            Text("Official Currencies:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                country.currencies.forEach { curr ->
                                    ChipPill(text = curr, icon = "💵", backgroundColor = Color(0x2210B981), borderColor = Color(0x4410B981), textColor = Color(0xFFA7F3D0))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (country.languages.isNotEmpty()) {
                            Text("Official Languages:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                country.languages.forEach { lang ->
                                    ChipPill(text = lang, icon = "🗣️", backgroundColor = Color(0x228B5CF6), borderColor = Color(0x448B5CF6), textColor = Color(0xFFDDD6FE))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Bordering Nations
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

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons
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

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Pinned Close Button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(30.dp)
                    .background(Color(0xE61E293B), CircleShape)
                    .border(BorderStroke(1.dp, Color(0x33FFFFFF)), CircleShape)
            ) {
                Text(
                    text = "✕",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
