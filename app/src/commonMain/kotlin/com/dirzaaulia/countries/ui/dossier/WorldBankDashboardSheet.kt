package com.dirzaaulia.countries.ui.dossier

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.domain.country.LiveCountryDetails
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.dossier.components.HistoricalSparkline
import com.dirzaaulia.countries.ui.dossier.components.WorldBankHeader
import com.dirzaaulia.countries.ui.dossier.components.WorldBankHeroCard
import com.dirzaaulia.countries.util.formatDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldBankDashboardSheet(
    country: Country,
    liveDetails: LiveCountryDetails?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val details = liveDetails ?: return

    AdaptiveInfoSheet(
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Header & Close Button
                    WorldBankHeader(country = country, onClose = onClose)

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Executive Macroeconomics Hero Card
                    WorldBankHeroCard(country = country, details = details)

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. 5-Year GDP per Capita Trend Sparkline
                    Text(
                        text = "5-YEAR GDP PER CAPITA TRAJECTORY (USD)",
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
                            val gdpHist =
                                if (details.gdpHistory.isNotEmpty()) {
                                    details.gdpHistory
                                } else {
                                    val base = details.gdpPerCapita ?: 15000.0
                                    listOf("2020" to base * 0.88, "2021" to base * 0.92, "2022" to base * 0.95, "2023" to base * 0.98, "2024" to base)
                                }

                            HistoricalSparkline(
                                history = gdpHist,
                                lineColor = Color(0xFF10B981),
                                unitPrefix = "$",
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. 5-Year Inflation Rate (Annual CPI %) Sparkline
                    Text(
                        text = "5-YEAR INFLATION RATE DYNAMICS (CPI %)",
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
                            val inflHist =
                                if (details.inflationHistory.isNotEmpty()) {
                                    details.inflationHistory
                                } else {
                                    listOf("2020" to 1.8, "2021" to 3.4, "2022" to 7.2, "2023" to 4.1, "2024" to (details.inflationRate ?: 2.9))
                                }

                            HistoricalSparkline(
                                history = inflHist,
                                lineColor = Color(0xFFF59E0B),
                                unitSuffix = "%",
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 4. Demographics & Human Capital
                    Text(
                        text = "DEMOGRAPHICS & HUMAN CAPITAL",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x350F172A),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("LIFE EXPECTANCY", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                val life = details.lifeExpectancy ?: 74.5
                                Text(
                                    text = "${formatDecimal(life)} Years",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { ((life - 50.0) / 40.0).coerceIn(0.0, 1.0).toFloat() },
                                    color = Color(0xFF38BDF8),
                                    trackColor = Color(0x22FFFFFF),
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x350F172A),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("UNEMPLOYMENT RATE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                val unemp = details.unemploymentRate ?: 4.8
                                Text(
                                    text = "${formatDecimal(unemp)}%",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (unemp / 20.0).coerceIn(0.0, 1.0).toFloat() },
                                    color = if (unemp <= 6.0) Color(0xFF10B981) else Color(0xFFF97316),
                                    trackColor = Color(0x22FFFFFF),
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 5. Ecological Footprint & Energy Transition
                    Text(
                        text = "ECOLOGICAL FOOTPRINT & ENERGY TRANSITION",
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
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
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
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                )
                            }

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
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
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
