package com.example.journii_version2.core.model

/**
 * The core content unit of Journii — an entire travel journey, not just a
 * photo post. This models what the Discovery Feed needs to render a card.
 * Deeper fields (itinerary, budget, transportation, hotels...) get their own
 * models when the Inspiration Detail screen and Create flow are built.
 */
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
    // Set when this Inspiration was created via "Copy Inspiration" — every
    // copy remembers its original creator, per the spec.
    val copiedFromInspirationId: String? = null
)

data class Creator(
    val id: String,
    val displayName: String,
    val username: String,
    val avatarUrl: String?
)

enum class Privacy { PUBLIC, FOLLOWERS_ONLY, PRIVATE }
