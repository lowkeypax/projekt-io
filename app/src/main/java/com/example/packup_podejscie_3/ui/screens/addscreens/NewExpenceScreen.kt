package com.example.packup_podejscie_3.ui.screens.addscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme
import com.example.packup_podejscie_3.viewmodel.AddEditExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AddEditExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Pobieramy wiadomości o błędach do lokalnych zmiennych, aby uniknąć problemów ze smart cast
    val userIdError = uiState.userIdError
    val nazwaPrzedmiotuError = uiState.nazwaPrzedmiotuError
    val kwotaError = uiState.kwotaError

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.userMessageShown()
            }
        }
    }

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
                        text = if (uiState.isNewExpense) stringResource(R.string.add_new_expense)
                        else stringResource(R.string.edit_expense)
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
        if (uiState.isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.userId,
                    onValueChange = viewModel::onUserIdChange,
                    label = { Text(stringResource(R.string.expense_paid_by)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = userIdError != null, // Używamy lokalnej zmiennej
                    supportingText = {
                        userIdError?.let { // Bezpieczne wywołanie dla tekstu wspierającego
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.nazwaPrzedmiotu,
                    onValueChange = viewModel::onNazwaPrzedmiotuChange,
                    label = { Text(stringResource(R.string.expense_item_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nazwaPrzedmiotuError != null, // Używamy lokalnej zmiennej
                    supportingText = {
                        nazwaPrzedmiotuError?.let { // Bezpieczne wywołanie dla tekstu wspierającego
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.kwota,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                            viewModel.onKwotaChange(newValue)
                        }
                    },
                    label = { Text(stringResource(R.string.expense_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = kwotaError != null, // Używamy lokalnej zmiennej
                    supportingText = {
                        kwotaError?.let { // Bezpieczne wywołanie dla tekstu wspierającego
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(1f)) // Wypełnienie przestrzeni

                // Przycisk "Dodaj wydatek" / "Aktualizuj wydatek"
                Button(
                    onClick = {
                        viewModel.saveExpense {
                            navController.navigateUp()
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp) // Zostaw tylko dolny padding, poziomy jest już w Column
                ) {
                    Text(text = if (uiState.isNewExpense) stringResource(R.string.save_expense) else stringResource(R.string.update_expense))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun AddEditExpenseScreenPreview() {
    PackUp_podejscie_3Theme {
        AddEditExpenseScreen(navController = rememberNavController())
    }
}