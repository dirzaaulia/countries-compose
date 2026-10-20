package com.dirzaaulia.countries.ui.economy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.dirzaaulia.countries.domain.astronomy.FinancialMarket
import com.dirzaaulia.countries.domain.astronomy.MarketStatus
import com.dirzaaulia.countries.platform.PlatformCountryFlag
import com.dirzaaulia.countries.ui.components.SemanticIcon
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
internal fun MarketExchangeCard(
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
            MarketExchangeHeader(market = market, statusText = statusText, statusColor = statusColor)
            Spacer(Modifier.height(8.dp))
            MarketExchangeTimestamps(exchangeTime = exchangeTime, tzLabel = tzLabel, deviceTime = deviceTime)
            if (onFlyToMarket != null) {
                Spacer(Modifier.height(8.dp))
                FlyToExchangeButton(market = market, statusColor = statusColor, onFlyToMarket = onFlyToMarket)
            }
        }
    }
}

@Composable
private fun MarketExchangeHeader(
    market: FinancialMarket,
    statusText: String,
    statusColor: Color,
) {
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
}

@Composable
private fun MarketExchangeTimestamps(
    exchangeTime: String,
    tzLabel: String,
    deviceTime: String,
) {
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
}

@Composable
private fun FlyToExchangeButton(
    market: FinancialMarket,
    statusColor: Color,
    onFlyToMarket: (FinancialMarket) -> Unit,
) {
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
