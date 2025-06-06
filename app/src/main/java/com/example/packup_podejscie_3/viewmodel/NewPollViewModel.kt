package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Ankiety // Import Modelu Domenowego
import com.example.packup_podejscie_3.domain.repository.AnkietyRepository // Import Repozytorium
import com.example.packup_podejscie_3.ui.navigation.AddEditEventScreenDestination // Użycie AddEditEventScreenDestination.eventId
// Usunięto import Destination, EventListScreenDestination, bo nie są używane bezpośrednio w ViewModelu
// import com.example.packup_podejscie_3.ui.navigation.Destination
// import com.example.packup_podejscie_3.ui.navigation.EventListScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPollViewModel @Inject constructor(
    private val ankietyRepository: AnkietyRepository,
    savedStateHandle: SavedStateHandle // Do pobierania argumentów nawigacyjnych
) : ViewModel() {

    // Stany dla pól formularza
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> = _question.asStateFlow()

    private val _options = MutableStateFlow<List<String>>(mutableListOf("", ""))
    val options: StateFlow<List<String>> = _options.asStateFlow()

    // Stany UI do obsługi ładowania i wyników operacji
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pollAddedSuccessfully = MutableStateFlow<Boolean?>(null) // true - sukces, false - błąd, null - brak akcji
    val pollAddedSuccessfully: StateFlow<Boolean?> = _pollAddedSuccessfully.asStateFlow()

    // ID wydarzenia, dla którego tworzymy ankietę (z argumentów nawigacji)
    // Zmienione na 'val' zamiast 'private val', aby było publicznie dostępne, jeśli potrzebne
    val wydarzenieId: String = checkNotNull(savedStateHandle[AddEditEventScreenDestination.eventId])

    fun onQuestionChange(newQuestion: String) {
        _question.value = newQuestion
    }

    fun onOptionChange(index: Int, newOption: String) {
        val currentOptions = _options.value.toMutableList()
        if (index >= 0 && index < currentOptions.size) {
            currentOptions[index] = newOption
            _options.value = currentOptions
        }
    }

    fun addOption() {
        if (_options.value.size < 4) { // Max 4 odpowiedzi
            val currentOptions = _options.value.toMutableList()
            currentOptions.add("")
            _options.value = currentOptions
        }
    }

    fun removeOption(index: Int) {
        if (_options.value.size > 2) { // Min 2 odpowiedzi
            val currentOptions = _options.value.toMutableList()
            if (index >= 0 && index < currentOptions.size) {
                currentOptions.removeAt(index)
                _options.value = currentOptions
            }
        }
    }

    fun createPoll() {
        viewModelScope.launch {
            _isLoading.value = true
            _pollAddedSuccessfully.value = null // Resetuj stan przed nową próbą

            val pytanie = _question.value.trim()
            val filteredOptions = _options.value.filter { it.isNotBlank() }.map { it.trim() }

            // Walidacja danych
            if (pytanie.isEmpty()) { // Pytanie nie może być puste
                _pollAddedSuccessfully.value = false // Błąd walidacji
                _isLoading.value = false
                return@launch
            }
            if (filteredOptions.size < 2) { // Minimum 2 niepuste odpowiedzi
                _pollAddedSuccessfully.value = false // Błąd walidacji
                _isLoading.value = false
                return@launch
            }

            try {
                // Tworzymy obiekt Ankiety
                val newAnkiety = Ankiety(
                    id = null, // ID będzie generowane w bazie danych
                    pytanie = pytanie,
                    wydarzenieId = wydarzenieId
                )

                // Wywołujemy metodę createAnkiety z repozytorium
                val success = ankietyRepository.createAnkiety(newAnkiety)
                _pollAddedSuccessfully.value = success

                // Jeśli sukces, zresetuj formularz (opcjonalnie)
                if (success) {
                    _question.value = ""
                    _options.value = mutableListOf("", "")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _pollAddedSuccessfully.value = false // Ustaw na false w przypadku błędu
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do resetowania stanu sukcesu/błędu po obsłużeniu przez UI
    fun resetPollAddedState() {
        _pollAddedSuccessfully.value = null
    }
}