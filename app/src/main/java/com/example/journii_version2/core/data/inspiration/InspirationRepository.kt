package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.CopyMode
import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.flow.Flow

interface InspirationRepository {
    fun observeFeed(): Flow<List<Inspiration>>
    fun observeInspiration(inspirationId: String): Flow<Inspiration?>
    suspend fun refreshFeed()
    suspend fun toggleLike(inspirationId: String)
    suspend fun toggleSave(inspirationId: String)

    /** Returns the new Inspiration's id, or null if the source no longer exists. */
    suspend fun copyInspiration(sourceId: String, mode: CopyMode): String?
}
