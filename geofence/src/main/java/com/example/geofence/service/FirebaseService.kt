package com.example.geofence.service

import com.example.geofence.model.GeoConfiguration
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface FirebaseService {
    @GET("getConfig")
    fun getConfig(@Query("id") configId: String): Call<GeoConfiguration>

    @PUT("updateConfig")
    fun updateConfig(@Query("id") configId: String, @Body geoConfiguration: GeoConfiguration): Call<String>
}