package com.example.geofence

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import com.example.geofence.repository.GeoLogRepository
import com.example.geofence.util.DetectedActivityMovingEvent
import com.example.geofence.util.DetectedActivityStillEvent
import com.example.geofence.util.GeofenceInjectorUtils
import com.example.geofence.util.PositionIntervalChangedEvent
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*


class GeofenceForegroundService : Service() {
    companion object {
        private val TAG = GeofenceForegroundService::class.qualifiedName
        const val CHANNEL_ID = "GeofenceForegroundService"
        const val INTERVAL_IN_MINUTES_EXTRA_ID = "INTERVAL_IN_MINUTES_EXTRA_ID"
        const val GEOCONFIGURATION_EXTRA_ID = "GEOCONFIGURATION_EXTRA_ID"
        const val START_FOREGROUND_ACTION = "START_FOREGROUND_ACTION"
        const val STOP_FOREGROUND_ACTION = "STOP_FOREGROUND_ACTION"

        var isRunning = false

        const val DEBUG_INTERVAL = 5 * 1000L
        const val SHOULD_INSERT_DEBUG_INTERVAL = false
    }

    private var updateIntervalInMilliseconds: Long = 1 * 3 * 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long =
        updateIntervalInMilliseconds / 2

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private var mLocation: Location? = null

    private lateinit var geoConfiguration: GeoConfiguration
    private lateinit var geoLogRepository: GeoLogRepository

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(START_FOREGROUND_ACTION) && !isRunning) {
            Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show()
            val intervalInMinutes = intent?.getIntExtra(INTERVAL_IN_MINUTES_EXTRA_ID, 15)

            Log.i(
                TAG,
                "GeofenceForegroundService starting with interval in minutes = $intervalInMinutes"
            )

            intervalInMinutes?.let {
                setUpdateIntervalInMilisecondsFromMinutes(intervalInMinutes)
            } ?: run {
                Log.e(TAG, "Didn't received intervalInMinutes, set default value")
            }

            intent?.let {
                geoConfiguration =
                    intent.getSerializableExtra(GEOCONFIGURATION_EXTRA_ID) as GeoConfiguration
            } ?: kotlin.run { geoConfiguration = GeoConfiguration() }

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

            insertDebugInterval()

            createLocationRequest()
            getLastLocation()

            geoLogRepository = GeofenceInjectorUtils.getGeoLogRepository(this)

            startForeground(1, notification)
            requestLocationUpdates()

            registerForActivityTransitions()
            isRunning = true
        } else if (intent?.action.equals(STOP_FOREGROUND_ACTION) && isRunning) {
            Log.v(TAG, "STOP_FOREGROUND_ACTION")
            removeLocationUpdates()
            stopForeground(true)
            stopSelf()
            isRunning = false
        }

        Log.v(TAG, "isRunning = $isRunning")
        return START_STICKY
    }

    private fun setUpdateIntervalInMilisecondsFromMinutes(intervalInMinutes: Int) {
        updateIntervalInMilliseconds = intervalInMinutes * 60L * 1000L
    }

    fun insertDebugInterval() {
        if (SHOULD_INSERT_DEBUG_INTERVAL) {
            updateIntervalInMilliseconds = DEBUG_INTERVAL
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(updateIntervalInMilliseconds)
        mLocationRequest.setFastestInterval(updateIntervalInMilliseconds)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
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
        GlobalScope.launch {
            Log.i(TAG, "New location: $location")
            mLocation = location
            val logContent = "Position: ${location.latitude}, ${location.longitude}"
            GeofenceController.sendLog(logContent)
        }

        // Notify anyone listening for broadcasts about the new location.
//        val intent = Intent(ACTION_BROADCAST)
//        intent.putExtra(EXTRA_LOCATION, location)
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//        // Update notification content if running as a foreground service.
//        if (serviceIsRunningInForeground(this)) {
//            mNotificationManager.notify(NOTIFICATION_ID, getNotification())
//        }
    }

    private fun registerForActivityTransitions() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
        this.registerReceiver(ActivityTransitionBroadcastReceiver(), intentFilter)

        val activityTransitionRequest = ActivityTransitionRequest(TransitionsList.transitions)

        val intent = Intent(this, ActivityTransitionBroadcastReceiver::class.java)
        intent.action = ActivityTransitionBroadcastReceiver.INTENT_ACTION
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val task = ActivityRecognition
            .getClient(this)
            .requestActivityTransitionUpdates(activityTransitionRequest, pendingIntent)

        task.addOnSuccessListener {
            Log.d(TAG, "Registered for updates")
        }

        task.addOnFailureListener { e ->
            Log.e(TAG, e.message)
        }
    }

    @Subscribe
    fun onMessageEvent(event : DetectedActivityStillEvent) {
        removeLocationUpdates()
    }

    @Subscribe
    fun onMessageEvent(event: DetectedActivityMovingEvent) {
        requestLocationUpdates()
    }

    @Subscribe
    fun onPositionIntervalChangeEvent(event : PositionIntervalChangedEvent) {
        geoConfiguration = GeoConfiguration(geoConfiguration.id, geoConfiguration.name, event.positionInterval, geoConfiguration.token, geoConfiguration.trackedObjectId)
        setUpdateIntervalInMilisecondsFromMinutes(event.positionInterval)
        removeLocationUpdates()
        createLocationRequest()
        requestLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}