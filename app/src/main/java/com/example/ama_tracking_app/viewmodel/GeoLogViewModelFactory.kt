package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geofence.repository.ConfigRepository

class GeoLogViewModelFactory (
    private val configRepository: ConfigRepository,
    private var configId: String = "") :
    ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return GeoLogViewModel(configRepository, configId) as T
        }
}