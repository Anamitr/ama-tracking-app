package com.example.ama_tracking_app.service

import com.example.ama_tracking_app.model.GeoConfiguration
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