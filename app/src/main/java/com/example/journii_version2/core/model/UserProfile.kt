package com.example.journii_version2.core.model

data class UserProfile(
    val id: String,
    val displayName: String,
    val username: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val coverImageUrl: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val visitedCountriesCount: Int = 0,
    val totalLikes: Int = 0,
    val totalSaves: Int = 0,
    val privacy: Privacy = Privacy.PUBLIC,
    // Fake-data plumbing only: which Inspirations the user has pinned to
    // their profile. A real backend would own this relationship server-side,
    // likely as its own join table rather than a field on the profile.
    val pinnedInspirationIds: List<String> = emptyList()
)
