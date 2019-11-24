package com.example.ama_tracking_app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ama_tracking_app.model.GeoConfiguration

@Database(entities = arrayOf(GeoConfiguration::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun geoConfigurationDao() : GeoConfigurationDao
}