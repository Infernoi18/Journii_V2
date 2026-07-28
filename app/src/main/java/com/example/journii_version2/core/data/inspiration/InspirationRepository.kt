package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.flow.Flow

interface InspirationRepository {
    fun observeFeed(): Flow<List<Inspiration>>
    fun observeInspiration(inspirationId: String): Flow<Inspiration?>
    suspend fun refreshFeed()
    suspend fun toggleLike(inspirationId: String)
    suspend fun toggleSave(inspirationId: String)
    suspend fun copyInspiration(inspirationId: String): String
    fun searchInspirations(query: String, tags: List<String>): Flow<List<Inspiration>>
}
