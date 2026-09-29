package com.dirzaaulia.countries.ui.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryComparisonSheet(
    uiState: ComparisonUiState,
    onClose: () -> Unit,
    onSwap: () -> Unit,
    onSelectSlotForPicker: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
            ) {
                SheetTopBar(onClose = onClose)
                ComparisonContentList(
                    uiState = uiState,
                    onSwap = onSwap,
                    onSelectSlotForPicker = onSelectSlotForPicker,
                )
            }
        }
    }
}

@Composable
private fun SheetTopBar(onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "COUNTRY COMPARISON & TRUE SIZE",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MinimalistCloseButton(onClick = onClose)
        }
    }
}

@Composable
private fun ComparisonContentList(
    uiState: ComparisonUiState,
    onSwap: () -> Unit,
    onSelectSlotForPicker: (Int) -> Unit,
) {
    val countryA = uiState.countryA
    val countryB = uiState.countryB

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "header") {
            ComparisonHeaderSection(
                countryA = countryA,
                countryB = countryB,
                onSwap = onSwap,
                onSelectSlot = onSelectSlotForPicker,
            )
        }

        if (countryA != null && countryB != null) {
            item(key = "area") {
                ComparisonAreaCard(countryA = countryA, countryB = countryB)
            }
            item(key = "population") {
                ComparisonPopulationCard(countryA = countryA, countryB = countryB)
            }
            item(key = "macroeconomics") {
                ComparisonMacroeconomicsCard(
                    countryA = countryA,
                    countryB = countryB,
                    detailsA = uiState.detailsA,
                    detailsB = uiState.detailsB,
                )
            }
            item(key = "weatherTime") {
                ComparisonWeatherTimeCard(
                    countryA = countryA,
                    countryB = countryB,
                    detailsA = uiState.detailsA,
                    detailsB = uiState.detailsB,
                )
            }
            item(key = "spacer") {
                Spacer(Modifier.height(24.dp))
            }
        } else {
            item(key = "prompt") {
                ComparisonPromptCard()
            }
        }
    }
}

@Composable
private fun ComparisonPromptCard() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Select two countries above to compare land area, population, economics, and view True Size silhouette overlay.",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )
    }
}
