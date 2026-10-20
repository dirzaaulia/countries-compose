package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.platform.PlatformCountryFlag

@Composable
fun CountryFlagIcon(
    country: Country,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0x330284C7),
        border = BorderStroke(1.dp, Color(0x6638BDF8)),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PlatformCountryFlag(
                iso2 = country.iso2,
                modifier =
                    Modifier
                        .size(width = 24.dp, height = 16.dp)
                        .clip(RoundedCornerShape(2.dp)),
            )
            if (country.iso2.isNotEmpty() && country.iso2 != "-99") {
                Text(
                    text = country.iso2.uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
