package com.example.projekt.ui.screens1.addScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
//fun AddExpenseScreen(
//    onSubmit: (paidBy: String, description: String, amount: Double) -> Unit = { _, _, _ -> }
//) {
fun AddExpenseScreen(onSubmit: (String) -> Unit = {}) {
    var paidBy by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Nowy wydatek", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = paidBy,
                onValueChange = { paidBy = it },
                label = { Text("Osoba płacąca") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis wydatku") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { newText ->
                    // Allow only valid decimal numbers
                    if (newText.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        amountText = newText
                    }
                },
                label = { Text("Kwota") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Button(
//            onClick = {
//                val amount = amountText.toDoubleOrNull() ?: 0.0
//                onSubmit(paidBy.trim(), description.trim(), amount)
//            },
            onClick = { onSubmit("x") },
            enabled = paidBy.isNotBlank() && description.isNotBlank() && amountText.toDoubleOrNull() != null && amountText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Dodaj wydatek")
        }
    }
}
