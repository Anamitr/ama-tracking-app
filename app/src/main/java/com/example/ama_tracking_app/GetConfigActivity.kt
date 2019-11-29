package com.example.ama_tracking_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ama_tracking_app.util.InjectorUtils
import com.example.ama_tracking_app.viewmodel.ConfigViewModel
//import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class GetConfigActivity : AppCompatActivity() {
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

        setTestConfigId()
    }

    //TODO: In MVVM should I start activity from activity or ViewModel?
    // Interfaces, don't use EventBus
    @Subscribe
    fun onMessageEvent(event: ConfigLoadedToViewModelEvent) {
//        Toast.makeText(this, "Config loaded!", Toast.LENGTH_SHORT).show()
        val configId = viewModel.geoConfiguration.id
        configId?.let { startActivity(GeofenceLogIntent(configId)) }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun setTestConfigId() {
        configIdEditText.setText("22")
    }
}