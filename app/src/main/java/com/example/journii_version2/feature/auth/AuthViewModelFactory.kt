package com.example.journii_version2.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.journii_version2.core.security.session.SecureTokenStore

class AuthViewModelFactory(
    private val secureTokenStore: SecureTokenStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AuthViewModel(secureTokenStore) as T
    }
}
