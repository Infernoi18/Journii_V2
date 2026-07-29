package com.example.journii_version2.core.session

/**
 * Shared identity for "the signed-in user" in the fake-data layer. Centralizing
 * this avoids the id/name/username getting typed inconsistently across
 * FakeInspirationRepository, FakeProfileRepository, and any screen that needs
 * to check "is this mine" — replace with real profile/session data once auth
 * is backed by a real API.
 */
object CurrentUser {
    const val ID = "current_user"
    const val DISPLAY_NAME = "You"
    const val USERNAME = "you.travels"
}