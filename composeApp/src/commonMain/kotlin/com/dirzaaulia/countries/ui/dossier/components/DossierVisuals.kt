package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun dossierHeaderBrush(status: String): Brush =
    when (status) {
        "SPLIT_WEST_DAY" ->
            Brush.horizontalGradient(
                listOf(Color(0x550284C7), Color(0x44F59E0B), Color(0x550F172A)),
            )
        "SPLIT_EAST_DAY" ->
            Brush.horizontalGradient(
                listOf(Color(0x550F172A), Color(0x44F59E0B), Color(0x550284C7)),
            )
        "DAY" ->
            Brush.horizontalGradient(
                listOf(Color(0x440284C7), Color(0x22F59E0B), Color(0x110284C7)),
            )
        else ->
            Brush.horizontalGradient(
                listOf(Color(0x44312E81), Color(0x221E1B4B), Color(0x110F172A)),
            )
    }
