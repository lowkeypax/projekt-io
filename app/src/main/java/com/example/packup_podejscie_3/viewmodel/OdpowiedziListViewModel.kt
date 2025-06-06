package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Odpowiedzi
import com.example.packup_podejscie_3.domain.repository.OdpowiedziRepository
import com.example.packup_podejscie_3.ui.navigation.AnkietyDetailsDestination // Będziemy potrzebować ID ankiety
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OdpowiedziListViewModel @Inject constructor(
    private val odpowiedziRepository: OdpowiedziRepository,
    savedStateHandle: SavedStateHandle // Do pobierania argumentów nawigacji
) : ViewModel() {

    // Stan listy odpowiedzi
    private val _odpowiedziList = MutableStateFlow<List<Odpowiedzi>>(emptyList())
    val odpowiedziList: StateFlow<List<Odpowiedzi>> = _odpowiedziList.asStateFlow()

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Stan błędów/komunikatów (np. dla Snackbar)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID ankiety, dla której pobieramy odpowiedzi
    private val ankietaId: String = savedStateHandle[AnkietyDetailsDestination.ankietyId]
        ?: throw IllegalArgumentException("Ankieta ID is required for OdpowiedziListViewModel")

    init {
        loadOdpowiedzi()
    }

    // Funkcja do ładowania listy odpowiedzi
    fun loadOdpowiedzi() {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null // Resetuj komunikat
            try {
                val answers = odpowiedziRepository.getOdpowiedzi(ankietaId)
                _odpowiedziList.value = answers
            } catch (e: Exception) {
                _userMessage.value = "Nie udało się załadować odpowiedzi: ${e.localizedMessage}"
                _odpowiedziList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do oddawania głosu na daną odpowiedź
    fun onVote(odpowiedz: Odpowiedzi) {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            try {
                // Zwiększamy liczbę głosów o 1. Zakładamy, że null = 0 głosów na początek.
                val currentVotes = odpowiedz.liczbaGlosow ?: 0
                val newVotes = currentVotes + 1

                // Aktualizujemy w repozytorium
                val success = odpowiedziRepository.updateLiczbaGlosow(odpowiedz.id!!, newVotes) // ID nie może być null
                if (success) {
                    // Odśwież listę, aby UI się zaktualizowało o nową liczbę głosów
                    loadOdpowiedzi()
                    _userMessage.value = "Głos oddany pomyślnie!"
                } else {
                    _userMessage.value = "Nie udało się oddać głosu."
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd podczas oddawania głosu: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do zresetowania komunikatu użytkownika
    fun userMessageShown() {
        _userMessage.value = null
    }

    // Możesz rozważyć dodanie funkcji do usuwania odpowiedzi, jeśli to możliwe w UI
    // fun onDeleteOdpowiedz(id: String) { ... }
    // Oraz do dodawania nowej odpowiedzi do ankiety, jeśli to możliwe z poziomu tego ekranu
    // fun addOdpowiedz(odpowiedz: String) { ... }
}