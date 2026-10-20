package com.dirzaaulia.countries.ui.mars

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.dirzaaulia.countries.domain.mars.MarsPhoto
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton
import com.dirzaaulia.countries.ui.theme.Spacing
import com.dirzaaulia.countries.ui.theme.extendedColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarsRoverGallerySheet(
    onClose: () -> Unit,
    viewModel: MarsRoverGalleryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(topStart = Spacing.lg, topEnd = Spacing.lg),
        containerColor = MaterialTheme.extendedColors.overlayBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFF64748B),
                width = 36.dp,
                height = 4.dp,
            )
        },
    ) {
        if (uiState.selectedPhoto != null) {
            FullScreenPhotoPreview(
                photo = uiState.selectedPhoto!!,
                onClose = { viewModel.selectPhoto(null) },
            )
        } else {
            GalleryContent(
                uiState = uiState,
                onSelectRover = viewModel::selectRover,
                onSelectPhoto = viewModel::selectPhoto,
                onClose = onClose,
            )
        }
    }
}

@Composable
private fun GalleryContent(
    uiState: MarsRoverUiState,
    onSelectRover: (String) -> Unit,
    onSelectPhoto: (MarsPhoto) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md)
                .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        HeaderRow(uiState.rover, onSelectRover, onClose)
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.errorMessage != null) {
            Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
        } else if (uiState.photos.isEmpty()) {
            Text("No photos found for this sol.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            PhotoGrid(uiState.photos, onSelectPhoto)
        }
    }
}

@Composable
private fun HeaderRow(
    currentRover: String,
    onSelectRover: (String) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoverSegmentedControl(currentRover, onSelectRover)
        MinimalistCloseButton(onClick = onClose)
    }
}

@Composable
private fun RoverSegmentedControl(
    currentRover: String,
    onSelectRover: (String) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(Spacing.sm))
                .background(MaterialTheme.extendedColors.telemetryBackground)
                .padding(4.dp),
    ) {
        RoverTab("Perseverance", currentRover == "perseverance") { onSelectRover("perseverance") }
        Spacer(modifier = Modifier.width(4.dp))
        RoverTab("Curiosity", currentRover == "curiosity") { onSelectRover("curiosity") }
    }
}

@Composable
private fun RoverTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.uppercase(),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun ShimmerPhotoPlaceholder() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmerAlpha",
    )
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF1E293B).copy(alpha = alpha)),
    )
}

@Composable
private fun PhotoGrid(
    photos: List<MarsPhoto>,
    onSelectPhoto: (MarsPhoto) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        modifier = Modifier.height(400.dp),
    ) {
        items(photos) { photo ->
            SubcomposeAsyncImage(
                model = photo.imgSrc,
                contentDescription = "Mars Photo ${photo.id}",
                modifier =
                    Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(Spacing.sm))
                        .clickable { onSelectPhoto(photo) },
                contentScale = ContentScale.Crop,
                loading = { ShimmerPhotoPlaceholder() },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("📷", fontSize = 18.sp)
                    }
                },
            )
        }
    }
}

@Composable
private fun FullScreenPhotoPreview(
    photo: MarsPhoto,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = photo.cameraFullName.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "Earth Date: ${photo.earthDate}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
            MinimalistCloseButton(onClick = onClose)
        }
        SubcomposeAsyncImage(
            model = photo.imgSrc,
            contentDescription = "Full Screen Mars Photo",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Spacing.md)),
            contentScale = ContentScale.Fit,
            loading = { ShimmerPhotoPlaceholder() },
            error = {
                Box(
                    modifier = Modifier.fillMaxWidth().height(240.dp).background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("📷", fontSize = 24.sp)
                }
            },
        )
    }
}
