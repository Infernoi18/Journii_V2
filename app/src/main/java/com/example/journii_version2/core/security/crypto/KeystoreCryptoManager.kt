package com.example.journii_version2.core.security.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * AES/GCM encryption backed by a key that lives only inside the Android
 * Keystore (hardware-backed on most devices). The key material never leaves
 * the keystore — even a rooted device can't extract it, only invoke it
 * through the OS-mediated Cipher API.
 */
class KeystoreCryptoManager(
    private val keyAlias: String = "journii_session_key"
) {
    private val androidKeyStore = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"
    private val gcmTagLength = 128

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
        (keyStore.getKey(keyAlias, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            androidKeyStore
        )
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // session token, not biometric-gated
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /** Returns "base64(iv):base64(ciphertext)" — both halves are needed to decrypt. */
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val payload = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        return "$iv:$payload"
    }

    fun decrypt(encoded: String): String {
        val (ivPart, payloadPart) = encoded.split(":", limit = 2)
        val iv = Base64.decode(ivPart, Base64.NO_WRAP)
        val cipherBytes = Base64.decode(payloadPart, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(gcmTagLength, iv))
        return String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    }
}
