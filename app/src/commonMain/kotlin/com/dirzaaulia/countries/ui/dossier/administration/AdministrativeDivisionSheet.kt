package com.dirzaaulia.countries.ui.dossier.administration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.InfoCard
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol
import com.dirzaaulia.countries.ui.components.uiSymbolFor
import com.dirzaaulia.countries.util.formatArea
import com.dirzaaulia.countries.util.formatCoordinates
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrativeDivisionSheet(
    country: Country,
    administrativeDivisions: List<AdministrativeDivision>,
    isFetchingAdministrativeDivisions: Boolean,
    selectedAdministrativeDivision: AdministrativeDivision?,
    administrativeLevel: AdminLevel,
    selectedAdm1Division: AdministrativeDivision?,
    onClose: () -> Unit,
    onLoadAdministrativeDivisions: () -> Unit,
    onShowAdm1: () -> Unit,
    onSelectAdministrativeDivision: (AdministrativeDivision) -> Unit,
    onCenterDivision: ((AdministrativeDivision) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    LaunchedEffect(country.id, administrativeLevel) {
        if (administrativeDivisions.isEmpty() && !isFetchingAdministrativeDivisions) {
            onLoadAdministrativeDivisions()
        }
    }

    val handleSelectDivision: (AdministrativeDivision) -> Unit = { division ->
        scope.launch {
            sheetState.hide()
            onClose()
            onSelectAdministrativeDivision(division)
        }
    }

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1220),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF38BDF8).copy(alpha = 0.6f),
                width = 36.dp,
                height = 4.dp,
            )
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Item 1: Header: Flag + Country Name & Title
            item {
                AdminHeaderCard(country = country, onClose = onClose)
            }

            // Item 2: Level Selection Chips / Breadcrumbs
            item {
                AdminLevelSelector(
                    administrativeLevel = administrativeLevel,
                    selectedAdm1Division = selectedAdm1Division,
                    onShowAdm1 = onShowAdm1,
                )
            }

            // Item 3: Selected Division Information Card
            if (selectedAdministrativeDivision != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x350F172A),
                        border = BorderStroke(1.dp, Color(0x5538BDF8)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedAdministrativeDivision.name,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text =
                                            if (selectedAdministrativeDivision.shapeType.isNotEmpty()) {
                                                "${selectedAdministrativeDivision.shapeType} • Code: ${selectedAdministrativeDivision.code}"
                                            } else {
                                                "${selectedAdministrativeDivision.level.name} Division • Code: ${selectedAdministrativeDivision.code}"
                                            },
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                ChipPill(
                                    text = selectedAdministrativeDivision.level.name,
                                    backgroundColor = Color(0x3310B981),
                                    borderColor = Color(0x6610B981),
                                    textColor = Color(0xFFA7F3D0),
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                InfoCard(
                                    symbol = UiSymbol.Landscape,
                                    title = "SURFACE AREA",
                                    value = formatArea(selectedAdministrativeDivision.areaSqKm),
                                    subValue = "${selectedAdministrativeDivision.boundaryPolygons.size} Polygons",
                                    modifier = Modifier.weight(1f),
                                )
                                InfoCard(
                                    symbol = UiSymbol.Location,
                                    title = "CENTER COORDINATES",
                                    value = formatCoordinates(selectedAdministrativeDivision.center),
                                    subValue = "${selectedAdministrativeDivision.boundaryPolygons.sumOf { it.size }} Vertices",
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            if (selectedAdministrativeDivision.weatherTempC != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x330284C7),
                                    border = BorderStroke(1.dp, Color(0x4438BDF8)),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    val weatherMarker =
                                        selectedAdministrativeDivision.weatherIcon
                                            ?.uppercase()
                                            ?.let { marker ->
                                                when {
                                                    marker.contains("THUNDER") || marker.contains("STORM") -> "[STORM]"
                                                    marker.contains("SNOW") || marker.contains("ICE") -> "[SNOW]"
                                                    marker.contains("RAIN") || marker.contains("DRIZZLE") || marker.contains("SHOWER") -> "[RAIN]"
                                                    marker.contains("CLOUD") || marker.contains("OVERCAST") || marker.contains("FOG") -> "[CLOUD]"
                                                    marker.contains("CLEAR") || marker.contains("SUN") || marker.contains("FAIR") -> "[CLEAR]"
                                                    else -> null
                                                }
                                            } ?: "[CLEAR]"

                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            SemanticIcon(
                                                symbol = uiSymbolFor(weatherMarker),
                                                contentDescription = selectedAdministrativeDivision.weatherDescription ?: "Local weather",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(18.dp),
                                            )
                                            Column {
                                                Text(
                                                    text = "${selectedAdministrativeDivision.weatherTempC}°C",
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = selectedAdministrativeDivision.weatherDescription ?: "Fair",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 10.sp,
                                                )
                                            }
                                        }
                                        Text(
                                            text = "LOCAL WEATHER",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { (onCenterDivision ?: onSelectAdministrativeDivision).invoke(selectedAdministrativeDivision) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0x6638BDF8)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                ) {
                                    Text("Center View", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }

                                if (selectedAdministrativeDivision.level == AdminLevel.ADM1) {
                                    Button(
                                        onClick = { handleSelectDivision(selectedAdministrativeDivision) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    ) {
                                        Text("View ADM2 Local ↗", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Item 4: Divisions List Section
            if (isFetchingAdministrativeDivisions) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Fetching ${administrativeLevel.name} boundary & metadata...",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            } else if (administrativeDivisions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x180F172A),
                        border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No ${administrativeLevel.name} administrative divisions available for ${country.name}.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "${administrativeLevel.name} DIVISIONS (${administrativeDivisions.size})",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                }

                items(administrativeDivisions, key = { it.id }) { division ->
                    val isSelected = division.id == selectedAdministrativeDivision?.id
                    AdminDivisionRow(
                        division = division,
                        isSelected = isSelected,
                        onClick = { handleSelectDivision(division) },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = administrativeDivisions.firstOrNull()?.attribution.orEmpty(),
                        color = Color(0xFF64748B),
                        fontSize = 9.sp,
                    )
                }
            }
        }
    }
}
