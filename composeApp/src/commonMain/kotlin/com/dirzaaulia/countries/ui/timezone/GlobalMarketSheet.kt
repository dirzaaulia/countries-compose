package com.dirzaaulia.countries.ui.timezone

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.MAJOR_FINANCIAL_MARKETS
import com.dirzaaulia.countries.domain.astronomy.MarketStatus
import com.dirzaaulia.countries.domain.astronomy.calculateActiveOverlapName
import com.dirzaaulia.countries.domain.astronomy.calculateMarketStatus
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.platform.formatLocalTime
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalMarketSheet(
    activeMarkets: List<FinancialMarket>,
    currentUtcMillis: Long,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onFlyToMarket: ((FinancialMarket) -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val utcMillis = ((currentUtcMillis % 86_400_000L) + 86_400_000L) % 86_400_000L
    val utcHour = utcMillis / 3_600_000.0
    val overlapName = calculateActiveOverlapName(activeMarkets)
    val deviceLocalTime = formatLocalTime(currentUtcMillis)

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xF20B1626),
        contentColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
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
            SheetHeader(overlapName = overlapName, activeCount = activeMarkets.size, onClose = onClose)
            Spacer(Modifier.height(12.dp))
            MarketList(
                utcHour = utcHour,
                deviceLocalTime = deviceLocalTime,
                onFlyToMarket = onFlyToMarket,
            )
        }
    }
}

@Composable
private fun SheetHeader(
    overlapName: String,
    activeCount: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SemanticIcon(UiSymbol.Economy, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "GLOBAL FINANCIAL MARKETS",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = overlapName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "$activeCount Exchanges Currently Open",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}

@Composable
private fun MarketList(
    utcHour: Double,
    deviceLocalTime: String,
    modifier: Modifier = Modifier,
    onFlyToMarket: ((FinancialMarket) -> Unit)? = null,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(MAJOR_FINANCIAL_MARKETS, key = { it.id }) { market ->
            val status = calculateMarketStatus(market, utcHour)
            val localHour = (utcHour + market.utcOffsetHours + 24.0) % 24.0
            val h = localHour.toInt()
            val m = ((localHour - h) * 60.0).toInt()
            val exchangeTimeStr = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"

            MarketExchangeCard(
                market = market,
                status = status,
                exchangeTime = exchangeTimeStr,
                deviceTime = deviceLocalTime,
                onFlyToMarket = onFlyToMarket,
            )
        }
    }
}

@Composable
private fun MarketExchangeCard(
    market: FinancialMarket,
    status: MarketStatus,
    exchangeTime: String,
    deviceTime: String,
    modifier: Modifier = Modifier,
    onFlyToMarket: ((FinancialMarket) -> Unit)? = null,
) {
    val (statusText, statusColor) =
        when (status) {
            MarketStatus.OPEN -> "[OPEN]" to Color(0xFF10B981)
            MarketStatus.OPENING_SOON -> "[SOON]" to Color(0xFFF59E0B)
            MarketStatus.CLOSED -> "[CLOSED]" to Color(0xFF64748B)
        }

    val tzLabel = if (market.utcOffsetHours >= 0) "UTC+${market.utcOffsetHours}" else "UTC${market.utcOffsetHours}"

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F0F172A),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformCountryFlag(
                        iso2 = market.countryIso2,
                        modifier = Modifier.height(16.dp).width(24.dp).clip(RoundedCornerShape(3.dp)),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = market.city,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = market.exchange,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                        )
                    }
                }
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Exchange: $exchangeTime ($tzLabel)",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Your Device: $deviceTime",
                    color = Color(0xFFF59E0B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (onFlyToMarket != null) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onFlyToMarket(market) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor.copy(alpha = 0.25f), contentColor = Color.White),
                    border = BorderStroke(1.dp, statusColor),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SemanticIcon(UiSymbol.Location, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Fly to Exchange", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
