package com.example.ama_tracking_app.db

import androidx.room.*
import com.example.ama_tracking_app.model.GeoConfiguration

//TODO: Should I write everywhere "suspend" to prevent executing on main thread?
@Dao
abstract class GeoConfigurationDao : BaseDao<GeoConfiguration>() {
    @Query("SELECT * FROM geoconfiguration WHERE id LIKE :configId")
    abstract suspend fun loadById(configId: String): GeoConfiguration?
}