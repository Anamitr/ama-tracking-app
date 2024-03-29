package com.example.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.geofence.GeofenceForegroundService.Companion.GEOCONFIGURATION_EXTRA_ID
import com.example.geofence.GeofenceForegroundService.Companion.INTERVAL_IN_MINUTES_EXTRA_ID
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import com.example.geofence.repository.GeoLogRepository
import com.example.geofence.util.DetectedActivityMovingEvent
import com.example.geofence.util.DetectedActivityStillEvent
import com.example.geofence.util.GeofenceInjectorUtils
import com.example.geofence.util.PositionIntervalChangedEvent
import com.google.android.gms.location.DetectedActivity
import org.greenrobot.eventbus.EventBus
import java.util.*


object GeofenceController {
    private val TAG = GeofenceController::class.java.simpleName

    private lateinit var context: Context
    private lateinit var geoLogRepository : GeoLogRepository

    private lateinit var geoConfiguration: GeoConfiguration
    public var currentActivityType = DetectedActivity.UNKNOWN


    fun init(context: Context, geoConfiguration: GeoConfiguration) {
        this.context = context
        this.geoConfiguration = geoConfiguration
        this.geoLogRepository = GeofenceInjectorUtils.getGeoLogRepository(context)
    }

    fun startGeofenceService() {
        val serviceIntent = Intent(context, GeofenceForegroundService::class.java)
        serviceIntent.setAction(GeofenceForegroundService.START_FOREGROUND_ACTION)
        serviceIntent.putExtra(
            INTERVAL_IN_MINUTES_EXTRA_ID,
            geoConfiguration.positionIntervalInMinutes
        )
        serviceIntent.putExtra(GEOCONFIGURATION_EXTRA_ID, geoConfiguration)
        context.startService(serviceIntent)

    }

    fun stopGeofenceService() {
        Log.v(TAG, "stopGeofenceService()")
        val serviceIntent = Intent(context, GeofenceForegroundService::class.java)
        serviceIntent.setAction(GeofenceForegroundService.STOP_FOREGROUND_ACTION)
        context.startService(serviceIntent)
    }

    fun toggleGeofenceService() : Boolean {
        var result = false
        if (GeofenceForegroundService.isRunning) {
            stopGeofenceService()
        } else {
            startGeofenceService()
            result = true
        }
        return result
    }

    fun sendLog(logContent : String) {
        val geoLog = GeoLog("", geoConfiguration.id, Date(), logContent)
        geoLogRepository.sendLogAndSaveToDb(geoLog)
    }

    fun setActivityType(activityType: Int) {
        if (currentActivityType == DetectedActivity.STILL || activityType != DetectedActivity.STILL) {
            // Started moving
            EventBus.getDefault().post(DetectedActivityMovingEvent())
        } else if (activityType == DetectedActivity.STILL || currentActivityType != DetectedActivity.STILL) {
            // Stopped moving
            EventBus.getDefault().post(DetectedActivityStillEvent())
        }
        currentActivityType = activityType
    }

    fun changePositionInterval(interval: Int) {
        EventBus.getDefault().post(PositionIntervalChangedEvent(interval))
        Toast.makeText(context, "Chaning interval to $interval minutes", Toast.LENGTH_SHORT).show()
    }

}