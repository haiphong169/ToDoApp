package com.haiphong.todoapp.ui.home

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haiphong.todoapp.data.Task
import com.haiphong.todoapp.data.TaskDatabase
import com.haiphong.todoapp.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class UiState(val taskList: List<Task> = listOf(), val currentlyViewedTask: Task? = null)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: TaskRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTasks().distinctUntilChanged().collect { taskList ->
                _uiState.update {
                    it.copy(
                        taskList = taskList
                    )
                }


            }
        }
    }

    fun getTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTask(id).distinctUntilChanged().collect { task ->
                _uiState.update {
                    it.copy(
                        currentlyViewedTask = task
                    )
                }
            }
        }
    }

    fun onCurrentlyViewedTaskTitleChange(newTitle: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentlyViewedTask = uiState.value.currentlyViewedTask?.copy(
                    title = newTitle
                ) ?: Task(title = newTitle, description = "")
            )
        }
    }

    fun onCurrentlyViewedTaskDescriptionChange(newDescription: String) {
        _uiState.update {
            it.copy(
                currentlyViewedTask = it.currentlyViewedTask?.copy(
                    description = newDescription
                ) ?: Task(title = "", description = newDescription)
            )
        }
    }

    fun onCurrentlyViewedTaskDateChange(newDate: LocalDate) {
        _uiState.update {
            it.copy(
                currentlyViewedTask = it.currentlyViewedTask?.copy(
                    dueDate = LocalDateTime.of(
                        newDate,
                        it.currentlyViewedTask.dueDate?.toLocalTime() ?: LocalTime.MAX
                    )
                )
            )
        }
    }

    fun onCurrentlyViewedTaskTimeChange(newTime: LocalTime) {
        _uiState.update {
            it.copy(
                currentlyViewedTask = uiState.value.currentlyViewedTask?.copy(
                    dueDate = LocalDateTime.of(
                        it.currentlyViewedTask?.dueDate?.toLocalDate() ?: LocalDate.now(), newTime
                    )
                )
            )
        }
    }

    fun addTask(task: Task) = viewModelScope.launch { repository.addTask(task) }
    fun removeTask(task: Task) = viewModelScope.launch {
        delay(800)
        repository.removeTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch { repository.updateTask(task) }
}