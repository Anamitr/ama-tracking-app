package com.example.ama_tracking_app.base

import android.app.Application
import android.widget.Toast
import com.example.geofence.util.ToastEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CustomApplication : Application() {

    //TODO: EventBus doesn't work in Application but according to the Internet it should
    @Subscribe
    fun onMessageEvent(event: ToastEvent) {
//        Toast.makeText(this, "Config loaded!", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, event.message, event.toastLength).show()
    }


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

}