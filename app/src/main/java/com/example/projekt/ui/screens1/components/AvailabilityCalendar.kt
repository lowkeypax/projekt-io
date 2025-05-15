//package com.example.ioapp.ui.screens.components
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import java.time.LocalDate
//
//@Composable
//fun AvailabilityCalendar() {
//    val calendarState = rememberCalendarState()
//    val selectedDates = remember { mutableStateListOf<LocalDate>() }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text("Zaznacz dostępne dni", style = MaterialTheme.typography.titleMedium)
//
//        ComposeCalendar(
//            calendarState = calendarState,
//            dayContent = { dayState ->
//                val date = dayState.date
//                val isSelected = selectedDates.contains(date)
//
//                Surface(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .padding(2.dp),
//                    color = when {
//                        isSelected -> Color(0xFF6C63FF)
//                        else -> Color.Transparent
//                    },
//                    shape = MaterialTheme.shapes.small,
//                    onClick = {
//                        if (isSelected) {
//                            selectedDates.remove(date)
//                        } else {
//                            selectedDates.add(date)
//                        }
//                    }
//                ) {
//                    Box(contentAlignment = Alignment.Center) {
//                        Text(
//                            text = date.dayOfMonth.toString(),
//                            style = MaterialTheme.typography.bodySmall,
//                            color = if (isSelected) Color.White else Color.Black
//                        )
//                    }
//                }
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text("Twoje dostępne dni:")
//        selectedDates.forEach {
//            Text(it.toString())
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            // TODO: Wyślij listę `selectedDates` do backendu
//        }) {
//            Text("Zapisz dostępność")
//        }
//    }
//}