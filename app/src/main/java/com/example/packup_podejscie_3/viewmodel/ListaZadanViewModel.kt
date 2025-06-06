package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Lista_zadan
import com.example.packup_podejscie_3.domain.repository.Lista_zadanRepository
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination // Będziemy potrzebować ID wydarzenia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaZadanListViewModel @Inject constructor(
    private val listaZadanRepository: Lista_zadanRepository,
    savedStateHandle: SavedStateHandle // Do pobierania argumentów nawigacji
) : ViewModel() {

    // Stan listy zadań
    private val _listaZadan = MutableStateFlow<List<Lista_zadan>>(emptyList())
    val listaZadan: StateFlow<List<Lista_zadan>> = _listaZadan.asStateFlow()

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Stan błędów/komunikatów (np. dla Snackbar)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID wydarzenia, dla którego pobieramy zadania
    private val wydarzenieId: String = savedStateHandle[EventDetailsScreenDestination.eventId]
        ?: throw IllegalArgumentException("Wydarzenie ID is required for ListaZadanListViewModel")

    init {
        loadListaZadan()
    }

    // Funkcja do ładowania listy zadań
    fun loadListaZadan() {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null // Resetuj komunikat
            try {
                val tasks = listaZadanRepository.getLista_zadan(wydarzenieId)
                _listaZadan.value = tasks
            } catch (e: Exception) {
                _userMessage.value = "Nie udało się załadować listy zadań: ${e.localizedMessage}"
                _listaZadan.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do zmiany statusu "czyWykonano"
    fun onTaskCompletedChange(zadanie: Lista_zadan, isChecked: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            try {
                // Użyjemy metody completedZadanie z repozytorium
                val success = listaZadanRepository.completedZadanie(
                    nazwa = zadanie.nazwa,
                    czyWykonano = isChecked,
                    wydarzenieId = zadanie.wydarzenieId,
                    userId = zadanie.userId
                )
                if (success) {
                    // Odśwież listę, aby UI się zaktualizowało
                    loadListaZadan()
                    _userMessage.value = "Status zadania zmieniono."
                } else {
                    _userMessage.value = "Nie udało się zmienić statusu zadania."
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd podczas zmiany statusu zadania: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do usuwania zadania
    fun onDeleteTask(zadanie: Lista_zadan) {
        viewModelScope.launch {
            _isLoading.value = true
            _userMessage.value = null
            try {
                val success = listaZadanRepository.deleteZadanie(
                    nazwa = zadanie.nazwa,
                    wydarzenieId = zadanie.wydarzenieId,
                    userId = zadanie.userId
                )
                if (success) {
                    loadListaZadan() // Odśwież listę po usunięciu
                    _userMessage.value = "Zadanie usunięto pomyślnie."
                } else {
                    _userMessage.value = "Nie udało się usunąć zadania."
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd podczas usuwania zadania: ${e.localizedMessage}"
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