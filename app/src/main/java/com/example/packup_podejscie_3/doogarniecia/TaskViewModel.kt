package com.example.packup_podejscie_3.doogarniecia/*package com.example.packup_podejscie_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packup_podejscie_3.doogarniecia.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: ToDoRepository) : ViewModel() {
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            repository.getTaskById(taskId).collect { fetchedTask ->
                _task.value = fetchedTask
            }
        }
    }
}
*/