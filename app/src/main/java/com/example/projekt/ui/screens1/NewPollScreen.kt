package com.example.ioapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
//fun NewPollScreen(onSubmit: (String, List<String>) -> Unit = { _, _ -> }) {
fun NewPollScreen(onSubmit: (String) -> Unit = {}) {
    var question by remember { mutableStateOf("") }
    var options = remember { mutableStateListOf("", "") }

    fun canAddMore() = options.size < 4
    fun canRemove(index: Int) = options.size > 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Nowa ankieta", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Treść pytania") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Odpowiedzi", style = MaterialTheme.typography.titleMedium)

            options.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = option,
                        onValueChange = { options[index] = it },
                        label = { Text("Odpowiedź ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    if (canRemove(index)) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { options.removeAt(index) }) {
                            Text("Usuń")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (canAddMore()) {
                TextButton(onClick = { options.add("") }) {
                    Text("Dodaj odpowiedź")
                }
            }
        }

        Button(
//            onClick = {
//                onSubmit(question, options.filter { it.isNotBlank() })
//            },
            onClick = {onSubmit(question)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Dodaj ankietę")
        }
    }
}