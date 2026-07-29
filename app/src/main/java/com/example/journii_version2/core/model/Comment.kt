package com.example.journii_version2.core.model

import java.time.Instant

data class Comment(
    val id: String,
    val inspirationId: String,
    val creator: Creator,
    val text: String,
    val createdAt: Instant = Instant.now()
)
