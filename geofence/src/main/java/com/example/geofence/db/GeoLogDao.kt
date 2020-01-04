package com.example.geofence.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.geofence.model.GeoLog

@Dao
abstract class GeoLogDao : BaseDao<GeoLog>(){
    @Query("SELECT * FROM geolog")
    abstract fun loadAll() : List<GeoLog>

    @Query("SELECT * FROM geolog where config_id LIKE :configId")
    abstract fun loadByConfigId(configId : String) : List<GeoLog>

    @Query("SELECT * FROM geolog WHERE config_id LIKE :configId ORDER BY date ")
    abstract fun getLiveDataGeoLogsByConfigId(configId: String) : LiveData<List<GeoLog>>

}
