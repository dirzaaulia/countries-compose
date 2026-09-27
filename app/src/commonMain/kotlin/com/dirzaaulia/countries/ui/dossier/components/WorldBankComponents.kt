package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.util.formatDecimal
import com.dirzaaulia.countries.util.formatNumber

@Composable
internal fun WorldBankHeader(
    country: Country,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PlatformCountryFlag(country.iso2, Modifier.size(28.dp), "${country.name} flag")
            Column {
                Text(
                    text = "WORLD BANK MACROECONOMIC SUITE",
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
                Text(
                    text = "${country.name} • Macro Analytics",
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
internal fun WorldBankHeroCard(
    country: Country,
    details: LiveCountryDetails,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0x28064E3B),
        border = BorderStroke(1.dp, Color(0x4410B981)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "GDP PER CAPITA (CURRENT USD)",
                        color = Color(0xFF6EE7B7),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                    )
                    val gdpCap = details.gdpPerCapita ?: 0.0
                    Text(
                        text = "$${formatNumber(gdpCap)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x3310B981),
                    border = BorderStroke(1.dp, Color(0x6610B981)),
                ) {
                    Text(
                        text = country.incomeGroup.ifEmpty { "Emerging Economy" },
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MacroStatBox(
                    title = "ESTIMATED TOTAL GDP",
                    value = "$${formatNumber(country.gdpMillions)}M",
                    sub = "Global Tier: ${country.economy.ifEmpty { "High-Income" }}",
                    modifier = Modifier.weight(1f),
                )
                MacroStatBox(
                    title = "POPULATION",
                    value = formatNumber(country.population.toDouble()),
                    sub = "Total Citizens",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
internal fun MacroStatBox(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x250F172A),
        border = BorderStroke(1.dp, Color(0x18FFFFFF)),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = Color(0xFF64748B), fontSize = 9.sp)
        }
    }
}

@Composable
internal fun HistoricalSparkline(
    history: List<Pair<String, Double>>,
    lineColor: Color,
    unitPrefix: String = "",
    unitSuffix: String = "",
    modifier: Modifier = Modifier,
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
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Year $year: $unitPrefix${formatDecimal(value)}$unitSuffix",
                        color = lineColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Selected Year", color = Color(0xFF64748B), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Canvas(
            modifier =
                modifier
                    .pointerInput(history) {
                        detectTapGestures(
                            onTap = { offset ->
                                val step = size.width / (history.size - 1).coerceAtLeast(1)
                                val idx = (offset.x / step).toInt().coerceIn(history.indices)
                                selectedIdx = idx
                            },
                        )
                    },
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
                brush =
                    Brush.verticalGradient(
                        listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                    ),
            )

            // Line
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.4f, cap = StrokeCap.Round),
            )

            // Data dots
            history.forEachIndexed { i, (_, v) ->
                val x = i * stepX
                val normY = ((v - minVal) / range).toFloat()
                val y = h - (normY * (h - 15f))

                drawCircle(color = lineColor, radius = 4f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 2f, center = Offset(x, y))
            }
        }
    }
}
