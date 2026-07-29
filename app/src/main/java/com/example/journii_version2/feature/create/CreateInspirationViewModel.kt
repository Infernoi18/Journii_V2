package com.example.journii_version2.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateBasicInfoState(
    val destination: String = "",
    val country: String = "",
    val days: String = "", // kept as text while editing; parsed on submit
    val coverImageUrl: String = "",
    val shortDescription: String = "",
    val destinationError: String? = null,
    val countryError: String? = null,
    val daysError: String? = null,
    val coverImageError: String? = null,
    val isSaving: Boolean = false,
    val createdInspirationId: String? = null,
    val generalError: String? = null
)

/**
 * Holds the Basic Info step's form state. Screen-scoped rather than
 * nav-graph-shared like Auth's ViewModel — there's only one step today.
 * Revisit that scoping once Itinerary Builder needs this draft's id passed
 * along a shared back stack, the same "wait until needed" call made for
 * Feed's ViewModel back in Batch 5.
 */
class CreateInspirationViewModel(
    private val repository: InspirationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBasicInfoState())
    val uiState: StateFlow<CreateBasicInfoState> = _uiState.asStateFlow()

    fun onDestinationChanged(value: String) {
        _uiState.value = _uiState.value.copy(destination = value, destinationError = null, generalError = null)
    }

    fun onCountryChanged(value: String) {
        _uiState.value = _uiState.value.copy(country = value, countryError = null, generalError = null)
    }

    fun onDaysChanged(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(days = value, daysError = null, generalError = null)
        }
    }

    fun onCoverImageUrlChanged(value: String) {
        _uiState.value = _uiState.value.copy(coverImageUrl = value, coverImageError = null, generalError = null)
    }

    fun onShortDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(shortDescription = value)
    }

    fun createDraft() {
        val current = _uiState.value

        val destinationError = if (current.destination.isBlank()) "Destination is required" else null
        val countryError = if (current.country.isBlank()) "Country is required" else null
        val daysValue = current.days.toIntOrNull()
        val daysError = when {
            current.days.isBlank() -> "Number of days is required"
            daysValue == null || daysValue <= 0 -> "Enter a valid number of days"
            else -> null
        }
        // Mandatory per spec: at least one image. This URL field stands in
        // for a real image picker until camera/gallery permissions are wired
        // up — that needs its own runtime-permissions batch, similar in
        // weight to the Google Sign-In setup in Batch 3.
        val coverImageError = if (current.coverImageUrl.isBlank()) "A cover image is required" else null

        if (destinationError != null || countryError != null || daysError != null || coverImageError != null) {
            _uiState.value = current.copy(
                destinationError = destinationError,
                countryError = countryError,
                daysError = daysError,
                coverImageError = coverImageError
            )
            return
        }

        _uiState.value = current.copy(isSaving = true, generalError = null)

        viewModelScope.launch {
            try {
                val newId = repository.createDraft(
                    destination = current.destination.trim(),
                    country = current.country.trim(),
                    days = daysValue!!,
                    coverImageUrl = current.coverImageUrl.trim(),
                    shortDescription = current.shortDescription.trim().ifBlank { null }
                )
                _uiState.value = _uiState.value.copy(isSaving = false, createdInspirationId = newId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    generalError = "Couldn't create the draft. Please try again."
                )
            }
        }
    }

    /** Called right after navigating away with the new draft's id, so re-entering Create starts fresh. */
    fun resetAfterNavigation() {
        _uiState.value = CreateBasicInfoState()
    }
}
