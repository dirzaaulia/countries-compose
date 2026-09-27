package com.dirzaaulia.countries.platform

import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private val flagHttpClient by lazy { HttpClient() }
private val flagBitmapCache = mutableMapOf<String, ImageBitmap>()

actual fun platformCountryMarker(iso2: String): String {
    val code = iso2.uppercase()
    return if (code.length == 2 && code.all { it in 'A'..'Z' }) "[$code]" else "[--]"
}

@Composable
actual fun PlatformCountryFlag(
    iso2: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    val code = iso2.lowercase().takeIf { it.length == 2 && it.all { char -> char in 'a'..'z' } }
    var flagBitmap by remember(code) { mutableStateOf(code?.let(flagBitmapCache::get)) }

    LaunchedEffect(code) {
        if (flagBitmap != null) return@LaunchedEffect
        flagBitmap =
            code?.let {
                runCatching {
                    val bytes = flagHttpClient.get("https://flagcdn.com/w80/$it.png").body<ByteArray>()
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                }.getOrNull()?.also { bitmap -> flagBitmapCache[it] = bitmap }
            }
    }

    val bitmap = flagBitmap
    if (bitmap != null) {
        Image(bitmap, contentDescription, modifier)
    } else {
        Icon(Icons.Outlined.Public, contentDescription, modifier)
    }
}
