package com.example.packup_podejscie_3.ui.screens.addscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R // Upewnij się, że masz plik R.string.
import com.example.packup_podejscie_3.viewmodel.NewAnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAnnouncementScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: NewAnnouncementViewModel = hiltViewModel() // Wstrzyknij NewAnnouncementViewModel
) {
    // Obserwuj stany z ViewModelu
    val nazwa by viewModel.nazwa.collectAsState()
    val opis by viewModel.opis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Efekt do obsługi komunikatów Snackbar i nawigacji
    LaunchedEffect(userMessage, saveSuccess) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.resetSaveState() // Zresetuj komunikat po wyświetleniu
        }
        // Jeśli zapis się powiódł, wróć do poprzedniego ekranu
        saveSuccess?.let { success ->
            if (success) {
                navController.navigateUp() // Wróć do poprzedniego ekranu (np. AnnouncementBoardScreen)
            }
            viewModel.resetSaveState() // Zresetuj stan sukcesu, aby nie wywołać nawigacji ponownie
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.new_announcement_title),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // Zastosuj paddingi ze Scaffold
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pole dla nazwy ogłoszenia (wymagane)
            OutlinedTextField(
                value = nazwa,
                onValueChange = viewModel::onNazwaChange,
                label = { Text(stringResource(R.string.announcement_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                // Prosta walidacja: sprawdź, czy komunikat zawiera informację o pustej nazwie
                isError = userMessage?.contains("Nazwa ogłoszenia nie może być pusta") == true
            )

            // Pole dla opisu ogłoszenia (opcjonalne)
            OutlinedTextField(
                value = opis ?: "", // Wyświetl pusty string, jeśli opis jest null
                onValueChange = viewModel::onOpisChange,
                label = { Text(stringResource(R.string.announcement_description_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Pozwala polu na zajęcie dostępnej przestrzeni
                maxLines = 10 // Ogranicz liczbę linii
            )

            // Przycisk "Dodaj ogłoszenie"
            Button(
                onClick = viewModel::saveAnnouncement,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Wyłącz przycisk podczas ładowania
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary // Kolor zgodny z motywem
                    )
                } else {
                    Text(stringResource(R.string.save_announcement_button))
                }
            }
        }
    }
}