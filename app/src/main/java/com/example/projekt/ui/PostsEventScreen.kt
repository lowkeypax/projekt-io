package com.example.projekt.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.todolistapp.data.Task

@Composable
fun PostsEventScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Event Posts")
    }
    //Text("Event: ${task?.name ?: "Unknown"}")
}

//@Composable
//fun PostsEventScreenWrapper(navController: NavController) {
//    val sharedViewModel = remember { SharedEventViewModel() }
//    val task by sharedViewModel.selectedTask.collectAsState()
//
//    if (task != null) {
//        PostsEventScreen(task = task!!, navController = navController)
//    } else {
//        CircularProgressIndicator() // Ładowanie
//    }
//}