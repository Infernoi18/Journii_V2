package com.example.journii_version2.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.journii_version2.core.data.inspiration.InspirationRepository

class OptionalSectionsViewModelFactory(
    private val repository: InspirationRepository,
    private val inspirationId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OptionalSectionsViewModel(repository, inspirationId) as T
    }
}
