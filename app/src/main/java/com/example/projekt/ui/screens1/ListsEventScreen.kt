@file:Suppress("FunctionName")
package com.example.todolistapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolistapp.data.Task

@Composable
fun ListsScreen(
    task: Task? = null,
    shoppingLists: List<ShoppingList>,
    onCreateList: () -> Unit,
    onAddItem: (listId: String) -> Unit,
    onCheckChanged: () -> Unit
) {
    val name = task?.name ?: "Zadania"

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
                    onClick = onCreateList
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
                        onAddItem = { onAddItem(list.id) },
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
                    style = MaterialTheme.typography.headlineSmall,
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
