package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geofence.repository.ConfigRepository
import com.example.geofence.repository.GeoLogRepository

class GeoLogViewModelFactory (
    private val configRepository: ConfigRepository,
    private var configId: String = "",
    private val geoLogRepository: GeoLogRepository) :
    ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return GeoLogViewModel(configRepository, configId, geoLogRepository) as T
        }
}