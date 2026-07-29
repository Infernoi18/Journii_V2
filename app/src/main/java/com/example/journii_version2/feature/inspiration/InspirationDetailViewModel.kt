package com.example.journii_version2.feature.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.Wishlist
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InspirationDetailUiState(
    val inspiration: Inspiration? = null,
    val isLoading: Boolean = true,
    val wishlists: List<Wishlist> = emptyList()
)

class InspirationDetailViewModel(
    private val repository: InspirationRepository,
    private val wishlistRepository: WishlistRepository,
    inspirationId: String
) : ViewModel() {

    val uiState: StateFlow<InspirationDetailUiState> = combine(
        repository.observeInspiration(inspirationId),
        wishlistRepository.getWishlists()
    ) { found, wishlists ->
        InspirationDetailUiState(
            inspiration = found,
            isLoading = false,
            wishlists = wishlists
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InspirationDetailUiState(isLoading = true)
    )

    fun toggleLike() {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch { repository.toggleLike(id) }
    }

    fun toggleSave() {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch { repository.toggleSave(id) }
    }

    fun addToWishlist(wishlistId: String) {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch {
            wishlistRepository.addToWishlist(wishlistId, id)
        }
    }

    fun createAndAddToWishlist(name: String) {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch {
            val wishlistId = wishlistRepository.createWishlist(name, null, false)
            wishlistRepository.addToWishlist(wishlistId, id)
        }
    }
}
