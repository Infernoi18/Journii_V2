package com.example.journii_version2.feature.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InspirationDetailUiState(
    val inspiration: Inspiration? = null,
    val isLoading: Boolean = true
)

class InspirationDetailViewModel(
    private val repository: InspirationRepository,
    inspirationId: String
) : ViewModel() {

    val uiState: StateFlow<InspirationDetailUiState> = repository
        .observeInspiration(inspirationId)
        .map { found -> InspirationDetailUiState(inspiration = found, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InspirationDetailUiState(isLoading = true)
        )

    fun toggleLike() {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch { repository.toggleLike(id) }
    }

    fun toggleSave() {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch { repository.toggleSave(id) }
    }
}
