package com.example.ama_tracking_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ConfigViewModelFactory(application: Application, configId: String? = null) :
    ViewModelProvider.Factory {
    private var application: Application = application
    private var configId: String? = configId

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ConfigViewModel(application, configId) as T
    }
}