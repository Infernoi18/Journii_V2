package com.example.journii_version2.feature.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.CopyMode
import com.example.journii_version2.core.model.CopySection
import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CopySheetUiState(
    val availableSections: Set<CopySection> = emptySet(),
    val selectedSections: Set<CopySection> = emptySet(),
    val isCopying: Boolean = false,
    val copiedInspirationId: String? = null,
    val errorMessage: String? = null
)

class CopyInspirationViewModel(
    private val repository: InspirationRepository,
    private val sourceInspiration: Inspiration
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CopySheetUiState(availableSections = computeAvailableSections(sourceInspiration))
    )
    val uiState: StateFlow<CopySheetUiState> = _uiState.asStateFlow()

    fun toggleSection(section: CopySection) {
        val current = _uiState.value.selectedSections
        _uiState.value = _uiState.value.copy(
            selectedSections = if (section in current) current - section else current + section,
            errorMessage = null
        )
    }

    fun copyEntire() = performCopy(CopyMode.Entire)

    fun copySelectedSections() {
        if (_uiState.value.selectedSections.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Choose at least one section to copy.")
            return
        }
        performCopy(CopyMode.Sections(_uiState.value.selectedSections))
    }

    fun importAndEditNow() = performCopy(CopyMode.ImportAndEdit)

    private fun performCopy(mode: CopyMode) {
        _uiState.value = _uiState.value.copy(isCopying = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val newId = repository.copyInspiration(sourceInspiration.id, mode)
                _uiState.value = if (newId == null) {
                    _uiState.value.copy(isCopying = false, errorMessage = "This inspiration is no longer available.")
                } else {
                    _uiState.value.copy(isCopying = false, copiedInspirationId = newId)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCopying = false,
                    errorMessage = "Couldn't copy this inspiration. Please try again."
                )
            }
        }
    }

    private fun computeAvailableSections(inspiration: Inspiration): Set<CopySection> {
        val sections = mutableSetOf<CopySection>()
        if (inspiration.itinerary.any { it.blocks.isNotEmpty() }) sections += CopySection.ITINERARY
        if (!inspiration.notes.isNullOrBlank()) sections += CopySection.NOTES
        if (inspiration.checklist.isNotEmpty()) sections += CopySection.CHECKLIST
        if (inspiration.tags.isNotEmpty()) sections += CopySection.TAGS
        return sections
    }
}
