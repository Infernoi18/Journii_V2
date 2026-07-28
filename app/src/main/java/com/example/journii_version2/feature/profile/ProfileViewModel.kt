package com.example.journii_version2.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class ProfileTab { PINNED, COPIED, DRAFTS }

data class ProfileUiState(
    val profile: UserProfile? = null,
    val selectedTab: ProfileTab = ProfileTab.PINNED,
    val pinnedInspirations: List<Inspiration> = emptyList(),
    val copiedInspirations: List<Inspiration> = emptyList(),
    val draftInspirations: List<Inspiration> = emptyList(),
    val isLoading: Boolean = true
) {
    val visibleInspirations: List<Inspiration>
        get() = when (selectedTab) {
            ProfileTab.PINNED -> pinnedInspirations
            ProfileTab.COPIED -> copiedInspirations
            ProfileTab.DRAFTS -> draftInspirations
        }
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val selectedTab = MutableStateFlow(ProfileTab.PINNED)

    val uiState: StateFlow<ProfileUiState> = combine(
        profileRepository.observeCurrentUserProfile(),
        profileRepository.observePinnedInspirations(),
        profileRepository.observeCopiedInspirations(),
        profileRepository.observeDraftInspirations(),
        selectedTab
    ) { profile, pinned, copied, drafts, tab ->
        ProfileUiState(
            profile = profile,
            selectedTab = tab,
            pinnedInspirations = pinned,
            copiedInspirations = copied,
            draftInspirations = drafts,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun selectTab(tab: ProfileTab) {
        selectedTab.value = tab
    }
}
