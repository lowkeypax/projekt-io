package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.data.dto.AnkietyDto // Import DTO
import com.example.packup_podejscie_3.domain.model.Ankiety // Import Modelu Domenowego
import com.example.packup_podejscie_3.domain.repository.AnkietyRepository // Import Repozytorium
// Zmieniono import z AddEditEventScreenDestination na AnkietyListScreenDestination
import com.example.packup_podejscie_3.ui.navigation.AnkietyListScreenDestination
// Usunięto nieużywany import Destination
// import com.example.packup_podejscie_3.ui.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnkietyListViewModel @Inject constructor(
    private val ankietyRepository: AnkietyRepository,
    savedStateHandle: SavedStateHandle // Dodano SavedStateHandle do pobierania argumentów nawigacyjnych
) : ViewModel() {

    // Stan listy ankiet
    private val _ankietyList = MutableStateFlow<List<Ankiety>?>(emptyList())
    val ankietyList: Flow<List<Ankiety>?> = _ankietyList.asStateFlow()

    // Stan ładowania danych
    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.asStateFlow()

    // ID wydarzenia, dla którego pobieramy ankiety (pobierane z argumentów nawigacji)
    // Zmienione na 'val' zamiast 'private val', aby było publicznie dostępne
    // Zmieniono AddEditEventScreenDestination.eventId na AnkietyListScreenDestination.eventId
    val wydarzenieId: String = checkNotNull(savedStateHandle[AnkietyListScreenDestination.eventId])

    init {
        // Po zainicjowaniu ViewModelu, pobierz ankiety dla danego wydarzenia
        getAnkietyForEvent()
    }

    /**
     * Pobiera listę ankiet dla konkretnego wydarzenia.
     */
    fun getAnkietyForEvent() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Wywołujemy metodę z repozytorium z odpowiednim wydarzenieId
                val ankietyDtoList = ankietyRepository.getAnkiety(wydarzenieId)
                _ankietyList.emit(ankietyDtoList.map { it.asDomainModel() })
            } catch (e: Exception) {
                // Obsługa błędów, np. logowanie, wyświetlenie komunikatu
                e.printStackTrace()
                _ankietyList.emit(emptyList()) // W przypadku błędu lista pusta
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Usuwa ankietę z listy i z bazy danych.
     * @param ankiety Ankieta do usunięcia.
     */
    fun removeAnkiety(ankiety: Ankiety) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Optymistyczne usunięcie z UI
                val currentList = _ankietyList.value?.toMutableList() ?: mutableListOf()
                val indexToRemove = currentList.indexOfFirst { it.id == ankiety.id }
                if (indexToRemove != -1) {
                    currentList.removeAt(indexToRemove)
                    _ankietyList.emit(currentList.toList())
                }

                // Wywołanie API do usunięcia
                ankiety.id?.let { ankietyRepository.deleteAnkieta(id = it) }

                // Po usunięciu, odśwież listę z bazy danych, aby upewnić się, że stan jest spójny
                getAnkietyForEvent()
            } catch (e: Exception) {
                e.printStackTrace()
                // Jeśli usunięcie z API się nie powiedzie, przywróć element do UI (lub ponownie pobierz listę)
                getAnkietyForEvent() // W przypadku błędu, odśwież listę z serwera
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Funkcja rozszerzająca do konwersji AnkietyDto na model domenowy Ankiety.
     */
    private fun AnkietyDto.asDomainModel(): Ankiety {
        return Ankiety(
            id = this.id,
            pytanie = this.pytanie,
            wydarzenieId = this.wydarzenieId
        )
    }
}