package com.example.journii_version2.core.model

/**
 * What "Select Specific Sections" can copy. Only lists sections that have a
 * real data model today. HOTELS, BUDGET, TRANSPORTATION, FLIGHTS,
 * RESTAURANTS, and ACTIVITIES join this list once each has its own model —
 * the copy mechanism itself won't need to change.
 */
enum class CopySection {
    ITINERARY, NOTES, CHECKLIST, TAGS
}
