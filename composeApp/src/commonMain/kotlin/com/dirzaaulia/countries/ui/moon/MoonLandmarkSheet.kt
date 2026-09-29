package com.dirzaaulia.countries.ui.moon

import androidx.compose.runtime.Composable
import com.dirzaaulia.countries.domain.moon.LunarLandmark

@Composable
fun MoonLandmarkSheet(
    landmark: LunarLandmark,
    onClose: () -> Unit,
    onFlyToSite: ((Double, Double) -> Unit)? = null,
) {
    LunarLandmarkSheet(
        landmark = landmark,
        onClose = onClose,
        onFlyToSite = onFlyToSite,
    )
}
