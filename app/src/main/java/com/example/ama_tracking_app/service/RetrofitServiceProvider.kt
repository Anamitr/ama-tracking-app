package com.example.ama_tracking_app.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitServiceProvider {
    private const val base_url: String = "https://us-central1-ama-tracking-app.cloudfunctions.net/"
    //TODO: How to init this? lateinit var or val and init right after declaration
    // Use dagger
    private val firebaseService: FirebaseService

    init {
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create())
                .build()
        firebaseService = retrofit.create(FirebaseService::class.java)
    }

    public fun getFirebaseService(): FirebaseService {
        return firebaseService
    }
}