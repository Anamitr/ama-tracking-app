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


//TODO: Move reading from db and comunication with internet to another class so that different
// view models could use same methods

//TODO: Everything below view model should be in seperate module

//TODO: Is AndroidViewModel ok or should I avoid using Context in ViewModel?
//It's ok but you can also use interfaces instead of having context in ViewModel
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