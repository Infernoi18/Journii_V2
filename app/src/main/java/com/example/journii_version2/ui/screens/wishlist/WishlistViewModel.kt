package com.example.journii_version2.ui.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journii_version2.JourniiApplication
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.Wishlist
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WishlistUiState(
    val customWishlists: List<Wishlist> = emptyList(),
    val savedInspirations: List<Inspiration> = emptyList(),
    val isLoading: Boolean = false
)

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val inspirationRepository: InspirationRepository
) : ViewModel() {

    val uiState: StateFlow<WishlistUiState> = combine(
        wishlistRepository.getWishlists(),
        inspirationRepository.observeFeed() // In a real app, we'd have a specific observeSaved()
    ) { wishlists, inspirations ->
        WishlistUiState(
            customWishlists = wishlists,
            savedInspirations = inspirations.filter { it.isSavedByCurrentUser }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WishlistUiState(isLoading = true)
    )

    fun createWishlist(name: String, description: String?, isPublic: Boolean) {
        viewModelScope.launch {
            wishlistRepository.createWishlist(name, description, isPublic)
        }
    }

    fun toggleSaveInspiration(inspiration: Inspiration) {
        viewModelScope.launch {
            inspirationRepository.updateInspiration(
                inspiration.copy(isSavedByCurrentUser = !inspiration.isSavedByCurrentUser)
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JourniiApplication)
                WishlistViewModel(
                    wishlistRepository = application.appContainer.wishlistRepository,
                    inspirationRepository = application.appContainer.inspirationRepository
                )
            }
        }
    }
}
