package com.example.journii_version2.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.ChecklistItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class OptionalSectionsUiState(
    val notes: String = "",
    val checklist: List<ChecklistItem> = emptyList(),
    val tags: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isPublished: Boolean = false,
    val errorMessage: String? = null
)

class OptionalSectionsViewModel(
    private val repository: InspirationRepository,
    private val inspirationId: String
) : ViewModel() {

    private val isSaving = MutableStateFlow(false)
    private val isPublished = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<OptionalSectionsUiState> = combine(
        repository.observeInspiration(inspirationId),
        isSaving,
        isPublished,
        errorMessage
    ) { inspiration, saving, published, error ->
        OptionalSectionsUiState(
            notes = inspiration?.notes.orEmpty(),
            checklist = inspiration?.checklist ?: emptyList(),
            tags = inspiration?.tags ?: emptyList(),
            isLoading = inspiration == null,
            isSaving = saving,
            isPublished = published,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OptionalSectionsUiState()
    )

    fun updateNotes(notes: String) {
        val current = uiState.value
        persist(notes, current.checklist, current.tags)
    }

    fun addChecklistItem(label: String) {
        val trimmed = label.trim()
        if (trimmed.isEmpty()) return
        val current = uiState.value
        val newItem = ChecklistItem(id = UUID.randomUUID().toString(), label = trimmed)
        persist(current.notes, current.checklist + newItem, current.tags)
    }

    fun toggleChecklistItem(itemId: String) {
        val current = uiState.value
        val updated = current.checklist.map {
            if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it
        }
        persist(current.notes, updated, current.tags)
    }

    fun removeChecklistItem(itemId: String) {
        val current = uiState.value
        val updated = current.checklist.filterNot { it.id == itemId }
        persist(current.notes, updated, current.tags)
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim().lowercase()
        if (trimmed.isEmpty()) return
        val current = uiState.value
        if (trimmed in current.tags) return
        persist(current.notes, current.checklist, current.tags + trimmed)
    }

    fun removeTag(tag: String) {
        val current = uiState.value
        persist(current.notes, current.checklist, current.tags.filterNot { it == tag })
    }

    fun publish() {
        isSaving.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                repository.publishInspiration(inspirationId)
                isPublished.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage.value = "Couldn't publish. Please try again."
            } finally {
                isSaving.value = false
            }
        }
    }

    private fun persist(notes: String?, checklist: List<ChecklistItem>, tags: List<String>) {
        isSaving.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                repository.updateOptionalSections(inspirationId, notes, checklist, tags)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage.value = "Couldn't save change."
            } finally {
                isSaving.value = false
            }
        }
    }
}
