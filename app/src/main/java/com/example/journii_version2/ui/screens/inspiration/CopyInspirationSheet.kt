package com.example.journii_version2.ui.screens.inspiration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.CopySection
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.feature.inspiration.CopyInspirationViewModel
import com.example.journii_version2.feature.inspiration.CopyInspirationViewModelFactory
import com.example.journii_version2.ui.theme.JourniiSpacing

private enum class CopySheetStep { CHOOSE_MODE, CHOOSE_SECTIONS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopyInspirationSheet(
    sourceInspiration: Inspiration,
    repository: InspirationRepository,
    onDismiss: () -> Unit,
    onCopyCompleted: (String) -> Unit
) {
    val viewModel: CopyInspirationViewModel = viewModel(
        factory = CopyInspirationViewModelFactory(repository, sourceInspiration)
    )
    val uiState by viewModel.uiState.collectAsState()
    var step by remember { mutableStateOf(CopySheetStep.CHOOSE_MODE) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.copiedInspirationId) {
        uiState.copiedInspirationId?.let { onCopyCompleted(it) }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(JourniiSpacing.md)
        ) {
            when (step) {
                CopySheetStep.CHOOSE_MODE -> ChooseModeStep(
                    isCopying = uiState.isCopying,
                    hasSelectableSections = uiState.availableSections.isNotEmpty(),
                    onCopyEntire = viewModel::copyEntire,
                    onSelectSections = { step = CopySheetStep.CHOOSE_SECTIONS },
                    onImportAndEdit = viewModel::importAndEditNow
                )
                CopySheetStep.CHOOSE_SECTIONS -> ChooseSectionsStep(
                    availableSections = uiState.availableSections,
                    selectedSections = uiState.selectedSections,
                    isCopying = uiState.isCopying,
                    onToggleSection = viewModel::toggleSection,
                    onBack = { step = CopySheetStep.CHOOSE_MODE },
                    onConfirm = viewModel::copySelectedSections
                )
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(JourniiSpacing.sm))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(JourniiSpacing.md))
        }
    }
}

@Composable
private fun ChooseModeStep(
    isCopying: Boolean,
    hasSelectableSections: Boolean,
    onCopyEntire: () -> Unit,
    onSelectSections: () -> Unit,
    onImportAndEdit: () -> Unit
) {
    Text(text = "Copy Inspiration", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(JourniiSpacing.xs))
    Text(
        text = "This creates a draft on your profile that remembers the original creator.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(JourniiSpacing.md))

    Button(onClick = onCopyEntire, enabled = !isCopying, modifier = Modifier.fillMaxWidth()) {
        Text("Copy Entire Inspiration")
    }

    Spacer(modifier = Modifier.height(JourniiSpacing.sm))

    OutlinedButton(
        onClick = onSelectSections,
        enabled = !isCopying && hasSelectableSections,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (hasSelectableSections) "Select Specific Sections" else "No optional sections to select")
    }

    Spacer(modifier = Modifier.height(JourniiSpacing.sm))

    OutlinedButton(onClick = onImportAndEdit, enabled = !isCopying, modifier = Modifier.fillMaxWidth()) {
        Text("Import Everything & Edit Now")
    }

    if (isCopying) {
        Spacer(modifier = Modifier.height(JourniiSpacing.md))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ChooseSectionsStep(
    availableSections: Set<CopySection>,
    selectedSections: Set<CopySection>,
    isCopying: Boolean,
    onToggleSection: (CopySection) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    TextButton(onClick = onBack) { Text("← Back") }
    Spacer(modifier = Modifier.height(JourniiSpacing.xs))
    Text(text = "Choose what to copy", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(JourniiSpacing.sm))

    availableSections.forEach { section ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = section in selectedSections,
                onCheckedChange = { onToggleSection(section) }
            )
            Text(text = section.displayName())
        }
    }

    Spacer(modifier = Modifier.height(JourniiSpacing.md))

    Button(
        onClick = onConfirm,
        enabled = !isCopying && selectedSections.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Copy Selected Sections")
    }
}

private fun CopySection.displayName(): String = when (this) {
    CopySection.ITINERARY -> "Itinerary"
    CopySection.NOTES -> "Personal Notes"
    CopySection.CHECKLIST -> "Checklist"
    CopySection.TAGS -> "Tags"
}
