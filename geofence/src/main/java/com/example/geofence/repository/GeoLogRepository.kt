package com.example.geofence.repository

import com.example.geofence.db.GeoLogDao
import com.example.geofence.model.GeoLog
import com.example.geofence.service.FirebaseService
import com.example.geofence.service.RetrofitServiceProvider

class GeoLogRepository private constructor(
    private val geoLogDao: GeoLogDao,
    private val firebaseService: FirebaseService
) {
    suspend fun insertGeoLog(geoLog : GeoLog) {
        geoLogDao.insert(geoLog)
//        firebaseService.postLog(geoLog)
    }

    fun getAllById(configId : String) : List<GeoLog>{
        return geoLogDao.loadByConfigId(configId)
    }

    companion object {
        private val TAG = GeoLogRepository::class.java.simpleName

        // For Singleton instantiation
        @Volatile
        private var instance: GeoLogRepository? = null

        fun getInstance(geoLogDao: GeoLogDao) =
            instance ?: synchronized(this) {
                instance ?: GeoLogRepository(
                    geoLogDao,
                    RetrofitServiceProvider.getFirebaseService()
                ).also { instance = it }
            }
    }

}