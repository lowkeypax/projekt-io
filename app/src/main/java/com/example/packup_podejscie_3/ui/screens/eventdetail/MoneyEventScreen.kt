package com.example.packup_podejscie_3.ui.screens.eventdetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.packup_podejscie_3.domain.model.Wydatki
import com.example.packup_podejscie_3.ui.navigation.NewExpenseScreenDestination
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme
import com.example.packup_podejscie_3.viewmodel.MoneyEventViewModel
import kotlinx.coroutines.launch

// --- Główny ekran wydatków (MoneyEventScreen) ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MoneyEventScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MoneyEventViewModel = hiltViewModel(),
) {
    val wydatki by viewModel.wydatki.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val currentEventId by viewModel.wydarzenieId.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Pobieramy string z zasobów przed blokiem launch
    val errorNoEventIdMessage = stringResource(R.string.error_no_event_id)

    LaunchedEffect(userMessage) {
        userMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(message = it)
                viewModel.userMessageShown()
            }
        }
    }

    val screenTitle = currentEventId?.let { "Wydatki dla wydarzenia: $it" } ?: "Wydatki"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                title = {
                    Text(
                        text = screenTitle,
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.add_expense)) },
                    icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_expense)) },
                    onClick = {
                        if (currentEventId != null) {
                            navController.navigate(NewExpenseScreenDestination.createRouteWithParam(currentEventId!!))
                        } else {
                            scope.launch {
                                // Używamy wcześniej pobranego stringu
                                snackbarHostState.showSnackbar(errorNoEventIdMessage)
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                wydatki.isEmpty() && !isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.no_expenses_yet),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(wydatki) { wydatek ->
                            WydatekCard(
                                wydatek = wydatek,
                                onEditClick = { wydatekId ->
                                    if (currentEventId != null) {
                                        navController.navigate(
                                            NewExpenseScreenDestination.createRouteWithParam(
                                                currentEventId!!,
                                                wydatekId
                                            )
                                        )
                                    } else {
                                        scope.launch {
                                            // Używamy wcześniej pobranego stringu
                                            snackbarHostState.showSnackbar(errorNoEventIdMessage)
                                        }
                                    }
                                },
                                onDelete = { wydatekId ->
                                    viewModel.deleteWydatek(wydatekId)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- Komponent do wyświetlania pojedynczego Wydatku ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WydatekCard(
    wydatek: Wydatki,
    onEditClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onEditClick(wydatek.id) },
                onLongClick = { showDeleteDialog = true }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${"%.2f".format(wydatek.kwota)} zł",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "${wydatek.userId} zapłacił(a)",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = wydatek.nazwaPrzedmiotu,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_expense_title)) },
            text = { Text(stringResource(R.string.delete_expense_message, wydatek.nazwaPrzedmiotu)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(wydatek.id)
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}


// --- Preview dla MoneyEventScreen ---
@Preview(showBackground = true)
@Composable
fun MoneyEventScreenPreview() {
    PackUp_podejscie_3Theme {
        MoneyEventScreen(navController = rememberNavController())
    }
}

// --- Preview dla WydatekCard ---
@Preview(showBackground = true)
@Composable
fun WydatekCardPreview() {
    PackUp_podejscie_3Theme {
        WydatekCard(
            wydatek = Wydatki(
                id = "sample-id-1",
                nazwaPrzedmiotu = "Kawa i ciastko",
                kwota = 35.50,
                wydarzenieId = "event-id-123",
                userId = "user123"
            ),
            onEditClick = {},
            onDelete = {}
        )
    }
}