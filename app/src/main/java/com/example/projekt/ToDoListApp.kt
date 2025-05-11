@file:Suppress("FunctionName")
package com.example.todolistapp

import com.example.projekt.viewmodel.TaskViewModel
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projekt.viewmodel.TaskViewModelFactory
import com.example.projekt.ui.AddEditScreen
import com.example.projekt.ui.LoginScreen
import com.example.todolistapp.database.ToDoRepository
import com.example.todolistapp.ui.EventScreen
import com.example.todolistapp.ui.MoneysEventScreen
import com.example.todolistapp.ui.PostsEventScreen
import com.example.todolistapp.ui.ToDoListScreen
import com.example.todolistapp.ui.ToDosEventScreen
import kotlinx.coroutines.launch

enum class ToDoAppDestinations(@StringRes val title: Int) {
    List(title = R.string.list_screen_title),
    Add(title = R.string.add_screen_title),
    Edit(title = R.string.edit_screen_title),
    Login(title = R.string.login_screen_title),
    Event(title = R.string.event_screen_title),

    PostsEvent(title = R.string.post_event_screen_title),
    ToDosEvent(title = R.string.todo_event_screen_title),
    MoneyEvent(title = R.string.money_event_screen_title);

    val showInBottomBar: Boolean
        get() = this in listOf(Event, PostsEvent, ToDosEvent, MoneyEvent)
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
fun BottomBar(
    navController: NavHostController,
    currentRoute: String?,
    taskId: Long?
) {
    NavigationBar {
        ToDoAppDestinations.entries
            .filter { it.showInBottomBar }
            .forEach { destination ->
                val isSubpage = destination in listOf(
                    ToDoAppDestinations.Event,
                    ToDoAppDestinations.PostsEvent,
                    ToDoAppDestinations.ToDosEvent,
                    ToDoAppDestinations.MoneyEvent
                )
                val route = if (isSubpage && taskId != null) {
                    "${destination.name}/$taskId"
                } else {
                    destination.name
                }

                NavigationBarItem(
                    selected = currentRoute?.startsWith(destination.name) == true,
                    onClick = {
                        navController.navigate(route) {
                            popUpTo(ToDoAppDestinations.Event.name) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { /* optional */ },
                    label = { Text(stringResource(destination.title)) }
                )
            }
    }
}





@Composable
fun ToDoListApp (repository: ToDoRepository, navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenName = backStackEntry?.destination?.route?.substringBefore("/")
    val currentScreen = ToDoAppDestinations.valueOf(
        screenName ?: ToDoAppDestinations.List.name
    )
    val taskIdArg = backStackEntry?.arguments?.getLong("taskToViewId")
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ToDoListTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigate(ToDoAppDestinations.List.name) {
                        popUpTo(ToDoAppDestinations.List.name) { inclusive = true }
                    }
                }

            )
        },
        bottomBar = {
            if (currentScreen.showInBottomBar) {
                BottomBar(
                    navController = navController,
                    currentRoute = screenName,
                    taskId = taskIdArg // if accessible here
                )
            }
        },
        floatingActionButton = {
            if (currentScreen == ToDoAppDestinations.List)
            FloatingActionButton(onClick = {
                navController.navigate(ToDoAppDestinations.Add.name)
            }) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Create")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ToDoAppDestinations.Login.name,
            modifier = Modifier
                .fillMaxSize()
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
                    },
                    onEvent = { taskToView ->
                        navController.navigate("${ToDoAppDestinations.Event.name}/${taskToView.taskId}")
                    }
                )
            }
            //eventGraph(navController = navController, repository = repository)
            composable(
                route = "${ToDoAppDestinations.Event.name}/{taskToViewId}",
                arguments = listOf(navArgument("taskToViewId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val taskToViewId = backStackEntry.arguments?.getLong("taskToViewId") ?: return@composable
                val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))

                LaunchedEffect(taskToViewId) {
                    viewModel.loadTask(taskToViewId)
                }

                val task by viewModel.task.collectAsState()

                if (task == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    EventScreen(
                        task = task,
                        navController = navController
                    )
                }
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
                arguments = listOf(navArgument("taskToEditId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskToEditId = backStackEntry.arguments?.getLong("taskToEditId") ?: return@composable
                val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))

                LaunchedEffect(taskToEditId) {
                    viewModel.loadTask(taskToEditId)
                }

                val task by viewModel.task.collectAsState()

                if (task == null) {
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
                        onCancel = { navController.navigateUp() }
                    )
                }
            }

            composable(
                route = "${ToDoAppDestinations.PostsEvent.name}/{taskToViewId}",
                arguments = listOf(navArgument("taskToViewId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val taskToViewId = backStackEntry.arguments?.getLong("taskToViewId") ?: return@composable
                val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))

                LaunchedEffect(taskToViewId) {
                    viewModel.loadTask(taskToViewId)
                }

                val task by viewModel.task.collectAsState()

                if (task == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    PostsEventScreen(
                        task = task,
                        navController = navController
                    )
                }
            }
            composable(
                route = "${ToDoAppDestinations.ToDosEvent.name}/{taskToViewId}",
                arguments = listOf(navArgument("taskToViewId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val taskToViewId = backStackEntry.arguments?.getLong("taskToViewId") ?: return@composable
                val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))

                LaunchedEffect(taskToViewId) {
                    viewModel.loadTask(taskToViewId)
                }

                val task by viewModel.task.collectAsState()

                if (task == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    ToDosEventScreen(
                        task = task,
                        navController = navController
                    )
                }
            }
            composable(
                route = "${ToDoAppDestinations.MoneyEvent.name}/{taskToViewId}",
                arguments = listOf(navArgument("taskToViewId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val taskToViewId = backStackEntry.arguments?.getLong("taskToViewId") ?: return@composable
                val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))

                LaunchedEffect(taskToViewId) {
                    viewModel.loadTask(taskToViewId)
                }

                val task by viewModel.task.collectAsState()

                if (task == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    MoneysEventScreen(
                        task = task,
                        navController = navController
                    )
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



