package com.example.ama_tracking_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.ama_tracking_app.base.BaseActivity
import com.example.ama_tracking_app.util.InjectorUtils
import com.example.ama_tracking_app.viewmodel.GeoLogViewModel
import com.example.geofence.GeofenceController
import com.example.geofence.util.ConfigLoadedFromDb
import kotlinx.android.synthetic.main.activity_geofence_log.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


fun Context.GeoLogIntent(id: String): Intent {
    return Intent(this, GeoLogActivity::class.java).apply {
        putExtra(INTENT_ID, id)
    }
}

fun Context.GeoLogIntent(): Intent {
    return Intent(this, GeoLogActivity::class.java)
}

private const val INTENT_ID = "id"

class GeoLogActivity : BaseActivity() {

    companion object {
        val TAG = GeoLogActivity::class.java.simpleName
    }

    //    private lateinit var viewModel: ConfigViewModel
    private lateinit var viewModel: GeoLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ama_tracking_app.R.layout.activity_geofence_log)
        EventBus.getDefault().register(this)

        val configId = intent.getStringExtra(INTENT_ID)
//        Toast.makeText(this, "Id: $id", Toast.LENGTH_SHORT).show()

        viewModel =
            ViewModelProviders.of(this, InjectorUtils.provideGeoLogViewModelFactory(this, configId))
                .get(GeoLogViewModel::class.java)

        //TODO: DataBinding
//        configNameTextView.text = viewModel.geoConfiguration.name

    }

    @Subscribe
    fun onMessageEvent(event: ConfigLoadedFromDb) {
        configNameTextView.text = event.geoConfiguration.name
        GeofenceController.init(applicationContext, event.geoConfiguration)
        GeofenceController.startGeofenceService()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(applicationContext, "Stopping foreground service", Toast.LENGTH_SHORT).show()
        GeofenceController.stopGeofenceService()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
