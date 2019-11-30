package com.example.ama_tracking_app.util

import android.content.Context
import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import com.example.geofence.util.GeofenceInjectorUtils

object InjectorUtils {


    fun provideConfigViewModelFactory(
        context: Context,
        configId: String? = null
    ): ConfigViewModelFactory {
        val repository = GeofenceInjectorUtils.getConfigRepository(context)
        return ConfigViewModelFactory(repository, configId)
    }
}