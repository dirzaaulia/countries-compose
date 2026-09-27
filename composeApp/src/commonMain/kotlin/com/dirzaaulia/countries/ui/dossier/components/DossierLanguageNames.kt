package com.dirzaaulia.countries.ui.dossier.components

internal fun dossierLanguageName(code: String): String =
    when (code.lowercase().substringBefore('-').substringBefore('_')) {
        "ara", "ar" -> "Arabic"
        "bre", "br" -> "Breton"
        "ces", "cze", "cs" -> "Czech"
        "cym", "wel", "cy" -> "Welsh"
        "deu", "ger", "de" -> "German"
        "est", "et" -> "Estonian"
        "fin", "fi" -> "Finnish"
        "fra", "fre", "fr" -> "French"
        "hrv", "hr" -> "Croatian"
        "hun", "hu" -> "Hungarian"
        "ita", "it" -> "Italian"
        "jpn", "ja" -> "Japanese"
        "kor", "ko" -> "Korean"
        "nld", "dut", "nl" -> "Dutch"
        "per", "fas", "fa" -> "Persian"
        "pol", "pl" -> "Polish"
        "por", "pt" -> "Portuguese"
        "rus", "ru" -> "Russian"
        "slk", "slo", "sk" -> "Slovak"
        "spa", "es" -> "Spanish"
        "srp", "sr" -> "Serbian"
        "swe", "sv" -> "Swedish"
        "tur", "tr" -> "Turkish"
        "urd", "ur" -> "Urdu"
        "zho", "chi", "zh" -> "Chinese"
        "eng", "en" -> "English"
        "ind", "id" -> "Indonesian"
        "hin", "hi" -> "Hindi"
        else -> code.uppercase()
    }
