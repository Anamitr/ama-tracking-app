package com.example.geofence.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.geofence.db.GeoConfigurationDao
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.service.FirebaseService
import com.example.geofence.service.RetrofitServiceProvider
import com.example.geofence.util.ConfigLoadedToDbEvent
import com.example.geofence.util.ToastEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfigRepository private constructor(
    private val geoConfigurationDao: GeoConfigurationDao,
    private val firebaseService: FirebaseService
) {
    suspend fun insertGeoConfiguration(geoConfiguration: GeoConfiguration) {
        geoConfigurationDao.insert(geoConfiguration)
    }

    fun loadGeoConfigurationFromDb(configId: String): GeoConfiguration {
//        GlobalScope.launch {
//            geoConfigurationDao.loadById(configId)?.let {
//                Log.v(TAG, "Loaded config: ${it}")
//                return it
//            }
//        }
        return geoConfigurationDao.loadById(configId)
    }

    fun loadConfigFromFirebase(configId: String) {
//        GlobalScope.launch {
        val geoConfigurationCall: Call<GeoConfiguration> = firebaseService.getConfig(configId)
        geoConfigurationCall.enqueue(object : Callback<GeoConfiguration> {
            override fun onFailure(call: Call<GeoConfiguration>, t: Throwable) {
                Log.e(TAG, "getConfig call failure " + t.localizedMessage)
                EventBus.getDefault().post(
                    ToastEvent(
                        "Failed to get config from firebase!"
                    )
                )
            }

            override fun onResponse(
                call: Call<GeoConfiguration>,
                response: Response<GeoConfiguration>
            ) {
                if (response.isSuccessful) {
                    var newGeoConfiguration = response.body()
                    if (newGeoConfiguration != null) {
                        EventBus.getDefault().post(
                            ToastEvent(
                                "Config loaded! id: " + newGeoConfiguration.id
                            )
                        )
                        GlobalScope.launch {
                            geoConfigurationDao.insert(newGeoConfiguration)
                            EventBus.getDefault().post(
                                ConfigLoadedToDbEvent(
                                    newGeoConfiguration
                                )
                            )

                        }
                    } else {
                        response.raw().request().url()
                        Log.e(
                            TAG,
                            "Call at url " + response.raw().request().url() + " returned null"
                        )
                        EventBus.getDefault().post(
                            ToastEvent(
                                "/getConfig returned null"
                            )
                        )
                    }
                }
            }
        })
//        }
    }

    companion object {
        private val TAG = ConfigRepository::class.java.simpleName

        // For Singleton instantiation
        @Volatile
        private var instance: ConfigRepository? = null

        fun getInstance(geoConfigurationDao: GeoConfigurationDao) =
            instance ?: synchronized(this) {
                instance ?: ConfigRepository(
                    geoConfigurationDao,
                    RetrofitServiceProvider.getFirebaseService()
                ).also { instance = it }
            }
    }
}