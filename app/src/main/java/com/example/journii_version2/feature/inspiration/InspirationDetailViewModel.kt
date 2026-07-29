package com.example.journii_version2.feature.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.core.model.Comment
import com.example.journii_version2.core.model.Inspiration
import com.example.journii_version2.core.model.Wishlist
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InspirationDetailUiState(
    val inspiration: Inspiration? = null,
    val isLoading: Boolean = true,
    val isOwner: Boolean = false,
    val wishlists: List<Wishlist> = emptyList(),
    val comments: List<Comment> = emptyList()
)

class InspirationDetailViewModel(
    private val repository: InspirationRepository,
    private val wishlistRepository: WishlistRepository,
    private val profileRepository: ProfileRepository,
    private val inspirationId: String
) : ViewModel() {

    val uiState: StateFlow<InspirationDetailUiState> = combine(
        repository.observeInspiration(inspirationId),
        repository.observeComments(inspirationId),
        wishlistRepository.getWishlists(),
        profileRepository.observeCurrentUserProfile()
    ) { found, comments, wishlists, profile ->
        InspirationDetailUiState(
            inspiration = found,
            isLoading = false,
            isOwner = found?.creator?.id == profile.id,
            wishlists = wishlists,
            comments = comments
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

    fun deleteInspiration(onDeleted: () -> Unit) {
        val id = uiState.value.inspiration?.id ?: return
        viewModelScope.launch {
            repository.deleteInspiration(id)
            onDeleted()
        }
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(inspirationId, text)
        }
    }
}
