package com.example.journii_version2.core.model

data class ItineraryDay(
    val dayNumber: Int,
    val blocks: List<ItineraryBlock> = emptyList()
)
