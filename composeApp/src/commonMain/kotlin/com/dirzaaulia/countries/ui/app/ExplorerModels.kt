package com.dirzaaulia.countries.ui.app

import androidx.compose.runtime.Composable
import com.dirzaaulia.countries.ui.comparison.ComparisonViewModel
import com.dirzaaulia.countries.ui.dossier.DossierViewModel
import com.dirzaaulia.countries.ui.globe.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.globe.QuizViewModel
import com.dirzaaulia.countries.ui.globe.SpaceWeatherViewModel
import com.dirzaaulia.countries.ui.satellite.SatelliteViewModel
import com.dirzaaulia.countries.ui.tectonic.TectonicViewModel
import com.dirzaaulia.countries.ui.timezone.TimezoneViewModel
import org.koin.compose.viewmodel.koinViewModel

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
    val timezone: TimezoneViewModel,
    val tectonic: TectonicViewModel,
)

@Composable
internal fun rememberExplorerModels(): ExplorerModels {
    val globe: GlobeViewModel = koinViewModel()
    val dossier: DossierViewModel = koinViewModel()
    val hazard: HazardViewModel = koinViewModel()
    val iss: IssViewModel = koinViewModel()
    val flight: FlightViewModel = koinViewModel()
    val quiz: QuizViewModel = koinViewModel()
    val spaceWeather: SpaceWeatherViewModel = koinViewModel()
    val satellite: SatelliteViewModel = koinViewModel()
    val comparison: ComparisonViewModel = koinViewModel()
    val timezone: TimezoneViewModel = koinViewModel()
    val tectonic: TectonicViewModel = koinViewModel()
    return ExplorerModels(
        globe = globe,
        dossier = dossier,
        hazard = hazard,
        iss = iss,
        flight = flight,
        quiz = quiz,
        spaceWeather = spaceWeather,
        satellite = satellite,
        comparison = comparison,
        timezone = timezone,
        tectonic = tectonic,
    )
}
