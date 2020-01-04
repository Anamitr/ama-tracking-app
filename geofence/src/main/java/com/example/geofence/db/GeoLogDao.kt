package com.example.geofence.db

import androidx.room.Dao
import androidx.room.Query
import com.example.geofence.model.GeoLog

@Dao
abstract class GeoLogDao : BaseDao<GeoLog>(){
    @Query("SELECT * FROM geolog")
    abstract fun loadAll() : List<GeoLog>

}
