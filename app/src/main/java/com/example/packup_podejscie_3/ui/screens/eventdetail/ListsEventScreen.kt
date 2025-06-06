@file:Suppress("FunctionName")
package com.example.packup_podejscie_3.ui.screens.eventdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.packup_podejscie_3.doogarniecia.Task

@Composable
fun ListsScreen(
    task: Task? = null,
    shoppingLists: List<ShoppingList>,
    onCreateList: (String) -> Unit = {},
    onAddItem: (listId: String, itemLabel: String, avatar: String?) -> Unit = { _, _, _ -> },
    onCheckChanged: () -> Unit
) {
    val name = task?.name ?: "Zadania"

    var showListDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    var showItemDialog by remember { mutableStateOf<String?>(null) } // holds listId
    var newItemLabel by remember { mutableStateOf("") }
    var newItemAvatar by remember { mutableStateOf("") }

    if (showListDialog) {
        AlertDialog(
            onDismissRequest = { showListDialog = false },
            title = { Text("Nowa lista") },
            text = {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Nazwa listy") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            onCreateList(newListName)
                            newListName = ""
                            showListDialog = false
                        }
                    }
                ) {
                    Text("Utwórz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showListDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    showItemDialog?.let { listId ->
        AlertDialog(
            onDismissRequest = { showItemDialog = null },
            title = { Text("Nowy element") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = newItemLabel,
                        onValueChange = { newItemLabel = it },
                        label = { Text("Nazwa elementu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = newItemAvatar,
                        onValueChange = { newItemAvatar = it },
                        label = { Text("Inicjały osoby (opcjonalne)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newItemLabel.isNotBlank()) {
                            onAddItem(listId, newItemLabel, newItemAvatar.ifBlank { null })
                            newItemLabel = ""
                            newItemAvatar = ""
                            showItemDialog = null
                        }
                    }
                ) {
                    Text("Dodaj")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showItemDialog = null
                }) {
                    Text("Anuluj")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text("lista") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Dodaj liste") },
                    onClick = { showListDialog = true }
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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(shoppingLists, key = { it.id }) { list ->
                    ShoppingListCard(
                        list = list,
                        onAddItem = { showItemDialog = list.id },
                        onCheckChanged = onCheckChanged
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListCard(
    list: ShoppingList,
    onAddItem: () -> Unit,
    onCheckChanged: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Button(onClick = onAddItem, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("+ CREATE")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            list.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (item.avatar != null) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        item.avatar,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(item.label, style = MaterialTheme.typography.bodyMedium)
                    }
                    Checkbox(
                        checked = item.checked,
                        onCheckedChange = { }
                    )
                }
            }
        }
    }
}

// Data classes

data class ShoppingList(
    val id: String,
    val name: String,
    val items: List<ShoppingItem>
)

data class ShoppingItem(
    val id: String,
    val label: String,
    val avatar: String? = null,
    val checked: Boolean = false
)
