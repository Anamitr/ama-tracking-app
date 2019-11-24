package com.example.ama_tracking_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.ama_tracking_app.viewmodel.ConfigViewModel
import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
//import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class GetConfigActivity : AppCompatActivity() {
    companion object {
        private val TAG = GetConfigActivity::class.qualifiedName
    }

    private lateinit var viewModel: ConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, ConfigViewModelFactory(application)).get(ConfigViewModel::class.java)
        getConfigButton.setOnClickListener { viewModel.loadConfig(configIdEditText.text.toString()) }

        setTestConfigId()
    }

    //TODO: In MVVM should I start activity from activity or ViewModel?
    @Subscribe
    fun onMessageEvent(event: ConfigViewModel.ConfigLoadedEvent?) {
        Toast.makeText(this, "Config loaded!", Toast.LENGTH_SHORT).show()
        startActivity(GeofenceLogIntent(viewModel.geoConfiguration.id))
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