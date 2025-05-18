package com.example.todolistapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todolistapp.data.Task

@Composable
fun MoneysEventScreen(
    task: Task? = null,
    navController: NavHostController,
    onCreateExpences: () -> Unit,
) {
    val name = task?.name ?: "Wydatki grupowe"

    val expenses = remember {
        listOf(
            Expense("Anna", "Zakupy spożywcze", 120.0),
            Expense("Paweł", "Paliwo", 180.0),
            Expense("Ewa", "Nocleg", 300.0)
        )
    }

    Scaffold (
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text("wydatek") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Dodaj wydatek") },
                    onClick = onCreateExpences
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(expenses) { expense ->
                    ExpenseCard(expense)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun ExpenseCard(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp) // smaller shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // smaller padding
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Smaller amount box
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
                    text = "${"%.2f".format(expense.amount)} zł",
                    style = MaterialTheme.typography.titleLarge, // slightly smaller text
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Compact text column
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "${expense.paidBy} zapłacił(a)",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



// Data class for expenses
data class Expense(
    val paidBy: String,
    val description: String,
    val amount: Double
)
