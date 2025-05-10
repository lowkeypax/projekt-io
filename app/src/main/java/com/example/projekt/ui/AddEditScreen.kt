@file:Suppress("FunctionName")
package com.example.projekt.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolistapp.R
import com.example.todolistapp.data.Task
import com.example.todolistapp.ui.theme.ToDoListAppTheme

/**
 * A composable function that displays the Add/Edit screen.
 * @param modifier The modifier to apply to this layout.
 * @param task The task to edit, if null a new task will be created.
 * @param onSave The callback that is called when the task is saved.
 * @param onCancel The callback that is called when the task is canceled.
 */
@Composable
fun AddEditScreen(
    modifier: Modifier = Modifier,
    task: Task? = null,
    onSave: (Task) -> Unit = {},
    onCancel: () -> Unit = {}
){
    var name by remember { mutableStateOf(task?.name ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var isImportant by remember { mutableStateOf(task?.isImportant == true) }
    var validationError by remember { mutableStateOf(false) }

    Surface (modifier = Modifier.padding(20.dp)) {
        Column {
            Text(
                text = stringResource(R.string.add_task_label),
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                )

            OutlinedTextFieldWithClearAndError(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.name_label),
                errorMessage = "",
                isError = validationError,
                singleLine = true
            )

            OutlinedTextFieldWithClearAndError(
                value = description,
                onValueChange = { description = it },
                label = stringResource(R.string.description_label),
                errorMessage = "",
                isError = validationError,
                singleLine = false
            )

            SwitchWithText(
                text = stringResource(R.string.important_label),
                checked = isImportant
            ) { isImportant = it }

            Row(modifier=Modifier.align(Alignment.CenterHorizontally)){
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onCancel() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.errorContainer, // Background color
                        contentColor = colorScheme.error // Text color
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        validationError = !validateInput(name, description)
                        if (!validationError)
                            onSave(
                                // We create the Task object here but it could be done
                                // in the onSave lambda based on the input
                                Task(
                                    name = name,
                                    description = description,
                                    isImportant = isImportant,
                                    isCompleted = task?.isCompleted ?: false,
                                    taskId = task?.taskId ?: System.currentTimeMillis()
                                )
                            )
                    }) {
                    Text(stringResource(R.string.save))
                }
            }
        }

    }

}

fun validateInput(name: String, description: String): Boolean {
    return true
}

@Preview
@Composable
fun AddEditScreenPreview() {
    ToDoListAppTheme {
        AddEditScreen()
    }
}

/**
 * A composable function that displays an [OutlinedTextField] with a clear button and an error message.
 * @param value The value of the text field.
 * @param onValueChange The callback that is called when the value changes.
 * @param label The label of the text field.
 * @param errorMessage The error message to display.
 * @param isError Whether the text field is in an error state.
 * @param modifier The modifier to apply to the text field.
 * @param singleLine Whether the text field should be single line.
 */
@Composable
fun OutlinedTextFieldWithClearAndError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine, // this is lifted to the caller to make it more reusable
        trailingIcon = {
            // The clear button is only displayed when the text field is not empty
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        // Clear is done by setting an empty string
                        onValueChange("")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear, // The clear icon from the Material Icons
                        contentDescription = stringResource(
                            R.string.clear_content_description,
                            label
                        )
                    )
                }
            }
        },
        isError = isError,
        supportingText = {
            Row {
                if (isError)
                    Text(errorMessage)
            }
        },
        modifier = modifier
    )
}

/**
 * A composable function that displays a [Switch] with a text label.
 * @param text The text to display next to the switch.
 * @param checked The checked state of the switch.
 * @param modifier The modifier to apply to the row.
 * @param onCheckedChange The callback that is called when the switch is toggled.
 */
@Composable
fun SwitchWithText(
    text: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        Text(text)
        // add some space between the text and the switch, could also be done with padding
        Spacer(modifier = Modifier.padding(8.dp))
        // The switch composable the most important parameters are checked and onCheckedChange
        // checked is the state of the switch and onCheckedChange is the callback that is called
        // when the switch is toggled
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}