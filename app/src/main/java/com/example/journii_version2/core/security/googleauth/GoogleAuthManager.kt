package com.example.journii_version2.core.security.googleauth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.journii_version2.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.MessageDigest
import java.util.UUID

/**
 * Wraps Credential Manager's "Sign in with Google" flow.
 *
 * The ID token this returns is only proof of a Google identity — it must be
 * sent to our backend and verified there (against Google's public keys)
 * before we trust it and issue a Journii session. We never trust it client-side alone.
 */
class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): String {
        val option = GetSignInWithGoogleOption.Builder(
            serverClientId = context.getString(R.string.google_web_client_id)
        )
            .setNonce(generateSecureNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val result = credentialManager.getCredential(context = context, request = request)
        val credential = result.credential

        check(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "Unexpected credential type returned" }

        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    /** Call on sign-out so Credential Manager forgets the last-used account for auto sign-in. */
    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    private fun generateSecureNonce(): String {
        val raw = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
