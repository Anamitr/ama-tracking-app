package com.example.ama_tracking_app

//import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.ama_tracking_app.base.BaseActivity
import com.example.geofence.util.ConfigLoadedToViewModelEvent
import com.example.ama_tracking_app.util.InjectorUtils
import com.example.ama_tracking_app.viewmodel.ConfigViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class GetConfigActivity : BaseActivity() {
    companion object {
        private val TAG = GetConfigActivity::class.qualifiedName
    }

    private val viewModel: ConfigViewModel by viewModels {
        InjectorUtils.provideConfigViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        viewModel = ViewModelProviders.of(this, ConfigViewModelFactory(application)).get(ConfigViewModel::class.java)
        getConfigButton.setOnClickListener { viewModel.loadConfigFromFirebase(configIdEditText.text.toString()) }

        if (!checkPermissions()) {
            requestPermissions()
        }

        setTestConfigId()
    }

    fun testSth(view : View) {
        val manualTester = ManualTester()
        manualTester.sendLog("hejo")
    }

    //TODO: In MVVM should I start activity from activity or ViewModel?
    // - It depends, but I think it is better when there is no direct use of Context in ViewModel

    //TODO: Interfaces, don't use EventBus
    // As described here: https://medium.com/@erik_90880/sending-events-from-an-mvvm-view-model-with-kotlin-19fdce61dcb9
    // Interfaces are bad because we don't know if objects behind them are alive
    // Events are said to be not understandable.
    // Proposition is to use SingleLiveEvent, that is supposed to combine pros of both solutions and eliminate their cons
    // Nevertheless SingleLiveEvent isn't official solution, so it may be not the best idea getting used to it
    @Subscribe
    fun onMessageEvent(event: ConfigLoadedToViewModelEvent) {
//        Toast.makeText(this, "Config loaded!", Toast.LENGTH_SHORT).show()
        val configId = viewModel.geoConfiguration.id
        configId?.let { startActivity(GeofenceLogIntent(configId)) }
    }


    fun setTestConfigId() {
        configIdEditText.setText("22")
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.i(GeofenceLogActivity.TAG, "Permission Granted")
                Toast.makeText(this@GetConfigActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.e(GeofenceLogActivity.TAG, "Permission Denied: $deniedPermissions")
                Toast.makeText(
                    this@GetConfigActivity,
                    "Permission Denied: $deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .check()
    }

}