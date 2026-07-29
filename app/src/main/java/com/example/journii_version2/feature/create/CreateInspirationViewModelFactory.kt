package com.example.journii_version2.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.journii_version2.core.data.inspiration.InspirationRepository

class CreateInspirationViewModelFactory(
    private val repository: InspirationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return CreateInspirationViewModel(repository) as T
    }
}
