package com.dirzaaulia.countries.core.dynamicapi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Common entry point contract for on-demand dynamic feature modules (e.g., :feature:quiz, :feature:comparison).
 */
interface DynamicFeatureEntry {
    val featureId: String

    @Composable
    fun RenderFeature(
        modifier: Modifier,
    )
}
