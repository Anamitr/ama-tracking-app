package com.example.geofence.service

import com.example.geofence.model.GeoConfiguration
import com.example.geofence.model.GeoLog
import retrofit2.Call
import retrofit2.http.*

interface FirebaseService {
    @GET("getConfig")
    fun getConfig(@Query("id") configId: String): Call<GeoConfiguration>

    @PUT("updateConfig")
    fun updateConfig(@Query("id") configId: String, @Body geoConfiguration: GeoConfiguration): Call<String>

    @POST("postLog")
    fun postLog(@Body geoLog: GeoLog): Call<String>

    @GET("getNextLogId")
    fun getNextLogId(@Query("id") configId: String) : Call<String>

    @DELETE("clearLogs")
    fun clearLogs(@Query("id") configId: String) : Call<String>
}