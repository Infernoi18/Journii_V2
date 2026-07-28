package com.example.journii_version2.core.model

/**
 * A single freeform stop within a day — Notion-style, no rigid template.
 * transportToNext describes how the traveler gets from this block to the
 * next one in the same day (null for the day's last block, or when the
 * creator didn't specify transportation).
 */
data class ItineraryBlock(
    val id: String,
    val title: String,
    val category: BlockCategory = BlockCategory.OTHER,
    val time: String? = null,
    val notes: String? = null,
    val transportToNext: TransportMode? = null
)

enum class BlockCategory {
    TRANSIT, FOOD, SIGHTSEEING, ACCOMMODATION, ACTIVITY, BEACH, OTHER
}

enum class TransportMode {
    WALKING, METRO, TAXI, RENTAL_CAR, TRAIN, BUS, FLIGHT, FERRY, BIKE
}
