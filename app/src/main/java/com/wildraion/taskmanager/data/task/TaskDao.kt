package com.wildraion.taskmanager.data.task

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wildraion.taskmanager.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery OR tags LIKE :searchQuery")
    fun searchData(searchQuery: String): LiveData<List<Task>>
}