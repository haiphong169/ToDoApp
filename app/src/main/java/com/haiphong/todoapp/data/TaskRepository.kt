package com.haiphong.todoapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject



class TaskRepository  @Inject constructor(private val taskDao: TaskDao) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().flowOn(Dispatchers.IO).conflate()
    fun getTask(id: Int) = taskDao.getTask(id).flowOn(Dispatchers.IO).conflate()
    suspend fun addTask(task: Task) = taskDao.insert(task)
    suspend fun updateTask(task: Task) = taskDao.update(task)
    suspend fun removeTask(task: Task) = taskDao.delete(task)
}