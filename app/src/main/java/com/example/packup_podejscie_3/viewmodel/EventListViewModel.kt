package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Wydarzenie
import com.example.packup_podejscie_3.domain.repository.WydarzenieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val wydarzenieRepository: WydarzenieRepository
) : ViewModel() {

    // Stan listy wydarzeń
    private val _events = MutableStateFlow<List<Wydarzenie>>(emptyList())
    val events: StateFlow<List<Wydarzenie>> = _events.asStateFlow()

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Stan błędów/komunikatów (np. dla Snackbar)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        loadEvents()
    }

    /**
     * Ładuje listę wydarzeń z repozytorium.
     */
    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null // Resetuj komunikat
            try {
                val wydarzenia = wydarzenieRepository.getWydarzenia()
                _events.value = wydarzenia
            } catch (e: Exception) {
                _userMessage.value = "Nie udało się załadować wydarzeń: ${e.localizedMessage}"
                _events.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Usuwa wydarzenie z listy i z repozytorium.
     * @param wydarzenie Wydarzenie do usunięcia.
     */
    fun onDeleteEvent(wydarzenie: Wydarzenie) {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            try {
                val success = wydarzenieRepository.deleteWydarzenie(wydarzenie.id)
                if (success) {
                    loadEvents() // Odśwież listę po usunięciu
                    _userMessage.value = "Wydarzenie usunięto pomyślnie."
                } else {
                    _userMessage.value = "Nie udało się usunąć wydarzenia."
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd podczas usuwania wydarzenia: ${e.localizedMessage}"
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