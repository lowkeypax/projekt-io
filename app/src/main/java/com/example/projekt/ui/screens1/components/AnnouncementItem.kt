package com.example.ioapp.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class AnnouncementType { TEXT, POLL }

data class UserAnnouncement(
    val author: String,
    val time: String,
    val content: String,
    val type: AnnouncementType = AnnouncementType.TEXT,
    val options: List<String> = emptyList()
)

@Composable
fun AnnouncementItem(announcement: UserAnnouncement) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = announcement.author, style = MaterialTheme.typography.titleSmall)
            Text(text = announcement.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = announcement.content, style = MaterialTheme.typography.bodyMedium)

            if (announcement.type == AnnouncementType.POLL) {
                Spacer(modifier = Modifier.height(8.dp))
                val selectedOption = remember { mutableStateOf<String?>(null) }

                Column {
                    announcement.options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedOption.value == option,
                                onClick = { selectedOption.value = option }
                            )
                            Text(text = option, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
