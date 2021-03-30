package com.hypertrack.quickstart

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hypertrack.quickstart.android.github.R
import com.hypertrack.sdk.HyperTrack
import com.hypertrack.sdk.TrackingError
import com.hypertrack.sdk.TrackingError.*
import com.hypertrack.sdk.TrackingStateObserver


class MainActivity : AppCompatActivity(), TrackingStateObserver.OnTrackingStateChangeListener {

    private lateinit var trackingStatus: TextView
    private lateinit var sdkInstance: HyperTrack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        sdkInstance = HyperTrack
                .getInstance(PUBLISHABLE_KEY)
                .addTrackingListener(this)

        trackingStatus = findViewById(R.id.statusLabel)
        val deviceId = findViewById<TextView>(R.id.deviceIdTextView)
        deviceId.text = sdkInstance.deviceID
        Log.d(TAG, "device id is ${sdkInstance.deviceID}")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        when {
            sdkInstance.isRunning -> onTrackingStart()
            else -> onTrackingStop()
        }

        sdkInstance.requestPermissionsIfNecessary()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        sdkInstance.removeTrackingListener(this)

    }

    // TrackingStateObserver.OnTrackingStateChangeListener interface methods

    override fun onError(trackingError: TrackingError?) {
        Log.d(TAG, "onError ${trackingError?.message}")

        when (trackingError?.code){
            GPS_PROVIDER_DISABLED_ERROR ->
                trackingStatus.text = resources.getText(R.string.enable_gps)
            INVALID_PUBLISHABLE_KEY_ERROR, AUTHORIZATION_ERROR ->
                trackingStatus.text = resources.getText(R.string.check_publishable_key)
            PERMISSION_DENIED_ERROR ->
                trackingStatus.text = resources.getText(R.string.permissions_not_granted)
            else ->
                trackingStatus.text = resources.getText(R.string.cant_start_tracking)
        }
        trackingStatus.setBackgroundColor(resources.getColor(R.color.colorLabelError))
    }

    override fun onTrackingStart() {
        Log.d(TAG, "onTrackingStart")
        trackingStatus.setBackgroundColor(resources.getColor(R.color.colorLabelActive))
        trackingStatus.text = resources.getText(R.string.tracking_active)
    }

    override fun onTrackingStop() {
        Log.d(TAG, "onTrackingStop")

        trackingStatus.setBackgroundColor(resources.getColor(R.color.colorLabelStopped))
        trackingStatus.text = resources.getText(R.string.tracking_stopped)
    }

    companion object {

        private const val TAG = "MainActivity"
        private const val PUBLISHABLE_KEY = "paste_your_key_here"
    }
}
