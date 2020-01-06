package com.example.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.geofence.repository.GeoLogRepository
import com.example.geofence.util.GeofenceInjectorUtils
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity


class ActivityTransitionBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val INTENT_ACTION = "ActivityTransitionBroadcastReceiver_ACTION"
        val TAG = ActivityTransitionBroadcastReceiver::class.java.simpleName
    }

//    override fun on

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v(TAG, "onReceive")
        GeofenceController.sendLog("ActivityTransitionBroadcastReceiver onReceive")

        Toast.makeText(context, "ActivityTransitionBroadcastReceiver : onReceive", Toast.LENGTH_SHORT).show()
        if (intent != null && INTENT_ACTION == intent.action) {
            if (ActivityTransitionResult.hasResult(intent)) {
                val intentResult = ActivityTransitionResult
                    .extractResult(intent)
                for(event in intentResult!!.transitionEvents) {
                    Toast.makeText(context, DetectedActivity(event.activityType, 100).toString(), Toast.LENGTH_SHORT).show()
                    GeofenceController.sendLog(DetectedActivity(event.activityType, 100).toString())
                }
                // handle activity transition result ...
            }
        }
    }
}