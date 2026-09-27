package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.InfoCard
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
internal fun DossierRestSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = Color(0xFF7DD3FC), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        content()
    }
}

@Composable
internal fun DossierRestStat(
    label: String,
    value: String?,
    symbol: UiSymbol,
    modifier: Modifier = Modifier,
) {
    if (value.isNullOrBlank()) return
    InfoCard(symbol = symbol, title = label.uppercase(), value = value, modifier = modifier)
}

@Composable
internal fun DossierRestNote(
    label: String,
    value: String?,
) {
    if (value.isNullOrBlank()) return
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label.uppercase(), color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color(0xFFE2E8F0), fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DossierRestChips(
    label: String,
    values: List<String>,
) {
    val visible = values.filter(String::isNotBlank).distinct()
    if (visible.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label.uppercase(), color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            visible.forEach { ChipPill(text = it) }
        }
    }
}

@Composable
internal fun DossierRestLinkTile(
    label: String,
    url: String?,
    onOpenLink: (String) -> Unit,
) {
    val safeUrl = url?.takeIf(::isSafeDossierUrl) ?: return
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onOpenLink(safeUrl) },
        shape = RoundedCornerShape(12.dp),
        color = Color(0x2228A7D8),
        border = BorderStroke(1.dp, Color(0x5538BDF8)),
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color(0xFFE0F2FE), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("OPEN", color = Color(0xFF7DD3FC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
