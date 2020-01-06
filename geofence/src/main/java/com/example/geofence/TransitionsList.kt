package com.example.geofence

import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity

object TransitionsList {

    private val list = listOf<Int>(DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE, DetectedActivity.ON_FOOT,
        DetectedActivity.RUNNING, DetectedActivity.WALKING, DetectedActivity.STILL)

    val transitions = mutableListOf<ActivityTransition>()

    init {
        for(element in list) {
            transitions +=
                ActivityTransition.Builder()
                    .setActivityType(element)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()

            transitions +=
                ActivityTransition.Builder()
                    .setActivityType(element)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()
        }
    }

    fun getActivityTypeNameFromInt(transitionInt : Int) : String {
        var result = "UNKNOWN"
        when (transitionInt) {
            DetectedActivity.IN_VEHICLE -> {
                result = "IN_VEHICLE"
            }
            DetectedActivity.ON_BICYCLE -> {
                result = "ON_BICYCLE"
            }
            DetectedActivity.ON_FOOT -> {
                result = "ON_FOOT"
            }
            DetectedActivity.RUNNING -> {
                result = "RUNNING"
            }
            DetectedActivity.STILL -> {
                result = "STILL"
            }
            DetectedActivity.WALKING -> {
                result = "WALKING"
            }
        }
        return result
    }

    fun getTransitionTypeFromInt(activityTransition : Int) : String {
        var result = "UNKNOWN"
        when (activityTransition) {
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                result = "EXIT"
            }
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                result = "ENTER"
            }
        }
        return result
    }

}