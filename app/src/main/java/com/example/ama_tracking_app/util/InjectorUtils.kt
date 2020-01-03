package com.example.ama_tracking_app.util

import android.content.Context
import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import com.example.ama_tracking_app.viewmodel.GeoLogViewModel
import com.example.ama_tracking_app.viewmodel.GeoLogViewModelFactory
import com.example.geofence.util.GeofenceInjectorUtils

object InjectorUtils {


    fun provideConfigViewModelFactory(
        context: Context
    ): ConfigViewModelFactory {
        val repository = GeofenceInjectorUtils.getConfigRepository(context)
        return ConfigViewModelFactory(repository)
    }

    fun provideGeoLogViewModelFactory(context: Context, configId: String?) : GeoLogViewModelFactory {
        var configIdToPass = configId ?: ""
        val configRepository = GeofenceInjectorUtils.getConfigRepository(context)
        return GeoLogViewModelFactory(configRepository, configIdToPass)
    }
}