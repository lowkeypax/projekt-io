package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.model.Wydatki
import com.example.packup_podejscie_3.domain.repository.WydatkiRepository
import com.example.packup_podejscie_3.ui.navigation.NewExpenseScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// Usunięto import UUID, bo już go nie używamy do generowania ID w ViewModelu.
// import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val wydatkiRepository: WydatkiRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditExpenseUiState())
    val uiState: StateFlow<AddEditExpenseUiState> = _uiState.asStateFlow()

    private val eventId: String? = savedStateHandle[NewExpenseScreenDestination.eventId]
    private val expenseId: String? = savedStateHandle[NewExpenseScreenDestination.expenseId]

    init {
        // Sprawdź, czy edytujemy istniejący wydatek
        if (expenseId != null) {
            loadExpenseForEditing(expenseId)
        } else {
            // Jeśli to nowy wydatek, upewnij się, że eventId jest dostępne
            if (eventId == null) {
                _uiState.update { it.copy(userMessage = "Błąd: Brak ID wydarzenia do dodania wydatku.") }
            }
        }
    }

    // Ładowanie wydatku do edycji
    private fun loadExpenseForEditing(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val wydatek = wydatkiRepository.getWydatekById(id)
                if (wydatek != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            expenseId = wydatek.id,
                            nazwaPrzedmiotu = wydatek.nazwaPrzedmiotu, // Poprawiono nazwę pola
                            kwota = wydatek.kwota.toString(),
                            userId = wydatek.userId,
                            isLoading = false,
                            isNewExpense = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(userMessage = "Nie znaleziono wydatku o podanym ID.", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Błąd ładowania wydatku: ${e.message}", isLoading = false) }
            }
        }
    }

    fun onNazwaPrzedmiotuChange(newValue: String) {
        _uiState.update { it.copy(nazwaPrzedmiotu = newValue) }
    }

    fun onKwotaChange(newValue: String) {
        _uiState.update { it.copy(kwota = newValue) }
    }

    fun onUserIdChange(newValue: String) {
        _uiState.update { it.copy(userId = newValue) }
    }

    // Zapisywanie/Aktualizowanie wydatku
    fun saveExpense(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return // Jeśli walidacja nie powiedzie się, przestań
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val currentExpenseId = _uiState.value.expenseId
                val wydatekKwota = _uiState.value.kwota.toDouble()
                val wydatekNazwa = _uiState.value.nazwaPrzedmiotu
                val wydatekUserId = _uiState.value.userId
                val wydatekWydarzenieId = eventId!! // Mamy pewność, że eventId jest, bo inaczej byłby userMessage

                if (_uiState.value.isNewExpense) {
                    // Tworzymy nowy obiekt Wydatki. ID będzie tymczasowe (np. puste),
                    // ponieważ zostanie nadane przez Supabase.
                    val newWydatek = Wydatki(
                        id = "", // Placeholder: to ID zostanie zignorowane przez Supabase i nadane nowe.
                        nazwaPrzedmiotu = wydatekNazwa,
                        kwota = wydatekKwota,
                        wydarzenieId = wydatekWydarzenieId,
                        userId = wydatekUserId
                    )
                    // Repozytorium zwróci obiekt z nowym ID z Supabase
                    val savedWydatek = wydatkiRepository.addWydatek(newWydatek)
                    _uiState.update { it.copy(userMessage = "Wydatek dodany pomyślnie!", expenseId = savedWydatek.id) }
                } else {
                    // Dla edycji, obiekt Wydatki musi mieć istniejące ID
                    val updatedWydatek = Wydatki(
                        id = currentExpenseId!!, // To ID musi istnieć i być pobrane z UI state
                        nazwaPrzedmiotu = wydatekNazwa,
                        kwota = wydatekKwota,
                        wydarzenieId = wydatekWydarzenieId,
                        userId = wydatekUserId
                    )
                    wydatkiRepository.updateWydatek(updatedWydatek)
                    _uiState.update { it.copy(userMessage = "Wydatek zaktualizowany pomyślnie!") }
                }
                onSuccess() // Wywołaj callback po sukcesie
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Błąd zapisu wydatku: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Walidacja formularza
    private fun validateForm(): Boolean {
        _uiState.update { it.copy(nazwaPrzedmiotuError = null, kwotaError = null, userIdError = null) }

        var isValid = true

        if (_uiState.value.nazwaPrzedmiotu.isBlank()) {
            _uiState.update { it.copy(nazwaPrzedmiotuError = "Nazwa przedmiotu nie może być pusta.") }
            isValid = false
        }
        if (_uiState.value.kwota.isBlank()) {
            _uiState.update { it.copy(kwotaError = "Kwota nie może być pusta.") }
            isValid = false
        } else {
            try {
                if (_uiState.value.kwota.toDouble() <= 0) {
                    _uiState.update { it.copy(kwotaError = "Kwota musi być większa od zera.") }
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                _uiState.update { it.copy(kwotaError = "Nieprawidłowy format kwoty.") }
                isValid = false
            }
        }
        if (_uiState.value.userId.isBlank()) {
            _uiState.update { it.copy(userIdError = "Kto zapłacił, nie może być puste.") }
            isValid = false
        }
        if (eventId == null) {
            _uiState.update { it.copy(userMessage = "Błąd wewnętrzny: Brak ID wydarzenia. Skontaktuj się z obsługą.") }
            isValid = false
        }

        return isValid
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}

// Stan UI dla AddEditExpenseViewModel
data class AddEditExpenseUiState(
    val expenseId: String? = null, // ID wydatku, jeśli edytujemy
    val nazwaPrzedmiotu: String = "",
    val kwota: String = "", // String dla łatwej obsługi wprowadzania użytkownika
    val userId: String = "", // Kto zapłacił
    val isLoading: Boolean = false,
    val userMessage: String? = null, // Komunikaty dla użytkownika (np. błędy, sukcesy)
    val isNewExpense: Boolean = true, // true jeśli dodajemy, false jeśli edytujemy

    // Błędy walidacji
    val nazwaPrzedmiotuError: String? = null,
    val kwotaError: String? = null,
    val userIdError: String? = null
)