package com.example.ama_tracking_app

import android.widget.Toast
import com.example.ama_tracking_app.model.GeoConfiguration

data class ToastEvent(val message: String, val toastLength : Int = Toast.LENGTH_SHORT)

class ConfigLoadedToDbEvent(val geoConfiguration: GeoConfiguration)

class ConfigLoadedToViewModelEvent