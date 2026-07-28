package com.example.journii_version2.core.model

/**
 * Filters implemented today, matched to fields that already exist on
 * Inspiration. Budget, Visa Free, Pet Friendly, and Wheelchair Friendly from
 * the spec aren't included yet — see Batch 9's notes for why. Adding any of
 * them later is additive to this data class, not a redesign.
 */
data class SearchFilters(
    val query: String = "",
    val minDays: Int? = null,
    val maxDays: Int? = null,
    val selectedTags: Set<String> = emptySet()
) {
    val isEmpty: Boolean
        get() = query.isBlank() && minDays == null && maxDays == null && selectedTags.isEmpty()
}

/** The trip-style facets from the spec that map onto Inspiration.tags today. */
object SearchTagOptions {
    val ALL = listOf(
        "Solo", "Family", "Luxury", "Backpacking", "Adventure", "Food", "Beach", "Nightlife"
    )
}
