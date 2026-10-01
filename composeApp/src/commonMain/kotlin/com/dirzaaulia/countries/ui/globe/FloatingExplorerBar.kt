package com.dirzaaulia.countries.ui.globe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun FloatingExplorerBar(
    onOpenSearch: () -> Unit,
    onExploreRandom: () -> Unit,
    showTimeMachine: Boolean,
    onToggleTimeMachine: () -> Unit,
    onOpenNasaCrisis: () -> Unit,
    isFlightMode: Boolean,
    onToggleFlightMode: () -> Unit,
    isQuizMode: Boolean,
    onToggleQuizMode: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSpaceWeather: () -> Unit = {},
    onToggleMarketCard: () -> Unit = {},
    onOpenSolarSystem: () -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                onClick = onOpenSearch,
                shape = RoundedCornerShape(18.dp),
                color = Color(0xF209111E),
                border = BorderStroke(1.dp, Color(0x5538BDF8)),
                shadowElevation = 10.dp,
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SemanticIcon(UiSymbol.Search, "Search", Color(0xFF38BDF8), Modifier.size(18.dp))
                    Text(
                        text = "Search countries, capitals...",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Surface(
                onClick = { isExpanded = !isExpanded },
                shape = CircleShape,
                color = if (isExpanded) Color(0xEE0284C7) else Color(0xF209111E),
                border = BorderStroke(1.dp, Color(0x5538BDF8)),
                shadowElevation = 10.dp,
                modifier = Modifier.size(46.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.Widgets,
                        contentDescription = if (isExpanded) "Collapse menu" else "Expand menu",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xF209111E),
                border = BorderStroke(1.dp, Color(0x4438BDF8)),
                shadowElevation = 12.dp,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
            ) {
                FlowRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    MenuActionButton(
                        label = "Random",
                        symbol = UiSymbol.Quiz,
                        color = Color(0xFF0284C7),
                        onClick = {
                            isExpanded = false
                            onExploreRandom()
                        },
                    )

                    MenuActionButton(
                        label = "Planetary Time",
                        symbol = UiSymbol.Time,
                        color = if (showTimeMachine) Color(0xFFF59E0B) else Color(0xFF64748B),
                        isActive = showTimeMachine,
                        onClick = { onToggleTimeMachine() },
                    )

                    MenuActionButton(
                        label = "Crisis Monitor",
                        symbol = UiSymbol.Hazard,
                        color = Color(0xFFEF4444),
                        onClick = {
                            isExpanded = false
                            onOpenNasaCrisis()
                        },
                    )

                    MenuActionButton(
                        label = "Flight Route",
                        symbol = UiSymbol.Flight,
                        color = if (isFlightMode) Color(0xFFF59E0B) else Color(0xFF64748B),
                        isActive = isFlightMode,
                        onClick = { onToggleFlightMode() },
                    )

                    MenuActionButton(
                        label = "World Quiz",
                        symbol = UiSymbol.Quiz,
                        color = if (isQuizMode) Color(0xFF10B981) else Color(0xFF64748B),
                        isActive = isQuizMode,
                        onClick = { onToggleQuizMode() },
                    )

                    MenuActionButton(
                        label = "Space Weather",
                        symbol = UiSymbol.Clear,
                        color = Color(0xFF10B981),
                        onClick = {
                            isExpanded = false
                            onOpenSpaceWeather()
                        },
                    )

                    MenuActionButton(
                        label = "Financial Markets",
                        symbol = UiSymbol.Economy,
                        color = Color(0xFFF59E0B),
                        onClick = {
                            isExpanded = false
                            onToggleMarketCard()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuActionButton(
    label: String,
    symbol: UiSymbol,
    color: Color,
    isActive: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = if (isActive) color.copy(alpha = 0.35f) else Color(0x351E293B),
                contentColor = Color.White,
            ),
        border = BorderStroke(1.dp, if (isActive) color else Color(0x3338BDF8)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SemanticIcon(symbol, label, if (isActive) Color.White else color, Modifier.size(15.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
