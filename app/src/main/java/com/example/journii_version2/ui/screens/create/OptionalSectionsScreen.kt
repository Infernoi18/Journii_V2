package com.example.journii_version2.ui.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.feature.create.OptionalSectionsViewModel
import com.example.journii_version2.feature.create.OptionalSectionsViewModelFactory
import com.example.journii_version2.ui.theme.JourniiSpacing
import com.example.journii_version2.ui.components.TagChip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OptionalSectionsScreen(
    repository: InspirationRepository,
    inspirationId: String,
    onBackClick: () -> Unit,
    onPublished: () -> Unit
) {
    val viewModel: OptionalSectionsViewModel = viewModel(
        factory = OptionalSectionsViewModelFactory(repository, inspirationId)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isPublished) {
        if (uiState.isPublished) {
            onPublished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Final Touches") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("← Back") }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = viewModel::publish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(JourniiSpacing.md),
                enabled = !uiState.isLoading && !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Publish Inspiration")
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(JourniiSpacing.md),
                verticalArrangement = Arrangement.spacedBy(JourniiSpacing.lg)
            ) {
                item {
                    Text("Notes", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::updateNotes,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Private notes, tips, or reminders...") }
                    )
                }

                item {
                    Text("Checklist", style = MaterialTheme.typography.titleLarge)
                    ChecklistSection(
                        items = uiState.checklist,
                        onToggle = viewModel::toggleChecklistItem,
                        onRemove = viewModel::removeChecklistItem,
                        onAdd = viewModel::addChecklistItem
                    )
                }

                item {
                    Text("Tags", style = MaterialTheme.typography.titleLarge)
                    TagsSection(
                        tags = uiState.tags,
                        onAdd = viewModel::addTag,
                        onRemove = viewModel::removeTag
                    )
                }

                uiState.errorMessage?.let {
                    item {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecklistSection(
    items: List<com.example.journii_version2.core.model.ChecklistItem>,
    onToggle: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAdd: (String) -> Unit
) {
    var newItemLabel by remember { mutableStateOf("") }

    Column {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = item.isChecked, onCheckedChange = { onToggle(item.id) })
                Text(item.label, modifier = Modifier.weight(1f))
                IconButton(onClick = { onRemove(item.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newItemLabel,
                onValueChange = { newItemLabel = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add item...") },
                singleLine = true
            )
            IconButton(onClick = {
                onAdd(newItemLabel)
                newItemLabel = ""
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
private fun TagsSection(
    tags: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var newTag by remember { mutableStateOf("") }

    Column {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)
        ) {
            tags.forEach { tag ->
                InputChip(
                    selected = false,
                    onClick = { onRemove(tag) },
                    label = { Text(tag) },
                    trailingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(JourniiSpacing.sm))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTag,
                onValueChange = { newTag = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add tag (e.g. food, hiking)") },
                singleLine = true
            )
            IconButton(onClick = {
                onAdd(newTag)
                newTag = ""
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
