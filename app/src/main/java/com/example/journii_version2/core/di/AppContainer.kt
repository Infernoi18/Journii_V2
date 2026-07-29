package com.example.journii_version2.core.di

import android.content.Context
import com.example.journii_version2.core.data.inspiration.FakeInspirationRepository
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.profile.FakeProfileRepository
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.core.data.wishlist.OfflineWishlistRepository
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.core.security.session.SecureTokenStore

class AppContainer(context: Context) {
    val secureTokenStore: SecureTokenStore = SecureTokenStore(context.applicationContext)
    val inspirationRepository: InspirationRepository = FakeInspirationRepository()
    val profileRepository: ProfileRepository = FakeProfileRepository(inspirationRepository)
    val wishlistRepository: WishlistRepository = OfflineWishlistRepository()
}
