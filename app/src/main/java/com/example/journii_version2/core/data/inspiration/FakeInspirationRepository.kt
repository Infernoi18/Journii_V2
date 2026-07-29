package com.example.journii_version2.core.data.inspiration

import com.example.journii_version2.core.model.BlockCategory
import com.example.journii_version2.core.model.ChecklistItem
import com.example.journii_version2.core.model.CopyMode
import com.example.journii_version2.core.model.CopySection
import com.example.journii_version2.core.model.Creator
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.ItineraryBlock
import com.example.journii_version2.core.model.ItineraryDay
import com.example.journii_version2.core.model.TransportMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakeInspirationRepository : InspirationRepository {

    private val _feed = MutableStateFlow(seedInspirations())

    override fun observeFeed() = _feed.asStateFlow()

    override fun observeInspiration(inspirationId: String) =
        _feed.map { list -> list.firstOrNull { it.id == inspirationId } }

    override suspend fun refreshFeed() {
        delay(500)
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

    override suspend fun updateInspiration(inspiration: Inspiration) {
        _feed.value = _feed.value.map { if (it.id == inspiration.id) inspiration else it }
    }

    override suspend fun copyInspiration(sourceId: String, mode: CopyMode): String? {
        val source = _feed.value.firstOrNull { it.id == sourceId } ?: return null
        val newId = "insp_copy_${UUID.randomUUID()}"

        val copy = when (mode) {
            is CopyMode.Entire, CopyMode.ImportAndEdit -> source.copy(
                id = newId,
                creator = currentUserAsCreator(),
                copiedFromInspirationId = source.id,
                isDraft = true,
                likeCount = 0,
                saveCount = 0,
                commentCount = 0,
                isLikedByCurrentUser = false,
                isSavedByCurrentUser = false
            )
            is CopyMode.Sections -> Inspiration(
                id = newId,
                creator = currentUserAsCreator(),
                destination = source.destination,
                country = source.country,
                coverImageUrl = source.coverImageUrl,
                imageUrls = source.imageUrls,
                days = source.days,
                copiedFromInspirationId = source.id,
                isDraft = true,
                itinerary = if (CopySection.ITINERARY in mode.sections) source.itinerary else emptyList(),
                notes = if (CopySection.NOTES in mode.sections) source.notes else null,
                checklist = if (CopySection.CHECKLIST in mode.sections) source.checklist else emptyList(),
                tags = if (CopySection.TAGS in mode.sections) source.tags else emptyList()
            )
        }

        _feed.value = _feed.value + copy
        return newId
    }

    override suspend fun createDraft(
        destination: String,
        country: String,
        days: Int,
        coverImageUrl: String,
        shortDescription: String?
    ): String {
        val newId = "insp_draft_${UUID.randomUUID()}"
        val draft = Inspiration(
            id = newId,
            creator = currentUserAsCreator(),
            destination = destination,
            country = country,
            coverImageUrl = coverImageUrl,
            days = days,
            shortDescription = shortDescription,
            isDraft = true
        )
        _feed.value = _feed.value + draft
        return newId
    }

    override suspend fun deleteInspiration(inspirationId: String) {
        _feed.value = _feed.value.filterNot { it.id == inspirationId }
    }

    // Fake-data stand-in for "the signed-in user" — replace with real
    // profile/session data once auth is backed by a real API.
    private fun currentUserAsCreator() = Creator(
        id = "current_user",
        displayName = "You",
        username = "you.travels",
        avatarUrl = null
    )

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
            commentCount = 34,
            itinerary = kyotoItinerary(),
            notes = "Bring cash — many small shops in Kyoto don't take cards.",
            checklist = listOf(
                ChecklistItem("c1", "Get a Japan Rail Pass"),
                ChecklistItem("c2", "Book tea ceremony in advance"),
                ChecklistItem("c3", "Pack comfortable walking shoes", isChecked = true)
            )
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
            commentCount = 88,
            itinerary = icelandItinerary()
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
            commentCount = 21,
            itinerary = List(4) { ItineraryDay(dayNumber = it + 1) },
            isDraft = true
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
            commentCount = 59,
            itinerary = List(10) { ItineraryDay(dayNumber = it + 1) }
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
            commentCount = 143,
            itinerary = List(6) { ItineraryDay(dayNumber = it + 1) }
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
            commentCount = 18,
            itinerary = List(5) { ItineraryDay(dayNumber = it + 1) },
            copiedFromInspirationId = "insp_2"
        )
    )

    private fun kyotoItinerary(): List<ItineraryDay> {
        val day1 = ItineraryDay(
            dayNumber = 1,
            blocks = listOf(
                ItineraryBlock("k1", "Arrive at Kansai Airport", BlockCategory.TRANSIT, "9:00 AM", transportToNext = TransportMode.TRAIN),
                ItineraryBlock("k2", "Hotel check-in", BlockCategory.ACCOMMODATION, "12:00 PM", transportToNext = TransportMode.WALKING),
                ItineraryBlock("k3", "Walk through Gion district", BlockCategory.SIGHTSEEING, "3:00 PM", transportToNext = TransportMode.WALKING),
                ItineraryBlock("k4", "Dinner at Pontocho Alley", BlockCategory.FOOD, "7:00 PM")
            )
        )
        val day2 = ItineraryDay(
            dayNumber = 2,
            blocks = listOf(
                ItineraryBlock("k5", "Fushimi Inari Shrine", BlockCategory.SIGHTSEEING, "8:00 AM", transportToNext = TransportMode.TRAIN),
                ItineraryBlock("k6", "Arashiyama Bamboo Grove", BlockCategory.SIGHTSEEING, "11:00 AM", transportToNext = TransportMode.WALKING),
                ItineraryBlock("k7", "Traditional tea ceremony", BlockCategory.ACTIVITY, "2:00 PM", transportToNext = TransportMode.WALKING),
                ItineraryBlock("k8", "Ramen dinner", BlockCategory.FOOD, "7:30 PM")
            )
        )
        return listOf(day1, day2) + List(3) { ItineraryDay(dayNumber = it + 3) }
    }

    private fun icelandItinerary(): List<ItineraryDay> {
        val day1 = ItineraryDay(
            dayNumber = 1,
            blocks = listOf(
                ItineraryBlock("i1", "Arrive at Keflavik Airport", BlockCategory.TRANSIT, "7:00 AM", transportToNext = TransportMode.RENTAL_CAR),
                ItineraryBlock("i2", "Blue Lagoon", BlockCategory.ACTIVITY, "10:00 AM", transportToNext = TransportMode.RENTAL_CAR),
                ItineraryBlock("i3", "Reykjavik hotel check-in", BlockCategory.ACCOMMODATION, "3:00 PM")
            )
        )
        val day2 = ItineraryDay(
            dayNumber = 2,
            blocks = listOf(
                ItineraryBlock("i4", "Þingvellir National Park", BlockCategory.SIGHTSEEING, "9:00 AM", transportToNext = TransportMode.RENTAL_CAR),
                ItineraryBlock("i5", "Geysir hot springs", BlockCategory.SIGHTSEEING, "12:00 PM", transportToNext = TransportMode.RENTAL_CAR),
                ItineraryBlock("i6", "Gullfoss waterfall", BlockCategory.SIGHTSEEING, "2:00 PM", transportToNext = TransportMode.RENTAL_CAR),
                ItineraryBlock("i7", "Northern Lights watch", BlockCategory.ACTIVITY, "9:00 PM")
            )
        )
        return listOf(day1, day2) + List(5) { ItineraryDay(dayNumber = it + 3) }
    }
}
