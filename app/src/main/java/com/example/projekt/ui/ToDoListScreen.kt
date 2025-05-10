@file:Suppress("FunctionName")
package com.example.projekt.ui

import android.util.Log
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolistapp.R
import com.example.todolistapp.data.DataSource
import com.example.todolistapp.data.Task
import com.example.todolistapp.database.ToDoRepository
import com.example.todolistapp.ui.theme.ToDoListAppTheme
import kotlinx.coroutines.launch

@Composable
fun DeleteConfirmationDialog(
    task: Task,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_task_title)) },
        text = { Text(stringResource(R.string.delete_msg, task.name)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete), color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
fun DeleteConfirmationDialogPreview() {
    ToDoListAppTheme {
        DeleteConfirmationDialog(
            task = Task("Task 1", "Description of task 1"),
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToDoListScreen(repository: ToDoRepository, modifier: Modifier = Modifier, onEdit: (Task) -> Unit = {}) {
    val tasks by repository.tasksFlow.collectAsState(initial = emptyList()) // Automatically updates when DB
    Log.d("ToDoListScreen", "Tasks: $tasks")
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete: Task? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope() // Get a CoroutineScope for DB operations (have to be done outside the main thread)
    Surface(
        modifier = modifier.padding(8.dp)
    ) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                items(items = tasks) { task ->
                    TaskItem(
                        task = task,
                        onEditClick = {onEdit(task)},
                        onDelete = {
                            showDeleteDialog = true
                            taskToDelete = task
                        },
                        onCheckedChange = {
                            coroutineScope.launch {
                                repository.updateTask(task.copy(isCompleted = it))
                            }
                        }
                    )
                }
            }
            if (showDeleteDialog && taskToDelete != null) {
                DeleteConfirmationDialog(
                    task = taskToDelete!!,
                    onConfirm = {
                        coroutineScope.launch {
                            repository.deleteTask(taskToDelete!!)
                            taskToDelete = null
                        }
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        taskToDelete = null
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ToDoListScreenPreview() {
    ToDoListAppTheme {
        //ToDoListScreen()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    onEditClick: () -> Unit,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } // Toggle expanded state
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {expanded = !expanded },
                onLongClick = onDelete //long press to delete
            ),
        color = if (task.isCompleted) Color.Gray
        else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp
    )
    {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            //

            //else
                Box(
                    modifier = Modifier
                )
                {}
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            )
            {
                if (task.isImportant)
                {Box(
                    modifier = Modifier.background(color = Color.Red, shape = RectangleShape).width(16.dp)
                        .fillMaxHeight()
                ) {}}
                if (task.isCompleted) {

                }
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onCheckedChange(it) }
                )
                Text(
                    text = task.name,
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None)
                IconButton(onClick = onEditClick){
                    Icon(Icons.TwoTone.Edit, contentDescription = "Edit Task")
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (expanded) 180f else 0f)
                )
            }
            if (expanded)
            Text(task.description)
        }
    }
}

@Preview
@Composable
fun TaskItemPreview(){
    ToDoListAppTheme {
        TaskItem(
            task = DataSource.getTasks()[0],
            onEditClick = { },
            onDelete = {},
            onCheckedChange = {  },
            modifier = Modifier
        )
    }
}