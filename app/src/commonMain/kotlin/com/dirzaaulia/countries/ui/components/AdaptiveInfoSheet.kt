package com.dirzaaulia.countries.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveInfoSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    containerColor: Color = Color(0xF209111E),
    contentColor: Color = Color.White,
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = Color.Transparent,
    dragHandle: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
                modifier = modifier,
                sheetState = sheetState,
                shape = shape,
                containerColor = containerColor,
                contentColor = contentColor,
                tonalElevation = tonalElevation,
                scrimColor = scrimColor,
                dragHandle = dragHandle,
                content = content,
            )
        } else {
            Box(Modifier.fillMaxSize()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .clickable(onClick = onDismissRequest),
                )
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInHorizontally { it },
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Surface(
                        modifier =
                            modifier
                                .fillMaxHeight()
                                .width(420.dp),
                        shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
                        color = containerColor,
                        contentColor = contentColor,
                        tonalElevation = tonalElevation,
                    ) {
                        Column {
                            if (dragHandle != null) {
                                Box(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    dragHandle()
                                }
                            }
                            Column(
                                verticalArrangement = Arrangement.Top,
                                content = content,
                            )
                        }
                    }
                }
            }
        }
    }
}
