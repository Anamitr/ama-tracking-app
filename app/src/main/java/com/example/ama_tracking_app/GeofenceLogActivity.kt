package com.example.ama_tracking_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

fun Context.GeofenceLogIntent(id: String): Intent {
    return Intent(this, GeofenceLogActivity::class.java).apply {
        putExtra(INTENT_ID, id)
    }
}

private const val INTENT_ID = "id";

class GeofenceLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence_log)

        val id = intent.getStringExtra(INTENT_ID)
        Toast.makeText(this, "Id: $id", Toast.LENGTH_SHORT).show()
    }
}
