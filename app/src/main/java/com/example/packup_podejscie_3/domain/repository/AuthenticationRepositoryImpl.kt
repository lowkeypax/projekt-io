package com.example.packup_podejscie_3.domain.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: Auth // 'auth' jest tutaj dostępne, bo jest wstrzykiwane przez Hilt
) : AuthenticationRepository {
    override suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signInWithGoogle(): Boolean {
        return try {
            auth.signInWith(Google)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signOut(): Boolean = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    // TERAZ POPRAWNA IMPLEMENTACJA METODY W KLASIE IMPL
    override suspend fun getCurrentUserId(): String? = withContext(Dispatchers.IO) {
        auth.currentUserOrNull()?.id
    }
}