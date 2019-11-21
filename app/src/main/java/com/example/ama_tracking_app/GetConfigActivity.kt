package com.example.ama_tracking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class GetConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getConfigButton.setOnClickListener { getConfig() }
    }

    private fun getConfig() {
        Toast.makeText(this, "getConfig", Toast.LENGTH_SHORT).show()
        startActivity(GeofenceLogIntent(idEditText.text.toString()))
    }
}