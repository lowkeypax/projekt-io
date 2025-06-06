// app/src/main/java/com/example/packup_podejscie_3/viewmodel/AnkietyDetailsViewModel.kt
package com.example.packup_podejscie_3.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.data.dto.AnkietyDto
import com.example.packup_podejscie_3.data.dto.asDomainModel
import com.example.packup_podejscie_3.domain.model.Ankiety
import com.example.packup_podejscie_3.domain.repository.AnkietyRepository
import com.example.packup_podejscie_3.ui.navigation.AnkietyDetailsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnkietyDetailsViewModel @Inject constructor(
    private val ankietyRepository: AnkietyRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _ankiety = MutableStateFlow<Ankiety?>(null)
    val ankiety: Flow<Ankiety?> = _ankiety.asStateFlow()

    private val _pytanie = MutableStateFlow("")
    val pytanie: Flow<String> = _pytanie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccessful = MutableStateFlow<Boolean?>(null)
    val saveSuccessful: Flow<Boolean?> = _saveSuccessful.asStateFlow()

    private val ankietyId: String? = savedStateHandle[AnkietyDetailsDestination.ankietyId]

    init {
        ankietyId?.let {
            getAnkietyById(ankietyId = it)
        }
    }

    /**
     * Pobiera szczegóły konkretnej ankiety po jej ID.
     */
    private fun getAnkietyById(ankietyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultDto: AnkietyDto = ankietyRepository.getAnkieta(ankietyId)
                val result = resultDto.asDomainModel()

                _ankiety.emit(result)
                _pytanie.emit(result.pytanie)
            } catch (e: Exception) {
                e.printStackTrace()
                _ankiety.emit(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onQuestionChange(newQuestion: String) {
        _pytanie.value = newQuestion
    }

    fun onSaveAnkiety() {
        viewModelScope.launch {
            _isLoading.value = true
            _saveSuccessful.value = null

            val currentAnkiety = _ankiety.value // currentAnkiety jest typu Ankiety?
            val currentPytanie = _pytanie.value.trim()

            // POPRAWKA TUTAJ: Dodajemy sprawdzenie czy currentAnkiety.id nie jest nullem
            if (currentAnkiety == null || currentPytanie.isEmpty() || currentAnkiety.id == null) {
                Log.e("AnkietyDetailsViewModel", "Brak ID ankiety lub pytanie jest puste. Nie można zapisać.")
                _saveSuccessful.value = false
                _isLoading.value = false
                return@launch
            }

            try {
                // Teraz, po sprawdzeniu nulli, możemy bezpiecznie użyć !!
                ankietyRepository.updateAnkieta(
                    id = currentAnkiety.id, // currentAnkiety.id jest teraz inteligentnie rzutowane na String
                    pytanie = currentPytanie,
                    wydarzenieId = currentAnkiety.wydarzenieId
                )
                _saveSuccessful.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _saveSuccessful.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveState() {
        _saveSuccessful.value = null
    }
}