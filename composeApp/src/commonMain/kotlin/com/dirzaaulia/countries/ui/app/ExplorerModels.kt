package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.dirzaaulia.countries.ui.comparison.ComparisonViewModel
import com.dirzaaulia.countries.ui.dossier.DossierViewModel
import com.dirzaaulia.countries.ui.economy.EconomyViewModel
import com.dirzaaulia.countries.ui.flight.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.hazards.HazardsViewModel
import com.dirzaaulia.countries.ui.quiz.QuizViewModel
import com.dirzaaulia.countries.ui.satellite.SatelliteViewModel
import com.dirzaaulia.countries.ui.weather.SpaceWeatherViewModel
import org.koin.compose.koinInject

internal class ExplorerModels(
    val globe: GlobeViewModel,
    val dossier: DossierViewModel,
    val hazard: HazardViewModel,
    val iss: IssViewModel,
    val flight: FlightViewModel,
    val quiz: QuizViewModel,
    val spaceWeather: SpaceWeatherViewModel,
    val satellite: SatelliteViewModel,
    val comparison: ComparisonViewModel,
    val economy: EconomyViewModel,
    val hazards: HazardsViewModel,
)

@Composable
internal fun rememberExplorerModels(): ExplorerModels {
    val globe: GlobeViewModel = koinInject()
    val dossier: DossierViewModel = koinInject()
    val hazard: HazardViewModel = koinInject()
    val iss: IssViewModel = koinInject()
    val flight: FlightViewModel = koinInject()
    val quiz: QuizViewModel = koinInject()
    val spaceWeather: SpaceWeatherViewModel = koinInject()
    val satellite: SatelliteViewModel = koinInject()
    val comparison: ComparisonViewModel = koinInject()
    val economy: EconomyViewModel = koinInject()
    val hazards: HazardsViewModel = koinInject()
    return remember(
        globe,
        dossier,
        hazard,
        iss,
        flight,
        quiz,
        spaceWeather,
        satellite,
        comparison,
        economy,
        hazards,
    ) {
        ExplorerModels(
            globe = globe,
            dossier = dossier,
            hazard = hazard,
            iss = iss,
            flight = flight,
            quiz = quiz,
            spaceWeather = spaceWeather,
            satellite = satellite,
            comparison = comparison,
            economy = economy,
            hazards = hazards,
        )
    }
}
