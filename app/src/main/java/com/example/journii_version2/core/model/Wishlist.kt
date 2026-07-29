package com.example.journii_version2.core.model

import java.util.UUID

data class Wishlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val inspirationIds: List<String> = emptyList(),
    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
