package com.example.journii_version2.core.model

data class Inspiration(
    val id: String,
    val creator: Creator,
    val destination: String,
    val country: String,
    val coverImageUrl: String,
    val imageUrls: List<String> = emptyList(),
    val days: Int,
    val shortDescription: String? = null,
    val tags: List<String> = emptyList(),
    val likeCount: Int = 0,
    val saveCount: Int = 0,
    val commentCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val isSavedByCurrentUser: Boolean = false,
    val privacy: Privacy = Privacy.PUBLIC,
    val copiedFromInspirationId: String? = null,
    val itinerary: List<ItineraryDay> = emptyList(),
    // True while the creator is still building this Inspiration and hasn't
    // published it — shown only in the creator's own Profile > Drafts tab.
    val isDraft: Boolean = false
)

data class Creator(
    val id: String,
    val displayName: String,
    val username: String,
    val avatarUrl: String?
)

enum class Privacy { PUBLIC, FOLLOWERS_ONLY, PRIVATE }
