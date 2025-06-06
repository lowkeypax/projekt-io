package com.example.packup_podejscie_3.ui.screens.eventdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.domain.model.Lista_zadan
import com.example.packup_podejscie_3.ui.navigation.NewListaZadanScreenDestination // Będziemy potrzebować tej destynacji
import com.example.packup_podejscie_3.viewmodel.ListaZadanListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaZadanListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ListaZadanListViewModel = hiltViewModel()
) {
    val listaZadan by viewModel.listaZadan.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Obsługa komunikatów Snackbar
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.userMessageShown() // Zresetuj komunikat po wyświetlenu
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
                        text = stringResource(R.string.task_list_title), // TODO: Dodaj ten string
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Nawigacja do ekranu dodawania nowego zadania
                // Przekazujemy ID wydarzenia, bo nowe zadanie musi do niego należeć
                val eventId = navController.currentBackStackEntry?.arguments?.getString("eventId") // Odczytaj eventId z argumentów
                if (eventId != null) {
                    navController.navigate(NewListaZadanScreenDestination.createRouteWithParam(eventId))
                } else {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Błąd: Brak ID wydarzenia do dodania zadania.") }
                }
            }) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_new_task)) // TODO: Dodaj ten string
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listaZadan.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_tasks_available), // TODO: Dodaj ten string
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = listaZadan, key = { it.nazwa + it.wydarzenieId + it.userId }) { zadanie ->
                        TaskListItem(
                            zadanie = zadanie,
                            onCompletedChange = { isChecked ->
                                viewModel.onTaskCompletedChange(zadanie, isChecked)
                            },
                            onDeleteClick = {
                                viewModel.onDeleteTask(zadanie)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskListItem(
    zadanie: Lista_zadan,
    onCompletedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = zadanie.nazwa,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                zadanie.opis?.let {
                    if (it.isNotBlank()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Checkbox(
                checked = zadanie.czyWykonano,
                onCheckedChange = onCompletedChange,
                enabled = true // Zawsze można zmienić status
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_task), // TODO: Dodaj ten string
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}