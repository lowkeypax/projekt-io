package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Wydarzenie
import com.example.packup_podejscie_3.domain.repository.WydarzenieRepository
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination // Potrzebne do pobrania eventId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val wydarzenieRepository: WydarzenieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Stan pojedynczego wydarzenia
    private val _event = MutableStateFlow<Wydarzenie?>(null)
    val event: StateFlow<Wydarzenie?> = _event.asStateFlow()

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Stan błędów/komunikatów
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID wydarzenia, dla którego pobieramy szczegóły
    private val eventId: String = savedStateHandle[EventDetailsScreenDestination.eventId]
        ?: throw IllegalArgumentException("Wydarzenie ID is required for EventDetailsViewModel")

    init {
        loadEventDetails()
    }

    /**
     * Ładuje szczegóły wydarzenia na podstawie eventId.
     */
    fun loadEventDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            try {
                val event = wydarzenieRepository.getWydarzenie(eventId)
                _event.value = event
                if (event == null) {
                    _userMessage.value = "Nie znaleziono wydarzenia o ID: $eventId"
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd ładowania szczegółów wydarzenia: ${e.localizedMessage}"
                _event.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Resetuje komunikat użytkownika po jego wyświetleniu.
     */
    fun userMessageShown() {
        _userMessage.value = null
    }
}