package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Wydarzenie
import com.example.packup_podejscie_3.domain.repository.WydarzenieRepository
import com.example.packup_podejscie_3.ui.navigation.AddEditEventScreenDestination // Potrzebne do pobrania eventId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditEventViewModel @Inject constructor(
    private val wydarzenieRepository: WydarzenieRepository,
    savedStateHandle: SavedStateHandle // Do pobierania argumentów nawigacji (eventId)
) : ViewModel() {

    // Stan pól formularza
    private val _nazwa = MutableStateFlow("")
    val nazwa: StateFlow<String> = _nazwa.asStateFlow()

    private val _dataRozpoczecia = MutableStateFlow<LocalDate?>(null)
    val dataRozpoczecia: StateFlow<LocalDate?> = _dataRozpoczecia.asStateFlow()

    private val _dataZakonczenia = MutableStateFlow<LocalDate?>(null)
    val dataZakonczenia: StateFlow<LocalDate?> = _dataZakonczenia.asStateFlow()

    private val _opisWydarzenia = MutableStateFlow<String?>(null)
    val opisWydarzenia: StateFlow<String?> = _opisWydarzenia.asStateFlow()

    // Stan operacji
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID edytowanego wydarzenia (null, jeśli dodajemy nowe)
    private val eventId: String? = savedStateHandle[AddEditEventScreenDestination.eventId]

    init {
        // Jeśli eventId nie jest nullem, oznacza to tryb edycji - załaduj dane wydarzenia
        if (eventId != null) {
            loadEventForEdit(eventId)
        } else {
            // Domyślne daty dla nowego wydarzenia (np. dzisiaj)
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            _dataRozpoczecia.value = today
            _dataZakonczenia.value = today
        }
    }

    // --- Funkcje aktualizujące stan pól formularza ---
    fun onNazwaChange(newValue: String) {
        _nazwa.value = newValue
    }

    fun onDataRozpoczeciaChange(newValue: LocalDate) {
        _dataRozpoczecia.value = newValue
    }

    fun onDataZakonczeniaChange(newValue: LocalDate) {
        _dataZakonczenia.value = newValue
    }

    fun onOpisWydarzeniaChange(newValue: String) {
        _opisWydarzenia.value = newValue
    }

    /**
     * Ładuje dane istniejącego wydarzenia do edycji.
     */
    private fun loadEventForEdit(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val event = wydarzenieRepository.getWydarzenie(id)
                event?.let {
                    _nazwa.value = it.nazwa
                    _dataRozpoczecia.value = it.data_rozpoczecia
                    _dataZakonczenia.value = it.data_zakonczenia
                    _opisWydarzenia.value = it.opis_wydarzenia
                } ?: run {
                    _userMessage.value = "Nie znaleziono wydarzenia o podanym ID."
                }
            } catch (e: Exception) {
                _userMessage.value = "Błąd ładowania wydarzenia: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Zapisuje lub aktualizuje wydarzenie.
     */
    fun saveEvent() {
        // --- Walidacja pól ---
        if (_nazwa.value.isBlank()) {
            _userMessage.value = "Nazwa wydarzenia nie może być pusta."
            return
        }
        if (_dataRozpoczecia.value == null || _dataZakonczenia.value == null) {
            _userMessage.value = "Proszę wybrać datę rozpoczęcia i zakończenia."
            return
        }
        if (_dataRozpoczecia.value!! > _dataZakonczenia.value!!) {
            _userMessage.value = "Data zakończenia nie może być wcześniejsza niż data rozpoczęcia."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _saveSuccess.value = null
            _userMessage.value = null

            try {
                val eventToSave = Wydarzenie(
                    id = eventId ?: UUID.randomUUID().toString(), // Użyj istniejącego ID lub wygeneruj nowe
                    nazwa = _nazwa.value,
                    data_rozpoczecia = _dataRozpoczecia.value!!,
                    data_zakonczenia = _dataZakonczenia.value!!,
                    opis_wydarzenia = _opisWydarzenia.value?.takeIf { it.isNotBlank() } // Zapisz null jeśli pusty
                )

                val success = if (eventId == null) {
                    wydarzenieRepository.createWydarzenie(eventToSave)
                } else {
                    wydarzenieRepository.updateWydarzenie(eventToSave)
                }

                _saveSuccess.value = success
                if (success) {
                    _userMessage.value = if (eventId == null) "Wydarzenie dodane pomyślnie!" else "Wydarzenie zaktualizowane pomyślnie!"
                } else {
                    _userMessage.value = if (eventId == null) "Nie udało się dodać wydarzenia." else "Nie udało się zaktualizować wydarzenia."
                }
            } catch (e: Exception) {
                _saveSuccess.value = false
                _userMessage.value = "Błąd podczas zapisu wydarzenia: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Resetuje stan sukcesu/błędu po wyświetleniu komunikatu.
     */
    fun resetSaveState() {
        _saveSuccess.value = null
        _userMessage.value = null
    }
}