package com.example.journii_version2.core.model

data class ChecklistItem(
    val id: String,
    val label: String,
    val isChecked: Boolean = false
)
