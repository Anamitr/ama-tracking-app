package com.example.geofence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO: Can model be entity and dto?
// It can but watch out if every field from dto should be written tp db etc.
@Entity
data class GeoConfiguration(
    @PrimaryKey val id : String = "",
    val name: String = "",
    val positionIntervalInMinutes: Int = 0,
    val token: String = "",
    val trackedObjectId: String = ""
)