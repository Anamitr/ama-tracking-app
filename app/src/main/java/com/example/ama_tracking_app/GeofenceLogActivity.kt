package com.example.ama_tracking_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.ama_tracking_app.viewmodel.ConfigViewModel
import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import kotlinx.android.synthetic.main.activity_geofence_log.*

fun Context.GeofenceLogIntent(id: String): Intent {
    return Intent(this, GeofenceLogActivity::class.java).apply {
        putExtra(INTENT_ID, id)
    }
}

fun Context.GeofenceLogIntent(): Intent {
    return Intent(this, GeofenceLogActivity::class.java)
}

private const val INTENT_ID = "id"

class GeofenceLogActivity : AppCompatActivity() {

    private lateinit var viewModel: ConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence_log)

        val id = intent.getStringExtra(INTENT_ID)
        Toast.makeText(this, "Id: $id", Toast.LENGTH_SHORT).show()

        viewModel = ViewModelProviders.of(this, ConfigViewModelFactory(application, id)).get(ConfigViewModel::class.java)
        configNameTextView.text = viewModel.geoConfiguration.name
    }
}
