package com.example.ama_tracking_app.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.ama_tracking_app.model.GeoConfiguration

//TODO: Should I write everywhere "suspend" to prevent executing on main thread?
// If you want to use coroutines then yes
@Dao
abstract class GeoConfigurationDao : BaseDao<GeoConfiguration>() {
    @Query("SELECT * FROM geoconfiguration WHERE id LIKE :configId")
    abstract fun loadById(configId: String): LiveData<GeoConfiguration>
}