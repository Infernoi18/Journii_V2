package com.example.journii_version2.ui.screens.wishlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.model.Wishlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onWishlistClick: (String) -> Unit,
    viewModel: WishlistViewModel = viewModel(factory = WishlistViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlists", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Text("＋", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Your Collections")
            }

            // Default Wishlist (All Saved)
            item {
                WishlistCard(
                    name = "All Saved",
                    count = uiState.savedInspirations.size,
                    onClick = { onWishlistClick("default") }
                )
            }

            // Custom Wishlists
            items(uiState.customWishlists) { wishlist ->
                WishlistCard(
                    name = wishlist.name,
                    count = wishlist.inspirationIds.size,
                    onClick = { onWishlistClick(wishlist.id) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateWishlistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, desc, isPublic ->
                viewModel.createWishlist(name, desc, isPublic)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun WishlistCard(
    name: String,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$count items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(text = "→", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun CreateWishlistDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Wishlist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPublic, onCheckedChange = { isPublic = it })
                    Text("Make Public")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, description.ifBlank { null }, isPublic) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
