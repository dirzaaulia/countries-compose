package com.dirzaaulia.countries.di

import com.dirzaaulia.countries.data.eclipse.EclipseFeedApiClient
import com.dirzaaulia.countries.data.geoboundaries.GeoBoundariesApiClient
import com.dirzaaulia.countries.data.iss.ISSTelemetryApiClient
import com.dirzaaulia.countries.data.nasa.NasaEonetApiClient
import com.dirzaaulia.countries.data.openmeteo.OpenMeteoApiClient
import com.dirzaaulia.countries.data.repository.AdministrativeRepository
import com.dirzaaulia.countries.data.repository.CountryDetailRepository
import com.dirzaaulia.countries.data.repository.CountryRepository
import com.dirzaaulia.countries.data.repository.EclipseRepository
import com.dirzaaulia.countries.data.repository.GlobeRepository
import com.dirzaaulia.countries.data.repository.HazardRepository
import com.dirzaaulia.countries.data.repository.IssRepository
import com.dirzaaulia.countries.data.restcountries.RestCountriesApiClient
import com.dirzaaulia.countries.data.worldbank.WorldBankApiClient
import com.dirzaaulia.countries.platform.countriesMiddlewareUrl
import com.dirzaaulia.countries.platform.platformHttpClientEngine
import com.dirzaaulia.countries.ui.dossier.DossierViewModel
import com.dirzaaulia.countries.ui.globe.AdministrativeViewModel
import com.dirzaaulia.countries.ui.globe.FlightViewModel
import com.dirzaaulia.countries.ui.globe.GlobeViewModel
import com.dirzaaulia.countries.ui.globe.HazardViewModel
import com.dirzaaulia.countries.ui.globe.IssViewModel
import com.dirzaaulia.countries.ui.globe.QuizViewModel
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
        singleOf(::GeoBoundariesApiClient)
    }

val repositoryModule =
    module {
        singleOf(::CountryRepository)
        singleOf(::EclipseRepository)
        singleOf(::HazardRepository)
        singleOf(::IssRepository)
        singleOf(::AdministrativeRepository)
        singleOf(::CountryDetailRepository)
        singleOf(::GlobeRepository)
    }

val viewModelModule =
    module {
        viewModelOf(::GlobeViewModel)
        viewModelOf(::DossierViewModel)
        viewModelOf(::HazardViewModel)
        viewModelOf(::IssViewModel)
        viewModelOf(::AdministrativeViewModel)
        viewModelOf(::FlightViewModel)
        viewModelOf(::QuizViewModel)
    }

val appModules = listOf(networkModule, apiModule, repositoryModule, viewModelModule)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModules)
    }
