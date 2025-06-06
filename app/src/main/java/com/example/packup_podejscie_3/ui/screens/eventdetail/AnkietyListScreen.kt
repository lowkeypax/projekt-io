// app/src/main/java/com/example/packup_podejscie_3/ui/screens/ankiety/AnkietyListScreen.kt
package com.example.packup_podejscie_3.ui.screens.eventdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.domain.model.Ankiety
import com.example.packup_podejscie_3.ui.navigation.AnkietyDetailsDestination // WAŻNE: Dodany import dla AnkietyDetailsDestination
// Poprawne importy destynacji, które będą używać eventId
import com.example.packup_podejscie_3.ui.navigation.AnkietyListScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewPollScreenDestination
// Poprawny import ViewModelu
import com.example.packup_podejscie_3.viewmodel.AnkietyListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


// Composable dla pojedynczego elementu listy ankiet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnkietyListItem(
    ankiety: Ankiety,
    modifier: Modifier = Modifier,
    onClick: (Ankiety) -> Unit, // Przyjmuje Ankiety
    onDelete: (Ankiety) -> Unit, // Dodana obsługa usuwania (np. przez IconButton)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = { onClick(ankiety) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = ankiety.pytanie,
                style = MaterialTheme.typography.headlineSmall,
            )
            // Użycie stringResource z parametrem
            Text(
                text = stringResource(R.string.ankiety_list_item_event_id, ankiety.wydarzenieId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(
                onClick = { onDelete(ankiety) },
                modifier = Modifier.align(Alignment.End) // Wyrównaj do prawej
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_icon_description),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnkietyListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    eventId: String, // eventId jest przekazywane z nawigacji do tego Composable
    viewModel: AnkietyListViewModel = hiltViewModel(),
) {
    // Ponieważ ViewModel przyjmuje eventId przez SavedStateHandle i w init pobiera dane,
    // nie musimy jawnie przekazywać eventId do ViewModelu tutaj.
    // ViewModel jest tworzony przez Hilt i SavedStateHandle jest wstrzykiwane.

    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    // Upewniamy się, że ankietyList nigdy nie będzie null
    val ankietyListState by viewModel.ankietyList.collectAsState(initial = emptyList())
    val ankietyList = ankietyListState ?: emptyList() // Zapewnia, że ankietyList nigdy nie będzie null

    Scaffold( // Material 3 Scaffold
        topBar = {
            TopAppBar( // Material 3 TopAppBar
                title = {
                    Text(
                        text = stringResource(R.string.ankiety_list_screen_title),
                        color = MaterialTheme.colorScheme.onPrimary,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            AddAnkietyButton(onClick = {
                // eventId jest dostępne jako parametr Composable AnkietyListScreen
                navController.navigate(NewPollScreenDestination.createRouteWithParam(eventId))
            })
        }
    ) { padding ->
        SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.getAnkietyForEvent() }) {
            if (isLoading && ankietyList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.loading_ankiety_message))
                }
            } else if (ankietyList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_ankiety_for_event_message))
                }
            } else {
                LazyColumn(
                    modifier = modifier.padding(padding),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    // Tutaj przekazujemy ankietyList, które na pewno nie jest null
                    itemsIndexed(
                        items = ankietyList,
                        key = { _, ankieta -> ankieta.id ?: ankieta.pytanie }
                    ) { _, item ->
                        AnkietyListItem(
                            ankiety = item,
                            modifier = modifier,
                            onClick = { ankieta ->
                                ankieta.id?.let {
                                    navController.navigate(AnkietyDetailsDestination.createRouteWithParam(it))
                                }
                            },
                            onDelete = { ankieta ->
                                viewModel.removeAnkiety(ankieta)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAnkietyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier = modifier.padding(16.dp),
        onClick = onClick,
        icon = { Icon(Icons.Filled.Add, stringResource(R.string.add_icon_description)) },
        text = { Text(stringResource(R.string.add_ankiety_button_text)) },
    )
}