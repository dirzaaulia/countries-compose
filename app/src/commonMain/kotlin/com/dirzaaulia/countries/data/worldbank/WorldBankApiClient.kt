package com.dirzaaulia.countries.data.worldbank

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

data class WorldBankResults(
    val gdpPerCapita: Double? = null,
    val inflationRate: Double? = null,
    val lifeExpectancy: Double? = null,
    val unemploymentRate: Double? = null,
    val renewableEnergyShare: Double? = null,
    val co2Emissions: Double? = null,
    val gdpHistory: List<Pair<String, Double>> = emptyList(),
    val inflationHistory: List<Pair<String, Double>> = emptyList(),
)

class WorldBankApiClient(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun fetchWorldBankData(countryCode: String): WorldBankResults? {
        if (countryCode.isBlank() || countryCode == "-99") return null
        return coroutineScope {
            val gdpCapDef = async { fetchWorldBankIndicator(countryCode, "NY.GDP.PCAP.CD") }
            val inflDef = async { fetchWorldBankIndicator(countryCode, "FP.CPI.TOTL.ZG") }
            val lifeDef = async { fetchWorldBankIndicator(countryCode, "SP.DYN.LE00.IN") }
            val unempDef = async { fetchWorldBankIndicator(countryCode, "SL.UEM.TOTL.ZS") }
            val renewDef = async { fetchWorldBankIndicator(countryCode, "EG.FEC.RNEW.ZS") }
            val co2Def = async { fetchWorldBankIndicator(countryCode, "EN.ATM.CO2E.PC") }
            val gdpHistDef = async { fetchWorldBankIndicatorHistory(countryCode, "NY.GDP.PCAP.CD") }
            val inflHistDef = async { fetchWorldBankIndicatorHistory(countryCode, "FP.CPI.TOTL.ZG") }

            val gdpCap = gdpCapDef.await()
            val infl = inflDef.await()
            val life = lifeDef.await()
            val unemp = unempDef.await()
            val renew = renewDef.await()
            val co2 = co2Def.await()
            val gdpHist = gdpHistDef.await()
            val inflHist = inflHistDef.await()

            if (gdpCap == null && infl == null && life == null && unemp == null && renew == null && co2 == null && gdpHist.isEmpty()) {
                null
            } else {
                WorldBankResults(
                    gdpPerCapita = gdpCap,
                    inflationRate = infl,
                    lifeExpectancy = life,
                    unemploymentRate = unemp,
                    renewableEnergyShare = renew,
                    co2Emissions = co2,
                    gdpHistory = gdpHist,
                    inflationHistory = inflHist,
                )
            }
        }
    }

    private suspend fun fetchWorldBankIndicator(
        countryCode: String,
        indicator: String,
    ): Double? =
        try {
            val url = "https://api.worldbank.org/v2/country/$countryCode/indicator/$indicator?format=json&mrnev=1"
            val response = client.get(url).bodyAsText()
            decodeWorldBankRecords(response).firstOrNull()?.value
        } catch (e: Exception) {
            null
        }

    private suspend fun fetchWorldBankIndicatorHistory(
        countryCode: String,
        indicator: String,
    ): List<Pair<String, Double>> {
        return try {
            val url = "https://api.worldbank.org/v2/country/$countryCode/indicator/$indicator?format=json&mrnev=5"
            val response = client.get(url).bodyAsText()
            decodeWorldBankRecords(response)
                .mapNotNull { item ->
                    val date = item?.date ?: return@mapNotNull null
                    val value = item.value ?: return@mapNotNull null
                    date to value
                }.reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun decodeWorldBankRecords(response: String): List<WorldBankRecord?> {
        val envelope = json.decodeFromString<JsonArray>(response)
        val records = envelope.getOrNull(1) as? JsonArray ?: return emptyList()
        return json.decodeFromString(ListSerializer(WorldBankRecord.serializer().nullable), records.toString())
    }
}
