package com.dirzaaulia.countries.ui.economy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.MAJOR_FINANCIAL_MARKETS
import com.dirzaaulia.countries.domain.astronomy.MarketStatus
import com.dirzaaulia.countries.domain.astronomy.calculateActiveOverlapName
import com.dirzaaulia.countries.domain.astronomy.calculateMarketStatus
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
    allMarkets: List<FinancialMarket> = MAJOR_FINANCIAL_MARKETS,
    onFlyToMarket: ((FinancialMarket) -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val listState = rememberLazyListState()
    val utcMillis = ((currentUtcMillis % 86_400_000L) + 86_400_000L) % 86_400_000L
    val utcHour = utcMillis / 3_600_000.0
    val overlapName = calculateActiveOverlapName(activeMarkets)
    val deviceLocalTime = formatLocalTime(currentUtcMillis)
    var selectedFilter by remember { mutableStateOf(MarketFilter.ALL) }

    val filteredMarkets =
        remember(allMarkets, selectedFilter, utcHour) {
            when (selectedFilter) {
                MarketFilter.ALL -> allMarkets
                MarketFilter.OPEN -> allMarkets.filter { calculateMarketStatus(it, utcHour) == MarketStatus.OPEN }
                MarketFilter.SOON -> allMarkets.filter { calculateMarketStatus(it, utcHour) == MarketStatus.OPENING_SOON }
                MarketFilter.CLOSED -> allMarkets.filter { calculateMarketStatus(it, utcHour) == MarketStatus.CLOSED }
            }
        }

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
        modifier = modifier,
    ) {
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "header") {
                SheetHeader(
                    overlapName = overlapName,
                    activeCount = activeMarkets.size,
                    totalCount = allMarkets.size,
                    onClose = onClose,
                )
            }
            item(key = "filters") {
                MarketFilterRow(
                    selectedFilter = selectedFilter,
                    allCount = allMarkets.size,
                    openCount = activeMarkets.size,
                    onSelectFilter = { selectedFilter = it },
                )
            }
            items(filteredMarkets, key = { it.id }) { market ->
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
            item(key = "bottom_spacer") {
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun SheetHeader(
    overlapName: String,
    activeCount: Int,
    totalCount: Int,
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
                text = "$activeCount of $totalCount Exchanges Currently Open",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}
