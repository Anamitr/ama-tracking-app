package com.example.geofence

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getText

object GeofenceController {
    fun startGeofenceService(context: Context) {
        val serviceIntent = Intent(context, GeofenceForegroundService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}