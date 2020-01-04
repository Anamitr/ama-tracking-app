package com.example.geofence.util

import android.content.Context
import com.example.geofence.db.AppDatabase
import com.example.geofence.repository.ConfigRepository
import com.example.geofence.repository.GeoLogRepository

object GeofenceInjectorUtils {
    fun getConfigRepository(context: Context): ConfigRepository {
        return ConfigRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).geoConfigurationDao())
    }

    fun getGeoLogRepository(context: Context) : GeoLogRepository {
        return GeoLogRepository.getInstance(AppDatabase.getInstance(context.applicationContext).geoLogDao())
    }
}