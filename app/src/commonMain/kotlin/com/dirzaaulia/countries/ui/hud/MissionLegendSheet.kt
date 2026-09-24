package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class LegendItem(
    val title: String,
    val description: String,
    val badgeColor: Color,
    val icon: String? = null,
    val isDashed: Boolean = false,
    val strokeWidth: Float = 3f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionLegendSheet(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF209111E),
        contentColor = Color.White,
        scrimColor = Color(0x66000000),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🛰️ MISSION CONTROL HUD LEGEND",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Cartographic Symbology & Sensor Guide",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                MinimalistCloseButton(onClick = onClose)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Planetary Boundaries & Astronomy
            LegendCategorySection(
                title = "PLANETARY BOUNDARIES & ASTRONOMY",
                items = listOf(
                    LegendItem(
                        title = "Selected Nation Focus",
                        description = "Inspected national borders highlighted with outer atmospheric neon cyan glow.",
                        badgeColor = Color(0xFF38BDF8),
                        strokeWidth = 4f
                    ),
                    LegendItem(
                        title = "Daylight Cartographic Borders",
                        description = "Crisp obsidian black boundaries with soft white halo visible across sunlit land and oceans.",
                        badgeColor = Color(0xFF0F172A),
                        strokeWidth = 2.5f
                    ),
                    LegendItem(
                        title = "Nighttime Cartographic Borders",
                        description = "Luminous ivory boundaries with dark rims illuminated against nocturnal city lights and darkness.",
                        badgeColor = Color(0xFFF8FAFC),
                        strokeWidth = 2.5f
                    ),
                    LegendItem(
                        title = "Solar Twilight Terminator",
                        description = "Dynamic Golden-amber transition band dividing solar daylight and night in astronomical real time.",
                        badgeColor = Color(0xFFF59E0B),
                        strokeWidth = 3f
                    )
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Aviation & Spaceflight Navigation
            LegendCategorySection(
                title = "AVIATION & SPACE TRAJECTORIES",
                items = listOf(
                    LegendItem(
                        title = "Geodesic Great Circle Flight Path",
                        description = "Aviation gold dashed geodesic flight route with supersonic aircraft heading & altitude climb arc.",
                        badgeColor = Color(0xFFF59E0B),
                        icon = "✈️",
                        isDashed = true,
                        strokeWidth = 3f
                    ),
                    LegendItem(
                        title = "ISS Orbital Ground Track",
                        description = "Real-time micro-dashed Low Earth Orbit trajectory completing a full 92.9-minute global pass at ~420km altitude.",
                        badgeColor = Color(0xFF06B6D4),
                        icon = "🛰️",
                        isDashed = true,
                        strokeWidth = 2.5f
                    ),
                    LegendItem(
                        title = "Apollo Lunar Landing Sites",
                        description = "NASA Apollo 11–17 historic touchdown sites on the 3D Moon explorer with mission telemetry cards.",
                        badgeColor = Color(0xFFFBBF24),
                        icon = "🚀"
                    )
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. NASA EONET Active Natural Hazards
            LegendCategorySection(
                title = "NASA EONET LIVE PLANETARY HAZARDS",
                items = listOf(
                    LegendItem(
                        title = "Volcanic Eruption",
                        description = "Active volcanic eruption or hazardous ash plume detected by satellite.",
                        badgeColor = Color(0xFFEF4444),
                        icon = "🌋"
                    ),
                    LegendItem(
                        title = "Wildfire Complex",
                        description = "Thermal anomaly or major active wildfire burning in the region.",
                        badgeColor = Color(0xFFF97316),
                        icon = "🔥"
                    ),
                    LegendItem(
                        title = "Severe Storm / Cyclone",
                        description = "Tropical cyclone, hurricane, typhoon, or extreme convective storm system.",
                        badgeColor = Color(0xFFA855F7),
                        icon = "🌀"
                    ),
                    LegendItem(
                        title = "Polar / Sea Ice Activity",
                        description = "Significant sea ice breakup, glacial movement, or navigational iceberg hazard.",
                        badgeColor = Color(0xFF38BDF8),
                        icon = "🧊"
                    )
                )
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun LegendCategorySection(
    title: String,
    items: List<LegendItem>
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x1F0F172A),
            border = BorderStroke(1.dp, Color(0x2238BDF8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stroke / Badge Preview
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(28.dp)
                                .background(Color(0x331E293B), CircleShape)
                                .border(BorderStroke(1.dp, Color(0x22FFFFFF)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.icon != null) {
                                Text(text = item.icon, fontSize = 13.sp)
                            } else {
                                Canvas(modifier = Modifier.size(16.dp)) {
                                    drawLine(
                                        color = item.badgeColor,
                                        start = Offset(0f, size.height / 2f),
                                        end = Offset(size.width, size.height / 2f),
                                        strokeWidth = item.strokeWidth,
                                        cap = StrokeCap.Round,
                                        pathEffect = if (item.isDashed) PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f) else null
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
