package ui.screens

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
import androidx.navigation.NavController
import com.example.ioapp.ui.screens.components.AnnouncementItem
import com.example.ioapp.ui.screens.components.AnnouncementType
import com.example.ioapp.ui.screens.components.UserAnnouncement
import com.example.todolistapp.data.Task


@Composable
fun AnnouncementBoardScreen(
    task: Task? = null,
    onCreateAnnouncement: () -> Unit,
    onCreatePoll: () -> Unit,
) {
    var name by remember { mutableStateOf(task?.name ?: "") }
    val announcements = remember {
        listOf(
            UserAnnouncement(
                "Anna Kowalska",
                "13:12",
                "Cześć! Czy wszystkim pasuje weekend 10–12 maja?"
            ),
            UserAnnouncement(
                "Paweł Nowak",
                "13:40",
                "Zróbmy zakupy w piątek wieczorem przed wyjazdem."
            ),
            UserAnnouncement(
                "Ewa Zielińska", "14:05", "Kiedy wyjazd?",
                type = AnnouncementType.POLL,
                options = listOf("10–12 maja", "17–19 maja", "24–26 maja")
            )
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
                    text = { Text("ankieta") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Dodaj ankietę") },
                    onClick = onCreatePoll
                )
                ExtendedFloatingActionButton(
                    text = { Text("ogłoszenie") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Dodaj ogłoszenie") },
                    onClick = onCreateAnnouncement
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
            if (name.isNotBlank()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(announcements) { announcement ->
                    AnnouncementItem(announcement)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}