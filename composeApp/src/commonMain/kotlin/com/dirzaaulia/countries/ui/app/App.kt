package com.dirzaaulia.countries.ui.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dirzaaulia.countries.di.appModules
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

import androidx.compose.runtime.CompositionLocalProvider
import com.dirzaaulia.countries.ui.theme.LocalExtendedColors
import com.dirzaaulia.countries.ui.theme.defaultExtendedColors

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration(declaration = { modules(appModules) })) {
        CompositionLocalProvider(LocalExtendedColors provides defaultExtendedColors) {
            MaterialTheme(
                colorScheme =
                    darkColorScheme(
                        background = Color(0xFF03060C),
                        surface = Color(0xFF0B1220),
                        primary = Color(0xFF38BDF8),
                        surfaceVariant = Color(0xFF1E293B),
                        onSurfaceVariant = Color(0xFF94A3B8),
                        outline = Color(0xFF475569),
                    ),
            ) {
                PlanetaryExplorerRoute()
            }
        }
    }
}
