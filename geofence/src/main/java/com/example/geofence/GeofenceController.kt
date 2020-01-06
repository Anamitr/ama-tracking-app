package com.example.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.geofence.GeofenceForegroundService.Companion.GEOCONFIGURATION_EXTRA_ID
import com.example.geofence.GeofenceForegroundService.Companion.INTERVAL_IN_MINUTES_EXTRA_ID
import com.example.geofence.model.GeoConfiguration


object GeofenceController {
    private val TAG = GeofenceController::class.java.simpleName

    private lateinit var context: Context
    private lateinit var geoConfiguration: GeoConfiguration

    fun init(context: Context, geoConfiguration: GeoConfiguration) {
        this.context = context
        this.geoConfiguration = geoConfiguration
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

}