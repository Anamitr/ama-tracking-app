package com.example.geofence

import android.content.Context
import android.util.Log
import com.example.geofence.db.AppDatabase
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import com.example.geofence.service.FirebaseService
import com.example.geofence.service.RetrofitServiceProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class GeoManualTester(val context: Context) {
    companion object {
        private val TAG = GeoManualTester::class.java.simpleName
    }

    val firebaseService: FirebaseService = RetrofitServiceProvider.getFirebaseService()
    val geoLogDao = AppDatabase.getInstance(context.applicationContext).geoLogDao()

    fun runTest() {
//        readLogsWithSpecificId()
        testSendingLog()
    }

    fun testGeoConfigWritingAndReadingFromDb() {
        GlobalScope.launch {
            val configId = "78"
            val geoConfigurationDao =
                AppDatabase.getInstance(context.applicationContext).geoConfigurationDao()

            val geoConfiguration1 = GeoConfiguration(configId, "geoConfiguration1", 7, "", "")

            GlobalScope.launch {
                geoConfigurationDao.insert(geoConfiguration1)
            }

            val loadedGeoConfiguration1 = geoConfigurationDao.loadAll()
            Log.v(TAG, "loadAll: $loadedGeoConfiguration1")

            val loadedGeoConfiguration2 = geoConfigurationDao.loadById(configId)
            Log.v(TAG, "loadById($configId): ${loadedGeoConfiguration2}")
        }
    }

    fun testGeoLogDbWritingAndReadingFromDb() {
        GlobalScope.launch {
            val configId = "78"
            val geoLog = GeoLog("", configId, Date(), "Test log 1")
            val geoLog2 = GeoLog("", "79", Date(), "Test log 2")
            geoLogDao.insert(geoLog)
            geoLogDao.insert(geoLog2)

            Log.v(TAG, "geoLogDao.loadAll(): ${geoLogDao.loadAll()}")
            Log.v(TAG, "geoLogDao.loadByConfigId($configId): ${geoLogDao.loadByConfigId(configId)}")
        }
    }

    fun readLogsWithSpecificId() {
        GlobalScope.launch {
            val configId = "80"
            Log.v(TAG, "geoLogDao.loadByConfigId($configId): ${geoLogDao.loadByConfigId(configId)}")
        }
    }

    fun testSendingLog() {
        GlobalScope.launch {
            val geoLog = GeoLog("", "80", Date(), "dupa")

            geoLog.internalId = geoLogDao.insert(geoLog)
            sendLog(geoLog)
        }
    }

    fun sendLog(geoLog : GeoLog) {
        val postLogCall : Call<String> = firebaseService.postLog(geoLog)

        postLogCall.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "postLog request failed: ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = response.body()
                Log.v(TAG, "postLog result: $result")
                if (response.isSuccessful) {
                    Log.v(TAG, "postLog successful")
                } else {
                    Log.e(TAG, "onResponse: postLog request failed")
                }
            }

        })
    }

    fun sendLogAndSaveToDb(geoLog : GeoLog) {
//        val
    }

//    fun sendLog(geoLog : GeoLog) {
//        val geoLogIdCall: Call<String> = firebaseService.getNextLogId(geoLog.configId)
//        geoLogIdCall.enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.e(TAG, "getNextLogId request failed: ${t.localizedMessage}")
//            }
//
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                val nextLogId: String? = response.body()
//                Log.v(TAG, "getNextLogId result: $nextLogId")
//                nextLogId?.let {
//                    val postLogCall =
//                        firebaseService.postLog(geoLog.configId, GeoLog(nextLogId, Date(), content))
//                    postLogCall.enqueue(object : Callback<String> {
//                        override fun onFailure(call: Call<String>, t: Throwable) {
//                            Log.e(TAG, "onFailure: postLog request failed: ${t.localizedMessage}")
//                        }
//
//                        override fun onResponse(call: Call<String>, response: Response<String>) {
//                            val result = response.body()
//                            Log.v(TAG, "postLog result: $result")
//                            if (response.isSuccessful) {
//                                Log.v(TAG, "postLog successful")
//                            } else {
//                                Log.e(TAG, "onResponse: postLog request failed")
//                            }
//                        }
//                    })
//                }
//            }
//        })
//    }

}