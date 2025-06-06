package com.example.packup_podejscie_3.ui.screens.eventdetail

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.packup_podejscie_3.R
import com.example.packup_podejscie_3.ui.components.AnnouncementItem
import com.example.packup_podejscie_3.viewmodel.AnnouncementBoardViewModel

// Poprawne importy destynacji
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination // <-- Poprawiony import dla EventDetailsScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewAnnouncementScreenDestination // <-- Poprawiony import dla NewAnnouncementScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewPollScreenDestination // <-- Poprawiony import dla NewPollScreenDestination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementBoardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AnnouncementBoardViewModel = hiltViewModel()
) {
    val announcements by viewModel.announcements.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.userMessageShown()
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
                        text = stringResource(R.string.announcement_board_title),
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.add_poll)) },
                    icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_poll_content_description)) },
                    onClick = {
                        val currentEventId = navController.currentBackStackEntry
                            ?.arguments?.getString(EventDetailsScreenDestination.eventId)
                            ?: throw IllegalStateException("Event ID must be present in NavGraph")
                        // Użyj createRouteWithParam z obiektu destynacji
                        navController.navigate(NewPollScreenDestination.createRouteWithParam(currentEventId))
                    }
                )
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.add_announcement)) },
                    icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_announcement_content_description)) },
                    onClick = {
                        val currentEventId = navController.currentBackStackEntry
                            ?.arguments?.getString(EventDetailsScreenDestination.eventId)
                            ?: throw IllegalStateException("Event ID must be present in NavGraph")
                        // Użyj createRouteWithParam z obiektu destynacji
                        navController.navigate(NewAnnouncementScreenDestination.createRouteWithParam(currentEventId))
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                announcements.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.no_announcements_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = announcements, key = { it.nazwa + it.wydarzenieId }) { announcement ->
                            AnnouncementItem(
                                ogloszenie = announcement,
                                onDeleteClick = {
                                    viewModel.onDeleteAnnouncement(announcement)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}