package com.example.packup_podejscie_3.ui.screens.addscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.* // Używamy tylko Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R // Upewnij się, że masz R
import com.example.packup_podejscie_3.viewmodel.NewPollViewModel // Poprawny import ViewModelu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPollScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NewPollViewModel = hiltViewModel() // Wstrzykiwanie ViewModelu
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Obserwuj stany z ViewModelu
    val question by viewModel.question.collectAsState()
    val options by viewModel.options.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val pollAddedSuccessfully by viewModel.pollAddedSuccessfully.collectAsState()

    // Efekt do obsługi Snackbarów i nawigacji po dodaniu ankiety
    LaunchedEffect(pollAddedSuccessfully) {
        when (pollAddedSuccessfully) {
            true -> {
                snackBarHostState.showSnackbar(
                    message = "Ankieta dodana pomyślnie!",
                    duration = SnackbarDuration.Short
                )
                // Opcjonalnie: nawiguj wstecz po sukcesie
                navController.popBackStack()
                viewModel.resetPollAddedState()
            }
            false -> {
                snackBarHostState.showSnackbar(
                    message = "Błąd podczas dodawania ankiety. Sprawdź poprawność danych.",
                    duration = SnackbarDuration.Long
                )
                viewModel.resetPollAddedState()
            }
            null -> { /* Brak akcji */ }
        }
    }

    Scaffold( // Material 3 Scaffold
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }, // SnackbarHost również z Material 3
        topBar = {
            TopAppBar( // Material 3 TopAppBar
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back), // Użyj stringResource
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.new_poll_screen_title), // Użyj stringResource
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors( // Material 3 TopAppBar colors
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary // Kolor ikony nawigacji
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally // Centrowanie, jeśli chcesz
        ) {
            if (isLoading) {
                // Wyświetl wskaźnik ładowania, gdy operacja jest w toku
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // Material 3 CircularProgressIndicator
                Text(
                    text = "Dodawanie ankiety...", // Możesz to przenieść do strings.xml
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Column(modifier = Modifier.weight(1f)) { // Użyj weight, aby górna sekcja zajęła dostępną przestrzeń
                    Text(
                        text = stringResource(R.string.new_poll_screen_title), // Użyj stringResource
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField( // Material 3 OutlinedTextField
                        value = question,
                        onValueChange = { viewModel.onQuestionChange(it) }, // Użyj ViewModelu
                        label = { Text("Treść pytania") }, // Możesz użyć stringResource
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Odpowiedzi", // Możesz użyć stringResource
                        style = MaterialTheme.typography.titleMedium
                    )

                    options.forEachIndexed { index, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField( // Material 3 OutlinedTextField
                                value = option,
                                onValueChange = { viewModel.onOptionChange(index, it) }, // Użyj ViewModelu
                                label = { Text("Odpowiedź ${index + 1}") }, // Możesz użyć stringResource
                                modifier = Modifier.weight(1f)
                            )
                            // Logika usuwania opcji - delegujemy do ViewModelu
                            if (options.size > 2) { // Minimalnie 2 opcje
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { viewModel.removeOption(index) }) {
                                    Text("Usuń") // Możesz użyć stringResource
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Logika dodawania opcji - delegujemy do ViewModelu
                    if (options.size < 4) { // Maksymalnie 4 opcje
                        TextButton(onClick = { viewModel.addOption() }) {
                            Text("Dodaj odpowiedź") // Możesz użyć stringResource
                        }
                    }
                }

                Button( // Material 3 Button
                    onClick = {
                        viewModel.createPoll()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = !isLoading // Wyłącz przycisk podczas ładowania
                ) {
                    Text("Dodaj ankietę") // Możesz użyć stringResource
                }
            }
        }
    }
}