package com.example.journii_version2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.journii_version2.core.model.Privacy
import com.example.journii_version2.ui.theme.JourniiSpacing

/**
 * Shared Public / Followers Only / Private picker — used by both Create's
 * Basic Info step and Edit Profile, so the two privacy settings (an
 * Inspiration's and a profile's) look and behave identically.
 */
@Composable
fun PrivacyPicker(
    selected: Privacy,
    onSelected: (Privacy) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.xs)) {
        Privacy.entries.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelected(option) },
                label = { Text(option.displayName()) }
            )
        }
    }
}

internal fun Privacy.displayName(): String = when (this) {
    Privacy.PUBLIC -> "Public"
    Privacy.FOLLOWERS_ONLY -> "Followers Only"
    Privacy.PRIVATE -> "Private"
}
