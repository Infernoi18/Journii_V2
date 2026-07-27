package com.example.journii_version2.core.security.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.journii_version2.core.security.crypto.KeystoreCryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "journii_session")

/**
 * Persists the session token encrypted at rest via Keystore-backed AES/GCM.
 * DataStore only ever sees ciphertext, never the raw token.
 */
class SecureTokenStore(
    private val context: Context,
    private val crypto: KeystoreCryptoManager = KeystoreCryptoManager()
) {
    private val tokenKey = stringPreferencesKey("encrypted_session_token")

    val isLoggedIn: Flow<Boolean> =
        context.sessionDataStore.data.map { prefs -> prefs[tokenKey] != null }

    suspend fun saveSessionToken(token: String) {
        val encrypted = crypto.encrypt(token)
        context.sessionDataStore.edit { prefs -> prefs[tokenKey] = encrypted }
    }

    suspend fun getSessionToken(): String? {
        val encrypted = context.sessionDataStore.data.map { it[tokenKey] }.first() ?: return null
        return crypto.decrypt(encrypted)
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs -> prefs.remove(tokenKey) }
    }
}
