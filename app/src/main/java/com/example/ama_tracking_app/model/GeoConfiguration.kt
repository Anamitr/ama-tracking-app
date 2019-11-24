package com.example.ama_tracking_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO: Can model be entity?
@Entity
data class GeoConfiguration(
    @PrimaryKey val id : String = "",
    val name: String = "",
    val positionIntervalInMinutes: Int = 0,
    val token: String = "",
    val trackedObjectId: String = ""
)