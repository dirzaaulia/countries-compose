package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.AstronomyMath
import com.dirzaaulia.countries.Country
import com.dirzaaulia.countries.LiveCountryDetails
import com.dirzaaulia.countries.SunPosition
import com.dirzaaulia.countries.currentEpochMillis
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.InfoCard
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.util.formatArea
import com.dirzaaulia.countries.util.formatCoordinates
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber
import com.dirzaaulia.countries.util.formatPopulation

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
    onOpenMeteorology: (() -> Unit)? = null,
    onOpenWorldBank: (() -> Unit)? = null,
    onOpenNasaCrisis: (() -> Unit)? = null,
    sunPos: SunPosition? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Dynamic Diurnal / Nocturnal Solar Analysis
    val isDaylight = remember(country.id, sunPos) {
        sunPos?.let { AstronomyMath.isDaylight(country.center, it.vector) } ?: true
    }

    val civilTimeInfo = remember(country.timezones, country.center.lng) {
        val now = currentEpochMillis()
        val tz = country.timezones.firstOrNull() ?: ""
        val offsetHours = if (tz.startsWith("UTC")) {
            val isNegative = tz.contains("-")
            val raw = tz.removePrefix("UTC").removePrefix("+").removePrefix("-")
            val parts = raw.split(":")
            val h = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val m = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
            val total = h + (m / 60.0)
            if (isNegative) -total else total
        } else {
            kotlin.math.round(country.center.lng / 15.0)
        }
        val utcMillis = ((now % 86400000L) + 86400000L) % 86400000L
        val localMillis = ((utcMillis + (offsetHours * 3600000L).toLong()) % 86400000L + 86400000L) % 86400000L
        val hourInt = (localMillis / 3600000L).toInt()
        val minInt = ((localMillis % 3600000L) / 60000L).toInt()
        val formattedTime = "${hourInt.toString().padStart(2, '0')}:${minInt.toString().padStart(2, '0')}"
        val tzDisplay = if (tz.isNotEmpty()) tz else "UTC${if (offsetHours >= 0) "+${offsetHours.toInt()}" else offsetHours.toInt().toString()}"
        Pair(formattedTime, tzDisplay)
    }

    val localSolarTimeStr = remember(country.center.lng) {
        val now = currentEpochMillis()
        val utcMillis = ((now % 86400000L) + 86400000L) % 86400000L
        val localSolarHour = (((utcMillis / 3600000.0) + (country.center.lng / 15.0)) % 24.0 + 24.0) % 24.0
        val hourInt = localSolarHour.toInt()
        val minInt = ((localSolarHour - hourInt) * 60.0).toInt()
        "${hourInt.toString().padStart(2, '0')}:${minInt.toString().padStart(2, '0')}"
    }

    val containerColor = if (isDaylight) Color(0xF20B1626) else Color(0xF2070D18)
    val headerGlowBrush = if (isDaylight) {
        Brush.horizontalGradient(listOf(Color(0x300284C7), Color(0x18F59E0B), Color.Transparent))
    } else {
        Brush.horizontalGradient(listOf(Color(0x38312E81), Color(0x181E1B4B), Color.Transparent))
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = containerColor,
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = if (isDaylight) Color(0xFF64748B) else Color(0xFF475569),
                width = 36.dp,
                height = 4.dp
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header: Flag + Country Name & Subtitle + Redesigned Sleek Vector Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(headerGlowBrush)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = country.flagEmoji,
                    fontSize = 32.sp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = country.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val subtitleText = listOfNotNull(
                        country.capital.takeIf { it.isNotEmpty() }?.let { "Capital: $it" },
                        country.continent.takeIf { it.isNotEmpty() }
                    ).joinToString(" • ")

                    if (subtitleText.isNotEmpty()) {
                        Text(
                            text = subtitleText,
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Minimalist Close Button
                MinimalistCloseButton(onClick = onClose)
            }

            // 2. Badges Row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChipPill(text = country.id)
                if (country.iso2.isNotEmpty() && country.iso2 != "-99") {
                    ChipPill(text = country.iso2)
                }
                val (civilTime, civilTz) = civilTimeInfo
                if (isDaylight) {
                    ChipPill(
                        text = "Daylight • $civilTime ($civilTz)",
                        icon = "☀️",
                        backgroundColor = Color(0x28F59E0B),
                        borderColor = Color(0x55F59E0B),
                        textColor = Color(0xFFFDE68A)
                    )
                } else {
                    ChipPill(
                        text = "Nighttime • $civilTime ($civilTz)",
                        icon = "🌙",
                        backgroundColor = Color(0x286366F1),
                        borderColor = Color(0x556366F1),
                        textColor = Color(0xFFC7D2FE)
                    )
                }
                if (country.timezones.size > 1) {
                    ChipPill(
                        text = "${country.timezones.size} Timezones",
                        icon = "🌐",
                        backgroundColor = Color(0x2238BDF8),
                        borderColor = Color(0x4438BDF8)
                    )
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Text(
                                    text = "🟢 Live Planetary APIs Connected",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (onOpenNasaCrisis != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x33EF4444),
                                    border = BorderStroke(1.dp, Color(0x66EF4444)),
                                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = "🚨 Crisis Monitor ↗",
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { onOpenNasaCrisis.invoke() }
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Real-Time Weather Card with Dynamic Atmospheric Motion Overlay
            liveDetails?.let { live ->
                if (live.weatherTempC != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x350F172A),
                        border = BorderStroke(1.dp, Color(0x3338BDF8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Atmospheric particle canvas (Rain, Snow, Lightning, Clouds, Heatwave, Solar flares)
                            WeatherAtmosphericOverlay(
                                weatherCode = live.weatherCode,
                                tempC = live.weatherTempC,
                                modifier = Modifier.matchParentSize()
                            )

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

                                if (onOpenMeteorology != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onOpenMeteorology.invoke() },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3338BDF8)),
                                        border = BorderStroke(1.dp, Color(0x5538BDF8)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "View Meteorology Station (7-Day & Hourly) ↗",
                                            color = Color(0xFFE0F2FE),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
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
                    subValue = "Solar: ~$localSolarTimeStr",
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

                    if (onOpenWorldBank != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onOpenWorldBank.invoke() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x3310B981)),
                            border = BorderStroke(1.dp, Color(0x5510B981)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "View Macroeconomic Analysis (5-Year Trends) ↗",
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
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
                    Text("Dismiss", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
