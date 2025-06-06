package com.example.packup_podejscie_3.ui.screens.eventdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.AttachMoney
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
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme
import com.example.packup_podejscie_3.viewmodel.EventDetailsViewModel
import kotlinx.datetime.LocalDate

// Import całego pakietu navigation, by odwoływać się przez np. AddEditEventScreenDestination.route
import com.example.packup_podejscie_3.ui.navigation.*

// --- Główny ekran szczegółów wydarzenia (EventDetailsScreen) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: EventDetailsViewModel = hiltViewModel() // Wstrzyknięcie ViewModelu
) {
    val event by viewModel.event.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.userMessageShown()
        }
    }

    val currentEventId = remember(event) { event?.id }

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
                        text = event?.nazwa ?: stringResource(R.string.event_details_title),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    event?.id?.let { eventId ->
                        IconButton(onClick = {
                            navController.navigate(AddEditEventScreenDestination.createRouteWithParam(eventId))
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_event),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            currentEventId?.let { eventId ->
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate(ListaZadanListScreenDestination.createRouteWithParam(eventId))
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = "Lista Zadań")
                            Text("Zadania", style = MaterialTheme.typography.labelSmall)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate(AnnouncementBoardScreenDestination.createRouteWithParam(eventId))
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Ogłoszenia")
                            Text("Ogłoszenia", style = MaterialTheme.typography.labelSmall)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate(AnkietyListScreenDestination.createRouteWithParam(eventId))
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Poll, contentDescription = "Ankiety")
                            Text("Ankiety", style = MaterialTheme.typography.labelSmall)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate(MoneyEventScreenDestination.createRouteWithParam(eventId))
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.AttachMoney, contentDescription = "Wydatki")
                            Text("Wydatki", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                event != null -> {
                    Text(
                        text = event!!.nazwa,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Od: ${event!!.data_rozpoczecia.toString()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Do: ${event!!.data_zakonczenia.toString()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    event!!.opis_wydarzenia?.let {
                        if (it.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.event_not_found_or_error),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}