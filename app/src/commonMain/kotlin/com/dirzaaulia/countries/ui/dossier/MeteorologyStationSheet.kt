package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.DailyForecastItem
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.components.uiSymbolFor
import com.dirzaaulia.countries.ui.dossier.weather.DiurnalSolarArcCanvas
import com.dirzaaulia.countries.ui.dossier.weather.HourlySplineChart
import com.dirzaaulia.countries.ui.dossier.weather.WindCompassCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeteorologyStationSheet(
    country: Country,
    liveDetails: LiveCountryDetails?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val details = liveDetails ?: return

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color(0xF2070D18),
        tonalElevation = 16.dp,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                color = Color(0x4438BDF8),
                shape = RoundedCornerShape(3.dp),
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Header & Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(country.flagEmoji, fontSize = 28.sp)
                            Column {
                                Text(
                                    text = "METEOROLOGY STATION",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                )
                                Text(
                                    text = "${country.name} (${country.capital.ifEmpty { "Capital" }})",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        MinimalistCloseButton(onClick = onClose)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Hero Atmospheric Station Card with Particle Overlay
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0x300F172A),
                        border = BorderStroke(1.dp, Color(0x3338BDF8)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            WeatherAtmosphericOverlay(
                                weatherCode = details.weatherCode,
                                tempC = details.weatherTempC,
                                modifier = Modifier.matchParentSize(),
                            )

                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${details.weatherTempC?.toInt() ?: 20}°",
                                                color = Color.White,
                                                fontSize = 44.sp,
                                                fontWeight = FontWeight.Black,
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "C",
                                                color = Color(0xFF38BDF8),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                        Text(
                                            text = details.weatherDescription ?: "Fair Conditions",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                        val feelsLike = ((details.weatherTempC ?: 20.0) + ((details.weatherHumidity ?: 50) - 50) * 0.05).toInt()
                                        Text(
                                            text = "Feels like $feelsLike°C • Wind Chill index normal",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp,
                                        )
                                    }

                                    SemanticIcon(
                                        symbol = uiSymbolFor(details.weatherIcon ?: "[CLEAR]"),
                                        contentDescription = details.weatherDescription,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(36.dp),
                                    )
                                }

                                // Mini metrics row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    SensorBadge(label = "HUMIDITY", value = "${details.weatherHumidity ?: 50}%", symbol = UiSymbol.Rain)
                                    SensorBadge(label = "WIND", value = "${details.weatherWindSpeed?.toInt() ?: 12} km/h", symbol = UiSymbol.Wind)
                                    SensorBadge(label = "UV INDEX", value = "${details.uvIndex ?: 4.0}", symbol = UiSymbol.Clear)
                                    SensorBadge(label = "PRESSURE", value = "${details.surfacePressureHpa?.toInt() ?: 1013} hPa", symbol = UiSymbol.Pressure)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Diurnal Daylight Arc
                    Text(
                        text = "DIURNAL SOLAR TRAJECTORY ARC",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x350F172A),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            DiurnalSolarArcCanvas(
                                sunrise = details.sunrise ?: "06:00",
                                sunset = details.sunset ?: "18:00",
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(84.dp),
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "🌅 Sunrise ${details.sunrise ?: "06:00"}",
                                    color = Color(0xFFFDE68A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = "☀️ Solar Noon",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                )
                                Text(
                                    text = "🌇 Sunset ${details.sunset ?: "18:00"}",
                                    color = Color(0xFFF97316),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. 24-Hour Interactive Hourly Temperature & Precipitation Curve
                    Text(
                        text = "📈 24-HOUR HOURLY TEMPERATURE & RAIN PROBABILITY",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x350F172A),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            HourlySplineChart(
                                hourly = details.hourlyForecast,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 4. 7-Day Forecast Cards
                    Text(
                        text = "📅 7-DAY SYNOPTIC OUTLOOK",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        details.dailyForecast.forEach { day ->
                            DailyForecastRow(day = day)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 5. Atmospheric Sensors & Wind Direction Compass Rose
                    Text(
                        text = "🧭 WIND VECTOR & ATMOSPHERIC SENSORS",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Wind Compass Rose
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0x350F172A),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "WIND BEARING",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                WindCompassCanvas(
                                    degrees = details.windDirectionDeg ?: 45.0,
                                    modifier = Modifier.size(90.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val knots = ((details.weatherWindSpeed ?: 12.0) * 0.539957).toInt()
                                Text(
                                    text = "${(details.windDirectionDeg ?: 45.0).toInt()}° • $knots kts",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Barometric & UV Sensors
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0x350F172A),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Column {
                                    Text("BAROMETRIC PRESSURE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${details.surfacePressureHpa?.toInt() ?: 1013} hPa",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = if ((details.surfacePressureHpa ?: 1013.0) >= 1013.0) "High Pressure (Stable)" else "Low Pressure (Unstable)",
                                        color = Color(0xFF10B981),
                                        fontSize = 10.sp,
                                    )
                                }

                                HorizontalDivider(color = Color(0x22FFFFFF))

                                Column {
                                    Text("SOLAR UV INDEX", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    val uv = details.uvIndex ?: 3.0
                                    val (uvText, uvCol) =
                                        when {
                                            uv <= 2.9 -> "Low" to Color(0xFF10B981)
                                            uv <= 5.9 -> "Moderate" to Color(0xFFF59E0B)
                                            uv <= 7.9 -> "Very High" to Color(0xFFEF4444)
                                            else -> "Extreme" to Color(0xFF9333EA)
                                        }
                                    Text(
                                        text = "$uv ($uvText)",
                                        color = uvCol,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text("Protection: Sunglasses & SPF", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorBadge(
    label: String,
    value: String,
    symbol: UiSymbol,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0x221E293B),
        border = BorderStroke(1.dp, Color(0x18FFFFFF)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SemanticIcon(symbol = symbol, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color(0xFF64748B), fontSize = 8.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DailyForecastRow(day: DailyForecastItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x250F172A),
        border = BorderStroke(1.dp, Color(0x18FFFFFF)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = day.dayName,
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(76.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(day.weatherIcon, fontSize = 16.sp)
                if (day.precipitationProb > 0) {
                    Text(
                        text = "💧${day.precipitationProb}%",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            // Min/Max Spread
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${day.tempMin.toInt()}°",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                )
                // Color bar
                Box(
                    modifier =
                        Modifier
                            .width(48.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFFF59E0B)),
                                ),
                            ),
                )
                Text(
                    text = "${day.tempMax.toInt()}°",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
