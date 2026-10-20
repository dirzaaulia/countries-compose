package com.dirzaaulia.countries.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.core.dynamicapi.FeatureInstallManager
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOnboardingSheet(
    installManager: FeatureInstallManager,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedModules by remember {
        mutableStateOf(DYNAMIC_MODULES_CATALOG.map { it.id }.toSet())
    }

    AdaptiveInfoSheet(
        onDismissRequest = onCompleteOnboarding,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = Color(0x99000000),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp,
            )
        },
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "WELCOME TO COUNTRIES COMPOSE",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Text(
                            text = "Select Your Feature Packs",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    MinimalistCloseButton(onClick = onCompleteOnboarding)
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(Spacing.md),
                    color = MaterialTheme.extendedColors.telemetryBackground,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Text(
                            text = "To keep your initial app installation fast and lightweight (~26 MB), extra 3D surfaces and space telemetry packs are delivered dynamically on-demand.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                        Text(
                            text = "Choose which feature packs you want to pre-download now or leave them for on-demand access later.",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            items(DYNAMIC_MODULES_CATALOG) { module ->
                val isSelected = selectedModules.contains(module.id)
                val isInstalled = installManager.isFeatureInstalled(module.id)

                Surface(
                    shape = RoundedCornerShape(Spacing.sm),
                    color = MaterialTheme.extendedColors.telemetryBackground,
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedModules =
                                    if (isSelected) {
                                        selectedModules - module.id
                                    } else {
                                        selectedModules + module.id
                                    }
                            },
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            Checkbox(
                                checked = isSelected || isInstalled,
                                onCheckedChange = { checked ->
                                    if (!isInstalled) {
                                        selectedModules =
                                            if (checked) {
                                                selectedModules + module.id
                                            } else {
                                                selectedModules - module.id
                                            }
                                    }
                                },
                                enabled = !isInstalled,
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                            )

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                ) {
                                    Text(
                                        text = module.title,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    if (isInstalled) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0x2210B981))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                                        ) {
                                            Text("READY", color = Color(0xFF34D399), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = module.description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(Spacing.xs))
                Button(
                    onClick = {
                        scope.launch {
                            selectedModules.forEach { fid ->
                                if (!installManager.isFeatureInstalled(fid)) {
                                    installManager.requestFeatureInstall(fid)
                                }
                            }
                            onCompleteOnboarding()
                        }
                    },
                    shape = RoundedCornerShape(Spacing.md),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                ) {
                    Text(
                        text = if (selectedModules.isEmpty()) "START EXPLORING (DOWNLOAD LATER)" else "DOWNLOAD SELECTED & EXPLORE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
