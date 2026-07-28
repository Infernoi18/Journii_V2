package com.example.journii_version2.core.data.profile

import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeCurrentUserProfile(): Flow<UserProfile>
    fun observePinnedInspirations(): Flow<List<Inspiration>>
    fun observeCopiedInspirations(): Flow<List<Inspiration>>
    fun observeDraftInspirations(): Flow<List<Inspiration>>
}
