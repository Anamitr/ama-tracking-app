package com.example.ama_tracking_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ama_tracking_app.recyclerview.RecyclerViewAdapter
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import com.example.geofence.repository.ConfigRepository
import com.example.geofence.repository.GeoLogRepository
import com.example.geofence.util.ConfigLoadedFromDbEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.*

class GeoLogViewModel(
    private val configRepository: ConfigRepository,
    private val configId: String,
    private val geoLogRepository: GeoLogRepository
) : ViewModel() {
    companion object {
        private val TAG = GeoLogViewModel::class.qualifiedName
    }

    var geoConfiguration: GeoConfiguration = GeoConfiguration("testId", "karambol", 1234)

    val testGeoLogList: List<GeoLog> =
        listOf(GeoLog("-1", "81", Date(), "test log 1"), GeoLog("-1", "81", Date(), "test log 2"),
            GeoLog("-1", "81", Date(), "test log 3"))

    val recyclerViewAdapter = RecyclerViewAdapter(testGeoLogList)

    init {
//        EventBus.getDefault().register(this)
        GlobalScope.launch {
            geoConfiguration = configRepository.loadGeoConfigurationFromDb(configId)
            EventBus.getDefault().post(ConfigLoadedFromDbEvent(geoConfiguration))
        }
    }

    fun getGeoLogsLiveData() : LiveData<List<GeoLog>>{
        return geoLogRepository.getGeoLogsLiveDataByConfigId(configId)
    }

    fun clearLogs() {
        geoLogRepository.clearLogs(configId)
    }

}