package com.example.geofence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class GeoLog (
    @ColumnInfo(name = "date")var date : Date? = null,
    @ColumnInfo(name = "content") val content : String = ""
) {
    @PrimaryKey(autoGenerate = true) var id : Int = 0

    override fun toString(): String {
        return "GeoLog(id=$id, date=$date, content='$content')"
    }
}

