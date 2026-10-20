package com.dirzaaulia.countries.data.worldbank

import kotlinx.serialization.Serializable

@Serializable
internal data class WorldBankRecord(
    val date: String? = null,
    val value: Double? = null,
)
