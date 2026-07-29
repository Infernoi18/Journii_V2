package com.example.journii_version2.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.security.session.SecureTokenStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthFormState(
    val isSignUp: Boolean = false,
    // Profile info for Signup
    val fullName: String = "",
    val username: String = "",
    val fullNameError: String? = null,
    val usernameError: String? = null,

    // Email sign-in/up
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,

    // Mobile + OTP sign-in
    val mobileNumber: String = "",
    val mobileError: String? = null,
    val otp: String = "",
    val otpError: String? = null,
    val isOtpSent: Boolean = false,
    val resendCooldownSeconds: Int = 0,

    val isSubmitting: Boolean = false,
    val isAuthenticated: Boolean = false,
    val generalError: String? = null
)

class AuthViewModel(
    private val secureTokenStore: SecureTokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthFormState())
    val uiState: StateFlow<AuthFormState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null

    // ---- General ----

    fun setSignUpMode(isSignUp: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSignUp = isSignUp,
            emailError = null,
            passwordError = null,
            fullNameError = null,
            usernameError = null,
            generalError = null
        )
    }

    fun toggleAuthMode() {
        setSignUpMode(!_uiState.value.isSignUp)
    }

    fun onFullNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, fullNameError = null, generalError = null)
    }

    fun onUsernameChanged(value: String) {
        _uiState.value = _uiState.value.copy(username = value, usernameError = null, generalError = null)
    }

    // ---- Email ----

    fun onEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null, generalError = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null, generalError = null)
    }

    fun submitEmailAuth() {
        val current = _uiState.value
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        
        var fullNameError: String? = null
        var usernameError: String? = null
        
        if (current.isSignUp) {
            fullNameError = validateFullName(current.fullName)
            usernameError = validateUsername(current.username)
        }

        if (emailError != null || passwordError != null || fullNameError != null || usernameError != null) {
            _uiState.value = current.copy(
                emailError = emailError, 
                passwordError = passwordError,
                fullNameError = fullNameError,
                usernameError = usernameError
            )
            return
        }

        _uiState.value = current.copy(isSubmitting = true, generalError = null)

        viewModelScope.launch {
            try {
                // Mocking registration/login
                delay(1000)
                completeAuthentication(mockToken())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onAuthFailure()
            }
        }
    }

    // ---- Mobile + OTP ----

    fun onMobileNumberChanged(value: String) {
        _uiState.value = _uiState.value.copy(mobileNumber = value, mobileError = null, generalError = null)
    }

    fun sendOtp() {
        val current = _uiState.value
        val mobileError = validateMobileNumber(current.mobileNumber)
        if (mobileError != null) {
            _uiState.value = current.copy(mobileError = mobileError)
            return
        }

        _uiState.value = current.copy(isSubmitting = true, generalError = null)

        viewModelScope.launch {
            try {
                delay(600) // stand-in for the real "send OTP" network call
                _uiState.value = _uiState.value.copy(isSubmitting = false, isOtpSent = true)
                startResendCooldown()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onAuthFailure()
            }
        }
    }

    fun onOtpChanged(value: String) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(otp = value, otpError = null, generalError = null)
        }
    }

    fun verifyOtp() {
        val current = _uiState.value
        if (current.otp.length != 6) {
            _uiState.value = current.copy(otpError = "Enter the full 6-digit code")
            return
        }

        _uiState.value = current.copy(isSubmitting = true, generalError = null)

        viewModelScope.launch {
            try {
                // No backend yet — any well-formed 6-digit code is accepted here.
                // A real implementation must verify this server-side and rate-limit
                // attempts there; client-side checks are UX only, never security.
                delay(400)
                completeAuthentication(mockToken())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onAuthFailure()
            }
        }
    }

    fun resendOtp() {
        if (_uiState.value.resendCooldownSeconds > 0) return
        sendOtp()
    }

    private fun startResendCooldown(seconds: Int = 30) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in seconds downTo 0) {
                _uiState.value = _uiState.value.copy(resendCooldownSeconds = remaining)
                if (remaining > 0) delay(1000)
            }
        }
    }

    // ---- Google ----

    fun completeGoogleSignIn(idToken: String) {
        _uiState.value = _uiState.value.copy(isSubmitting = true, generalError = null)
        viewModelScope.launch {
            try {
                // idToken proves a Google identity but must still be sent to our
                // backend and verified there before we trust it. Until that backend
                // exists, we mock the resulting session the same way the other flows do.
                require(idToken.isNotBlank())
                completeAuthentication(mockToken())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onAuthFailure()
            }
        }
    }

    fun onGoogleSignInError(message: String) {
        _uiState.value = _uiState.value.copy(generalError = message)
    }

    // ---- Shared ----

    private suspend fun completeAuthentication(sessionToken: String) {
        secureTokenStore.saveSessionToken(sessionToken)
        _uiState.value = _uiState.value.copy(isSubmitting = false, isAuthenticated = true)
    }

    private fun onAuthFailure() {
        _uiState.value = _uiState.value.copy(
            isSubmitting = false,
            generalError = "Something went wrong. Please try again."
        )
    }

    private fun mockToken(): String = UUID.randomUUID().toString()

    private fun validateFullName(name: String): String? = when {
        name.isBlank() -> "Name is required"
        name.length < 2 -> "Name is too short"
        else -> null
    }

    private fun validateUsername(username: String): String? = when {
        username.isBlank() -> "Username is required"
        username.length < 3 -> "Username must be at least 3 characters"
        !USERNAME_REGEX.matches(username) -> "Username can only contain letters, numbers, underscores and dots"
        else -> null
    }

    private fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "Email is required"
            !EMAIL_REGEX.matches(trimmed) -> "Enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Password is required"
        password.length < 8 -> "Use at least 8 characters"
        password.none { it.isDigit() } -> "Include at least one number"
        else -> null
    }

    private fun validateMobileNumber(number: String): String? {
        val trimmed = number.trim()
        return when {
            trimmed.isEmpty() -> "Mobile number is required"
            !MOBILE_REGEX.matches(trimmed) -> "Enter a valid number in international format, e.g. +919876543210"
            else -> null
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val MOBILE_REGEX = Regex("^\\+[1-9]\\d{7,14}$")
        private val USERNAME_REGEX = Regex("^[a-zA-Z0-9._]+$")
    }
}
