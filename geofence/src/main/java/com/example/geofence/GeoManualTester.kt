package com.example.geofence

import android.content.Context
import android.nfc.Tag
import android.util.Log
import com.example.geofence.db.AppDatabase
import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class GeoManualTester(val context: Context) {
    companion object {
        private val TAG = GeoManualTester::class.java.simpleName
    }

    fun testGeoConfigWritingAndReadingFromDb() {
        GlobalScope.launch {
            val configId = "78"
            val geoConfigurationDao =
                AppDatabase.getInstance(context.applicationContext).geoConfigurationDao()

            val geoConfiguration1 = GeoConfiguration(configId, "geoConfiguration1", 7, "", "")

            GlobalScope.launch {
                geoConfigurationDao.insert(geoConfiguration1)
            }

            val loadedGeoConfiguration1 = geoConfigurationDao.loadAll()
            Log.v(TAG, "loadAll: $loadedGeoConfiguration1")

            val loadedGeoConfiguration2 = geoConfigurationDao.loadById(configId)
            Log.v(TAG, "loadById($configId): ${loadedGeoConfiguration2}")
        }
    }

    fun testGeoLogDbWritingAndReadingFromDb() {
        GlobalScope.launch {
            val geoLog = GeoLog(Date(), "Test log 1")
            val geoLogDao = AppDatabase.getInstance(context.applicationContext).geoLogDao()
            geoLogDao.insert(geoLog)

            Log.v(TAG, "geoLogDao.loadAll(): ${geoLogDao.loadAll()}")
        }
    }
}