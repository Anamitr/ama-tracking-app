package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.repository.ConfigRepository
import com.example.geofence.util.ConfigLoadedFromDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class GeoLogViewModel(
    private val configRepository: ConfigRepository,
    private val configId: String
) : ViewModel() {
    companion object {
        private val TAG = GeoLogViewModel::class.qualifiedName
    }

    var geoConfiguration: GeoConfiguration = GeoConfiguration("test", "test", 1234)

    init {
//        EventBus.getDefault().register(this)
        GlobalScope.launch {
            geoConfiguration = configRepository.loadGeoConfigurationFromDb(configId)
            EventBus.getDefault().post(ConfigLoadedFromDb(geoConfiguration))
        }
    }


}