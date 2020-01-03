package com.example.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.geofence.GeofenceForegroundService.Companion.INTERVAL_IN_MINUTES_EXTRA_ID
import com.example.geofence.model.GeoConfiguration


object GeofenceController {
    private val TAG = GeofenceController::class.java.simpleName

    private lateinit var context:Context
    private lateinit var geoConfiguration : GeoConfiguration

    fun init(context: Context, geoConfiguration: GeoConfiguration) {
        this.context = context
        this.geoConfiguration = geoConfiguration
    }

    fun startGeofenceService() {
        if(isServiceRunning(context, GeofenceForegroundService::class.java.name)) {
            Log.i(TAG, "GeofenceForegroundService already running")
        } else {
            val serviceIntent = Intent(context, GeofenceForegroundService::class.java)
            serviceIntent.setAction(GeofenceForegroundService.START_FOREGROUND_ACTION)
            serviceIntent.putExtra(INTERVAL_IN_MINUTES_EXTRA_ID, geoConfiguration.positionIntervalInMinutes)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    fun stopGeofenceService() {
        val serviceIntent = Intent(context, GeofenceForegroundService::class.java)
        serviceIntent.setAction(GeofenceForegroundService.STOP_FOREGROUND_ACTION)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

}