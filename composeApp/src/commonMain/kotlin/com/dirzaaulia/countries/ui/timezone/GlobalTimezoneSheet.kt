package com.dirzaaulia.countries.ui.timezone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.CountryFlagIcon
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.dossier.components.localSolarTime
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalTimezoneSheet(
    utcOffset: Int,
    allCountries: List<Country>,
    onClose: () -> Unit,
    onSelectCountry: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val meridianLng = utcOffset * 15.0
    val formattedMeridian =
        if (meridianLng == 0.0) {
            "0° Greenwich"
        } else if (meridianLng > 0) {
            "${meridianLng.toInt()}°E"
        } else {
            "${abs(meridianLng).toInt()}°W"
        }

    val matchingCountries =
        allCountries
            .filter { country ->
                abs(country.center.lng - meridianLng) <= 12.0 ||
                    country.timezones.any { tz ->
                        tz.contains(if (utcOffset >= 0) "UTC+$utcOffset" else "UTC$utcOffset") ||
                            tz.contains(if (utcOffset >= 0) "+$utcOffset" else "$utcOffset")
                    }
            }.take(12)

    val solarTime = localSolarTime(meridianLng)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1626),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
        ) {
            SheetHeader(
                utcOffset = utcOffset,
                meridianLabel = formattedMeridian,
                solarTime = solarTime,
                onClose = onClose,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "COUNTRIES & TERRITORIES IN THIS MERIDIAN BAND",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(8.dp))
            CountryBandList(
                countries = matchingCountries,
                onSelectCountry = onSelectCountry,
            )
        }
    }
}

@Composable
private fun SheetHeader(
    utcOffset: Int,
    meridianLabel: String,
    solarTime: String,
    onClose: () -> Unit,
) {
    val offsetLabel = if (utcOffset >= 0) "UTC+$utcOffset" else "UTC$utcOffset"
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$offsetLabel MERIDIAN",
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = meridianLabel,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Solar Time: ~$solarTime",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}

@Composable
private fun CountryBandList(
    countries: List<Country>,
    onSelectCountry: (Country) -> Unit,
) {
    if (countries.isEmpty()) {
        Text(
            text = "No major populated landmasses directly on this meridian band.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 16.dp),
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(countries, key = { it.id }) { country ->
                CountryTileRow(country = country, onSelectCountry = onSelectCountry)
            }
        }
    }
}

@Composable
private fun CountryTileRow(
    country: Country,
    onSelectCountry: (Country) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onSelectCountry(country) }
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            CountryFlagIcon(country = country)
            Spacer(Modifier.padding(horizontal = 6.dp))
            Column {
                Text(
                    text = country.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = country.capital.ifEmpty { "N/A" },
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                )
            }
        }
    }
}
