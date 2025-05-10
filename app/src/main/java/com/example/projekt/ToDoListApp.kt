@file:Suppress("FunctionName")
package com.example.projekt

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolistapp.data.DataSource
import com.example.todolistapp.database.ToDoRepository
import com.example.todolistapp.ui.AddEditScreen
import com.example.todolistapp.ui.LoginScreen
import com.example.todolistapp.ui.ToDoListScreen
import kotlinx.coroutines.launch

enum class ToDoAppDestinations(@StringRes val title: Int) {
    List(title = R.string.list_screen_title),
    Add(title = R.string.add_screen_title),
    Edit(title = R.string.edit_screen_title),
    Login(title = R.string.login_screen_title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListTopBar(
    currentScreen: ToDoAppDestinations,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = currentScreen.title)) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        modifier = modifier.padding(start = 8.dp, end = 8.dp)
    )
}


@Composable
fun ToDoListApp (repository: ToDoRepository, navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenName = backStackEntry?.destination?.route?.substringBefore("/")
    val currentScreen = ToDoAppDestinations.valueOf(
        screenName ?: ToDoAppDestinations.List.name
    )
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ToDoListTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(ToDoAppDestinations.Add.name)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ToDoAppDestinations.Login.name,
            modifier = Modifier
                .fillMaxSize() s
                .padding(innerPadding)
        ) {
            composable(route = ToDoAppDestinations.Login.name) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(ToDoAppDestinations.List.name) {
                        popUpTo(ToDoAppDestinations.Login.name) { inclusive = true }
                    }
                })
            }
            composable(route = ToDoAppDestinations.List.name) {
                ToDoListScreen(repository = repository,
                    onEdit = { taskToEdit ->
                        navController.navigate("${ToDoAppDestinations.Edit.name}/${taskToEdit.taskId}")
                    }
                )
            }
            composable(route = ToDoAppDestinations.Add.name) {
                AddEditScreen(
                    onSave = {
                        coroutineScope.launch {
                            repository.addTask(it)
                        }
                        navController.navigateUp()
                    },
                    onCancel = {
                        navController.navigateUp()
                    })
            }
            composable(
                route = ToDoAppDestinations.Edit.name + "/{taskToEditId}",
                arguments = listOf(navArgument(name = "taskToEditId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val taskToEditId = backStackEntry.arguments?.getLong("taskToEditId")
                taskToEditId ?: return@composable
                val task by repository.getTaskById(taskToEditId).collectAsState(initial = null)
                if (task == null) {
                    // Show a loading screen while waiting for the task
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AddEditScreen(
                        task = task,
                        onSave = {
                            coroutineScope.launch {
                                repository.updateTask(it)
                            }
                            navController.navigateUp()
                        },
                        onCancel = {
                            navController.navigateUp()
                        })
                }
            }
        }
    }

}

@Preview
@Composable
fun ToDoListAppPreview() {
    //ToDoListApp( )
}


private fun sendList(context: Context, listString: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "ToDo List")
        putExtra(Intent.EXTRA_TEXT, listString)
    }
    context.startActivity(intent)
}