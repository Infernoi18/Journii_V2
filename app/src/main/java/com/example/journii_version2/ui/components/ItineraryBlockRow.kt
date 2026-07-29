package com.example.journii_version2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.journii_version2.core.model.BlockCategory
import com.example.journii_version2.core.model.ItineraryBlock
import com.example.journii_version2.core.model.TransportMode

@Composable
fun ItineraryBlockRow(
    block: ItineraryBlock,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = block.time ?: "—",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(64.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
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
    }
}

@Composable
fun TransportConnectorRow(
    transportMode: TransportMode,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 76.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "↓ ${transportMode.displayName()}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** Internal (not private) so Create's Itinerary Builder can reuse these labels too. */
internal fun BlockCategory.displayName(): String = when (this) {
    BlockCategory.TRANSIT -> "Transit"
    BlockCategory.FOOD -> "Food"
    BlockCategory.SIGHTSEEING -> "Sightseeing"
    BlockCategory.ACCOMMODATION -> "Accommodation"
    BlockCategory.ACTIVITY -> "Activity"
    BlockCategory.BEACH -> "Beach"
    BlockCategory.OTHER -> "Other"
}

internal fun TransportMode.displayName(): String = when (this) {
    TransportMode.WALKING -> "Walking"
    TransportMode.METRO -> "Metro"
    TransportMode.TAXI -> "Taxi"
    TransportMode.RENTAL_CAR -> "Rental Car"
    TransportMode.TRAIN -> "Train"
    TransportMode.BUS -> "Bus"
    TransportMode.FLIGHT -> "Flight"
    TransportMode.FERRY -> "Ferry"
    TransportMode.BIKE -> "Bike"
}