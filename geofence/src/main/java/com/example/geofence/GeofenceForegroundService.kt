package com.example.geofence

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*


class GeofenceForegroundService : Service() {
    companion object {
        private val TAG = GeofenceForegroundService::class.qualifiedName
        const val CHANNEL_ID = "GeofenceForegroundService"
    }

    private var updateIntervalInMilliseconds: Long = 1 * 3 * 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long =
        updateIntervalInMilliseconds / 2

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private var mLocation: Location? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "GeofenceForegroundService starting")

        createNotificationChannel()

        //TODO: content not showing on notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Geofence Foreground Service")
            .setContentText("Geofence")
            .setSmallIcon(R.drawable.gps_logo_small_icon)
            .build()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }

        createLocationRequest()
        getLastLocation()




        startForeground(1, notification)
        requestLocationUpdates()

        return START_STICKY
    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
//        Utils.setRequestingLocationUpdates(this, true)
//        startService(Intent(applicationContext, GeofenceForegroundService::class.java))
        try {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
//            Utils.setRequestingLocationUpdates(this, false)
//            stopSelf()
        } catch (unlikely: SecurityException) {
//            Utils.setRequestingLocationUpdates(this, true)
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel: NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        } else {
            Toast.makeText(this, "SDK_INT < 0 !", Toast.LENGTH_LONG).show()
            Log.e(TAG, "SDK_INT < 0 !")
        }
    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(updateIntervalInMilliseconds)
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    } else {
                        Log.w(TAG, "Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }
    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        mLocation = location
        // Notify anyone listening for broadcasts about the new location.
//        val intent = Intent(ACTION_BROADCAST)
//        intent.putExtra(EXTRA_LOCATION, location)
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//        // Update notification content if running as a foreground service.
//        if (serviceIsRunningInForeground(this)) {
//            mNotificationManager.notify(NOTIFICATION_ID, getNotification())
//        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}