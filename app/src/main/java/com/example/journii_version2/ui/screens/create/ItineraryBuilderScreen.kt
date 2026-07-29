package com.example.journii_version2.ui.screens.create

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.BlockCategory
import com.example.journii_version2.core.model.ItineraryBlock
import com.example.journii_version2.core.model.TransportMode
import com.example.journii_version2.feature.create.ItineraryBuilderViewModel
import com.example.journii_version2.feature.create.ItineraryBuilderViewModelFactory
import com.example.journii_version2.ui.components.displayName
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryBuilderScreen(
    repository: InspirationRepository,
    inspirationId: String,
    onBackClick: () -> Unit,
    onContinueClick: (String) -> Unit
) {
    val viewModel: ItineraryBuilderViewModel = viewModel(
        factory = ItineraryBuilderViewModelFactory(repository, inspirationId)
    )
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.destination.ifBlank { "Itinerary" }) },
                navigationIcon = { TextButton(onClick = onBackClick) { Text("← Back") } },
                actions = {
                    TextButton(onClick = { onContinueClick(inspirationId) }) {
                        Text("Next")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.days.isNotEmpty()) {
                FloatingActionButton(onClick = { showAddDialog = true }) { Text("+") }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = JourniiSpacing.md, vertical = JourniiSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)
                ) {
                    uiState.days.forEachIndexed { index, day ->
                        val isSelected = index == uiState.selectedDayIndex
                        TextButton(onClick = { viewModel.selectDay(index) }) {
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

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = JourniiSpacing.md)
                    )
                }

                val selectedDay = uiState.days.getOrNull(uiState.selectedDayIndex)
                val blocks = selectedDay?.blocks.orEmpty()

                if (blocks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No stops yet for this day. Tap + to add the first one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(JourniiSpacing.md)
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(JourniiSpacing.md),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                            EditableItineraryBlockRow(
                                block = block,
                                isFirst = index == 0,
                                isLast = index == blocks.lastIndex,
                                onMoveUp = { viewModel.moveBlockUp(block.id) },
                                onMoveDown = { viewModel.moveBlockDown(block.id) },
                                onRemove = { viewModel.removeBlock(block.id) },
                                onTransportSelected = { mode -> viewModel.setTransportToNext(block.id, mode) }
                            )
                            Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStopDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, category, time, notes ->
                viewModel.addBlock(title, category, time, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EditableItineraryBlockRow(
    block: ItineraryBlock,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    onTransportSelected: (TransportMode?) -> Unit
) {
    var showTransportMenu by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.padding(JourniiSpacing.sm)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    block.time?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(text = block.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = block.category.displayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    block.notes?.let {
                        Text(text = it, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = onMoveUp, enabled = !isFirst) { Text("↑") }
                    TextButton(onClick = onMoveDown, enabled = !isLast) { Text("↓") }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLast) {
                    Box {
                        TextButton(onClick = { showTransportMenu = true }) {
                            Text(block.transportToNext?.let { "→ ${it.displayName()}" } ?: "Set transport to next")
                        }
                        DropdownMenu(expanded = showTransportMenu, onDismissRequest = { showTransportMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = { onTransportSelected(null); showTransportMenu = false }
                            )
                            TransportMode.entries.forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode.displayName()) },
                                    onClick = { onTransportSelected(mode); showTransportMenu = false }
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(1.dp))
                }
                TextButton(onClick = onRemove) { Text("Remove") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStopDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: BlockCategory, time: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BlockCategory.OTHER) }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a stop") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                Text(text = "Category", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)
                ) {
                    BlockCategory.entries.forEach { option ->
                        FilterChip(
                            selected = option == category,
                            onClick = { category = option },
                            label = { Text(option.displayName()) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (optional, e.g. 9:00 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(title, category, time, notes) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}