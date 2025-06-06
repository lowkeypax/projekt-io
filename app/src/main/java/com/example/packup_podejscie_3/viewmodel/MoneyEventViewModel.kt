package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Wydatki
import com.example.packup_podejscie_3.domain.repository.WydatkiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoneyEventViewModel @Inject constructor(
    private val wydatkiRepository: WydatkiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Argumenty nawigacji
    // To eventId będzie teraz publiczne i może być obserwowane/odczytywane przez UI
    val wydarzenieId: StateFlow<String?> = MutableStateFlow(savedStateHandle["eventId"])

    // Używamy StateFlow do przechowywania listy wydatków
    private val _wydatki = MutableStateFlow<List<Wydatki>>(emptyList())
    val wydatki: StateFlow<List<Wydatki>> = _wydatki.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Obserwuj eventId z SavedStateHandle. Gdy się zmieni, załaduj wydatki.
        // Chociaż w przypadku nawigacji to eventId jest zazwyczaj ustawiane raz.
        // Jeśli chcesz reagować na zmiany tego ID, możesz użyć .collectLatest
        viewModelScope.launch {
            wydarzenieId.collect { id ->
                if (id != null) {
                    loadWydatkiForEvent(id)
                } else {
                    // Obsłuż przypadek, gdy eventId nie jest dostępne (np. ogólna lista wydatków)
                    // Na ten moment MoneyEventScreenDestination wymaga eventId, więc ten blok może być zbędny
                    // lub oznaczać błąd w nawigacji.
                    _userMessage.value = "Brak ID wydarzenia. Nie można załadować wydatków."
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadWydatkiForEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                wydatkiRepository.getWydatkiForEvent(eventId)
                    .catch { e ->
                        _userMessage.value = "Błąd ładowania wydatków dla wydarzenia: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { wydatkiList ->
                        _wydatki.value = wydatkiList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _userMessage.value = "Nieoczekiwany błąd podczas ładowania wydatków: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun deleteWydatek(wydatekId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                wydatkiRepository.deleteWydatek(wydatekId)
                _userMessage.value = "Wydatek został usunięty."
                // Po usunięciu, Flow z repozytorium powinien automatycznie zaktualizować listę wydatków
                // Jeśli nie, możesz wymusić odświeżenie: loadWydatkiForEvent(wydarzenieId.value!!)
            } catch (e: Exception) {
                _userMessage.value = "Błąd podczas usuwania wydatku: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }

    // Funkcja do odświeżania danych na żądanie (np. po pociągnięciu w dół)
    fun refreshWydatki() {
        // Odśwież tylko jeśli mamy wydarzenieId
        wydarzenieId.value?.let { loadWydatkiForEvent(it) }
    }
}