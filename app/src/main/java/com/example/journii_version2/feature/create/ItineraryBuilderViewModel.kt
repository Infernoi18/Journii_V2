package com.example.journii_version2.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.model.BlockCategory
import com.example.journii_version2.core.model.ItineraryBlock
import com.example.journii_version2.core.model.ItineraryDay
import com.example.journii_version2.core.model.TransportMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ItineraryBuilderUiState(
    val destination: String = "",
    val days: List<ItineraryDay> = emptyList(),
    val selectedDayIndex: Int = 0,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ItineraryBuilderViewModel(
    private val repository: InspirationRepository,
    private val inspirationId: String
) : ViewModel() {

    private val selectedDayIndex = MutableStateFlow(0)
    private val isSaving = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ItineraryBuilderUiState> = combine(
        repository.observeInspiration(inspirationId),
        selectedDayIndex,
        isSaving,
        errorMessage
    ) { inspiration, dayIndex, saving, error ->
        ItineraryBuilderUiState(
            destination = inspiration?.destination.orEmpty(),
            days = inspiration?.itinerary ?: emptyList(),
            selectedDayIndex = dayIndex,
            isLoading = inspiration == null,
            isSaving = saving,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ItineraryBuilderUiState()
    )

    fun selectDay(index: Int) {
        selectedDayIndex.value = index
    }

    fun addBlock(title: String, category: BlockCategory, time: String?, notes: String?) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            errorMessage.value = "Give this stop a title."
            return
        }
        val newBlock = ItineraryBlock(
            id = "block_${UUID.randomUUID()}",
            title = trimmedTitle,
            category = category,
            time = time?.trim()?.ifBlank { null },
            notes = notes?.trim()?.ifBlank { null }
        )
        mutateSelectedDay { blocks -> blocks + newBlock }
    }

    fun removeBlock(blockId: String) {
        mutateSelectedDay { blocks -> blocks.filterNot { it.id == blockId } }
    }

    fun moveBlockUp(blockId: String) {
        mutateSelectedDay { blocks -> blocks.moved(blockId, -1) }
    }

    fun moveBlockDown(blockId: String) {
        mutateSelectedDay { blocks -> blocks.moved(blockId, +1) }
    }

    fun setTransportToNext(blockId: String, mode: TransportMode?) {
        mutateSelectedDay { blocks ->
            blocks.map { block -> if (block.id == blockId) block.copy(transportToNext = mode) else block }
        }
    }

    private fun mutateSelectedDay(transform: (List<ItineraryBlock>) -> List<ItineraryBlock>) {
        val current = uiState.value
        val dayIndex = current.selectedDayIndex
        val day = current.days.getOrNull(dayIndex) ?: return
        val updatedDay = day.copy(blocks = transform(day.blocks))
        val updatedDays = current.days.toMutableList().also { it[dayIndex] = updatedDay }
        persist(updatedDays)
    }

    private fun persist(updatedDays: List<ItineraryDay>) {
        isSaving.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                repository.updateItinerary(inspirationId, updatedDays)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage.value = "Couldn't save that change. Please try again."
            } finally {
                isSaving.value = false
            }
        }
    }

    private fun List<ItineraryBlock>.moved(blockId: String, offset: Int): List<ItineraryBlock> {
        val index = indexOfFirst { it.id == blockId }
        val targetIndex = index + offset
        if (index == -1 || targetIndex !in indices) return this
        return toMutableList().apply {
            val item = removeAt(index)
            add(targetIndex, item)
        }
    }
}