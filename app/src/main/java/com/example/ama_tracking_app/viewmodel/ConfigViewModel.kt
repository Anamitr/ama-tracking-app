package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ama_tracking_app.util.ConfigLoadedToDbEvent
import com.example.ama_tracking_app.util.ConfigLoadedToViewModelEvent
import com.example.ama_tracking_app.model.GeoConfiguration
import com.example.ama_tracking_app.repository.ConfigRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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