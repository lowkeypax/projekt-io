package com.example.packup_podejscie_3.ui.screens.eventlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.domain.model.Wydarzenie
import com.example.packup_podejscie_3.ui.navigation.AddEditEventScreenDestination
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme
import com.example.packup_podejscie_3.viewmodel.EventListViewModel
import kotlinx.datetime.LocalDate // Zachowujemy ten import
// Usunięto: import kotlinx.datetime.format
// Usunięto: import kotlinx.datetime.format.DayOfWeek
// Usunięto: import kotlinx.datetime.format.Month
// Usunięto: import kotlinx.datetime.format.char


// --- Komponent dialogu potwierdzenia usunięcia (dla Wydarzenie) ---
@Composable
fun DeleteEventConfirmationDialog(
    wydarzenie: Wydarzenie,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_event_title)) },
        text = { Text(stringResource(R.string.delete_event_msg, wydarzenie.nazwa)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// --- Komponent elementu listy wydarzeń (EventItem) ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventItem(
    wydarzenie: Wydarzenie,
    onViewDetails: (String) -> Unit, // Przekazujemy ID do szczegółów
    onEditClick: (String) -> Unit,   // Przekazujemy ID do edycji
    onDelete: (Wydarzenie) -> Unit, // Przekazujemy całe wydarzenie do usunięcia
    modifier: Modifier = Modifier
) {
    // Usunięto blok remember { LocalDate.format(...) }
    // Daty będą formatowane domyślnie przez .toString()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .combinedClickable(
                onClick = { onViewDetails(wydarzenie.id) }, // Kliknięcie przenosi do szczegółów
                onLongClick = { onDelete(wydarzenie) } // Długie naciśnięcie dla usunięcia
            ),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium, // Użyj kształtów z motywu
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = wydarzenie.nazwa,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Od: ${wydarzenie.data_rozpoczecia.toString()}", // Używamy .toString()
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Do: ${wydarzenie.data_zakonczenia.toString()}", // Używamy .toString()
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            wydarzenie.opis_wydarzenia?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Możesz dodać tutaj opcjonalny przycisk "Edytuj", jeśli chcesz, by edycja była dostępna bez długiego naciśnięcia
            // Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            //     TextButton(onClick = { onEditClick(wydarzenie.id) }) {
            //         Text(stringResource(R.string.edit))
            //     }
            // }
        }
    }
}

// --- Główny ekran listy wydarzeń (EventListScreen) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel() // Wstrzyknięcie ViewModelu
) {
    // Obserwacja stanów z ViewModelu
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Stan dialogu usuwania
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete: Wydarzenie? by remember { mutableStateOf(null) }

    // Efekt do obsługi komunikatów Snackbar i odświeżania listy
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.userMessageShown() // Zresetuj komunikat po wyświetleniu
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.event_list_title),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AddEditEventScreenDestination.createRouteWithParam(null)) }, // Nawigacja do dodawania nowego wydarzenia
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_event_content_description))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // Zastosuj paddingi ze Scaffold
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                events.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.no_events_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    // Wyświetlanie listy wydarzeń
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Odstępy między elementami
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = events, key = { it.id }) { event ->
                            EventItem(
                                wydarzenie = event,
                                onViewDetails = { eventId ->
                                    navController.navigate(EventDetailsScreenDestination.createRouteWithParam(eventId))
                                },
                                onEditClick = { eventId ->
                                    navController.navigate(AddEditEventScreenDestination.createRouteWithParam(eventId))
                                },
                                onDelete = { eventToDeleteFromList ->
                                    showDeleteDialog = true
                                    eventToDelete = eventToDeleteFromList
                                }
                            )
                        }
                    }
                }
            }

            // Dialog potwierdzenia usunięcia
            if (showDeleteDialog && eventToDelete != null) {
                DeleteEventConfirmationDialog(
                    wydarzenie = eventToDelete!!,
                    onConfirm = {
                        viewModel.onDeleteEvent(eventToDelete!!) // Wywołaj funkcję ViewModelu
                        eventToDelete = null
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        eventToDelete = null
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}

// --- Preview dla EventListScreen ---
@Preview(showBackground = true)
@Composable
fun EventListScreenPreview() {
    PackUp_podejscie_3Theme {
        EventListScreen(navController = rememberNavController())
    }
}

// --- Preview dla EventItem ---
@Preview(showBackground = true)
@Composable
fun EventItemPreview() {
    PackUp_podejscie_3Theme {
        EventItem(
            wydarzenie = Wydarzenie(
                id = "123",
                nazwa = "Przykładowe Wydarzenie",
                data_rozpoczecia = LocalDate(2025, 6, 1),
                data_zakonczenia = LocalDate(2025, 6, 7),
                opis_wydarzenia = "To jest opis przykładowego wydarzenia, które ma miejsce przez kilka dni."
            ),
            onViewDetails = {},
            onEditClick = {},
            onDelete = {}
        )
    }
}