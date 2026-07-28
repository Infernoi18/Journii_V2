package com.example.journii_version2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.ui.theme.JourniiSpacing

/**
 * Pinterest-style card for the Discovery Feed. Lives in ui/components (not
 * the feed package) because Profile's pinned/copied grids and Wishlists
 * reuse it unchanged.
 */
@Composable
fun InspirationCard(
    inspiration: Inspiration,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageAspectRatio: Float = 1.1f
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = inspiration.coverImageUrl,
                contentDescription = inspiration.destination,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio)
            )

            Column(modifier = Modifier.padding(JourniiSpacing.sm)) {
                Text(
                    text = inspiration.destination,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "${inspiration.country} · ${inspiration.days} days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(JourniiSpacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onLikeClick) {
                        Text(
                            text = if (inspiration.isLikedByCurrentUser) {
                                "♥ ${inspiration.likeCount}"
                            } else {
                                "♡ ${inspiration.likeCount}"
                            },
                            color = if (inspiration.isLikedByCurrentUser) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    TextButton(onClick = onSaveClick) {
                        Text(if (inspiration.isSavedByCurrentUser) "Saved" else "Save")
                    }
                }
            }
        }
    }
}
