package com.example.geofence.util

import android.content.Context
import com.example.geofence.db.AppDatabase
import com.example.geofence.repository.ConfigRepository

object GeofenceInjectorUtils {
    fun getConfigRepository(context: Context): ConfigRepository {
        return ConfigRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).geoConfigurationDao())
    }
}