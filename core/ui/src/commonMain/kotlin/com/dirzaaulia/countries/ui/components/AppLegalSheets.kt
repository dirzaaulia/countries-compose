package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppPrivacyPolicySheet(onClose: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val uriHandler = LocalUriHandler.current

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp,
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Privacy Policy",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                MinimalistCloseButton(onClick = onClose)
            }

            Surface(
                shape = RoundedCornerShape(Spacing.md),
                color = MaterialTheme.extendedColors.telemetryBackground,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(
                        text = "1. Zero Personal Data Collection\nCountries Compose does NOT collect, store, or share any personally identifiable information (PII). No user accounts or advertising identifiers are used.\n\n2. Network Scientific APIs\nDirect requests are made to open scientific APIs (NASA, NOAA, USGS, Open-Meteo, World Bank, REST Countries) to fulfill telemetry data. No personal data is transmitted.\n\n3. Local In-Memory Cache\nSession data is stored temporarily in local memory to reduce bandwidth and is cleared when the app closes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )

                    Button(
                        onClick = { uriHandler.openUri("https://countries-509405.web.app/privacy") },
                        shape = RoundedCornerShape(Spacing.sm),
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("OPEN WEB PRIVACY POLICY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppTermsSheet(onClose: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val uriHandler = LocalUriHandler.current

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp,
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Terms & Conditions",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                MinimalistCloseButton(onClick = onClose)
            }

            Surface(
                shape = RoundedCornerShape(Spacing.md),
                color = MaterialTheme.extendedColors.telemetryBackground,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(
                        text = "1. Acceptance of Terms\nBy accessing Countries Compose, you agree to these terms. The app is designed for educational and informational visualization.\n\n2. Scientific Telemetry Disclaimer\nData is provided 'AS IS' from open scientific sources. Telemetry should not be used for emergency navigation or life-safety decisions.\n\n3. Developer Support\nVoluntary donations via Ko-fi or Saweria support ongoing independent development.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )

                    Button(
                        onClick = { uriHandler.openUri("https://countries-509405.web.app/tnc") },
                        shape = RoundedCornerShape(Spacing.sm),
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("OPEN WEB TERMS & CONDITIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
