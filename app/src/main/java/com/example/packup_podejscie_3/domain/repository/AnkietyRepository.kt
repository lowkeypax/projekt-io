package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.data.dto.AnkietyDto
import com.example.packup_podejscie_3.domain.model.Ankiety

interface AnkietyRepository {
    suspend fun createAnkiety(ankiety: Ankiety): Boolean
    suspend fun getAnkiety(wydarzenieId: String): List<AnkietyDto>
    suspend fun getAnkieta(id: String): AnkietyDto
    suspend fun deleteAnkieta(id: String)
    // --- THIS IS THE CORRECTED LINE ---
    suspend fun updateAnkieta(id: String, pytanie: String, wydarzenieId: String)
    // ----------------------------------
}