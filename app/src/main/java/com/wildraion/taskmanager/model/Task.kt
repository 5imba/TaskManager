package com.wildraion.taskmanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "task_table")
data class Task (
       @PrimaryKey(autoGenerate = true)
       val id: Int,
       val date: String,
       val time: String,
       val title: String,
       val colorTag: Int,
       val tags: String,
       val isDone: Boolean
): Parcelable