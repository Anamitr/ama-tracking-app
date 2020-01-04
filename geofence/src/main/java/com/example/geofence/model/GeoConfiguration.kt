package com.example.geofence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//TODO: Can model be entity and dto?
// It can but watch out if every field from dto should be written tp db etc.
@Entity
data class GeoConfiguration(
    @PrimaryKey val id: String = "",
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "position_interval_in_minutes") val positionIntervalInMinutes: Int = 0,
    @ColumnInfo(name = "token") val token: String = "",
    @ColumnInfo(name = "tracked_object_id") val trackedObjectId: String = ""
) : Serializable