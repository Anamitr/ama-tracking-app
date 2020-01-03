package com.example.geofence.util

import android.widget.Toast
import com.example.geofence.model.GeoConfiguration

data class ToastEvent(val message: String, val toastLength : Int = Toast.LENGTH_SHORT)

class ConfigLoadedToDbEvent(val geoConfiguration: GeoConfiguration)

class ConfigLoadedToViewModelEvent

class ConfigLoadedFromDb(val geoConfiguration: GeoConfiguration)