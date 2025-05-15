package com.example.ioapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.ioapp.ui.screens.components.AvailabilityCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventName: String,
    participants: List<String>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("O wyjeździe") }
    val tabItems = listOf("O wyjeździe", "Ogłoszenia", "Listy", "Wydatki")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(eventName) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: open drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            // Tabs
            TabRow(selectedTabIndex = tabItems.indexOf(selectedTab)) {
                tabItems.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == title,
                        onClick = { selectedTab = title },
                        text = { Text(title) }
                    )
                }
            }

            // Content
            when (selectedTab) {
                "O wyjeździe" -> AboutTripTab(participants) {
                    //AvailabilityCalendar()
                }
                "Ogłoszenia" -> PlaceholderTab("Ogłoszenia")
                "Listy" -> PlaceholderTab("Listy zadań / rzeczy")
                "Wydatki" -> PlaceholderTab("Wydatki")
            }
        }
    }
}

@Composable
fun AboutTripTab(
    participants: List<String>,
    content: @Composable () -> Unit // <- przyjmujemy kalendarz jako slot
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📅 Kalendarz", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        content() // <- tutaj wyświetlamy AvailabilityCalendar

        Spacer(modifier = Modifier.height(16.dp))

        Text("Uczestnicy:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(participants) { participant ->
                ListItem(
                    headlineContent = { Text(participant) }
                )
                //Divider()
            }
        }
    }
}

@Composable
fun PlaceholderTab(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("$title – zawartość wkrótce", fontSize = 18.sp, textAlign = TextAlign.Center)
    }
}
