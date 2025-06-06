package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.data.dto.OgloszenieDto
import com.example.packup_podejscie_3.data.dto.asDomainModel // Importuj funkcję rozszerzającą asDomainModel
import com.example.packup_podejscie_3.domain.model.Ogloszenie
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// import android.util.Log // Możesz odkomentować, jeśli używasz Android Log
import javax.inject.Inject

class OgloszenieRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : OgloszenieRepository {

    // Definiowanie stałych dla nazw tabel i kolumn, zgodnie z DTO
    private val TABLE_NAME = "ogloszenie"
    private val COLUMN_NAZWA_OGLOSZENIA = "nazwa_ogloszenia"
    private val COLUMN_OPIS_OGLOSZENIA = "opis_ogloszenia"
    private val COLUMN_WYDARZENIE_UUID = "wydarzenieuuid"

    override suspend fun createOgloszenie(ogloszenie: Ogloszenie): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val ogloszenieDto = OgloszenieDto(
                    nazwa = ogloszenie.nazwa,
                    opis = ogloszenie.opis,
                    wydarzenieId = ogloszenie.wydarzenieId
                )
                postgrest.from(TABLE_NAME).insert(ogloszenieDto)
                true
            }
        } catch (e: Exception) {
            // Log.e("OgloszenieRepository", "Błąd podczas tworzenia ogłoszenia: ${e.message}", e)
            false // Zwróć false w przypadku błędu
        }
    }

    override suspend fun getOgloszenia(wydarzenieId: String): List<Ogloszenie> {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).select {
                    filter {
                        eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                    }
                }.decodeList<OgloszenieDto>().map { it.asDomainModel() } // Konwersja DTO na model domenowy
            }
        } catch (e: Exception) {
            // Log.e("OgloszenieRepository", "Błąd podczas pobierania listy ogłoszeń: ${e.message}", e)
            emptyList() // Zwróć pustą listę w przypadku błędu
        }
    }

    override suspend fun getOgloszenie(nazwa: String, wydarzenieId: String): Ogloszenie? {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).select {
                    filter {
                        and {
                            eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                            eq(COLUMN_NAZWA_OGLOSZENIA, nazwa)
                        }
                    }
                }.decodeSingleOrNull<OgloszenieDto>()?.asDomainModel() // Użyj decodeSingleOrNull i konwertuj
            }
        } catch (e: Exception) {
            // Log.e("OgloszenieRepository", "Błąd podczas pobierania pojedynczego ogłoszenia: ${e.message}", e)
            null // Zwróć null w przypadku błędu
        }
    }

    override suspend fun deleteOgloszenie(nazwa: String, wydarzenieId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).delete {
                    filter {
                        and {
                            eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                            eq(COLUMN_NAZWA_OGLOSZENIA, nazwa)
                        }
                    }
                }
                true // Jeśli usunięcie przebiegło pomyślnie
            }
        } catch (e: Exception) {
            // Log.e("OgloszenieRepository", "Błąd podczas usuwania ogłoszenia: ${e.message}", e)
            false // Zwróć false w przypadku błędu
        }
    }

    override suspend fun updateOgloszenie(updatedOgloszenie: Ogloszenie): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Tworzymy mapę pól do zaktualizowania. Zakładamy, że Nazwa_ogloszenia
                // i WydarzenieUUID są kluczami złożonymi i nie są zmieniane,
                // a aktualizowany jest tylko opis.
                val updates = mapOf(
                    COLUMN_OPIS_OGLOSZENIA to updatedOgloszenie.opis as Any? // Rzutowanie na Any?
                )

                postgrest.from(TABLE_NAME).update(updates) {
                    filter {
                        and {
                            eq(COLUMN_WYDARZENIE_UUID, updatedOgloszenie.wydarzenieId)
                            eq(COLUMN_NAZWA_OGLOSZENIA, updatedOgloszenie.nazwa)
                        }
                    }
                }
                true
            }
        } catch (e: Exception) {
            // Log.e("OgloszenieRepository", "Błąd podczas aktualizacji ogłoszenia: ${e.message}", e)
            false
        }
    }
}