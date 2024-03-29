package com.example.geofence.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.geofence.GeoManualTester
import com.example.geofence.db.GeoLogDao
import com.example.geofence.model.GeoLog
import com.example.geofence.service.FirebaseService
import com.example.geofence.service.RetrofitServiceProvider
import com.example.geofence.util.ToastEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeoLogRepository private constructor(
    private val geoLogDao: GeoLogDao,
    private val firebaseService: FirebaseService
) {

    //TODO: When to unregister, is there any destructor?
//    init {
//        EventBus.getDefault().register(this)
//    }

//    val selectedGeoLogs : LiveData<List<GeoLog>> = geoLogDao.getLiveDataGeoLogsByConfigId()

    suspend fun insertGeoLog(geoLog : GeoLog) {
        geoLogDao.insert(geoLog)
//        firebaseService.postLog(geoLog)
    }

    fun getAllById(configId : String) : List<GeoLog>{
        return geoLogDao.loadByConfigId(configId)
    }

    fun sendLogAndSaveToDb(geoLog : GeoLog) {
        val postLogCall : Call<String> = firebaseService.postLog(geoLog)

        postLogCall.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "postLog request failed: ${t.localizedMessage}, inserting geoLog to db without externalId")
                GlobalScope.launch {
                    geoLogDao.insert(geoLog)
                }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = response.body()
                Log.v(TAG, "postLog result: $result")
                if (response.isSuccessful) {
                    Log.v(TAG, "postLog successful")
                    geoLog.externalId = result ?: ""
                } else {
                    Log.e(TAG, "onResponse: postLog request failed, inserting geoLog to db without externalId")
                }
                GlobalScope.launch {
                    geoLogDao.insert(geoLog)
                }
            }
        })
    }

    fun getGeoLogsLiveDataByConfigId(configId: String) : LiveData<List<GeoLog>>{
        return geoLogDao.getLiveDataGeoLogsByConfigId(configId)
    }

    fun clearLogs(configId: String) {
        val clearLogCall = firebaseService.clearLogs(configId)

        clearLogCall.enqueue(object :Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "clearLogs($configId) request failed: ${t.localizedMessage}")
                EventBus.getDefault().post(ToastEvent("clearLogs request failed: ${t.localizedMessage}"))
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = response.body()
                Log.v(TAG, "clearLogs result: $result")
                if (response.isSuccessful) {
                    Log.v(TAG, "clearLogs successful")
                    GlobalScope.launch {
                        geoLogDao.deleteLogs(configId)
                    }
                } else {
                    Log.e(TAG, "onResponse: clearLogs($configId) request failed")
                    EventBus.getDefault().post(ToastEvent("clearLogs request failed"))
                }
            }

        })
    }

    companion object {
        private val TAG = GeoLogRepository::class.java.simpleName

        // For Singleton instantiation
        @Volatile
        private var instance: GeoLogRepository? = null

        fun getInstance(geoLogDao: GeoLogDao) =
            instance ?: synchronized(this) {
                instance ?: GeoLogRepository(
                    geoLogDao,
                    RetrofitServiceProvider.getFirebaseService()
                ).also { instance = it }
            }
    }

}