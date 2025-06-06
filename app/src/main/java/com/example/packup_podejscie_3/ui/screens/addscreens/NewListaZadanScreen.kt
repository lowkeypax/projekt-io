package com.example.packup_podejscie_3.ui.screens.addscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.viewmodel.NewListaZadanViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewListaZadanScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NewListaZadanViewModel = hiltViewModel()
) {
    val nazwa by viewModel.nazwa.collectAsState()
    val opis by viewModel.opis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current // Możesz to usunąć, jeśli wolisz, ale to jest już zaimplementowane

    // Obsługa komunikatów i nawigacji po zapisie
    LaunchedEffect(saveSuccess, userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.resetSaveState() // Resetuj stan komunikatu
        }
        if (saveSuccess == true) {
            navController.navigateUp() // Wróć do poprzedniego ekranu (listy zadań)
            viewModel.resetSaveState() // Resetuj stan sukcesu
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
                        text = stringResource(R.string.new_task_title), // TODO: Dodaj ten string
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
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nazwa,
                onValueChange = { viewModel.onNazwaChange(it) },
                label = { Text(stringResource(R.string.task_name_label)) }, // TODO: Dodaj ten string
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = opis ?: "",
                onValueChange = { viewModel.onOpisChange(it) },
                label = { Text(stringResource(R.string.task_description_label)) }, // TODO: Dodaj ten string
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    localSoftwareKeyboardController?.hide() // Schowaj klawiaturę
                    viewModel.saveZadanie()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.save_task_button), color = MaterialTheme.colorScheme.onPrimary) // TODO: Dodaj ten string
                }
            }
        }
    }
}