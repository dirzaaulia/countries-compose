package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

private data class CountrySearchEntry(
    val country: Country,
    val fields: List<String>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySearchSheet(
    countries: List<Country>,
    onSelectCountry: (Country) -> Unit,
    onClose: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val index =
        remember(countries) {
            countries.map { country ->
                CountrySearchEntry(
                    country = country,
                    fields =
                        listOf(country.name, country.capital, country.iso2, country.id)
                            .filter { it.isNotBlank() && it != "N/A" }
                            .map(String::searchNormalized),
                )
            }
        }
    val results =
        remember(query, index) {
            val normalizedQuery = query.searchNormalized()
            if (normalizedQuery.isBlank()) {
                emptyList()
            } else {
                index
                    .mapNotNull { entry ->
                        entry.fields
                            .mapNotNull { field -> field.searchScore(normalizedQuery) }
                            .minOrNull()
                            ?.let { score -> entry.country to score }
                    }.sortedBy { it.second }
                    .take(8)
                    .map { it.first }
            }
        }

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF209111E),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF64748B)) },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GLOBAL TELEPORT",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "Country, capital, or ISO code",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                    )
                }
                MinimalistCloseButton(onClick = onClose)
            }

            Spacer(Modifier.height(12.dp))
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search loaded countries") },
                leadingIcon = { SemanticIcon(UiSymbol.Search, null, Color(0xFF38BDF8), Modifier.size(20.dp)) },
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF111C2F),
                        unfocusedContainerColor = Color(0xFF111C2F),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color(0xFF38BDF8),
                        unfocusedIndicatorColor = Color(0x33475569),
                    ),
                shape = RoundedCornerShape(14.dp),
            )

            Spacer(Modifier.height(12.dp))
            when {
                query.isBlank() -> SearchHint("Type to search the countries loaded on this globe.")
                results.isEmpty() -> SearchHint("No loaded country matches that search.")
                else ->
                    LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                        items(results, key = { it.id }) { country ->
                            SearchResultRow(country = country, onClick = { onSelectCountry(country) })
                        }
                    }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    country: Country,
    onClick: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 2.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PlatformCountryFlag(country.iso2, Modifier.size(24.dp), "${country.name} flag")
            Column(modifier = Modifier.weight(1f)) {
                Text(country.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(
                    text = if (country.capital != "N/A") country.capital else country.id,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
            }
            Text(
                text = country.iso2.ifBlank { country.id },
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SearchHint(text: String) {
    Text(text, color = Color(0xFF94A3B8), fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
}

private fun String.searchNormalized(): String =
    buildString {
        this@searchNormalized.lowercase().forEach { character ->
            append(
                when (character) {
                    'à', 'á', 'â', 'ã', 'ä', 'å', 'ā', 'ă', 'ą' -> "a"
                    'ç', 'ć', 'č', 'ĉ', 'ċ' -> "c"
                    'ď', 'đ' -> "d"
                    'è', 'é', 'ê', 'ë', 'ē', 'ĕ', 'ė', 'ę', 'ě' -> "e"
                    'ğ', 'ĝ', 'ġ', 'ģ' -> "g"
                    'ĥ', 'ħ' -> "h"
                    'ì', 'í', 'î', 'ï', 'ī', 'ĭ', 'į', 'ı' -> "i"
                    'ĵ' -> "j"
                    'ķ' -> "k"
                    'ĺ', 'ļ', 'ľ', 'ł' -> "l"
                    'ñ', 'ń', 'ņ', 'ň' -> "n"
                    'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'ō', 'ŏ', 'ő' -> "o"
                    'ŕ', 'ŗ', 'ř' -> "r"
                    'ś', 'ŝ', 'ş', 'š' -> "s"
                    'ţ', 'ť', 'ŧ' -> "t"
                    'ù', 'ú', 'û', 'ü', 'ū', 'ŭ', 'ů', 'ű', 'ų' -> "u"
                    'ŵ' -> "w"
                    'ý', 'ÿ', 'ŷ' -> "y"
                    'ź', 'ż', 'ž' -> "z"
                    'æ' -> "ae"
                    'œ' -> "oe"
                    'ß' -> "ss"
                    else -> character.toString()
                },
            )
        }
    }

private fun String.searchScore(query: String): Int? {
    if (this == query) return 0
    indexOf(query).takeIf { it >= 0 }?.let { return 10 + it }
    var queryIndex = 0
    var gaps = 0
    var previousMatch = -1
    forEachIndexed { index, character ->
        if (queryIndex < query.length && character == query[queryIndex]) {
            if (previousMatch >= 0) gaps += index - previousMatch - 1
            previousMatch = index
            queryIndex++
        }
    }
    return if (queryIndex == query.length) 100 + gaps + length - query.length else null
}
