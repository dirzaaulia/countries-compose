package com.dirzaaulia.countries.ui.dossier.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.ChipPill
import com.dirzaaulia.countries.ui.components.UiSymbol

@Composable
fun DossierCultureSection(
    currencies: List<String>,
    languages: List<String>,
) {
    Spacer(Modifier.height(14.dp))
    DossierSectionTitle("CULTURE")
    if (currencies.isNotEmpty()) {
        DossierPillGroup(
            label = "Official Currencies:",
            values = currencies,
            symbol = UiSymbol.Currency,
            background = Color(0x2210B981),
            border = Color(0x4410B981),
            text = Color(0xFFA7F3D0),
        )
    }
    if (languages.isNotEmpty()) {
        DossierPillGroup(
            label = "Official Languages:",
            values = languages,
            symbol = UiSymbol.Language,
            background = Color(0x228B5CF6),
            border = Color(0x448B5CF6),
            text = Color(0xFFDDD6FE),
        )
    }
}

@Composable
private fun DossierPillGroup(
    label: String,
    values: List<String>,
    symbol: UiSymbol,
    background: Color,
    border: Color,
    text: Color,
) {
    Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp)
    Spacer(Modifier.height(4.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        values.forEach { value ->
            ChipPill(text = value, symbol = symbol, backgroundColor = background, borderColor = border, textColor = text)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun DossierActionButtons(
    onClose: () -> Unit,
    onNextCountry: () -> Unit,
    onCompare: (() -> Unit)? = null,
) {
    Spacer(Modifier.height(18.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onClose,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0x44EF4444)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
        ) {
            Text("Dismiss", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        if (onCompare != null) {
            OutlinedButton(
                onClick = onCompare,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x6638BDF8)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
            ) {
                Text("Compare", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
        Button(
            onClick = onNextCountry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7), contentColor = Color.White),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
        ) {
            Text("Next Country", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(24.dp))
}
