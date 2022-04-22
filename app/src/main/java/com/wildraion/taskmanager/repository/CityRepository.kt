package com.wildraion.taskmanager.repository

import androidx.lifecycle.LiveData
import com.wildraion.taskmanager.data.city.CityDao
import com.wildraion.taskmanager.model.City

class CityRepository(private val cityDao: CityDao) {

    fun searchData(searchQuery: String): LiveData<List<City>> {
        return cityDao.searchData(searchQuery)
    }

}