package com.dirzaaulia.countries.di

import com.dirzaaulia.countries.data.eclipse.EclipseFeedApiClient
import com.dirzaaulia.countries.data.iss.ISSTelemetryApiClient
import com.dirzaaulia.countries.data.nasa.NasaEonetApiClient
import com.dirzaaulia.countries.data.openmeteo.OpenMeteoApiClient
import com.dirzaaulia.countries.data.satellite.SatelliteApiClient
import com.dirzaaulia.countries.data.satellite.SatelliteRepository
import com.dirzaaulia.countries.data.mars.MarsRoverApiClient
import com.dirzaaulia.countries.data.repository.MarsRoverRepository
import com.dirzaaulia.countries.data.repository.SpaceWeatherRepository
import com.dirzaaulia.countries.data.repository.CountryDetailRepositoryImpl
import com.dirzaaulia.countries.data.repository.CountryRepositoryImpl
import com.dirzaaulia.countries.data.repository.EclipseRepositoryImpl
import com.dirzaaulia.countries.data.repository.GlobeRepository
import com.dirzaaulia.countries.data.repository.HazardRepositoryImpl
import com.dirzaaulia.countries.data.repository.IssRepositoryImpl
import com.dirzaaulia.countries.data.restcountries.RestCountriesApiClient
import com.dirzaaulia.countries.data.worldbank.WorldBankApiClient
import com.dirzaaulia.countries.data.tectonic.TectonicRepositoryImpl
import com.dirzaaulia.countries.data.tectonic.UsgsApiClient
import com.dirzaaulia.countries.domain.repository.CountryDetailRepository
import com.dirzaaulia.countries.domain.repository.CountryRepository
import com.dirzaaulia.countries.domain.repository.EclipseRepository
import com.dirzaaulia.countries.domain.repository.HazardRepository
import com.dirzaaulia.countries.domain.repository.IssRepository
import com.dirzaaulia.countries.domain.repository.TectonicRepository
import com.dirzaaulia.countries.platform.countriesMiddlewareUrl
import com.dirzaaulia.countries.platform.platformHttpClientEngine
import com.dirzaaulia.countries.ui.comparison.ComparisonViewModel
import com.dirzaaulia.countries.ui.dossier.DossierViewModel
import com.dirzaaulia.countries.ui.moon.LunarViewModel
import com.dirzaaulia.countries.ui.tectonic.TectonicViewModel
import com.dirzaaulia.countries.ui.timezone.TimezoneViewModel
import com.dirzaaulia.countries.ui.globe.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.globe.QuizViewModel
import com.dirzaaulia.countries.ui.globe.SpaceWeatherViewModel
import com.dirzaaulia.countries.ui.satellite.SatelliteViewModel
import com.dirzaaulia.countries.ui.mars.MarsRoverGalleryViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val jsonFormatter =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

val networkModule =
    module {
        single { jsonFormatter }
        single {
            HttpClient(platformHttpClientEngine()) {
                expectSuccess = false
                install(ContentNegotiation) {
                    json(get())
                }
            }
        }
    }

val apiModule =
    module {
        single { RestCountriesApiClient(get(), get(), countriesMiddlewareUrl) }
        singleOf(::EclipseFeedApiClient)
        singleOf(::WorldBankApiClient)
        singleOf(::OpenMeteoApiClient)
        singleOf(::ISSTelemetryApiClient)
        singleOf(::NasaEonetApiClient)
        singleOf(::MarsRoverApiClient)
        singleOf(::SatelliteApiClient)
        singleOf(::UsgsApiClient)
    }

val repositoryModule =
    module {
        single<CountryRepository> { CountryRepositoryImpl() }
        single<EclipseRepository> { EclipseRepositoryImpl(get()) }
        single<HazardRepository> { HazardRepositoryImpl(get()) }
        single<IssRepository> { IssRepositoryImpl(get()) }
        single<CountryDetailRepository> { CountryDetailRepositoryImpl(get(), get(), get(), get()) }
        single<TectonicRepository> { TectonicRepositoryImpl(get()) }
        singleOf(::GlobeRepository)
        singleOf(::MarsRoverRepository)
        singleOf(::SpaceWeatherRepository)
        singleOf(::SatelliteRepository)
    }

val viewModelModule =
    module {
        viewModelOf(::GlobeViewModel)
        viewModelOf(::DossierViewModel)
        viewModelOf(::HazardViewModel)
        viewModelOf(::IssViewModel)
        viewModelOf(::FlightViewModel)
        viewModelOf(::QuizViewModel)
        viewModelOf(::MarsRoverGalleryViewModel)
        viewModelOf(::SpaceWeatherViewModel)
        viewModelOf(::SatelliteViewModel)
        viewModelOf(::ComparisonViewModel)
        viewModelOf(::TimezoneViewModel)
        viewModelOf(::TectonicViewModel)
        viewModelOf(::LunarViewModel)
    }

val appModules = listOf(networkModule, apiModule, repositoryModule, viewModelModule)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModules)
    }
