package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.data.dto.OdpowiedziDto
import com.example.packup_podejscie_3.data.dto.asDomainModel
import com.example.packup_podejscie_3.domain.model.Odpowiedzi
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// import android.util.Log // Możesz odkomentować, jeśli używasz Android Log
import javax.inject.Inject

class OdpowiedziRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
) : OdpowiedziRepository {

    private val TABLE_NAME = "odpowiedzi"
    private val COLUMN_ID = "id"
    private val COLUMN_ODPOWIEDZ = "odpowiedz"
    private val COLUMN_LICZBA_GLOSOW = "liczba_glosow"
    private val COLUMN_ANKIETA_UUID = "ankietauuid"


    override suspend fun createOdpowiedzi(odpowiedzi: Odpowiedzi): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Supabase automatycznie generuje ID dla kolumn z PRIMARY KEY, jeśli ID jest null w DTO.
                // Jeśli chcesz dostarczyć ID ręcznie (np. UUID.randomUUID().toString()),
                // musisz upewnić się, że to ID jest unikalne.
                // Jeśli kolumna ID w bazie danych jest typu UUID i ma DEFAULT UUID_GENERATE_V4(),
                // najlepiej jest przekazać id = null w DTO, a baza je sama wygeneruje.
                // Jeśli w modelu masz id: String?, a w bazie id: text (PK), to możesz przekazać UUID.randomUUID().toString()
                // lub null. Zakładam, że baza generuje UUID, więc id=null jest ok.
                val odpowiedziDto = OdpowiedziDto(
                    id = odpowiedzi.id, // Przekaż ID z modelu (może być null, jeśli baza generuje)
                    ankietaId = odpowiedzi.ankietaId,
                    odpowiedz = odpowiedzi.odpowiedz,
                    liczbaGlosow = odpowiedzi.liczbaGlosow
                )
                postgrest.from(TABLE_NAME).insert(odpowiedziDto)
                true
            }
        } catch (e: Exception) {
            // Log.e("OdpowiedziRepository", "Błąd podczas tworzenia odpowiedzi: ${e.message}", e)
            false
        }
    }

    override suspend fun getOdpowiedzi(ankietaId: String): List<Odpowiedzi> {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).select {
                    filter {
                        eq(COLUMN_ANKIETA_UUID, ankietaId)
                    }
                }.decodeList<OdpowiedziDto>().map { it.asDomainModel() }
            }
        } catch (e: Exception) {
            // Log.e("OdpowiedziRepository", "Błąd podczas pobierania listy odpowiedzi: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getOdpowiedz(id: String): Odpowiedzi? {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).select {
                    filter {
                        eq(COLUMN_ID, id)
                    }
                }.decodeSingleOrNull<OdpowiedziDto>()?.asDomainModel()
            }
        } catch (e: Exception) {
            // Log.e("OdpowiedziRepository", "Błąd podczas pobierania pojedynczej odpowiedzi: ${e.message}", e)
            null
        }
    }

    override suspend fun deleteOdpowiedz(id: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from(TABLE_NAME).delete {
                    filter {
                        eq(COLUMN_ID, id)
                    }
                }
                true
            }
        } catch (e: Exception) {
            // Log.e("OdpowiedziRepository", "Błąd podczas usuwania odpowiedzi: ${e.message}", e)
            false
        }
    }

    override suspend fun updateOdpowiedz(updatedOdpowiedz: Odpowiedzi): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Tworzymy DTO z zaktualizowanych danych z modelu domenowego.
                // Ważne: ID w DTO jest używane przez Supabase do znalezienia rekordu do aktualizacji
                // (jeśli jest skonfigurowane jako klucz główny).
                // Reszta pól w DTO będzie użyta do aktualizacji.
                val odpowiedziDto = OdpowiedziDto(
                    id = updatedOdpowiedz.id, // ID jest kluczowe do znalezienia rekordu
                    odpowiedz = updatedOdpowiedz.odpowiedz,
                    liczbaGlosow = updatedOdpowiedz.liczbaGlosow,
                    ankietaId = updatedOdpowiedz.ankietaId // Ankieta ID również jest częścią modelu
                )
                // Używamy obiektu DTO bezpośrednio w metodzie update,
                // a filter określa, który rekord ma być zaktualizowany.
                // Upewnij się, że updatedOdpowiedz.id NIE JEST NULLEM tutaj,
                // ponieważ używamy go do filtrowania.
                if (updatedOdpowiedz.id == null) {
                    // Log.e("OdpowiedziRepository", "Błąd: ID odpowiedzi do aktualizacji nie może być null.")
                    return@withContext false
                }
                postgrest.from(TABLE_NAME).update(odpowiedziDto) {
                    filter {
                        eq(COLUMN_ID, updatedOdpowiedz.id)
                    }
                }
                true
            }
        } catch (e: Exception) {
            // Log.e("OdpowiedziRepository", "Błąd podczas aktualizacji odpowiedzi: ${e.message}", e)
            false
        }
    }

    override suspend fun updateLiczbaGlosow(id: String, liczbaGlosow: Int): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Zmieniamy sposób przekazywania wartości dla set.
                // set przyjmuje parę klucz-wartość (String to Any?),
                // więc jawne rzutowanie typu Int na Any jest bezpieczniejsze.
                postgrest.from(TABLE_NAME).update(
                    // Użyj mapOf z jawnie określonym typem dla wartości, lub po prostu przekaż nazwę kolumny i wartość
                    mapOf(COLUMN_LICZBA_GLOSOW to liczbaGlosow as Any) // Tutaj poprawka
                ) {
                    filter {
                        eq(COLUMN_ID, id)
                    }
                }
                true
            }
        } catch (e: Exception) {
            // Log.e("OdpowiedziRepository", "Błąd podczas aktualizacji liczby głosów: ${e.message}", e)
            false
        }
    }
}