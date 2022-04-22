package com.wildraion.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.wildraion.taskmanager.data.city.CityDatabase
import com.wildraion.taskmanager.model.City
import com.wildraion.taskmanager.repository.CityRepository

class CityViewModel(application: Application): AndroidViewModel(application) {

    private val repository: CityRepository

    init {
        val cityDao = CityDatabase.getDatabase(application).cityDao()
        repository = CityRepository(cityDao)
    }

    fun searchData(searchQuery: String): LiveData<List<City>> {
        return repository.searchData(searchQuery)
    }

}