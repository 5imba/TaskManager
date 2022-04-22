package com.wildraion.taskmanager.data.city

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wildraion.taskmanager.model.City

@Database(entities = [City::class], version = 1, exportSchema = false)
abstract class CityDatabase: RoomDatabase() {

    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getDatabase(context: Context): CityDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CityDatabase::class.java,
                    "city_list"
                ).createFromAsset("database/city_list.db").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}