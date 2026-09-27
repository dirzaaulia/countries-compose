package com.dirzaaulia.countries.platform

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.jetbrains.compose.resources.decodeToSvgPainter

private val flagHttpClient by lazy { HttpClient() }
private val flagSvgCache = mutableMapOf<String, ByteArray>()

actual fun platformCountryMarker(iso2: String): String {
    val code = iso2.uppercase()
    return if (code.length == 2 && code != "-99" && code.all { it in 'A'..'Z' }) "[$code]" else "[--]"
}

@Composable
actual fun PlatformCountryFlag(
    iso2: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    val code = iso2.lowercase().takeIf { it.length == 2 && it.all { char -> char in 'a'..'z' } }
    var svgBytes by remember(code) { mutableStateOf(code?.let(flagSvgCache::get)) }

    LaunchedEffect(code) {
        if (svgBytes != null) return@LaunchedEffect
        svgBytes =
            code?.let {
                runCatching { flagHttpClient.get("https://flagcdn.com/$it.svg").body<ByteArray>() }
                    .getOrNull()
                    ?.also { bytes -> flagSvgCache[it] = bytes }
            }
    }

    val bytes = svgBytes
    if (bytes != null) {
        Image(bytes.decodeToSvgPainter(LocalDensity.current), contentDescription, modifier)
    } else {
        Icon(Icons.Outlined.Public, contentDescription, modifier)
    }
}
