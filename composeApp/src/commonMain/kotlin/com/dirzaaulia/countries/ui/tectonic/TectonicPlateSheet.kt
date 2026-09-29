package com.dirzaaulia.countries.ui.tectonic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.tectonic.TectonicPlate
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TectonicPlateSheet(
    plate: TectonicPlate,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1626),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFFEF4444),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
        ) {
            PlateSheetHeader(plate = plate, onClose = onClose)
            Spacer(Modifier.height(14.dp))
            PlateDetailsList(plate = plate)
        }
    }
}

@Composable
private fun PlateSheetHeader(
    plate: TectonicPlate,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SemanticIcon(UiSymbol.Landscape, contentDescription = null, tint = Color(0xFFEF4444))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${plate.plateType.uppercase()} LITHOSPHERIC PLATE",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = plate.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}

@Composable
private fun PlateDetailsList(plate: TectonicPlate) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "stats") {
            PlateStatsCard(plate = plate)
        }
        item(key = "dynamics") {
            PlateDynamicsCard(plate = plate)
        }
    }
}

@Composable
private fun PlateStatsCard(plate: TectonicPlate) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x33EF4444)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("PLATE METRICS & DRIFT", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Surface Area", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("${plate.areaMillionSqKm} M km²", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Drift Velocity", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("${plate.driftVelocityCmYear} cm/year", color = Color(0xFFF97316), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Heading: ${plate.driftDirection}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PlateDynamicsCard(plate: TectonicPlate) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, Color(0x33EF4444)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("TECTONIC MARGIN DYNAMICS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                text = when (plate.id) {
                    "pacific" -> "Features active convergent subduction zones along the Pacific Ring of Fire, giving rise to intense volcanism and deep-focus megathrust earthquakes."
                    "eurasian" -> "Forms collision zones with the Indian plate creating the Himalayas, and transform fault lines across southern Europe and Central Asia."
                    "north_american" -> "Intersects the Pacific plate along the San Andreas strike-slip fault system and subducts the Juan de Fuca plate in the Pacific Northwest."
                    else -> "Extends across oceanic ridges and continental margins with active seismic strain along divergent and convergent fault boundaries."
                },
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}
