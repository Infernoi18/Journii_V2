package com.example.journii_version2.core.data.profile

import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.Privacy
import com.example.journii_version2.core.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Depends on InspirationRepository rather than keeping its own copy of
 * Inspiration data — Pinned/Copied/Draft are filtered views over the same
 * single source of truth, not a separate store to keep in sync.
 */
class FakeProfileRepository(
    private val inspirationRepository: InspirationRepository
) : ProfileRepository {

    private val _profile = MutableStateFlow(seedProfile())

    override fun observeCurrentUserProfile(): Flow<UserProfile> = _profile.asStateFlow()

    override fun observePinnedInspirations(): Flow<List<Inspiration>> =
        combine(inspirationRepository.observeFeed(), _profile) { all, profile ->
            all.filter { it.id in profile.pinnedInspirationIds }
        }

    override fun observeCopiedInspirations(): Flow<List<Inspiration>> =
        inspirationRepository.observeFeed().map { all -> all.filter { it.copiedFromInspirationId != null } }

    override fun observeDraftInspirations(): Flow<List<Inspiration>> =
        inspirationRepository.observeFeed().map { all -> all.filter { it.isDraft } }

    private fun seedProfile(): UserProfile = UserProfile(
        id = "current_user",
        displayName = "You",
        username = "you.travels",
        bio = "Collecting sunsets and stamps, one trip at a time.",
        avatarUrl = null,
        coverImageUrl = "https://images.unsplash.com/photo-1502920917128-1aa500764cbd",
        followersCount = 128,
        followingCount = 96,
        visitedCountriesCount = 12,
        totalLikes = 640,
        totalSaves = 310,
        privacy = Privacy.PUBLIC,
        pinnedInspirationIds = listOf("insp_1", "insp_5")
    )
}
