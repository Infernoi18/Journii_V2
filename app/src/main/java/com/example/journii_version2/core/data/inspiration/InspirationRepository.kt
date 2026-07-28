package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over where Inspirations come from. FakeInspirationRepository
 * backs this today; swap in a real backend-backed implementation later
 * without touching any ViewModel or screen.
 */
interface InspirationRepository {
    fun observeFeed(): Flow<List<Inspiration>>
    suspend fun refreshFeed()
    suspend fun toggleLike(inspirationId: String)
    suspend fun toggleSave(inspirationId: String)
}
