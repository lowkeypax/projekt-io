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
class NewAnnouncementViewModel @Inject constructor(
    private val ogloszenieRepository: OgloszenieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Pola formularza dla nowego ogłoszenia
    private val _nazwa = MutableStateFlow("")
    val nazwa: StateFlow<String> = _nazwa.asStateFlow()

    private val _opis = MutableStateFlow<String?>(null)
    val opis: StateFlow<String?> = _opis.asStateFlow()

    // Stan operacji (np. czy ogłoszenie jest dodawane, czy operacja zakończyła się sukcesem)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // ID wydarzenia, do którego dodajemy ogłoszenie
    private val wydarzenieId: String = savedStateHandle[EventDetailsScreenDestination.eventId]
        ?: throw IllegalArgumentException("Wydarzenie ID is required for NewAnnouncementViewModel")

    fun onNazwaChange(newNazwa: String) {
        _nazwa.value = newNazwa
    }

    fun onOpisChange(newOpis: String) {
        _opis.value = newOpis
    }

    fun saveAnnouncement() {
        // Sprawdź, czy nazwa ogłoszenia nie jest pusta
        if (_nazwa.value.isBlank()) {
            _userMessage.value = "Nazwa ogłoszenia nie może być pusta."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _saveSuccess.value = null // Resetuj stan sukcesu
            _userMessage.value = null // Resetuj komunikat

            try {
                val newOgloszenie = Ogloszenie(
                    nazwa = _nazwa.value,
                    opis = _opis.value,
                    wydarzenieId = wydarzenieId
                )
                val success = ogloszenieRepository.createOgloszenie(newOgloszenie)
                _saveSuccess.value = success
                if (success) {
                    _userMessage.value = "Ogłoszenie dodane pomyślnie!"
                } else {
                    _userMessage.value = "Nie udało się dodać ogłoszenia. Może ogłoszenie o tej nazwie już istnieje w wydarzeniu?" // Dodano informację o unikalności
                }
            } catch (e: Exception) {
                _saveSuccess.value = false
                _userMessage.value = "Błąd podczas dodawania ogłoszenia: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja do zresetowania stanu sukcesu/błędu po wyświetleniu komunikatu
    fun resetSaveState() {
        _saveSuccess.value = null
        _userMessage.value = null
    }
}