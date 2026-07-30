package com.example.journii_version2.core.data.profile

import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.Privacy
import com.example.journii_version2.core.model.UserProfile
import com.example.journii_version2.core.session.CurrentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

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

    override suspend fun updateProfile(displayName: String, bio: String, privacy: Privacy) {
        _profile.value = _profile.value.copy(
            displayName = displayName,
            bio = bio,
            privacy = privacy
        )
    }

    private fun seedProfile(): UserProfile = UserProfile(
        id = CurrentUser.ID,
        displayName = CurrentUser.DISPLAY_NAME,
        username = CurrentUser.USERNAME,
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