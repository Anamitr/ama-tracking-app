package com.example.geofence

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService


object GeofenceController {
    private val TAG = GeofenceController::class.java.simpleName

    private lateinit var context:Context

    fun init(context: Context) {
        this.context = context
    }

    //TODO: Check if service is running
    fun startGeofenceService(context: Context) {
        if(isServiceRunning(GeofenceForegroundService::class.java.name)) {
            Log.i(TAG, "GeofenceForegroundService already running")
        } else {
            val serviceIntent = Intent(context, GeofenceForegroundService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    private fun isServiceRunning(serviceName: String): Boolean {
        var serviceRunning = false
        val am =
            context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val l =
            am.getRunningServices(50)
        val i: Iterator<ActivityManager.RunningServiceInfo> = l.iterator()
        while (i.hasNext()) {
            val runningServiceInfo = i
                .next()
            if (runningServiceInfo.service.className == serviceName) {
                serviceRunning = true
                if (runningServiceInfo.foreground) { //service run in foreground
                }
            }
        }
        return serviceRunning
    }


}