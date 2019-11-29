package com.example.ama_tracking_app.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.ama_tracking_app.model.GeoConfiguration

//TODO: Should I write everywhere "suspend" to prevent executing on main thread?
// Not necessarily, removed it from function loadById taking example from Google Sunflower app
@Dao
abstract class GeoConfigurationDao : BaseDao<GeoConfiguration>() {
    @Query("SELECT * FROM geoconfiguration WHERE id LIKE :configId")
    abstract fun loadById(configId: String): LiveData<GeoConfiguration>
}