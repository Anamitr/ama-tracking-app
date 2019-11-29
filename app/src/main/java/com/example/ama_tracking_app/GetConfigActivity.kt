package com.example.ama_tracking_app

//import com.example.ama_tracking_app.viewmodel.ConfigViewModelFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ama_tracking_app.base.BaseActivity
import com.example.ama_tracking_app.util.ConfigLoadedToViewModelEvent
import com.example.ama_tracking_app.util.InjectorUtils
import com.example.ama_tracking_app.viewmodel.ConfigViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


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

        setTestConfigId()
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

}