package com.example.ama_tracking_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ama_tracking_app.repository.ConfigRepository

class ConfigViewModelFactory(
    private val repository: ConfigRepository,
    private var configId: String? = null) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ConfigViewModel(repository, configId) as T
    }
}