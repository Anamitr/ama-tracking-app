package com.example.ama_tracking_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class GetConfigActivity : AppCompatActivity() {
    companion object {
        private val TAG = GetConfigActivity::class.qualifiedName
    }

    private lateinit var viewModel: ConfigViewModel

//    private val viewModel: ConfigViewModel by viewModels(
//        factoryProducer = { SavedStateVMFactory(this) }
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        getConfigButton.setOnClickListener { getConfig() }

        setTestConfigId()
    }

    private fun getConfig() {
        viewModel.loadConfig(configIdEditText.text.toString())
    }

    @Subscribe
    fun onMessageEvent(event: ConfigViewModel.ConfigLoadedEvent?) {
        Toast.makeText(this, "Config loaded!", Toast.LENGTH_SHORT).show()
        startActivity(GeofenceLogIntent())
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