package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.CopyMode
import com.example.journii_version2.core.model.ChecklistItem
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.ItineraryDay
import kotlinx.coroutines.flow.Flow

interface InspirationRepository {
    fun observeFeed(): Flow<List<Inspiration>>
    fun observeInspiration(inspirationId: String): Flow<Inspiration?>
    suspend fun refreshFeed()
    suspend fun toggleLike(inspirationId: String)
    suspend fun toggleSave(inspirationId: String)
    suspend fun updateInspiration(inspiration: Inspiration)

    /** Returns the new Inspiration's id, or null if the source no longer exists. */
    suspend fun copyInspiration(sourceId: String, mode: CopyMode): String?

    /** Creates a new draft Inspiration with just the mandatory fields filled in. Returns its id. */
    suspend fun createDraft(
        destination: String,
        country: String,
        days: Int,
        coverImageUrl: String,
        shortDescription: String?
    ): String

    suspend fun deleteInspiration(inspirationId: String)

    /** Replaces the full itinerary for a draft — called by the Itinerary Builder on every edit. */
    suspend fun updateItinerary(inspirationId: String, itinerary: List<ItineraryDay>)

    suspend fun updateOptionalSections(
        inspirationId: String,
        notes: String?,
        checklist: List<ChecklistItem>,
        tags: List<String>
    )

    suspend fun publishInspiration(inspirationId: String)

    fun observeComments(inspirationId: String): Flow<List<com.example.journii_version2.core.model.Comment>>
    suspend fun addComment(inspirationId: String, text: String)
}
