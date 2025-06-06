package com.example.packup_podejscie_3.data.repository

import com.example.packup_podejscie_3.data.dto.WydatkiDto
import com.example.packup_podejscie_3.domain.model.Wydatki
import com.example.packup_podejscie_3.domain.repository.WydatkiRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns // Dodaj ten import
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.lang.Exception // Lepiej używać ogólnego Exception, lub specyficznego typu

class WydatkiRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : WydatkiRepository {

    private val TABLE_NAME = "wydatki"

    // Dodaj nowy wydatek. Supabase generuje ID.
    override suspend fun addWydatek(wydatek: Wydatki): Wydatki {
        return withContext(Dispatchers.IO) {
            val wydatkiDto = WydatkiDto(
                // id jest null, bo Supabase je wygeneruje
                id = null,
                nazwaPrzedmiotu = wydatek.nazwaPrzedmiotu,
                kwota = wydatek.kwota,
                wydarzenieId = wydatek.wydarzenieId,
                userId = wydatek.userId
            )
            val response = postgrest.from(TABLE_NAME).insert(wydatkiDto) {
                // To ważne: poproś Supabase o zwrócenie wstawionego rekordu
                select(Columns.ALL) // Zwróć wszystkie kolumny, w tym wygenerowane ID
            }.decodeSingle<WydatkiDto>() // Deserializuj do WydatkiDto (z już wypełnionym ID)

            // Przekształć WydatkiDto z powrotem na model domenowy Wydatki
            // Sprawdź, czy ID nie jest nullem, bo Supabase powinno je wygenerować.
            // Możesz dodać obsługę błędu, jeśli response.id będzie nullem w nieoczekiwanym przypadku.
            response.let { dto ->
                Wydatki(
                    id = dto.id ?: throw IllegalStateException("Supabase didn't return ID for new expense"),
                    nazwaPrzedmiotu = dto.nazwaPrzedmiotu,
                    kwota = dto.kwota,
                    wydarzenieId = dto.wydarzenieId,
                    userId = dto.userId
                )
            }
        }
    }

    // Aktualizuj istniejący wydatek. Wymaga ID.
    override suspend fun updateWydatek(wydatek: Wydatki) {
        withContext(Dispatchers.IO) {
            val wydatkiDto = WydatkiDto(
                id = wydatek.id, // Używamy istniejącego ID do aktualizacji
                nazwaPrzedmiotu = wydatek.nazwaPrzedmiotu,
                kwota = wydatek.kwota,
                wydarzenieId = wydatek.wydarzenieId,
                userId = wydatek.userId
            )
            postgrest.from(TABLE_NAME).update(wydatkiDto) {
                filter {
                    eq("id", wydatek.id) // Filtrujemy po ID, aby zaktualizować konkretny rekord
                }
            }
        }
    }

    // Usuń wydatek po ID (zgodnie z nowym interfejsem)
    override suspend fun deleteWydatek(wydatekId: String) {
        withContext(Dispatchers.IO) {
            postgrest.from(TABLE_NAME).delete {
                filter {
                    eq("id", wydatekId) // Usuwamy po ID
                }
            }
        }
    }

    // Pobierz wydatek po ID (zgodnie z nowym interfejsem)
    override suspend fun getWydatekById(id: String): Wydatki? {
        return withContext(Dispatchers.IO) {
            try {
                val response = postgrest.from(TABLE_NAME).select {
                    filter {
                        eq("id", id)
                    }
                    limit(1)
                }.decodeSingleOrNull<WydatkiDto>() // Pobierz DTO

                // Przekształć WydatkiDto na model domenowy Wydatki
                response?.let { dto ->
                    Wydatki(
                        id = dto.id ?: throw IllegalStateException("Received DTO with null ID for existing expense"),
                        nazwaPrzedmiotu = dto.nazwaPrzedmiotu,
                        kwota = dto.kwota,
                        wydarzenieId = dto.wydarzenieId,
                        userId = dto.userId
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Pobierz wszystkie wydatki dla konkretnego wydarzenia (jako Flow)
    override fun getWydatkiForEvent(eventId: String): Flow<List<Wydatki>> = flow {
        // Używamy "WydarzenieUUID" jako nazwy kolumny w bazie danych, zgodnie z Twoim DTO
        try {
            val response = postgrest.from(TABLE_NAME).select {
                filter {
                    eq("wydarzenieuuid", eventId)
                }
            }.decodeList<WydatkiDto>() // Pobierz listę DTOs

            // Mapuj listę DTOs na listę modeli domenowych Wydatki
            val wydatkiList = response.map { dto ->
                Wydatki(
                    id = dto.id ?: throw IllegalStateException("Received DTO with null ID for existing expense"),
                    nazwaPrzedmiotu = dto.nazwaPrzedmiotu,
                    kwota = dto.kwota,
                    wydarzenieId = dto.wydarzenieId,
                    userId = dto.userId
                )
            }
            emit(wydatkiList)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList()) // Emituj pustą listę w przypadku błędu
        }
    }

    // Usunięte: stare metody, które nie są już zgodne z interfejsem
    // override suspend fun getWydatki(wydarzenieId: String): List<WydatkiDto> { ... }
    // override suspend fun getWydatek(nazwaPrzedmiotu: String, wydarzenieId: String, userId: String): WydatkiDto { ... }
    // override suspend fun deleteWydatek(nazwaPrzedmiotu: String, wydarzenieId: String, userId: String) { ... }
    // override suspend fun updateWydatek(nazwaPrzedmiotu: String, kwota: Double, wydarzenieId: String, userId: String) { ... }
}