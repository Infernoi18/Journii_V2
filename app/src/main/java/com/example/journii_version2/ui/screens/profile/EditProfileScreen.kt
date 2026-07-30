package com.example.journii_version2.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.journii_version2.ui.components.PrivacyPicker
import com.example.journii_version2.feature.profile.EditProfileViewModel
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBack: () -> Unit,
    onNavigateBackAfterSave: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveProfile(onNavigateBackAfterSave) },
                        enabled = !uiState.isSaving && !uiState.isLoading
                    ) {
                        Text("Save")
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
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(JourniiSpacing.md)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(JourniiSpacing.md)
            ) {
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = viewModel::onDisplayNameChanged,
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.error != null && uiState.displayName.isBlank()
                )

                OutlinedTextField(
                    value = uiState.bio,
                    onValueChange = viewModel::onBioChanged,
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text("Profile Privacy", style = MaterialTheme.typography.titleSmall)
                Text(
                    "This controls who can see your profile details and your list of inspirations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PrivacyPicker(
                    selected = uiState.privacy,
                    onSelected = viewModel::onPrivacyChanged,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
