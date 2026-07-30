package com.example.journii_version2.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.core.model.Privacy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val displayName: String = "",
    val bio: String = "",
    val privacy: Privacy = Privacy.PUBLIC,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)

class EditProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.observeCurrentUserProfile().first()
            _uiState.value = _uiState.value.copy(
                displayName = profile.displayName,
                bio = profile.bio ?: "",
                privacy = profile.privacy,
                isLoading = false
            )
        }
    }

    fun onDisplayNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(displayName = name)
    }

    fun onBioChanged(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    fun onPrivacyChanged(privacy: Privacy) {
        _uiState.value = _uiState.value.copy(privacy = privacy)
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.displayName.isBlank()) {
            _uiState.value = currentState.copy(error = "Display name cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                profileRepository.updateProfile(
                    displayName = currentState.displayName,
                    bio = currentState.bio,
                    privacy = currentState.privacy
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to update profile. Please try again."
                )
            }
        }
    }
}
