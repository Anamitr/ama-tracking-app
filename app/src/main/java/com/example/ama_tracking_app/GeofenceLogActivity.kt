package com.example.ama_tracking_app

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.example.ama_tracking_app.base.BaseActivity
import com.example.ama_tracking_app.util.InjectorUtils
import com.example.ama_tracking_app.viewmodel.ConfigViewModel
import com.example.geofence.GeofenceController
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.ArrayList


fun Context.GeofenceLogIntent(id: String): Intent {
    return Intent(this, GeofenceLogActivity::class.java).apply {
        putExtra(INTENT_ID, id)
    }
}

fun Context.GeofenceLogIntent(): Intent {
    return Intent(this, GeofenceLogActivity::class.java)
}

private const val INTENT_ID = "id"

class GeofenceLogActivity : BaseActivity() {

    companion object {
        val TAG = GeofenceLogActivity::class.java.simpleName
    }

    private lateinit var viewModel: ConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ama_tracking_app.R.layout.activity_geofence_log)

        val configId = intent.getStringExtra(INTENT_ID)
//        Toast.makeText(this, "Id: $id", Toast.LENGTH_SHORT).show()

        viewModel = ViewModelProviders.of(this, InjectorUtils.provideConfigViewModelFactory(this, configId)).get(ConfigViewModel::class.java)

        //TODO: DataBinding
//        configNameTextView.text = viewModel.geoConfiguration.name

        GeofenceController.init(applicationContext)
        GeofenceController.startGeofenceService(this)

    }






}
