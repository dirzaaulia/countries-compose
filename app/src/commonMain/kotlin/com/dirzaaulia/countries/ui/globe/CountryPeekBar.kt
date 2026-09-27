package com.dirzaaulia.countries.ui.globe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.components.CountryFlagIcon

@Composable
fun CountryPeekBar(
    country: Country,
    capital: String?,
    isLoading: Boolean,
    onExpandDossier: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF20B1626),
        border = BorderStroke(1.dp, Color(0x6638BDF8)),
        shadowElevation = 12.dp,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onExpandDossier() },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CountryFlagIcon(country = country)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (isLoading) "Loading live metrics..." else capital?.takeIf { it.isNotBlank() && it != "N/A" }?.let { "Capital: $it" } ?: country.continent,
                    color = if (isLoading) Color(0xFF7DD3FC) else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF38BDF8),
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x330284C7),
                    border = BorderStroke(1.dp, Color(0x6638BDF8)),
                ) {
                    Text(
                        text = "Dossier ↗",
                        color = Color(0xFFE0F2FE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
        }
    }
}
