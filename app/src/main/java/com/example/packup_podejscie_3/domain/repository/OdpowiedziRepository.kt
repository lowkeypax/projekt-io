package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.domain.model.Odpowiedzi

interface OdpowiedziRepository {
    // Tworzenie nowej odpowiedzi
    suspend fun createOdpowiedzi(odpowiedzi: Odpowiedzi): Boolean

    // Pobieranie listy odpowiedzi dla danej ankiety
    // Zmieniono zwracany typ na List<Odpowiedzi> (model domenowy)
    suspend fun getOdpowiedzi(ankietaId: String): List<Odpowiedzi>

    // Pobieranie pojedynczej odpowiedzi po ID
    // Zmieniono zwracany typ na Odpowiedzi? (model domenowy, może być null)
    suspend fun getOdpowiedz(id: String): Odpowiedzi?

    // Usuwanie odpowiedzi po ID
    // Zmieniono zwracany typ na Boolean, aby sygnalizować sukces/porażkę
    suspend fun deleteOdpowiedz(id: String): Boolean

    // Aktualizacja odpowiedzi - przyjmuje cały obiekt z nowymi wartościami
    // Implementacja będzie używać id z updatedOdpowiedz do znalezienia rekordu
    suspend fun updateOdpowiedz(updatedOdpowiedz: Odpowiedzi): Boolean

    // Aktualizacja liczby głosów - można zostawić jako osobną metodę, jeśli to częsta operacja
    // Zmieniono zwracany typ na Boolean
    suspend fun updateLiczbaGlosow(
        id: String,
        liczbaGlosow: Int
    ): Boolean
}