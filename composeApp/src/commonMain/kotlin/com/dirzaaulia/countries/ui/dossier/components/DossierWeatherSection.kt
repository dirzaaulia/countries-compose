package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.LiveCountryDetails

@Composable
fun DossierWeatherSection(
    liveDetails: LiveCountryDetails?,
    capital: String,
    onOpenMeteorology: (() -> Unit)?,
) {
    val live = liveDetails?.takeIf { it.weatherTempC != null } ?: return
    Spacer(Modifier.height(10.dp))
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x350F172A),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = live.weatherIcon ?: "[SUN]", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "${live.weatherTempC}°C",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(text = live.weatherDescription ?: "Fair", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "CAPITAL WEATHER",
                        color = Color(0xFF38BDF8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                    )
                    Text(text = capital, color = Color(0xFFE2E8F0), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                live.weatherHumidity?.let { Text("Humidity $it%", color = Color(0xFF94A3B8), fontSize = 11.sp) }
                live.weatherWindSpeed?.let { Text("Wind ${it.toInt()} km/h", color = Color(0xFF94A3B8), fontSize = 11.sp) }
                live.uvIndex?.let { Text("UV $it", color = Color(0xFF94A3B8), fontSize = 11.sp) }
            }
            if (onOpenMeteorology != null) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onOpenMeteorology,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x3338BDF8)),
                    border = BorderStroke(1.dp, Color(0x5538BDF8)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "View Meteorology Station (7-Day & Hourly) ↗",
                        color = Color(0xFFE0F2FE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
