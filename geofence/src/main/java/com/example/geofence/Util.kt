package com.example.geofence

import android.app.ActivityManager
import android.content.Context

fun isServiceRunning(context: Context, serviceName: String): Boolean {
    var serviceRunning = false
    val am =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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