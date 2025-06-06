package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.data.dto.Lista_zadanDto
import com.example.packup_podejscie_3.data.dto.asDomainModel // Importuj funkcję rozszerzającą asDomainModel
import com.example.packup_podejscie_3.domain.model.Lista_zadan
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Lista_zadanRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : Lista_zadanRepository {

    // Nazwa tabeli w Supabase
    private val TABLE_NAME = "lista_zadan"
    private val COLUMN_NAZWA_ZADANIA = "nazwa_zadania"
    private val COLUMN_OPIS_ZADANIA = "opis_zadania"
    private val COLUMN_CZY_WYKONANO = "czy_wykonano"
    private val COLUMN_WYDARZENIE_UUID = "wydarzenieuuid"
    private val COLUMN_USER_ID = "user_id"


    override suspend fun createZadanie(lista_zadan: Lista_zadan): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val lista_zadanDto = Lista_zadanDto(
                    nazwa = lista_zadan.nazwa,
                    wydarzenieId = lista_zadan.wydarzenieId,
                    userId = lista_zadan.userId,
                    czyWykonano = lista_zadan.czyWykonano, // Użyj wartości z modelu, a nie stałej false
                    opis = lista_zadan.opis
                )
                postgrest.from(TABLE_NAME).insert(lista_zadanDto)
                true
            }
        } catch (e: Exception) {
            // Możesz zalogować błąd: Log.e("Lista_zadanRepository", "Error creating task", e)
            false // Zwróć false w przypadku błędu
        }
    }

    override suspend fun getLista_zadan(wydarzenieId: String): List<Lista_zadan> {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).select {
                    filter {
                        eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                    }
                }.decodeList<Lista_zadanDto>().map { it.asDomainModel() } // Konwersja DTO na model domenowy
            }
        } catch (e: Exception) {
            // Log.e("Lista_zadanRepository", "Error getting task list", e)
            emptyList() // Zwróć pustą listę w przypadku błędu
        }
    }

    override suspend fun getZadanie(nazwa: String, wydarzenieId: String, userId: String): Lista_zadan? {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).select {
                    filter {
                        and {
                            eq(COLUMN_NAZWA_ZADANIA, nazwa)
                            eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                            eq(COLUMN_USER_ID, userId)
                        }
                    }
                }.decodeSingleOrNull<Lista_zadanDto>()?.asDomainModel() // Zwróć null jeśli nie znaleziono i konwertuj
            }
        } catch (e: Exception) {
            // Log.e("Lista_zadanRepository", "Error getting single task", e)
            null // Zwróć null w przypadku błędu
        }
    }

    override suspend fun deleteZadanie(nazwa: String, wydarzenieId: String, userId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).delete {
                    filter {
                        and {
                            eq(COLUMN_NAZWA_ZADANIA, nazwa)
                            eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                            eq(COLUMN_USER_ID, userId)
                        }
                    }
                }
                true // Jeśli usunięcie przebiegło pomyślnie
            }
        } catch (e: Exception) {
            // Log.e("Lista_zadanRepository", "Error deleting task", e)
            false // Zwróć false w przypadku błędu
        }
    }

    override suspend fun updateZadanie(updatedZadanie: Lista_zadan): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Supabase aktualizuje dane na podstawie filtru.
                // Aby zaktualizować konkretne zadanie, używamy oryginalnego klucza złożonego
                // z updatedZadanie do identyfikacji rekordu.
                val updatedDto = Lista_zadanDto(
                    nazwa = updatedZadanie.nazwa, // Upewnij się, że nazwa nie uległa zmianie, jeśli ma być częścią klucza
                    opis = updatedZadanie.opis,
                    czyWykonano = updatedZadanie.czyWykonano,
                    wydarzenieId = updatedZadanie.wydarzenieId,
                    userId = updatedZadanie.userId
                )

                postgrest.from(TABLE_NAME).update(updatedDto) {
                    filter {
                        and {
                            // Używamy oryginalnych wartości z updatedZadanie do znalezienia rekordu
                            eq(COLUMN_NAZWA_ZADANIA, updatedZadanie.nazwa)
                            eq(COLUMN_WYDARZENIE_UUID, updatedZadanie.wydarzenieId)
                            eq(COLUMN_USER_ID, updatedZadanie.userId)
                        }
                    }
                }
                true
            }
        } catch (e: Exception) {
            // Log.e("Lista_zadanRepository", "Error updating task", e)
            false
        }
    }

    override suspend fun completedZadanie(
        nazwa: String,
        czyWykonano: Boolean,
        wydarzenieId: String,
        userId: String
    ): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).update(
                    mapOf(COLUMN_CZY_WYKONANO to czyWykonano) // Użyj mapOf do aktualizacji tylko jednego pola
                ) {
                    filter {
                        and {
                            eq(COLUMN_NAZWA_ZADANIA, nazwa)
                            eq(COLUMN_WYDARZENIE_UUID, wydarzenieId)
                            eq(COLUMN_USER_ID, userId)
                        }
                    }
                }
                true
            }
        } catch (e: Exception) {
            // Log.e("Lista_zadanRepository", "Error completing task", e)
            false
        }
    }
}