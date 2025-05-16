@file:Suppress("FunctionName")
package com.example.todolistapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ioapp.ui.screens.components.MultiSelectCalendar
import com.example.todolistapp.data.Task

@Composable
fun EventScreen(
    task: Task? = null,
    navController: NavHostController
) {
    var name by remember { mutableStateOf(task?.name ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
        AboutTripTab(
            participants = listOf("Participant 1", "Participant 2", "Participant 3"),
        ) { MultiSelectCalendar() }
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