package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ama_tracking_app.ConfigLoadedToDbEvent
import com.example.ama_tracking_app.ConfigLoadedToViewModelEvent
import com.example.ama_tracking_app.model.GeoConfiguration
import com.example.ama_tracking_app.repository.ConfigRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ConfigViewModel(
    private val configRepository: ConfigRepository,
    private val initialConfigId: String?
) :
    ViewModel() {
    companion object {
        private val TAG = ConfigViewModel::class.qualifiedName
    }

    lateinit var geoConfiguration: GeoConfiguration

    init {
        initialConfigId?.let {
            geoConfiguration = configRepository.loadGeoConfigurationFromDb(initialConfigId).value ?: GeoConfiguration()
        } ?: run {
            //TODO: handle it somehow
            // geoConfiguration must be initialized here, but how if initialConfigId is null?
            geoConfiguration = configRepository.loadGeoConfigurationFromDb("").value ?: GeoConfiguration()
        }

        EventBus.getDefault().register(this)
    }

    fun loadConfigFromFirebase(configId: String) {
        configRepository.loadConfigFromFirebase(configId)
    }

    @Subscribe
    fun onMessageEvent(event: ConfigLoadedToDbEvent) {
        geoConfiguration = event.geoConfiguration
        EventBus.getDefault().post(ConfigLoadedToViewModelEvent())
    }
}