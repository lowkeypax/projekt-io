package com.example.packup_podejscie_3.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete // Dodano ikonę usuwania
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource // Do stringResource
import androidx.compose.ui.unit.dp
import com.example.packup_podejscie_3.R // Upewnij się, że masz R.string.
import com.example.packup_podejscie_3.domain.model.Ogloszenie // Importuj nasz model Ogloszenie

@Composable
fun AnnouncementItem(
    ogloszenie: Ogloszenie, // Używamy naszego modelu Ogloszenie
    onDeleteClick: () -> Unit, // Callback do obsługi usunięcia ogłoszenia
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, // Wyśrodkowanie w pionie
            horizontalArrangement = Arrangement.SpaceBetween // Rozłożenie elementów na boki
        ) {
            Column(modifier = Modifier.weight(1f)) { // Kolumna na tekst ogłoszenia
                Text(
                    text = ogloszenie.nazwa, // Nazwa ogłoszenia jako tytuł
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ogloszenie.opis?.let {
                    if (it.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it, // Opis ogłoszenia
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Jeśli chcesz dodać autora lub czas, musiałbyś rozszerzyć model Ogloszenie
                // np. val author: String? i val timestamp: Long?
                // Text(text = "Autor: ${ogloszenie.author}", style = MaterialTheme.typography.labelSmall)
                // Text(text = "Data: ${formatTimestamp(ogloszenie.timestamp)}", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.width(8.dp)) // Odstęp między tekstem a ikoną usuwania

            // Przycisk do usuwania ogłoszenia
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.Top) // Wyrównanie ikony do góry
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_announcement), // TODO: Dodaj ten string
                    tint = MaterialTheme.colorScheme.error // Czerwony kolor dla usunięcia
                )
            }
        }
    }
}