package com.example.ama_tracking_app

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.ama_tracking_app.model.GeoConfiguration
import com.example.ama_tracking_app.service.FirebaseService
import com.example.ama_tracking_app.service.RetrofitServiceProvider
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConfigViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val TAG = ConfigViewModel::class.qualifiedName
    }

    var geoConfiguration: GeoConfiguration = GeoConfiguration()
    val firebaseService: FirebaseService = RetrofitServiceProvider.getFirebaseService()

    //TODO: Probably a total heresy to do it here
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

    class ConfigLoadedEvent
}