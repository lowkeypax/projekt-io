package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Ogloszenie
import com.example.packup_podejscie_3.domain.repository.OgloszenieRepository
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination // Będziemy potrzebować ID wydarzenia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementBoardViewModel @Inject constructor(
    private val ogloszenieRepository: OgloszenieRepository,
    savedStateHandle: SavedStateHandle // Do pobierania argumentów nawigacji
) : ViewModel() {

    // Stan listy ogłoszeń
    private val _announcements = MutableStateFlow<List<Ogloszenie>>(emptyList())
    val announcements: StateFlow<List<Ogloszenie>> = _announcements.asStateFlow()

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Stan błędów/komunikatów (np. dla Snackbar)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID wydarzenia, dla którego pobieramy ogłoszenia
    private val wydarzenieId: String = savedStateHandle[EventDetailsScreenDestination.eventId]
        ?: throw IllegalArgumentException("Wydarzenie ID is required for AnnouncementBoardViewModel")

    init {
        loadAnnouncements()
    }

    // Funkcja do ładowania listy ogłoszeń
    fun loadAnnouncements() {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null // Resetuj komunikat
            try {
                val announcements = ogloszenieRepository.getOgloszenia(wydarzenieId)
                _announcements.value = announcements
            } catch (e: Exception) {
                _userMessage.value = "Nie udało się załadować ogłoszeń: ${e.localizedMessage}"
                _announcements.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do usuwania ogłoszenia
    fun onDeleteAnnouncement(ogloszenie: Ogloszenie) {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            try {
                // Usuwamy ogłoszenie na podstawie nazwy i wydarzenieId (klucz złożony)
                val success = ogloszenieRepository.deleteOgloszenie(ogloszenie.nazwa, ogloszenie.wydarzenieId)
                if (success) {
                    loadAnnouncements() // Odśwież listę po usunięciu
                    _userMessage.value = "Ogłoszenie usunięto pomyślnie."
                } else {
                    _userMessage.value = "Nie udało się usunąć ogłoszenia."
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd podczas usuwania ogłoszenia: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do zresetowania komunikatu użytkownika
    fun userMessageShown() {
        _userMessage.value = null
    }
}