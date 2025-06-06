package com.example.packup_podejscie_3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun MultiSelectCalendar() {
    var selectedDates by remember { mutableStateOf(setOf<LocalDate>()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysOfWeek = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd")

    Column(modifier = Modifier.padding(16.dp)) {
        // Nagłówek z nazwą miesiąca i strzałkami
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Poprzedni miesiąc")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Następny miesiąc")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nagłówki dni tygodnia
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val firstDayOfMonth = currentMonth.atDay(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7) // pon=1 → 1%7 = 1, ndz=7 → 0

        val totalCells = ((firstDayOfWeek - 1) + daysInMonth + 6) / 7 * 7

        Column {
            for (week in 0 until totalCells step 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayIndex in 0..6) {
                        val cell = week + dayIndex - (firstDayOfWeek - 1)
                        val day = if (cell in 1..daysInMonth) currentMonth.atDay(cell) else null
                        val isSelected = day != null && selectedDates.contains(day)

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clickable(enabled = day != null) {
                                    day?.let {
                                        selectedDates = if (it in selectedDates)
                                            selectedDates - it
                                        else
                                            selectedDates + it
                                    }
                                }
                                .background(
                                    color = if (isSelected) Color(0xFF6C63FF) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(1.dp, Color.LightGray, CircleShape)
                        ) {
                            Text(
                                text = day?.dayOfMonth?.toString() ?: "",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Zaznaczone dni:")
        selectedDates.sorted().forEach {
            Text("- ${it}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MultiSelectCalendarPreview() {
    MultiSelectCalendar()
}
