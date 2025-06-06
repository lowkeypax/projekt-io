package com.example.packup_podejscie_3.data.repository

import com.example.packup_podejscie_3.data.dto.WydarzenieDto
import com.example.packup_podejscie_3.data.dto.asDomainModel
import com.example.packup_podejscie_3.data.dto.asDto
import com.example.packup_podejscie_3.domain.model.Wydarzenie
import com.example.packup_podejscie_3.domain.repository.WydarzenieRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns // For specifying columns to return
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.util.UUID // For generating UUIDs if needed at the client side

class WydarzenieRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage, // Assuming storage is used elsewhere or will be.
) : WydarzenieRepository {

    private val tableName = "wydarzenie" // Define table name for consistency
    private val uuidColumnName = "wydarzenieuuid" // Define UUID column name for consistency

    override suspend fun createWydarzenie(wydarzenie: Wydarzenie): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Convert domain model to DTO for insertion
                // Ensure the 'id' from the domain model is used for the DTO if it's client-generated
                // If Supabase generates, you'd typically omit 'id' and retrieve it after insertion.
                // For this scenario, let's assume 'id' in domain model is already client-generated or will be matched by Supabase.
                val wydarzenieDto = wydarzenie.asDto()

                // Insert the DTO. The .insert() method by default returns the inserted row.
                // We don't necessarily need the returned data if we just care about success.
                postgrest.from(tableName).insert(wydarzenieDto)
                true // Return true on successful insertion
            } catch (e: Exception) {
                // Log the exception for debugging
                println("Error creating event: ${e.localizedMessage}")
                false // Return false on failure
            }
        }
    }

    override suspend fun getWydarzenia(): List<Wydarzenie> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch DTOs and convert them to domain models
                val resultDto = postgrest.from(tableName)
                    .select().decodeList<WydarzenieDto>()
                resultDto.map { it.asDomainModel() } // Convert each DTO to domain model
            } catch (e: Exception) {
                println("Error getting events: ${e.localizedMessage}")
                emptyList() // Return empty list on failure
            }
        }
    }

    override suspend fun getWydarzenie(id: String): Wydarzenie? {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch a single DTO and convert it to a domain model
                val resultDto = postgrest.from(tableName).select {
                    filter {
                        eq(uuidColumnName, id) // Use the defined column name
                    }
                }.decodeSingleOrNull<WydarzenieDto>() // Use decodeSingleOrNull for nullable result
                resultDto?.asDomainModel() // Convert to domain model if found, otherwise null
            } catch (e: Exception) {
                println("Error getting single event: ${e.localizedMessage}")
                null // Return null on failure
            }
        }
    }

    override suspend fun deleteWydarzenie(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from(tableName).delete {
                    filter {
                        eq(uuidColumnName, id) // Use the defined column name
                    }
                }
                true // Return true on successful deletion
            } catch (e: Exception) {
                println("Error deleting event: ${e.localizedMessage}")
                false // Return false on failure
            }
        }
    }

    override suspend fun updateWydarzenie(updatedWydarzenie: Wydarzenie): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Convert the updated domain model to DTO for updating
                val updatedWydarzenieDto = updatedWydarzenie.asDto()

                postgrest.from(tableName).update(updatedWydarzenieDto) {
                    filter {
                        eq(uuidColumnName, updatedWydarzenie.id) // Use the ID from the domain model for filtering
                    }
                }
                true // Return true on successful update
            } catch (e: Exception) {
                println("Error updating event: ${e.localizedMessage}")
                false // Return false on failure
            }
        }
    }
}