package com.example.packup_podejscie_3.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.* // Używamy Material3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource // Dodaj import dla stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R // Upewnij się, że masz plik R
import com.example.packup_podejscie_3.viewmodel.SignUpViewModel // Import Twojego SignUpViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // Wymagane dla TopAppBar w Material3
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel(), // Wstrzykujemy ViewModel
    onRegisterSuccess: () -> Unit // Pozostawiamy dla nawigacji po sukcesie
) {
    // Stan z ViewModelu
    val email by viewModel.email.collectAsState(initial = "")
    val password by viewModel.password.collectAsState(initial = "")

    // Dodatkowe pola z Twojego oryginalnego kodu (jeśli planujesz je użyć)
    // Jeśli te pola nie są obsługiwane przez AuthenticationRepository/SignUpViewModel,
    // to na razie pozostaną jako lokalny stan UI i nie będą przesyłane do ViewModelu/repozytorium.
    // Aby je uwzględnić w rejestracji, musiałbyś rozszerzyć SignUpViewModel i AuthenticationRepository.
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }


    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }, // Zmienione na Material3 SnackbarHost
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back), // Użyj stringResource
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.register_screen_title), // TODO: Dodaj ten string
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors( // Użyj Material3 TopAppBarDefaults
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(32.dp) // Z Twojego kodu
                .fillMaxSize(), // Z Twojego kodu, aby wyśrodkować
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Twoje pole Nazwa użytkownika
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username_label)) }, // TODO: Dodaj stringResource
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp) // Styl z poradnika
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Pole Email z ViewModelu
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) }, // Aktualizujemy ViewModel
                label = {
                    Text(
                        text = stringResource(R.string.email_label), // TODO: Dodaj stringResource
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Pole Hasło z ViewModelu
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) }, // Aktualizujemy ViewModel
                label = {
                    Text(
                        text = stringResource(R.string.password_label), // TODO: Dodaj stringResource
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                visualTransformation = PasswordVisualTransformation(), // Ukrywa hasło
                maxLines = 1,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Twoje pole Numer telefonu
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone_label)) }, // TODO: Dodaj stringResource
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp) // Styl z poradnika
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    localSoftwareKeyboardController?.hide()
                    viewModel.onSignUp() // Wywołanie metody rejestracji z ViewModelu
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Konto utworzone pomyślnie. Zaloguj się!", // TODO: Użyj stringResource
                            duration = SnackbarDuration.Long
                        )
                    }
                    onRegisterSuccess() // Wywołaj callback po (pozornie) udanej rejestracji
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Używamy koloru Material3
            ) {
                Text(
                    stringResource(R.string.register_button_text), // TODO: Dodaj stringResource
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}