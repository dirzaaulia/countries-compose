package com.dirzaaulia.countries.ui.solarsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.solarsystem.InteriorLayer
import com.dirzaaulia.countries.domain.solarsystem.PlanetaryInterior
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetaryInteriorSheet(
    interior: PlanetaryInterior,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Auto-select the outermost layer by default
    var selectedLayer by remember { mutableStateOf<InteriorLayer?>(interior.layers.lastOrNull()) }

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF209111E),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFF64748B), width = 36.dp, height = 4.dp)
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "X-RAY CUTAWAY",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${interior.planetId.name} Interior",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
                MinimalistCloseButton(onClick = onClose)
            }

            Text(
                text = "Tap a concentric shell to inspect layer telemetry.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
            )

            InteriorCrossSectionCanvas(
                interior = interior,
                selectedLayerId = selectedLayer?.id,
                onLayerSelected = { selectedLayer = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(280.dp),
            )

            selectedLayer?.let { layer ->
                InteriorLayerCard(layer = layer)
            }
        }
    }
}
