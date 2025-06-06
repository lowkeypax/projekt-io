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
class SignUpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email.asStateFlow() // Zalecane użycie asStateFlow

    private val _password = MutableStateFlow("")
    val password: Flow<String> = _password.asStateFlow() // Zalecane użycie asStateFlow

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignUp() {
        viewModelScope.launch {
            // Dodaj logikę obsługi sukcesu/błędu rejestracji, np.
            // try-catch lub zbieranie wyników z Flow/suspend function w repozytorium
            authenticationRepository.signUp(
                email = _email.value,
                password = _password.value
            )
            // Tutaj możesz dodać nawigację po udanej rejestracji lub wyświetlić komunikat
        }
    }
}