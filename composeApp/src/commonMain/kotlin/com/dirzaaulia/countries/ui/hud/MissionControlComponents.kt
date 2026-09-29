package com.dirzaaulia.countries.ui.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
internal fun TimeDisplayCard(
    utcTime: String,
    localTime: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(19.dp),
        color = Color(0xEE0B1220),
        border = BorderStroke(1.dp, Color(0x4438BDF8)),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SemanticIcon(
                symbol = UiSymbol.Time,
                contentDescription = "Time",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(14.dp),
            )
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = utcTime.ifEmpty { "12:00 UTC" },
                    color = Color.White,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (localTime.isNotEmpty()) {
                    Text(
                        text = "$localTime LOCAL",
                        color = Color(0xFF94A3B8),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
internal fun CelestialSwitcher(
    currentPage: Int,
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(19.dp),
        color = Color(0xEE0B1220),
        border = BorderStroke(1.dp, Color(0x4438BDF8)),
        modifier = modifier.heightIn(min = 38.dp),
    ) {
        Row(modifier = Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
            CelestialTabPill("Earth", UiSymbol.Earth, currentPage == 0, Color(0xFF38BDF8), { onSelectPage(0) }, Modifier.weight(1f).fillMaxHeight())
            CelestialTabPill("Moon", UiSymbol.Moon, currentPage == 1, Color(0xFFFFD54F), { onSelectPage(1) }, Modifier.weight(1f).fillMaxHeight())
            CelestialTabPill("Mars", UiSymbol.Mars, currentPage == 2, Color(0xFFE67E22), { onSelectPage(2) }, Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
internal fun CelestialTabPill(
    label: String,
    icon: UiSymbol,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(17.dp))
                .background(if (isSelected) activeColor.copy(alpha = 0.25f) else Color.Transparent)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SemanticIcon(icon, label, if (isSelected) Color.White else Color(0xFF94A3B8), Modifier.size(13.dp))
            Text(label, color = if (isSelected) Color.White else Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
internal fun TopBarIconButton(
    symbol: UiSymbol? = null,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null,
) {
    Surface(
        shape = CircleShape,
        color = Color(0xDD0B1220),
        border = BorderStroke(1.dp, Color(0x3338BDF8)),
        modifier = modifier.aspectRatio(1f),
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            if (content != null) content() else SemanticIcon(symbol!!, contentDescription, Color.White, Modifier.size(18.dp))
        }
    }
}

@Composable
internal fun MissionStatusCard(
    currentPage: Int,
    moonDistanceKm: Double,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x351E293B),
        border = BorderStroke(1.dp, Color(0x2238BDF8)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            val statusColor = when (currentPage) {
                0 -> Color(0xFF10B981)
                1 -> Color(0xFFFFD54F)
                else -> Color(0xFFE67E22)
            }
            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = when (currentPage) {
                        0 -> "EARTH ORBITAL TRACKING"
                        1 -> "LUNAR RANGE"
                        else -> "MARS ROVER LINK"
                    },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = when (currentPage) {
                        0 -> "OPERATIONAL · LIVE TELEMETRY"
                        1 -> "${(moonDistanceKm / 1000.0).toInt()}K KM FROM EARTH"
                        else -> "DSN LINK ESTABLISHED"
                    },
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
internal fun MissionActionRow(
    title: String,
    symbol: UiSymbol,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x351E293B),
        border = BorderStroke(1.dp, Color(0x2238BDF8)),
        modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).clickable { onClick() },
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SemanticIcon(symbol, title, tint, Modifier.size(20.dp))
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun LayerToggleCard(
    label: String,
    subLabel: String,
    icon: UiSymbol,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) activeColor.copy(alpha = 0.20f) else Color(0x351E293B),
        border = BorderStroke(1.dp, if (isActive) activeColor.copy(alpha = 0.70f) else Color(0x33475569)),
        modifier = modifier.clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SemanticIcon(icon, label, if (isActive) activeColor else Color(0xFF94A3B8), Modifier.size(15.dp))
                Text(label, color = if (isActive) Color.White else Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(subLabel, color = if (isActive) activeColor else Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}
