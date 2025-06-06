package com.example.packup_podejscie_3.viewmodel // lub com.example.packup_podejscie_3.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.domain.repository.AuthenticationRepository // Pamiętaj o poprawnym imporcie Twojego repozytorium
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow // Dodaj asStateFlow dla immutability
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email.asStateFlow() // Zalecane użycie asStateFlow

    private val _password = MutableStateFlow("")
    val password: Flow<String> = _password.asStateFlow() // Zalecane użycie asStateFlow

    // Stan do obsługi ładowania/błędu logowania (dodatkowe, ale zalecane)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.asStateFlow()

    private val _signInSuccess = MutableStateFlow<Boolean?>(null)
    val signInSuccess: Flow<Boolean?> = _signInSuccess.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignIn() {
        viewModelScope.launch {
            _isLoading.value = true
            _signInSuccess.value = null // Resetuj stan
            try {
                val success = authenticationRepository.signIn(
                    email = _email.value,
                    password = _password.value
                )
                _signInSuccess.value = success
            } catch (e: Exception) {
                e.printStackTrace()
                _signInSuccess.value = false // Obsługa błędu
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onGoogleSignIn() {
        viewModelScope.launch {
            _isLoading.value = true
            _signInSuccess.value = null // Resetuj stan
            try {
                val success = authenticationRepository.signInWithGoogle()
                _signInSuccess.value = success
            } catch (e: Exception) {
                e.printStackTrace()
                _signInSuccess.value = false // Obsługa błędu
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSignInState() {
        _signInSuccess.value = null
    }
}