package com.dirzaaulia.countries.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Brightness3
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.SatelliteAlt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Volcano
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class UiSymbol {
    Earth,
    Moon,
    Info,
    Layers,
    Search,
    Iss,
    Flight,
    Apollo,
    Quiz,
    Hazard,
    Volcano,
    Fire,
    Storm,
    Ice,
    Clear,
    Cloud,
    Rain,
    People,
    Landscape,
    Capital,
    Location,
    Economy,
    LifeExpectancy,
    Currency,
    Language,
    Time,
    Wind,
    Pressure,
}

fun uiSymbolFor(marker: String): UiSymbol =
    when (marker) {
        "[ISS]" -> UiSymbol.Iss
        "[APOLLO]" -> UiSymbol.Apollo
        "[EARTH]" -> UiSymbol.Earth
        "[MOON]", "[NEW]", "[WAX-C]", "[Q1]", "[WAX-G]", "[FULL]", "[WAN-G]", "[Q3]", "[WAN-C]" -> UiSymbol.Moon
        "[INFO]" -> UiSymbol.Info
        "[LAYERS]" -> UiSymbol.Layers
        "[SEARCH]" -> UiSymbol.Search
        "[FLIGHT]" -> UiSymbol.Flight
        "[QUIZ]" -> UiSymbol.Quiz
        "[VOLCANO]" -> UiSymbol.Volcano
        "[FIRE]" -> UiSymbol.Fire
        "[STORM]" -> UiSymbol.Storm
        "[ICE]", "[SNOW]" -> UiSymbol.Ice
        "[CLEAR]", "[PARTLY]", "[FAIR]" -> UiSymbol.Clear
        "[CLOUD]", "[FOG]" -> UiSymbol.Cloud
        "[RAIN]", "[FLOOD]" -> UiSymbol.Rain
        else -> UiSymbol.Hazard
    }

@Composable
fun SemanticIcon(
    symbol: UiSymbol,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(imageVector = symbol.imageVector, contentDescription = contentDescription, tint = tint, modifier = modifier)
}

private val UiSymbol.imageVector: ImageVector
    get() =
        when (this) {
            UiSymbol.Earth -> Icons.Outlined.Public
            UiSymbol.Moon -> Icons.Outlined.Brightness3
            UiSymbol.Info -> Icons.Outlined.Info
            UiSymbol.Layers -> Icons.Outlined.Layers
            UiSymbol.Search -> Icons.Outlined.Search
            UiSymbol.Iss -> Icons.Outlined.SatelliteAlt
            UiSymbol.Flight -> Icons.Outlined.Flight
            UiSymbol.Apollo -> Icons.Outlined.RocketLaunch
            UiSymbol.Quiz -> Icons.Outlined.Games
            UiSymbol.Hazard -> Icons.Outlined.WarningAmber
            UiSymbol.Volcano -> Icons.Outlined.Volcano
            UiSymbol.Fire -> Icons.Outlined.LocalFireDepartment
            UiSymbol.Storm -> Icons.Outlined.Thunderstorm
            UiSymbol.Ice -> Icons.Outlined.AcUnit
            UiSymbol.Clear -> Icons.Outlined.WbSunny
            UiSymbol.Cloud -> Icons.Outlined.Cloud
            UiSymbol.Rain -> Icons.Outlined.WaterDrop
            UiSymbol.People -> Icons.Outlined.People
            UiSymbol.Landscape -> Icons.Outlined.Landscape
            UiSymbol.Capital -> Icons.Outlined.LocationCity
            UiSymbol.Location -> Icons.Outlined.MyLocation
            UiSymbol.Economy -> Icons.Outlined.AccountBalance
            UiSymbol.LifeExpectancy -> Icons.Outlined.FavoriteBorder
            UiSymbol.Currency -> Icons.Outlined.CurrencyExchange
            UiSymbol.Language -> Icons.Outlined.Translate
            UiSymbol.Time -> Icons.Outlined.Schedule
            UiSymbol.Wind -> Icons.Outlined.Air
            UiSymbol.Pressure -> Icons.Outlined.Speed
        }
