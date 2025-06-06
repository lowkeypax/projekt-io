package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.domain.model.Lista_zadan // Zmieniono na model domenowy Lista_zadan

interface Lista_zadanRepository {
    // Tworzenie zadania
    suspend fun createZadanie(lista_zadan: Lista_zadan): Boolean

    // Pobieranie listy zadań dla konkretnego wydarzenia
    // Zmieniono zwracany typ na List<Lista_zadan> (model domenowy)
    suspend fun getLista_zadan(wydarzenieId: String): List<Lista_zadan>

    // Pobieranie pojedynczego zadania (może zwrócić null, jeśli nie znaleziono)
    // Zmieniono zwracany typ na Lista_zadan? (model domenowy)
    suspend fun getZadanie(nazwa: String, wydarzenieId: String, userId: String): Lista_zadan?

    // Usuwanie zadania - na podstawie klucza złożonego
    suspend fun deleteZadanie(nazwa: String, wydarzenieId: String, userId: String): Boolean // Zwróć Boolean, aby wiedzieć, czy się powiodło

    // Aktualizacja zadania - przyjmuje cały obiekt z nowymi wartościami
    // Użyjemy obiektu Lista_zadan, a identyfikacja będzie na podstawie oryginalnych nazwa, wydarzenieId, userId
    suspend fun updateZadanie(updatedZadanie: Lista_zadan): Boolean // Zwróć Boolean

    // Zmiana statusu wykonania zadania - jeśli jest to częsta operacja, można zostawić osobną metodę
    // Alternatywnie, to mogłoby być częścią ogólnej metody updateZadanie.
    suspend fun completedZadanie(
        nazwa: String, czyWykonano: Boolean, wydarzenieId: String, userId: String
    ): Boolean // Zwróć Boolean
}