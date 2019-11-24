package com.example.ama_tracking_app.db

import android.content.Context
import androidx.room.Room
import com.example.ama_tracking_app.util.DATABASE_NAME

object DatabaseProvider {
    fun getDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()

}