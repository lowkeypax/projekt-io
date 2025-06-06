// app/src/main/java/com/example/packup_podejscie_3/ui/screens/ankiety/AnkietyDetailsScreen.kt
package com.example.packup_podejscie_3.ui.screens.eventdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.viewmodel.AnkietyDetailsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnkietyDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AnkietyDetailsViewModel = hiltViewModel(),
) {
    // Poprawki tutaj: dodano initial = ...
    val pytanie by viewModel.pytanie.collectAsState(initial = "") // Początkowa wartość dla pytania
    val isLoading by viewModel.isLoading.collectAsState(initial = false) // Początkowa wartość dla ładowania
    val saveSuccessful by viewModel.saveSuccessful.collectAsState(initial = null) // Początkowa wartość dla statusu zapisu

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val successMessage = stringResource(R.string.ankieta_zapisana_pomyslnie)
    val errorMessage = stringResource(R.string.blad_zapisu_ankiety)


    LaunchedEffect(saveSuccessful) {
        when (saveSuccessful) {
            true -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = successMessage // Teraz przekazujemy już gotowy String
                    )
                }
                viewModel.resetSaveState()
                navController.popBackStack()
            }
            false -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = errorMessage // Teraz przekazujemy już gotowy String
                    )
                }
                viewModel.resetSaveState()
            }
            null -> { /* Nic nie rób */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ankiety_details_screen_title),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onSaveAnkiety() },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = stringResource(R.string.save),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = pytanie,
                    onValueChange = viewModel::onQuestionChange,
                    label = { Text(stringResource(R.string.poll_question_label)) },
                    singleLine = false,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    enabled = !isLoading
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}