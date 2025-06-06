package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.data.dto.AnkietyDto
import com.example.packup_podejscie_3.data.dto.WydarzenieDto
import com.example.packup_podejscie_3.domain.model.Ankiety

import com.example.packup_podejscie_3.domain.model.Wydarzenie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject

class AnkietyRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : AnkietyRepository {


    override suspend fun createAnkiety(ankiety: Ankiety): Boolean {
        return try{
            withContext(Dispatchers.IO) {
                val ankietyDto = AnkietyDto(
                    pytanie = ankiety.pytanie,
                    wydarzenieId = ankiety.wydarzenieId
                )
                postgrest.from("ankiety").insert(ankietyDto)
                true
            }
            true
        } catch (e: java.lang.Exception){
            throw e
        }
    }

    //użycie getAnkiety powinno być val ankiety = repository.getAnkiety("konkretne-wydarzenie-uuid")

    override suspend fun getAnkiety(wydarzenieId: String): List<AnkietyDto> {
        return withContext(Dispatchers.IO){
            postgrest.from("ankiety").select {
                filter {
                    eq("wydarzenieuuid", wydarzenieId)
                }
            }.decodeList<AnkietyDto>()

        }
    }

    override suspend fun getAnkieta(id: String): AnkietyDto {
        return withContext(Dispatchers.IO){
            postgrest.from("ankiety").select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle<AnkietyDto>()
        }
    }

    override suspend fun deleteAnkieta(id: String) {
        return withContext(Dispatchers.IO){
            postgrest.from("ankiety").delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }

    override suspend fun updateAnkieta(id: String, pytanie: String, wydarzenieId: String) {
        withContext(Dispatchers.IO){
            postgrest.from("ankiety").update({
                set("pytanie", pytanie)
            }) {
                filter {
                    eq("id", id)
                }
            }
        }
    }


}