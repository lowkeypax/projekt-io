package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.domain.model.Wydarzenie

interface WydarzenieRepository {
    /**
     * Creates a new event in the data source.
     * @param wydarzenie The Wydarzenie domain model to create.
     * @return true if creation was successful, false otherwise.
     */
    suspend fun createWydarzenie(wydarzenie: Wydarzenie): Boolean

    /**
     * Retrieves a list of all events from the data source.
     * @return A list of Wydarzenie domain models.
     */
    suspend fun getWydarzenia(): List<Wydarzenie> // Changed return type to List<Wydarzenie>

    /**
     * Retrieves a single event by its ID.
     * @param id The unique ID of the event.
     * @return The Wydarzenie domain model if found, null otherwise.
     */
    suspend fun getWydarzenie(id: String): Wydarzenie? // Changed return type to Wydarzenie?

    /**
     * Deletes an event by its ID.
     * @param id The unique ID of the event to delete.
     * @return true if deletion was successful, false otherwise.
     */
    suspend fun deleteWydarzenie(id: String): Boolean // Changed return type to Boolean

    /**
     * Updates an existing event.
     * @param updatedWydarzenie The Wydarzenie domain model with updated fields.
     * The 'id' field within this object will be used to locate the event.
     * @return true if update was successful, false otherwise.
     */
    suspend fun updateWydarzenie(updatedWydarzenie: Wydarzenie): Boolean // Changed signature
}