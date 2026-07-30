package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.CopyMode
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.ItineraryDay
import com.example.journii_version2.core.model.Privacy
import kotlinx.coroutines.flow.Flow

interface InspirationRepository {
    fun observeFeed(): Flow<List<Inspiration>>
    fun observeInspiration(inspirationId: String): Flow<Inspiration?>
    suspend fun refreshFeed()
    suspend fun toggleLike(inspirationId: String)
    suspend fun toggleSave(inspirationId: String)
    suspend fun copyInspiration(sourceId: String, mode: CopyMode): String?
    suspend fun createDraft(
        destination: String,
        country: String,
        days: Int,
        coverImageUrl: String,
        shortDescription: String?,
        privacy: Privacy = Privacy.PUBLIC
    ): String
    suspend fun updateItinerary(inspirationId: String, itinerary: List<ItineraryDay>)
    suspend fun publishInspiration(inspirationId: String)
    suspend fun deleteInspiration(inspirationId: String)
    fun observeComments(inspirationId: String): Flow<List<com.example.journii_version2.core.model.Comment>>
    suspend fun addComment(inspirationId: String, text: String)
}
