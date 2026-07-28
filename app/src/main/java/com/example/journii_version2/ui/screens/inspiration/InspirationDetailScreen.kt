package com.example.journii_version2.ui.screens.inspiration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.ItineraryDay
import com.example.journii_version2.feature.inspiration.InspirationDetailViewModel
import com.example.journii_version2.feature.inspiration.InspirationDetailViewModelFactory
import com.example.journii_version2.ui.components.ItineraryBlockRow
import com.example.journii_version2.ui.components.TagChip
import com.example.journii_version2.ui.components.TransportConnectorRow
import com.example.journii_version2.ui.theme.JourniiSpacing

@Composable
fun InspirationDetailScreen(
    repository: InspirationRepository,
    inspirationId: String,
    onBackClick: () -> Unit,
    onCopyCompleted: (String) -> Unit
) {
    val viewModel: InspirationDetailViewModel = viewModel(
        factory = InspirationDetailViewModelFactory(repository, inspirationId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val inspiration = uiState.inspiration
    var showCopySheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (inspiration != null) {
                Button(
                    onClick = { showCopySheet = true },
                    modifier = Modifier.fillMaxWidth().padding(JourniiSpacing.md)
                ) {
                    Text("Copy Inspiration")
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            inspiration == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { Text("This inspiration is no longer available.") }
            }
            else -> {
                InspirationDetailContent(
                    inspiration = inspiration,
                    onBackClick = onBackClick,
                    onLikeClick = viewModel::toggleLike,
                    onSaveClick = viewModel::toggleSave,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    if (showCopySheet && inspiration != null) {
        CopyInspirationSheet(
            sourceInspiration = inspiration,
            repository = repository,
            onDismiss = { showCopySheet = false },
            onCopyCompleted = { newId ->
                showCopySheet = false
                onCopyCompleted(newId)
            }
        )
    }
}

@Composable
private fun InspirationDetailContent(
    inspiration: Inspiration,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val days = inspiration.itinerary

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Box {
                AsyncImage(
                    model = inspiration.coverImageUrl,
                    contentDescription = inspiration.destination,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(280.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent)
                            )
                        )
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(JourniiSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onBackClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) { Text("← Back") }

                    Row {
                        TextButton(
                            onClick = onLikeClick,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text(if (inspiration.isLikedByCurrentUser) "♥ ${inspiration.likeCount}" else "♡ ${inspiration.likeCount}")
                        }
                        TextButton(
                            onClick = onSaveClick,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text(if (inspiration.isSavedByCurrentUser) "Saved" else "Save")
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(JourniiSpacing.md)) {
                Text(text = inspiration.destination, style = MaterialTheme.typography.displaySmall)
                Text(
                    text = "${inspiration.country} · ${inspiration.days} days",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(JourniiSpacing.sm))

                Text(
                    text = "By ${inspiration.creator.displayName} (@${inspiration.creator.username})",
                    style = MaterialTheme.typography.bodyMedium
                )

                inspiration.copiedFromInspirationId?.let {
                    Spacer(modifier = Modifier.height(JourniiSpacing.xs))
                    Text(
                        text = "Copied from another traveler's inspiration",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (inspiration.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                    Row(horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)) {
                        inspiration.tags.forEach { tag -> TagChip(label = tag) }
                    }
                }

                inspiration.shortDescription?.let {
                    Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                }

                inspiration.notes?.let {
                    Spacer(modifier = Modifier.height(JourniiSpacing.md))
                    Text(text = "Personal Notes", style = MaterialTheme.typography.titleMedium)
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }

                if (inspiration.checklist.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(JourniiSpacing.md))
                    Text(text = "Checklist", style = MaterialTheme.typography.titleMedium)
                    inspiration.checklist.forEach { checklistItem ->
                        Text(
                            text = if (checklistItem.isChecked) "☑ ${checklistItem.label}" else "☐ ${checklistItem.label}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (days.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = JourniiSpacing.md)) {
                    Text(text = "Itinerary", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                    Row(horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)) {
                        days.forEachIndexed { index, day ->
                            val isSelected = index == selectedDayIndex
                            TextButton(onClick = { selectedDayIndex = index }) {
                                Text(
                                    text = "Day ${day.dayNumber}",
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }

            val selectedDay: ItineraryDay? = days.getOrNull(selectedDayIndex)
            if (selectedDay != null) {
                if (selectedDay.blocks.isEmpty()) {
                    item {
                        Text(
                            text = "No stops added for this day yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(JourniiSpacing.md)
                        )
                    }
                } else {
                    items(selectedDay.blocks, key = { it.id }) { block ->
                        Column(modifier = Modifier.padding(horizontal = JourniiSpacing.md)) {
                            ItineraryBlockRow(block = block)
                            block.transportToNext?.let { mode -> TransportConnectorRow(transportMode = mode) }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(JourniiSpacing.xxl)) }
    }
}
