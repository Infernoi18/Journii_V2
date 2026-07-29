package com.example.journii_version2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.journii_version2.core.model.Wishlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToWishlistSheet(
    wishlists: List<Wishlist>,
    onWishlistSelected: (String) -> Unit,
    onCreateNewWishlist: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Save to Wishlist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                item {
                    ListItem(
                        headlineContent = { Text("Create New Wishlist", color = MaterialTheme.colorScheme.primary) },
                        leadingContent = { Text("＋", style = MaterialTheme.typography.titleLarge) },
                        modifier = Modifier.clickable(onClick = onCreateNewWishlist)
                    )
                }

                items(wishlists) { wishlist ->
                    ListItem(
                        headlineContent = { Text(wishlist.name) },
                        supportingContent = wishlist.description?.let { { Text(it) } },
                        leadingContent = { Text("📁") },
                        modifier = Modifier.clickable { onWishlistSelected(wishlist.id) }
                    )
                }
            }
        }
    }
}
