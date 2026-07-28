package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.Creator
import com.example.journii_version2.core.model.Inspiration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeInspirationRepository : InspirationRepository {

    private val _feed = MutableStateFlow(seedInspirations())

    override fun observeFeed() = _feed.asStateFlow()

    override suspend fun refreshFeed() {
        delay(500) // stand-in for a real network refresh
        _feed.value = seedInspirations()
    }

    override suspend fun toggleLike(inspirationId: String) {
        _feed.value = _feed.value.map { inspiration ->
            if (inspiration.id == inspirationId) {
                val liked = !inspiration.isLikedByCurrentUser
                inspiration.copy(
                    isLikedByCurrentUser = liked,
                    likeCount = inspiration.likeCount + if (liked) 1 else -1
                )
            } else {
                inspiration
            }
        }
    }

    override suspend fun toggleSave(inspirationId: String) {
        _feed.value = _feed.value.map { inspiration ->
            if (inspiration.id == inspirationId) {
                val saved = !inspiration.isSavedByCurrentUser
                inspiration.copy(
                    isSavedByCurrentUser = saved,
                    saveCount = inspiration.saveCount + if (saved) 1 else -1
                )
            } else {
                inspiration
            }
        }
    }

    private fun seedInspirations(): List<Inspiration> = listOf(
        Inspiration(
            id = "insp_1",
            creator = Creator("u1", "Maya Chen", "mayawanders", null),
            destination = "Kyoto",
            country = "Japan",
            coverImageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e",
            days = 5,
            shortDescription = "Temples, tea houses, and a whole lot of matcha.",
            tags = listOf("Solo", "Culture", "Food"),
            likeCount = 482,
            saveCount = 210,
            commentCount = 34
        ),
        Inspiration(
            id = "insp_2",
            creator = Creator("u2", "Arjun Mehta", "arjuntravels", null),
            destination = "Reykjavik",
            country = "Iceland",
            coverImageUrl = "https://images.unsplash.com/photo-1504829857797-ddff29c27927",
            days = 7,
            shortDescription = "Chasing the Northern Lights on a shoestring budget.",
            tags = listOf("Adventure", "Budget", "Nature"),
            likeCount = 917,
            saveCount = 540,
            commentCount = 88
        ),
        Inspiration(
            id = "insp_3",
            creator = Creator("u3", "Priya Nair", "priyapacks", null),
            destination = "Lisbon",
            country = "Portugal",
            coverImageUrl = "https://images.unsplash.com/photo-1585208798174-6cedd86e019a",
            days = 4,
            shortDescription = "A long weekend of pastel streets and pastéis de nata.",
            tags = listOf("Family", "Beach", "Food"),
            likeCount = 356,
            saveCount = 190,
            commentCount = 21
        ),
        Inspiration(
            id = "insp_4",
            creator = Creator("u4", "Diego Ramirez", "diegoexplores", null),
            destination = "Patagonia",
            country = "Chile",
            coverImageUrl = "https://images.unsplash.com/photo-1531065208531-4036c0dba3ca",
            days = 10,
            shortDescription = "Trekking Torres del Paine end to end.",
            tags = listOf("Adventure", "Backpacking"),
            likeCount = 703,
            saveCount = 402,
            commentCount = 59
        ),
        Inspiration(
            id = "insp_5",
            creator = Creator("u5", "Sara Kim", "sararoams", null),
            destination = "Bali",
            country = "Indonesia",
            coverImageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4",
            days = 6,
            shortDescription = "Rice terraces, surf breaks, and slow mornings.",
            tags = listOf("Luxury", "Beach", "Nightlife"),
            likeCount = 1204,
            saveCount = 810,
            commentCount = 143
        ),
        Inspiration(
            id = "insp_6",
            creator = Creator("u6", "Tom Okafor", "tomwandering", null),
            destination = "Marrakech",
            country = "Morocco",
            coverImageUrl = "https://images.unsplash.com/photo-1553603227-2358aabe821e",
            days = 5,
            shortDescription = "Souks, riads, and a sunset camel ride.",
            tags = listOf("Culture", "Adventure"),
            likeCount = 289,
            saveCount = 133,
            commentCount = 18
        )
    )
}
