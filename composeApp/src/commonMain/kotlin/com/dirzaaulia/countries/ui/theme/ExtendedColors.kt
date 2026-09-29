package com.dirzaaulia.countries.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val categoryCrewed: Color,
    val categoryRobotic: Color,
    val categoryGeological: Color,
    val categoryPolar: Color,
    val statusActive: Color,
    val statusWarning: Color,
    val statusCompleted: Color,
    val overlayBackground: Color,
    val telemetryBackground: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        categoryCrewed = Color.Unspecified,
        categoryRobotic = Color.Unspecified,
        categoryGeological = Color.Unspecified,
        categoryPolar = Color.Unspecified,
        statusActive = Color.Unspecified,
        statusWarning = Color.Unspecified,
        statusCompleted = Color.Unspecified,
        overlayBackground = Color.Unspecified,
        telemetryBackground = Color.Unspecified,
    )
}

val defaultExtendedColors = ExtendedColors(
    categoryCrewed = Color(0xFF06B6D4), // Cyan
    categoryRobotic = Color(0xFFF59E0B), // Golden Amber
    categoryGeological = Color(0xFFE11D48), // Crimson Red
    categoryPolar = Color(0xFFE2E8F0), // Icy White
    statusActive = Color(0xFF10B981), // Emerald
    statusWarning = Color(0xFFFACC15), // Yellow
    statusCompleted = Color(0xFF94A3B8), // Slate 400
    overlayBackground = Color(0xD90B1220), // Translucent dark
    telemetryBackground = Color(0xFF1E293B), // Slate 800
)

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
