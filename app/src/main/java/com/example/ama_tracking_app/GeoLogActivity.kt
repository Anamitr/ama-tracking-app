package com.example.ama_tracking_app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ama_tracking_app.base.BaseActivity
import com.example.ama_tracking_app.util.InjectorUtils
import com.example.ama_tracking_app.viewmodel.GeoLogViewModel
import com.example.geofence.ActivityTransitionBroadcastReceiver
import com.example.geofence.GeofenceController
import com.example.geofence.util.ConfigLoadedFromDbEvent
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
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

        log_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@GeoLogActivity)
            adapter = viewModel.recyclerViewAdapter
        }

        viewModel.getGeoLogsLiveData()
            .observe(
                this,
                Observer { geoLogs ->
                    run {
                        viewModel.recyclerViewAdapter.setData(geoLogs)
                        log_recycler_view.scrollToPosition(geoLogs.size - 1)
                    }
                })
    }

    @Subscribe
    fun onMessageEvent(event: ConfigLoadedFromDbEvent) {
        configNameTextView.text = event.geoConfiguration.name
        GeofenceController.init(applicationContext, event.geoConfiguration)
        GeofenceController.startGeofenceService()
    }

    fun toggleGeoLocationService(view: View) {
        val result = GeofenceController.toggleGeofenceService()
        if (result) {
            toggleGeofenceServiceButton.text = "STOP"
        } else {
            toggleGeofenceServiceButton.text = "START"
        }
    }

    fun clearLogsButtonClick(view: View) {
        val dialogBuilder = AlertDialog.Builder(this).apply {
            title = "Clear logs"
            setMessage("Are you sure you want to clear logs?")
            setPositiveButton("Yes") { dialog, which ->
                viewModel.clearLogs()
            }
            setNegativeButton("No") { dialog, which -> }
            show()
        }
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

    fun testFun(view: View) {
        emulateActivityTransition()
    }

    fun emulateActivityTransition() {
        var intent = Intent()

        // Your broadcast receiver action

        intent.action = ActivityTransitionBroadcastReceiver.INTENT_ACTION
        var events: ArrayList<ActivityTransitionEvent> = arrayListOf()

        // You can set desired events with their corresponding state
        var detectedActivity = DetectedActivity.STILL
        if (GeofenceController.currentActivityType == DetectedActivity.STILL) detectedActivity =
            DetectedActivity.ON_BICYCLE
        var transitionEvent = ActivityTransitionEvent(
            detectedActivity,
            ActivityTransition.ACTIVITY_TRANSITION_ENTER,
            SystemClock.elapsedRealtimeNanos()
        )
//        GeofenceController.setActivityType(detectedActivity)

        events.add(transitionEvent)
        var result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(
            result,
            intent,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )
//        activity?.sendBroadcast(intent)
        this.sendBroadcast(intent)

//        intent = Intent(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
//        context.sendBroadcast(intent)
//
//        intent = Intent()
//        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION)
//        context.sendBroadcast(intent)
    }

}
