package com.example.journii_version2.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FeedUiState(
    val inspirations: List<Inspiration> = emptyList(),
    val selectedTag: String? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class FeedViewModel(
    private val repository: InspirationRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val selectedTag = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FeedUiState> = combine(
        repository.observeFeed(),
        isRefreshing,
        errorMessage,
        selectedTag
    ) { inspirations, refreshing, error, tag ->
        val filtered = if (tag == null) {
            inspirations.filter { !it.isDraft }
        } else {
            inspirations.filter { !it.isDraft && tag in it.tags }
        }
        FeedUiState(
            inspirations = filtered,
            selectedTag = tag,
            isRefreshing = refreshing,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedUiState()
    )

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            errorMessage.value = null
            try {
                repository.refreshFeed()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage.value = "Couldn't refresh the feed. Please try again."
            } finally {
                isRefreshing.value = false
            }
        }
    }

    fun toggleLike(inspirationId: String) {
        viewModelScope.launch { repository.toggleLike(inspirationId) }
    }

    fun toggleSave(inspirationId: String) {
        viewModelScope.launch { repository.toggleSave(inspirationId) }
    }

    fun selectTag(tag: String?) {
        selectedTag.value = tag
    }
}
