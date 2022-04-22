package com.wildraion.taskmanager.repository

import androidx.lifecycle.LiveData
import com.wildraion.taskmanager.data.task.TaskDao
import com.wildraion.taskmanager.model.Task

class TaskRepository(private  val taskDao: TaskDao) {

    val readAllData: LiveData<List<Task>> = taskDao.readAllData()

    suspend fun addTask(task: Task): Long{
        return taskDao.addTask(task)
    }

    suspend fun  updateTask(task: Task){
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }

    suspend fun deleteAllTasks(){
        taskDao.deleteAllTasks()
    }

    fun searchData(searchQuery: String): LiveData<List<Task>> {
        return taskDao.searchData(searchQuery)
    }
}