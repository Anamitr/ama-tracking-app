package com.example.ama_tracking_app.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ama_tracking_app.db.DatabaseProvider
import com.example.ama_tracking_app.db.GeoConfigurationDao
import com.example.ama_tracking_app.model.GeoConfiguration
import com.example.ama_tracking_app.service.FirebaseService
import com.example.ama_tracking_app.service.RetrofitServiceProvider
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO: Move reading from db and comunication with internet to another class so that different
// view models could use same methods

//TODO: Everything below view model should be in seperate module

//TODO: Is AndroidViewModel ok or should I avoid using Context in ViewModel?
//It's ok but you can also use interfaces instead of having context in ViewModel
class ConfigViewModel(application: Application, configId: String? = null) :
    AndroidViewModel(application) {
    companion object {
        private val TAG = ConfigViewModel::class.qualifiedName
    }

    var geoConfiguration: GeoConfiguration = GeoConfiguration()
    val firebaseService: FirebaseService = RetrofitServiceProvider.getFirebaseService()
    val geoConfigurationDao: GeoConfigurationDao =
        DatabaseProvider.getDatabase(getApplication()).geoConfigurationDao()

    init {
        configId?.let {
            loadGeoConfigationFromDb(it)
        }
    }

    //TODO: Probably a total heresy to do it here
    // Move to another class
    fun loadConfig(configId: String) {
        val geoConfigurationCall: Call<GeoConfiguration> = firebaseService.getConfig(configId)
        geoConfigurationCall.enqueue(object : Callback<GeoConfiguration> {
            override fun onFailure(call: Call<GeoConfiguration>, t: Throwable) {
                Log.e(TAG, "getConfig call failure " + t.localizedMessage)
                Toast.makeText(
                    getApplication(),
                    "getConfig call failure, " + t.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<GeoConfiguration>,
                response: Response<GeoConfiguration>
            ) {
                if (response.isSuccessful) {
                    var newGeoConfiguration = response.body()
                    if (newGeoConfiguration != null) {
                        geoConfiguration = newGeoConfiguration
                        upsertGeoConfiguration(geoConfiguration)
                        EventBus.getDefault().post(ConfigLoadedEvent())
                    } else {
                        response.raw().request().url()
                        Log.e(
                            TAG,
                            "Call at url " + response.raw().request().url() + " returned null"
                        )
                        Toast.makeText(
                            getApplication(),
                            "/getConfig returned null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    fun upsertGeoConfiguration(geoConfiguration: GeoConfiguration) = viewModelScope.launch {
        geoConfigurationDao.upsert(geoConfiguration)
    }

    fun loadGeoConfigationFromDb(configId: String) = viewModelScope.launch {
        geoConfigurationDao.loadById(configId)?.let {
            geoConfiguration = it
            Log.v(TAG, "Loaded config: $it")
        } ?: run {
            Log.e(TAG, "Config with id $configId not found")
        }
    }

    class ConfigLoadedEvent
}