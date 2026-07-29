package com.example.journii_version2.ui.screens.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.feature.create.CreateInspirationViewModel
import com.example.journii_version2.feature.create.CreateInspirationViewModelFactory
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBasicInfoScreen(
    repository: InspirationRepository,
    onDraftCreated: (String) -> Unit
) {
    val viewModel: CreateInspirationViewModel = viewModel(factory = CreateInspirationViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdInspirationId) {
        uiState.createdInspirationId?.let { id ->
            onDraftCreated(id)
            viewModel.resetAfterNavigation()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("New Inspiration") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(JourniiSpacing.md)
        ) {
            Text(text = "Start with the basics", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "You can add the day-by-day itinerary and everything else after this.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(JourniiSpacing.md))

            OutlinedTextField(
                value = uiState.destination,
                onValueChange = viewModel::onDestinationChanged,
                label = { Text("Destination *") },
                singleLine = true,
                isError = uiState.destinationError != null,
                supportingText = { uiState.destinationError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(JourniiSpacing.sm))

            OutlinedTextField(
                value = uiState.country,
                onValueChange = viewModel::onCountryChanged,
                label = { Text("Country *") },
                singleLine = true,
                isError = uiState.countryError != null,
                supportingText = { uiState.countryError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(JourniiSpacing.sm))

            OutlinedTextField(
                value = uiState.days,
                onValueChange = viewModel::onDaysChanged,
                label = { Text("Number of days *") },
                singleLine = true,
                isError = uiState.daysError != null,
                supportingText = { uiState.daysError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(JourniiSpacing.sm))

            OutlinedTextField(
                value = uiState.coverImageUrl,
                onValueChange = viewModel::onCoverImageUrlChanged,
                label = { Text("Cover image URL *") },
                singleLine = true,
                isError = uiState.coverImageError != null,
                supportingText = {
                    Text(
                        uiState.coverImageError
                            ?: "Standing in for a real image picker until camera/gallery access is wired up."
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(JourniiSpacing.sm))

            OutlinedTextField(
                value = uiState.shortDescription,
                onValueChange = viewModel::onShortDescriptionChanged,
                label = { Text("Short description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.generalError?.let {
                Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(JourniiSpacing.md))

            Button(
                onClick = viewModel::createDraft,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Continue")
                }
            }

            Spacer(modifier = Modifier.height(JourniiSpacing.xxl))
        }
    }
}
