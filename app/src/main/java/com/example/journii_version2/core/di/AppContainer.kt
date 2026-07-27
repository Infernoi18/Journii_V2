package com.example.journii_version2.core.di

import android.content.Context
import com.example.journii_version2.core.security.session.SecureTokenStore

/**
 * Minimal manual dependency container. Deliberately simple for now —
 * swap for Hilt once the network/data layer grows enough to justify it.
 */
class AppContainer(context: Context) {
    val secureTokenStore: SecureTokenStore = SecureTokenStore(context.applicationContext)
}