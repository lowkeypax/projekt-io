package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Lista_zadan
import com.example.packup_podejscie_3.domain.repository.Lista_zadanRepository
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination // Będziemy potrzebować ID wydarzenia
import com.example.packup_podejscie_3.domain.repository.AuthenticationRepository // Potrzebne do pobrania userId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewListaZadanViewModel @Inject constructor(
    private val listaZadanRepository: Lista_zadanRepository,
    private val authenticationRepository: AuthenticationRepository, // Potrzebne do userId
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Pola formularza dla nowego zadania
    private val _nazwa = MutableStateFlow("")
    val nazwa: StateFlow<String> = _nazwa.asStateFlow()

    private val _opis = MutableStateFlow<String?>(null)
    val opis: StateFlow<String?> = _opis.asStateFlow()

    // Stan operacji (np. czy zadanie jest dodawane, czy operacja zakończyła się sukcesem)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID wydarzenia, do którego dodajemy zadanie
    private val wydarzenieId: String = savedStateHandle[EventDetailsScreenDestination.eventId]
        ?: throw IllegalArgumentException("Wydarzenie ID is required for NewListaZadanViewModel")

    // Zalogowany użytkownik
    private var currentUserId: String? = null

    init {
        // Pobierz ID aktualnie zalogowanego użytkownika
        // Supabase-kt powinien mieć sposób na pobranie ID zalogowanego użytkownika
        // Założenie: authenticationRepository może dostarczyć ID użytkownika.
        // Jeśli nie, będziesz musiał to zaimplementować (np. auth.currentUser?.id)
        viewModelScope.launch {
            // TUTAJ MUSISZ POBRAĆ AKTUALNE ID UŻYTKOWNIKA Z SUPABASE AUTH.
            // Przykład (może wymagać dostosowania do Twojej implementacji AuthenticationRepository):
            // currentUserId = authenticationRepository.getCurrentUserId()
            // Na razie zakładam, że jest to dostępne lub zostanie przekazane inaczej
            // Dla testów możesz ustawić stałe ID użytkownika, np. "test_user_id"
            // TODO: Zaimplementuj pobieranie faktycznego userId z AuthenticationRepository
            currentUserId = "dummy_user_id" // Tymczasowe ID użytkownika
        }
    }

    fun onNazwaChange(newNazwa: String) {
        _nazwa.value = newNazwa
    }

    fun onOpisChange(newOpis: String) {
        _opis.value = newOpis
    }

    fun saveZadanie() {
        // Sprawdź, czy nazwa zadania nie jest pusta
        if (_nazwa.value.isBlank()) {
            _userMessage.value = "Nazwa zadania nie może być pusta."
            return
        }

        // Sprawdź, czy ID użytkownika jest dostępne
        val userId = currentUserId
        if (userId == null) {
            _userMessage.value = "Brak ID użytkownika. Nie można dodać zadania."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _saveSuccess.value = null // Resetuj stan sukcesu
            _userMessage.value = null // Resetuj komunikat

            try {
                val newZadanie = Lista_zadan(
                    nazwa = _nazwa.value,
                    opis = _opis.value,
                    czyWykonano = false, // Nowe zadanie jest zawsze niewykonane
                    wydarzenieId = wydarzenieId,
                    userId = userId
                )
                val success = listaZadanRepository.createZadanie(newZadanie)
                _saveSuccess.value = success
                if (success) {
                    _userMessage.value = "Zadanie dodane pomyślnie!"
                } else {
                    _userMessage.value = "Nie udało się dodać zadania."
                }
            } catch (e: Exception) {
                _saveSuccess.value = false
                _userMessage.value = "Błąd podczas dodawania zadania: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do zresetowania stanu sukcesu po wyświetleniu komunikatu
    fun resetSaveState() {
        _saveSuccess.value = null
        _userMessage.value = null
    }
}