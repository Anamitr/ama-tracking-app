package com.example.ama_tracking_app.model

data class GeoConfiguration(
    val id : String = "",
    val name: String = "",
    val positionIntervalInMinutes: Int = 0,
    val token: String = "",
    val trackedObjectId: String = ""
)