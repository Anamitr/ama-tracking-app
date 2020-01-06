package com.example.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.example.geofence.db.AppDatabase
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import com.example.geofence.service.FirebaseService
import com.example.geofence.service.RetrofitServiceProvider
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.*
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
//        testSendingLog()

//        testSendingAndSavingToDbGeoLog()
//        readLogsWithSpecificId()

//        testClearLogs()

        testTransitionApi()
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

    fun testSendingAndSavingToDbGeoLog() {
        val geoLog = GeoLog("", "80", Date(), "dupa")

        sendLogAndSaveToDb(geoLog)
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

    fun testClearLogs() {
        val configId = "82"
        val clearLogCall = firebaseService.clearLogs(configId)

        clearLogCall.enqueue(object :Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "clearLogs request failed: ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = response.body()
                Log.v(TAG, "clearLogs result: $result")
                if (response.isSuccessful) {
                    Log.v(TAG, "clearLogs successful")
                } else {
                    Log.e(TAG, "onResponse: clearLogs request failed")
                }
            }

        })
    }

    fun testTransitionApi() {
        val requiredPermission = "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        val checkVal: Int = context.checkCallingOrSelfPermission(requiredPermission)
        if (checkVal== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "PERMISSION_GRANTED dsagsdfg", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "PERMISSION NOT GRANTED!!!", Toast.LENGTH_SHORT).show()
        }
        Log.v(TAG, "ACTIVITY_RECOGNITION permission: ${checkVal== PackageManager.PERMISSION_GRANTED}")


        val intentFilter = IntentFilter()
        intentFilter.addAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
        context.registerReceiver(ActivityTransitionBroadcastReceiver(), intentFilter)

        val activityTransitionRequest = ActivityTransitionRequest(getTransitionList())

//        val intent = Intent(context, TransitionIntentService::class.java)
//        val pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val intent = Intent(context, ActivityTransitionBroadcastReceiver::class.java)
        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val task = ActivityRecognition
            .getClient(context)
            .requestActivityTransitionUpdates(activityTransitionRequest, pendingIntent)

        task.addOnSuccessListener {
            Log.d(TAG, "Registered for updates")
        }

        task.addOnFailureListener { e ->
            Log.e(TAG, e.message)
        }



        emulateActivityTransition()
    }

    fun emulateActivityTransition() {
        var intent = Intent()

        // Your broadcast receiver action

        intent.action = ActivityTransitionBroadcastReceiver.INTENT_ACTION
        var events: ArrayList<ActivityTransitionEvent> = arrayListOf()

        // You can set desired events with their corresponding state

        var transitionEvent = ActivityTransitionEvent(DetectedActivity.IN_VEHICLE, ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos())
        events.add(transitionEvent)
        var result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(result, intent, "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT")
//        activity?.sendBroadcast(intent)
        context.sendBroadcast(intent)

//        intent = Intent(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
//        context.sendBroadcast(intent)
//
//        intent = Intent()
//        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
//        context.sendBroadcast(intent)
    }

    fun getTransitionList() :List<ActivityTransition> {
        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        return transitions
    }

}