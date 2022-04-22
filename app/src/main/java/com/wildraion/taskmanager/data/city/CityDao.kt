package com.wildraion.taskmanager.data.city

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.wildraion.taskmanager.model.City

@Dao
interface CityDao {

    @Query("SELECT * FROM city_list WHERE name LIKE :searchQuery")
    fun searchData(searchQuery: String): LiveData<List<City>>

}