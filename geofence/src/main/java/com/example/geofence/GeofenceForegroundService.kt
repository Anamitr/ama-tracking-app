package com.example.geofence

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class GeofenceForegroundService : Service() {
    companion object {
        private val TAG = GeofenceForegroundService::class.qualifiedName
        const val CHANNEL_ID = "GeofenceForegroundService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "GeofenceForegroundService starting")

        createNotificationChannel()

        //TODO: content not showing on notification
        val notification : Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Geofence Foreground Service")
            .setContentText("Geofence")
            .setSmallIcon(R.drawable.gps_logo_small_icon)
            .build()

        startForeground(1, notification)

//        return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel: NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        } else {
            Toast.makeText(this, "SDK_INT < 0 !", Toast.LENGTH_LONG).show()
            Log.e(TAG, "SDK_INT < 0 !")
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}