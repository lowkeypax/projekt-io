package com.example.packup_podejscie_3.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext // Ten import jest już niepotrzebny w interfejsie

interface AuthenticationRepository {
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signInWithGoogle(): Boolean
    suspend fun signOut(): Boolean
    suspend fun getCurrentUserId(): String? // TYLKO DEKLARACJA
}