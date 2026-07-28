package com.example.journii_version2.feature.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.Inspiration

class CopyInspirationViewModelFactory(
    private val repository: InspirationRepository,
    private val sourceInspiration: Inspiration
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CopyInspirationViewModel(repository, sourceInspiration) as T
    }
}
