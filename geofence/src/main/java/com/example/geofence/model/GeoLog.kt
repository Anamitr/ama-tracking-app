package com.example.geofence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class GeoLog (
    @ColumnInfo(name = "external_id") var externalId : String = "",
    @ColumnInfo(name = "config_id") var configId : String = "",
    @ColumnInfo(name = "date")var date : Date? = null,
    @ColumnInfo(name = "content") val content : String = ""
) {
    @PrimaryKey(autoGenerate = true) var internalId : Long = 0

    override fun toString(): String {
        return "GeoLog(externalId='$externalId', internalId=$internalId, configId='$configId', date=$date, content='$content')"
    }
}

