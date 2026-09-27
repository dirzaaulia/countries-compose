package com.dirzaaulia.countries.domain.astronomy

import kotlinx.serialization.Serializable

@Serializable
data class EclipseFeed(
    val schemaVersion: Int,
    val generatedAt: String,
    val attribution: String,
    val events: List<EclipseEvent>,
)

@Serializable
data class EclipseEvent(
    val id: String,
    val kind: String,
    val type: String,
    val date: String,
    val greatestTimeTd: String,
    val magnitude: Double,
    val saros: Int,
    val duration: String? = null,
    val visibility: String,
    val mapUrl: String,
    val pathUrl: String? = null,
    val sourceUrl: String,
)
