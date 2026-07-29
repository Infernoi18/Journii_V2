package com.example.journii_version2.ui.screens.wishlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journii_version2.JourniiApplication
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.Wishlist
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WishlistDetailUiState(
    val wishlist: Wishlist? = null,
    val inspirations: List<Inspiration> = emptyList(),
    val isLoading: Boolean = false,
    val isDefaultWishlist: Boolean = false
)

class WishlistDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val wishlistRepository: WishlistRepository,
    private val inspirationRepository: InspirationRepository
) : ViewModel() {

    private val wishlistId: String = checkNotNull(savedStateHandle["wishlistId"])

    val uiState: StateFlow<WishlistDetailUiState> = if (wishlistId == "default") {
        inspirationRepository.observeFeed().map { inspirations ->
            WishlistDetailUiState(
                wishlist = Wishlist(id = "default", name = "All Saved"),
                inspirations = inspirations.filter { it.isSavedByCurrentUser },
                isDefaultWishlist = true
            )
        }
    } else {
        combine(
            wishlistRepository.getWishlist(wishlistId),
            inspirationRepository.observeFeed()
        ) { wishlist, inspirations ->
            WishlistDetailUiState(
                wishlist = wishlist,
                inspirations = inspirations.filter { wishlist?.inspirationIds?.contains(it.id) == true }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WishlistDetailUiState(isLoading = true)
    )

    fun removeFromWishlist(inspirationId: String) {
        viewModelScope.launch {
            if (wishlistId == "default") {
                inspirationRepository.toggleSave(inspirationId)
            } else {
                wishlistRepository.removeFromWishlist(wishlistId, inspirationId)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JourniiApplication)
                val savedStateHandle = this.createSavedStateHandle()
                WishlistDetailViewModel(
                    savedStateHandle = savedStateHandle,
                    wishlistRepository = application.appContainer.wishlistRepository,
                    inspirationRepository = application.appContainer.inspirationRepository
                )
            }
        }
    }
}
