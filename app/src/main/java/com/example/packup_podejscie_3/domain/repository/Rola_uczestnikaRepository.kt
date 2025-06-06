package com.example.packup_podejscie_3.domain.repository

import com.example.packup_podejscie_3.domain.model.RolaUczestnika
import kotlinx.coroutines.flow.Flow // Keep this if you intend to observe changes in real-time

interface RolaUczestnikaRepository { // Changed class name to follow Kotlin conventions (PascalCase)

    // Adds a new participant role. Returns the created RolaUczestnika if successful, or null/throws if not.
    // Given the unique composite key, this should handle preventing duplicates.
    suspend fun addRolaUczestnika(rolaUczestnika: RolaUczestnika): Boolean

    // Gets all participant roles for a specific event. Using Flow for potential real-time updates.
    fun getRoleUczestnikowForEvent(wydarzenieId: String): Flow<List<RolaUczestnika>>

    // Gets a single participant role by user and event ID.
    suspend fun getRolaUczestnika(userId: String, wydarzenieId: String): RolaUczestnika?

    // Deletes a participant role by user and event ID.
    suspend fun deleteRolaUczestnika(userId: String, wydarzenieId: String)

    // Updates the role for a specific user in a specific event.
    suspend fun updateRolaUczestnika(rolaUczestnika: RolaUczestnika)
}