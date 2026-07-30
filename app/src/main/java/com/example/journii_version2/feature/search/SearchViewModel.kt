package com.example.journii_version2.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.SearchFilters
import com.example.journii_version2.core.model.isVisibleInDiscovery
import com.example.journii_version2.core.session.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SearchUiState(
    val filters: SearchFilters = SearchFilters(),
    val results: List<Inspiration> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * Filters over the existing feed stream rather than a dedicated repository
 * search method — there's only one data source (the in-memory feed) today.
 * A real backend will likely need its own paginated search endpoint; at that
 * point this filtering logic moves into the repository layer.
 */
class SearchViewModel(
    private val repository: InspirationRepository
) : ViewModel() {

    private val filters = MutableStateFlow(SearchFilters())

    val uiState: StateFlow<SearchUiState> = combine(
        repository.observeFeed(),
        filters
    ) { all, currentFilters ->
        val discoveryList = all.filter { it.isVisibleInDiscovery(CurrentUser.ID) }
        SearchUiState(
            filters = currentFilters,
            // Empty filters -> empty results (a search screen, not a mirrored feed)
            results = if (currentFilters.isEmpty) emptyList() else discoveryList.filter { it.matches(currentFilters) },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    fun onQueryChanged(query: String) {
        filters.value = filters.value.copy(query = query)
    }

    fun onDurationRangeChanged(minDays: Int?, maxDays: Int?) {
        filters.value = filters.value.copy(minDays = minDays, maxDays = maxDays)
    }

    fun toggleTag(tag: String) {
        val current = filters.value.selectedTags
        filters.value = filters.value.copy(
            selectedTags = if (tag in current) current - tag else current + tag
        )
    }

    fun clearFilters() {
        filters.value = SearchFilters()
    }

    private fun Inspiration.matches(filters: SearchFilters): Boolean {
        val queryMatches = filters.query.isBlank() ||
            destination.contains(filters.query, ignoreCase = true) ||
            country.contains(filters.query, ignoreCase = true)

        val minDaysMatches = filters.minDays == null || days >= filters.minDays
        val maxDaysMatches = filters.maxDays == null || days <= filters.maxDays
        val tagsMatch = filters.selectedTags.isEmpty() || filters.selectedTags.all { it in tags }

        return queryMatches && minDaysMatches && maxDaysMatches && tagsMatch
    }
}
