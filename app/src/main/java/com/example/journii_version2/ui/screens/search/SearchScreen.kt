package com.example.journii_version2.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.SearchTagOptions
import com.example.journii_version2.feature.search.SearchViewModel
import com.example.journii_version2.feature.search.SearchViewModelFactory
import com.example.journii_version2.ui.components.InspirationCard
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    repository: InspirationRepository,
    onInspirationClick: (String) -> Unit
) {
    val viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()
    var durationRange by remember { mutableStateOf(1f..14f) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = JourniiSpacing.sm,
            end = JourniiSpacing.sm,
            top = JourniiSpacing.sm,
            bottom = JourniiSpacing.lg
        ),
        horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.sm),
        verticalItemSpacing = JourniiSpacing.sm,
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Column(modifier = Modifier.padding(horizontal = JourniiSpacing.xs)) {
                Text(text = "Search", style = MaterialTheme.typography.headlineMedium)
                Spacer1()

                OutlinedTextField(
                    value = uiState.filters.query,
                    onValueChange = viewModel::onQueryChanged,
                    label = { Text("Destination or country") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer2()

                Text(
                    text = "Trip length: ${durationRange.start.toInt()}–${durationRange.endInclusive.toInt()} days",
                    style = MaterialTheme.typography.titleSmall
                )
                RangeSlider(
                    value = durationRange,
                    onValueChange = { range ->
                        durationRange = range
                        viewModel.onDurationRangeChanged(range.start.toInt(), range.endInclusive.toInt())
                    },
                    valueRange = 1f..14f,
                    steps = 12
                )

                Spacer1()

                Text(text = "Trip style", style = MaterialTheme.typography.titleSmall)
                Spacer1()

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)
                ) {
                    SearchTagOptions.ALL.forEach { tag ->
                        FilterChip(
                            selected = tag in uiState.filters.selectedTags,
                            onClick = { viewModel.toggleTag(tag) },
                            label = { Text(tag) }
                        )
                    }
                }

                if (!uiState.filters.isEmpty) {
                    Spacer1()
                    TextButton(onClick = {
                        durationRange = 1f..14f
                        viewModel.clearFilters()
                    }) {
                        Text("Clear filters")
                    }
                }

                Spacer2()
            }
        }

        when {
            uiState.filters.isEmpty -> item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text = "Start typing or pick a filter to explore inspirations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(JourniiSpacing.md)
                )
            }
            uiState.results.isEmpty() -> item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text = "No inspirations match those filters yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(JourniiSpacing.md)
                )
            }
            else -> items(uiState.results, key = { it.id }) { inspiration ->
                InspirationCard(
                    inspiration = inspiration,
                    onClick = { onInspirationClick(inspiration.id) },
                    onLikeClick = {},
                    onSaveClick = {}
                )
            }
        }
    }
}

@Composable
private fun Spacer1() {
    Spacer(modifier = Modifier.height(JourniiSpacing.xs))
}

@Composable
private fun Spacer2() {
    Spacer(modifier = Modifier.height(JourniiSpacing.md))
}
