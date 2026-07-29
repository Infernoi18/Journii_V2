package com.example.journii_version2.core.data.wishlist

import com.example.journii_version2.core.model.Wishlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class OfflineWishlistRepository : WishlistRepository {
    private val _wishlists = MutableStateFlow<List<Wishlist>>(emptyList())

    override fun getWishlists(): Flow<List<Wishlist>> = _wishlists

    override fun getWishlist(id: String): Flow<Wishlist?> = 
        _wishlists.map { list -> list.find { it.id == id } }

    override suspend fun createWishlist(name: String, description: String?, isPublic: Boolean): String {
        val newWishlist = Wishlist(
            name = name,
            description = description,
            isPublic = isPublic
        )
        _wishlists.update { it + newWishlist }
        return newWishlist.id
    }

    override suspend fun updateWishlist(wishlist: Wishlist) {
        _wishlists.update { list ->
            list.map { if (it.id == wishlist.id) wishlist.copy(updatedAt = System.currentTimeMillis()) else it }
        }
    }

    override suspend fun deleteWishlist(id: String) {
        _wishlists.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun addToWishlist(wishlistId: String, inspirationId: String) {
        _wishlists.update { list ->
            list.map { wishlist ->
                if (wishlist.id == wishlistId && !wishlist.inspirationIds.contains(inspirationId)) {
                    wishlist.copy(
                        inspirationIds = wishlist.inspirationIds + inspirationId,
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    wishlist
                }
            }
        }
    }

    override suspend fun removeFromWishlist(wishlistId: String, inspirationId: String) {
        _wishlists.update { list ->
            list.map { wishlist ->
                if (wishlist.id == wishlistId) {
                    wishlist.copy(
                        inspirationIds = wishlist.inspirationIds - inspirationId,
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    wishlist
                }
            }
        }
    }

    override suspend fun isSavedToWishlist(wishlistId: String, inspirationId: String): Boolean {
        return _wishlists.value.find { it.id == wishlistId }?.inspirationIds?.contains(inspirationId) == true
    }
}
