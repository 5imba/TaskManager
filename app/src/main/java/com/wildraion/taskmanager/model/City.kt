package com.wildraion.taskmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_list")
data class City (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val state: String,
    val country: String,
    val lon: Double,
    val lat: Double
)