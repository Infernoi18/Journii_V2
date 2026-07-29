package com.example.journii_version2.ui.screens.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.SearchTagOptions
import com.example.journii_version2.feature.feed.FeedViewModel
import com.example.journii_version2.feature.feed.FeedViewModelFactory
import com.example.journii_version2.ui.components.InspirationCard
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    repository: InspirationRepository,
    onInspirationClick: (String) -> Unit
) {
    val viewModel: FeedViewModel = viewModel(factory = FeedViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Discover") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = JourniiSpacing.sm),
                modifier = Modifier.fillMaxWidth().padding(vertical = JourniiSpacing.xs)
            ) {
                lazyItems(SearchTagOptions.ALL) { tag ->
                    val isSelected = uiState.selectedTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectTag(if (isSelected) null else tag) },
                        label = { Text(tag) },
                        modifier = Modifier.padding(horizontal = JourniiSpacing.xs)
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
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
                    items(uiState.inspirations, key = { it.id }) { inspiration ->
                        InspirationCard(
                            inspiration = inspiration,
                            onClick = { onInspirationClick(inspiration.id) },
                            onLikeClick = { viewModel.toggleLike(inspiration.id) },
                            onSaveClick = { viewModel.toggleSave(inspiration.id) }
                        )
                    }
                }
            }
        }
    }
}
