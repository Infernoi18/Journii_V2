package com.example.journii_version2.ui.screens.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.feature.feed.FeedViewModel
import com.example.journii_version2.feature.feed.FeedViewModelFactory
import com.example.journii_version2.ui.components.InspirationCard
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    repository: InspirationRepository,
    onInspirationClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val viewModel: FeedViewModel = viewModel(factory = FeedViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                actions = { TextButton(onClick = onProfileClick) { Text("Profile") } }
            )
        }
    ) { innerPadding ->
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
                    top = innerPadding.calculateTopPadding() + JourniiSpacing.sm,
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
