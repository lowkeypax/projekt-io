package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.domain.model.Ogloszenie

interface OgloszenieRepository {
    // Tworzenie nowego ogłoszenia
    suspend fun createOgloszenie(ogloszenie: Ogloszenie): Boolean

    // Pobieranie listy ogłoszeń dla danego wydarzenia
    // Zmieniono zwracany typ na List<Ogloszenie> (model domenowy)
    suspend fun getOgloszenia(wydarzenieId: String): List<Ogloszenie>

    // Pobieranie pojedynczego ogłoszenia po nazwie i wydarzenieId (klucz złożony)
    // Zmieniono zwracany typ na Ogloszenie? (model domenowy, może być null)
    suspend fun getOgloszenie(nazwa: String, wydarzenieId: String): Ogloszenie?

    // Usuwanie ogłoszenia po nazwie i wydarzenieId (klucz złożony)
    // Zmieniono zwracany typ na Boolean, aby sygnalizować sukces/porażkę
    suspend fun deleteOgloszenie(nazwa: String, wydarzenieId: String): Boolean

    // Aktualizacja ogłoszenia - przyjmuje cały obiekt z nowymi wartościami
    // Implementacja będzie używać nazwa i wydarzenieId z updatedOgloszenie do znalezienia rekordu
    suspend fun updateOgloszenie(updatedOgloszenie: Ogloszenie): Boolean
}