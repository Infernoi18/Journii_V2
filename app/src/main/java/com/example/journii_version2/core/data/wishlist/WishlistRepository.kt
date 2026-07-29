package com.example.journii_version2.core.data.wishlist

import com.example.journii_version2.core.model.Wishlist
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlists(): Flow<List<Wishlist>>
    fun getWishlist(id: String): Flow<Wishlist?>
    suspend fun createWishlist(name: String, description: String?, isPublic: Boolean): String
    suspend fun updateWishlist(wishlist: Wishlist)
    suspend fun deleteWishlist(id: String)
    suspend fun addToWishlist(wishlistId: String, inspirationId: String)
    suspend fun removeFromWishlist(wishlistId: String, inspirationId: String)
    suspend fun isSavedToWishlist(wishlistId: String, inspirationId: String): Boolean
}
