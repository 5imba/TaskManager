package com.wildraion.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.wildraion.taskmanager.data.task.TaskDatabase
import com.wildraion.taskmanager.repository.TaskRepository
import com.wildraion.taskmanager.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val repository: TaskRepository

    val readAllData: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        readAllData = repository.readAllData
    }

    suspend fun addTask(task: Task): Long{
        return repository.addTask(task)
        //viewModelScope.launch(Dispatchers.IO) {
        //}
    }

    fun updateTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun deleteAllTasks(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }

    fun searchData(searchQuery: String): LiveData<List<Task>> {
        return repository.searchData(searchQuery)
    }
}