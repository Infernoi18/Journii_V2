package com.example.journii_version2.ui.screens.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.ui.components.InspirationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailScreen(
    onBackClick: () -> Unit,
    onInspirationClick: (String) -> Unit,
    viewModel: WishlistDetailViewModel = viewModel(factory = WishlistDetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.wishlist?.name ?: "Wishlist", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp
            ) {
                items(uiState.inspirations, key = { it.id }) { inspiration ->
                    InspirationCard(
                        inspiration = inspiration,
                        onClick = { onInspirationClick(inspiration.id) },
                        onLikeClick = { /* No-op in wishlist */ },
                        onSaveClick = { viewModel.removeFromWishlist(inspiration.id) }
                    )
                }
            }
        }
    }
}
