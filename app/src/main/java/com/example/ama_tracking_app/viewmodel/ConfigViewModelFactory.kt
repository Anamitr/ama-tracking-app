package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geofence.repository.ConfigRepository

class ConfigViewModelFactory(
    private val repository: ConfigRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ConfigViewModel(repository) as T
    }
}