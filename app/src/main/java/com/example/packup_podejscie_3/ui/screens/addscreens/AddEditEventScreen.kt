package com.example.packup_podejscie_3.ui.screens.addscreens // Zmieniono pakiet

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme
import com.example.packup_podejscie_3.viewmodel.AddEditEventViewModel // Import naszego ViewModelu
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime // Import do konwersji Long na LocalDate

// --- Główny ekran dodawania/edycji wydarzenia (AddEditEventScreen) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AddEditEventViewModel = hiltViewModel() // Wstrzyknięcie ViewModelu
) {
    // Obserwacja stanów z ViewModelu
    val nazwa by viewModel.nazwa.collectAsState()
    val dataRozpoczecia by viewModel.dataRozpoczecia.collectAsState()
    val dataZakonczenia by viewModel.dataZakonczenia.collectAsState()
    val opisWydarzenia by viewModel.opisWydarzenia.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Stan dla DatePickerDialog
    val datePickerStateStart = rememberDatePickerState()
    val showDatePickerStart = remember { mutableStateOf(false) }

    val datePickerStateEnd = rememberDatePickerState()
    val showDatePickerEnd = remember { mutableStateOf(false) }


    // Efekt do obsługi komunikatów Snackbar i nawigacji
    LaunchedEffect(userMessage, saveSuccess) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.resetSaveState() // Zresetuj komunikat po wyświetleniu
        }
        // Jeśli zapis się powiódł, wróć do poprzedniego ekranu
        saveSuccess?.let { success ->
            if (success) {
                navController.navigateUp() // Wróć do poprzedniego ekranu (np. EventListScreen)
            }
            viewModel.resetSaveState() // Zresetuj stan sukcesu
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
                        text = stringResource(R.string.add_edit_event_title), // TODO: Dodaj string
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pole Nazwa wydarzenia
            OutlinedTextField(
                value = nazwa,
                onValueChange = viewModel::onNazwaChange,
                label = { Text(stringResource(R.string.event_name_label)) }, // TODO: Dodaj string
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                // Sprawdź błąd na podstawie userMessage z ViewModelu
                isError = userMessage?.contains("Nazwa wydarzenia nie może być pusta") == true
            )

            // Pole Data rozpoczęcia z DatePickerDialog
            OutlinedTextField(
                value = dataRozpoczecia?.toString() ?: "",
                onValueChange = { /* Tylko do wyświetlania, zmiana przez DatePicker */ },
                label = { Text(stringResource(R.string.start_date_label)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Sprawdź w Logcat, czy ten komunikat się pojawia po kliknięciu
                        Log.d("AddEditEventScreen", "Kliknięto pole daty rozpoczęcia!")
                        showDatePickerStart.value = true
                    },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.select_start_date),
                        modifier = Modifier.clickable {
                            Log.d("AddEditEventScreen", "Kliknięto ikonę plusa!")
                            showDatePickerStart.value = true
                        }
                    )
                }
                ,
                isError = userMessage?.contains("Proszę wybrać datę rozpoczęcia") == true ||
                        userMessage?.contains("Data zakończenia nie może być wcześniejsza") == true
            )
            // DatePickerDialog dla daty rozpoczęcia
            if (showDatePickerStart.value) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerStart.value = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerStateStart.selectedDateMillis?.let { millis ->
                                    // Konwertuj milisekundy na LocalDate
                                    val selectedDate = Instant.fromEpochMilliseconds(millis)
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    viewModel.onDataRozpoczeciaChange(selectedDate)
                                }
                                showDatePickerStart.value = false
                            }
                        ) {
                            Text(stringResource(R.string.confirm)) // TODO: Dodaj string
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerStart.value = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerStateStart)
                }
            }

            // Pole Data zakończenia z DatePickerDialog
            OutlinedTextField(
                value = dataZakonczenia?.toString() ?: "",
                onValueChange = { /* Tylko do wyświetlania, zmiana przez DatePicker */ },
                label = { Text(stringResource(R.string.end_date_label)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Log.d("AddEditEventScreen", "Kliknięto pole daty zakończenia!")
                        showDatePickerEnd.value = true
                    },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.select_end_date),
                        modifier = Modifier.clickable {
                            Log.d("AddEditEventScreen", "Kliknięto ikonę plusa daty zakończenia!")
                            showDatePickerEnd.value = true
                        }
                    )
                },
                isError = userMessage?.contains("Proszę wybrać datę zakończenia") == true ||
                        userMessage?.contains("Data zakończenia nie może być wcześniejsza") == true
            )

            // DatePickerDialog dla daty zakończenia
            if (showDatePickerEnd.value) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerEnd.value = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerStateEnd.selectedDateMillis?.let { millis ->
                                    val selectedDate = Instant.fromEpochMilliseconds(millis)
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    viewModel.onDataZakonczeniaChange(selectedDate)
                                }
                                showDatePickerEnd.value = false
                            }
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerEnd.value = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerStateEnd)
                }
            }

            // Pole Opis wydarzenia
            OutlinedTextField(
                value = opisWydarzenia ?: "",
                onValueChange = viewModel::onOpisWydarzeniaChange,
                label = { Text(stringResource(R.string.event_description_label)) }, // TODO: Dodaj string
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                singleLine = false,
                maxLines = 6
            )

            Spacer(modifier = Modifier.weight(1f)) // Rozpychacz, by przyciski były na dole

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Przycisk Anuluj
                Button(
                    onClick = { navController.navigateUp() }, // Po prostu wracamy
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Przycisk Zapisz
                Button(
                    onClick = viewModel::saveEvent,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading // Wyłącz przycisk podczas ładowania
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.save_event_button)) // TODO: Dodaj string
                    }
                }
            }
        }
    }
}

// --- Preview dla AddEditEventScreen ---
@Preview(showBackground = true)
@Composable
fun AddEditEventScreenPreview() {
    PackUp_podejscie_3Theme {
        AddEditEventScreen(navController = rememberNavController())
    }
}