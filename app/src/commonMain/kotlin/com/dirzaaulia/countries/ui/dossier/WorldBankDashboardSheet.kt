package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.Country
import com.dirzaaulia.countries.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldBankDashboardSheet(
    country: Country,
    liveDetails: LiveCountryDetails?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
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
                shape = RoundedCornerShape(3.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(country.flagEmoji, fontSize = 28.sp)
                    Column {
                        Text(
                            text = "WORLD BANK MACROECONOMIC SUITE",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "${country.name} • Macro Analytics",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                MinimalistCloseButton(onClick = onClose)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Executive Macroeconomics Hero Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0x28064E3B),
                border = BorderStroke(1.dp, Color(0x4410B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GDP PER CAPITA (CURRENT USD)",
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            val gdpCap = details.gdpPerCapita ?: 0.0
                            Text(
                                text = "$${formatNumber(gdpCap)}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3310B981),
                            border = BorderStroke(1.dp, Color(0x6610B981))
                        ) {
                            Text(
                                text = country.incomeGroup.ifEmpty { "Emerging Economy" },
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroStatBox(
                            title = "ESTIMATED TOTAL GDP",
                            value = "$${formatNumber(country.gdpMillions)}M",
                            sub = "Global Tier: ${country.economy.ifEmpty { "High-Income" }}",
                            modifier = Modifier.weight(1f)
                        )
                        MacroStatBox(
                            title = "POPULATION",
                            value = formatNumber(country.population.toDouble()),
                            sub = "Total Citizens",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. 5-Year GDP per Capita Trend Sparkline
            Text(
                text = "📈 5-YEAR GDP PER CAPITA TRAJECTORY (USD)",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x350F172A),
                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    val gdpHist = if (details.gdpHistory.isNotEmpty()) {
                        details.gdpHistory
                    } else {
                        val base = details.gdpPerCapita ?: 15000.0
                        listOf("2020" to base * 0.88, "2021" to base * 0.92, "2022" to base * 0.95, "2023" to base * 0.98, "2024" to base)
                    }

                    HistoricalSparkline(
                        history = gdpHist,
                        lineColor = Color(0xFF10B981),
                        unitPrefix = "$",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. 5-Year Inflation Rate (Annual CPI %) Sparkline
            Text(
                text = "📊 5-YEAR INFLATION RATE DYNAMICS (CPI %)",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x350F172A),
                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    val inflHist = if (details.inflationHistory.isNotEmpty()) {
                        details.inflationHistory
                    } else {
                        listOf("2020" to 1.8, "2021" to 3.4, "2022" to 7.2, "2023" to 4.1, "2024" to (details.inflationRate ?: 2.9))
                    }

                    HistoricalSparkline(
                        history = inflHist,
                        lineColor = Color(0xFFF59E0B),
                        unitSuffix = "%",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Demographics & Human Capital
            Text(
                text = "🩺 DEMOGRAPHICS & HUMAN CAPITAL",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x350F172A),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("LIFE EXPECTANCY", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val life = details.lifeExpectancy ?: 74.5
                        Text(
                            text = "${formatDecimal(life)} Years",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { ((life - 50.0) / 40.0).coerceIn(0.0, 1.0).toFloat() },
                            color = Color(0xFF38BDF8),
                            trackColor = Color(0x22FFFFFF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x350F172A),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("UNEMPLOYMENT RATE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val unemp = details.unemploymentRate ?: 4.8
                        Text(
                            text = "${formatDecimal(unemp)}%",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (unemp / 20.0).coerceIn(0.0, 1.0).toFloat() },
                            color = if (unemp <= 6.0) Color(0xFF10B981) else Color(0xFFF97316),
                            trackColor = Color(0x22FFFFFF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Ecological Footprint & Energy Transition
            Text(
                text = "🌱 ECOLOGICAL FOOTPRINT & ENERGY TRANSITION",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x350F172A),
                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("RENEWABLE ENERGY SHARE", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            val renew = details.renewableEnergyShare ?: 22.4
                            Text("${formatDecimal(renew)}% of Total", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val renewProg = ((details.renewableEnergyShare ?: 22.4) / 100.0).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { renewProg },
                            color = Color(0xFF10B981),
                            trackColor = Color(0x22FFFFFF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CO2 EMISSIONS (PER CAPITA)", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            val co2 = details.co2Emissions ?: 4.6
                            Text("${formatDecimal(co2)} Metric Tons", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val co2Prog = ((details.co2Emissions ?: 4.6) / 20.0).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { co2Prog },
                            color = Color(0xFFF59E0B),
                            trackColor = Color(0x22FFFFFF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroStatBox(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x250F172A),
        border = BorderStroke(1.dp, Color(0x18FFFFFF)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = Color(0xFF64748B), fontSize = 9.sp)
        }
    }
}

@Composable
private fun HistoricalSparkline(
    history: List<Pair<String, Double>>,
    lineColor: Color,
    unitPrefix: String = "",
    unitSuffix: String = "",
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Trend data loading...", color = Color(0xFF64748B), fontSize = 11.sp)
        }
        return
    }

    var selectedIdx by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        selectedIdx?.let { idx ->
            if (idx in history.indices) {
                val (year, value) = history[idx]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Year $year: $unitPrefix${formatDecimal(value)}$unitSuffix",
                        color = lineColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Selected Year", color = Color(0xFF64748B), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Canvas(
            modifier = modifier
                .pointerInput(history) {
                    detectTapGestures(
                        onTap = { offset ->
                            val step = size.width / (history.size - 1).coerceAtLeast(1)
                            val idx = (offset.x / step).toInt().coerceIn(history.indices)
                            selectedIdx = idx
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height - 20f

            val minVal = history.minOf { it.second } * 0.95
            val maxVal = history.maxOf { it.second } * 1.05
            val range = (maxVal - minVal).coerceAtLeast(0.001)

            val stepX = w / (history.size - 1).coerceAtLeast(1)

            val linePath = Path()
            val fillPath = Path()

            history.forEachIndexed { i, (_, v) ->
                val x = i * stepX
                val normY = ((v - minVal) / range).toFloat()
                val y = h - (normY * (h - 15f))

                if (i == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(w, h)
            fillPath.close()

            // Soft gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = 0.25f), Color.Transparent)
                )
            )

            // Line
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.4f, cap = StrokeCap.Round)
            )

            // Data dots
            history.forEachIndexed { i, (year, v) ->
                val x = i * stepX
                val normY = ((v - minVal) / range).toFloat()
                val y = h - (normY * (h - 15f))

                drawCircle(color = lineColor, radius = 4f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 2f, center = Offset(x, y))
            }
        }
    }
}
