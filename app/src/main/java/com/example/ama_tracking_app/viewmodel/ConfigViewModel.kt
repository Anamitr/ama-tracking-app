package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.geofence.util.ConfigLoadedToDbEvent
import com.example.geofence.util.ConfigLoadedToViewModelEvent
import com.example.geofence.repository.ConfigRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.example.geofence.model.GeoConfiguration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConfigViewModel(
    private val configRepository: ConfigRepository
) :
    ViewModel() {
    companion object {
        private val TAG = ConfigViewModel::class.qualifiedName
    }

    var geoConfiguration: GeoConfiguration = GeoConfiguration()

    init {
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