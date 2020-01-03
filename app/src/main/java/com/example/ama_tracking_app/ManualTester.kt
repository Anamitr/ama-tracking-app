package com.example.ama_tracking_app

import android.util.Log
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import com.example.geofence.service.FirebaseService
import com.example.geofence.service.RetrofitServiceProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// Debug class for manual testing
class ManualTester {
    companion object {
        private val TAG = ManualTester::class.java.simpleName
    }
    val firebaseService : FirebaseService = RetrofitServiceProvider.getFirebaseService()

    fun sendLog(content : String) {
        val configId = "1"
        val geoLogIdCall : Call<String> = firebaseService.getNextLogId(configId)
        geoLogIdCall.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "getNextLogId request failed: ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val nextLogId : String? = response.body()
                Log.v(TAG, "getNextLogId result: $nextLogId")
                nextLogId?.let {
                    val postLogCall = firebaseService.postLog(configId, GeoLog(nextLogId, Date(), content))
                    postLogCall.enqueue(object : Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.e(TAG, "onFailure: postLog request failed: ${t.localizedMessage}")
                        }

                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            val result = response.body()
                            Log.v(TAG, "postLog result: $result")
                            if(response.isSuccessful) {
                                Log.v(TAG, "postLog successful")
                            } else {
                                Log.e(TAG, "onResponse: postLog request failed")
                            }
                        }
                    })
                }
            }
        })

//        val geoLog:GeoLog = Ge
//        firebaseService.postLog("1", )
    }
}