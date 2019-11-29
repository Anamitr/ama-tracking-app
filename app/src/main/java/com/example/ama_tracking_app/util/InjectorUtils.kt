package com.example.ama_tracking_app.util

import android.content.Context
import com.example.ama_tracking_app.db.AppDatabase
import com.example.ama_tracking_app.repository.ConfigRepository
import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory

object InjectorUtils {

    fun getConfigRepository(context: Context): ConfigRepository {
        return ConfigRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).geoConfigurationDao())
    }

    fun provideConfigViewModelFactory(context: Context, configId: String? = null) : ConfigViewModelFactory {
        val repository = getConfigRepository(context)
        return ConfigViewModelFactory(repository, configId)
    }
}