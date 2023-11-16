package com.haiphong.todoapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haiphong.todoapp.R
import com.haiphong.todoapp.ToDoAppBar
import com.haiphong.todoapp.data.Task
import com.haiphong.todoapp.ui.composables.DateTimePicker
import com.haiphong.todoapp.ui.composables.InputTextField
import com.haiphong.todoapp.ui.composables.ToDoButton
import com.haiphong.todoapp.ui.navigation.NavigationDestination
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by homeScreenViewModel.uiState.collectAsState()

    var addTaskDialogOpen by remember {
        mutableStateOf(false)
    }

    var detailTaskDialogOpen by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ToDoAppBar(
                title = stringResource(id = HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addTaskDialogOpen = true },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.padding_large)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_task)
                )

            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeBody(
                taskList = uiState.taskList,
                onCheck = { task ->
                    homeScreenViewModel.removeTask(task)
                }, onClick = { taskId ->
                    homeScreenViewModel.getTask(id = taskId)
                    detailTaskDialogOpen = true
                }
            )
            if (addTaskDialogOpen) {
                AddTaskDialog(onSave = { newTask ->
                    homeScreenViewModel.addTask(newTask)
                }, onDismiss = { addTaskDialogOpen = false })
            }
            if (detailTaskDialogOpen) {
                TaskDetailDialog(onSave = { newTask ->
                    homeScreenViewModel.updateTask(newTask)
                }, onDismiss = {
                    detailTaskDialogOpen = false
                }, onTitleChange = homeScreenViewModel::onCurrentlyViewedTaskTitleChange,
                    onDescriptionChange = homeScreenViewModel::onCurrentlyViewedTaskDescriptionChange,
                    onDateChange = homeScreenViewModel::onCurrentlyViewedTaskDateChange,
                    onTimeChange = homeScreenViewModel::onCurrentlyViewedTaskTimeChange,
                    task = uiState.currentlyViewedTask
                )
            }
        }
    }
}

@Composable
private fun HomeBody(taskList: List<Task>, onCheck: (Task) -> Unit, onClick: (Int) -> Unit) {
    if (taskList.isEmpty()) {
        Text(
            text = stringResource(id = R.string.no_item_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    } else {
        TaskList(
            taskList = taskList, onCheck = onCheck, onClick = onClick, modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen.padding_small
                )
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskList(
    taskList: List<Task>,
    onCheck: (Task) -> Unit,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = taskList, key = { it.id }) { item ->
            TaskListItem(
                task = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .animateItemPlacement(
                        animationSpec = tween(durationMillis = 300, delayMillis = 20)
                    ),
                onCheck = onCheck,
                onClick = onClick
            )
        }
    }
}

@Composable
fun TaskListItem(
    task: Task,
    onCheck: (Task) -> Unit,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var checked by remember {
        mutableStateOf(false)
    }
    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
                .clickable { onClick(task.id) },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = checked, onCheckedChange = {
                    checked = !checked
                    onCheck(task)
                })
                Spacer(modifier = Modifier.width(25.dp))
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (checked) Color.LightGray else Color.Black
                    )
                    if (task.description != null) {
                        Text(
                            text = task.description, style = MaterialTheme.typography.labelMedium,
                            color = if (checked) Color.LightGray else Color.Black
                        )
                    }
                    if (task.dueDate != null) Text(
                        text = task.dueDate.format(
                            DateTimeFormatter.ofPattern(
                                "EEE, d MMM HH:mm"
                            )
                        ), style = MaterialTheme.typography.labelSmall,
                        color = if (checked) Color.LightGray else Color.Black
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = checked,
            enter = slideIn(
                animationSpec = tween(durationMillis = 500),
                initialOffset = { _ ->
                    IntOffset(0, 0)
                })
        ) {
            Divider()
        }
    }
}

@Composable
private fun AddTaskDialog(
    modifier: Modifier = Modifier,
    onSave: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }
    var date by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var time by remember {
        mutableStateOf<LocalTime?>(null)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .width(400.dp)
                .height(500.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputTextField(value = title, label = "Title", onValueChange = { title = it })
                Spacer(modifier = Modifier.height(30.dp))
                InputTextField(
                    value = description,
                    label = "Description (Optional)",
                    onValueChange = { description = it }
                )
                Spacer(modifier = Modifier.height(30.dp))
                DateTimePicker(
                    onDateChange = { date = it },
                    onTimeChange = { time = it },
                    date = date,
                    time = time
                )

                Spacer(modifier = Modifier.height(50.dp))
                ToDoButton(text = "Save", onClick = {
                    val dateTime: LocalDateTime? = if (date == null && time == null) {
                        null
                    } else {
                        LocalDateTime.of(date ?: LocalDate.now(), time ?: LocalTime.MIDNIGHT)
                    }
                    if (title.trim().isNotEmpty()) {
                        onSave(
                            Task(
                                title = title,
                                description = if (description.trim()
                                        .isEmpty()
                                ) null else description,
                                dueDate = dateTime
                            )
                        )
                    }
                    onDismiss()
                })
            }
        }
    }
}

@Composable
private fun TaskDetailDialog(
    modifier: Modifier = Modifier,
    task: Task?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onSave: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .width(400.dp)
                .height(500.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputTextField(
                    value = task?.title ?: "",
                    label = "Title",
                    onValueChange = onTitleChange
                )
                Spacer(modifier = Modifier.height(30.dp))
                InputTextField(
                    value = task?.description ?: "",
                    label = "Description (Optional)",
                    onValueChange = onDescriptionChange
                )
                Spacer(modifier = Modifier.height(30.dp))
                DateTimePicker(
                    onDateChange = onDateChange,
                    onTimeChange = onTimeChange,
                    date = task?.dueDate?.toLocalDate(),
                    time = task?.dueDate?.toLocalTime()
                )
                Spacer(modifier = Modifier.height(50.dp))
                ToDoButton(text = "Save", onClick = {
                    onSave(task!!)
                    onDismiss()
                })
            }
        }
    }
}
